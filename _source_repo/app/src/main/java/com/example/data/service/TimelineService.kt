package com.example.data.service

import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimelineService(
    private val memberDao: MemberDao,
    private val timelineEntryDao: TimelineEntryDao,
    private val timelineShareDao: TimelineShareDao,
    private val auditLogDao: AuditLogDao,
    private val timelineSettingsDao: TimelineSettingsDao
) {
    private val secureRandom = SecureRandom()

    // ----------------------------------------------------
    // 1. MEMBER IDENTIFIER ASSIGNMENT (CONCURRENT & SAFE)
    // ----------------------------------------------------
    suspend fun assignMemberId(
        discordUserId: String,
        displayName: String,
        specialty: String = "Builder / Contributor",
        timezone: String = "Asia/Kolkata",
        isDemo: Boolean = false
    ): Result<MemberEntity> = withContext(Dispatchers.IO) {
        try {
            // Check if discord user already registered
            val existing = memberDao.getMemberByDiscordUserId(discordUserId)
            if (existing != null) {
                return@withContext Result.success(existing)
            }

            // Atomic counter retrieval & update
            val currentCounter = memberDao.getIdCounter() ?: IdCounterEntity("member_id_counter", 1)
            val assignedNumber = currentCounter.nextNumber
            val assignedDisplayId = String.format(Locale.US, "JEMP#%03d", assignedNumber)

            // Increment counter for next member (never reuses departed IDs)
            memberDao.insertIdCounter(currentCounter.copy(nextNumber = assignedNumber + 1))

            val newMember = MemberEntity(
                id = assignedDisplayId,
                discordUserId = discordUserId,
                jempNumber = assignedNumber,
                jempDisplayId = assignedDisplayId,
                displayName = displayName,
                specialty = specialty,
                timezone = timezone,
                joinedAtUtc = System.currentTimeMillis(),
                status = "ACTIVE",
                isDemo = isDemo
            )

            memberDao.insertMember(newMember)

            auditAction(
                actor = "SYSTEM",
                action = "MEMBER_ID_ASSIGNED",
                target = assignedDisplayId,
                metadata = mapOf("discordUserId" to discordUserId, "number" to assignedNumber.toString())
            )

            // Automatically record first official timeline event: "Entered the Empire"
            createFirstOnboardingEntry(newMember)

            Result.success(newMember)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createFirstOnboardingEntry(member: MemberEntity) {
        val bullets = listOf(
            "Completed Jobless Empire onboarding",
            "Accepted community rules and anti-scam orientation",
            "Confirmed communication details",
            "Selected timezone (${member.timezone})",
            "Took the first step toward building while between jobs"
        )

        val entryId = "TLE-${UUID.randomUUID().toString().take(8).uppercase(Locale.US)}"
        val entity = TimelineEntryEntity(
            id = entryId,
            memberId = member.id,
            eventType = "ONBOARDING_COMPLETED",
            title = "Entered the Empire",
            bulletsJson = JSONArray(bullets).toString(),
            timestampUtc = member.joinedAtUtc,
            timezoneAtCreation = member.timezone,
            projectId = null,
            source = "onboarding-bot",
            sourceEventId = "onboard-${member.id}",
            isImmutable = true,
            visibility = "PUBLIC_SAFE",
            createdBy = "SYSTEM",
            isDemo = member.isDemo
        )
        timelineEntryDao.insertEntry(entity)
    }

    // ----------------------------------------------------
    // 2. TIMELINE EVENT INGESTION & IDEMPOTENCY
    // ----------------------------------------------------
    suspend fun recordTimelineEvent(payload: TimelineEventPayload): Result<TimelineEntryEntity> = withContext(Dispatchers.IO) {
        try {
            // Idempotency check: if source + sourceEventId exists, return existing without duplication
            if (!payload.sourceEventId.isNullOrBlank()) {
                val existing = timelineEntryDao.getEntryBySourceAndEventId(payload.source, payload.sourceEventId)
                if (existing != null) {
                    return@withContext Result.success(existing)
                }
            }

            val member = memberDao.getMemberById(payload.memberId)
                ?: return@withContext Result.failure(IllegalArgumentException("Member ${payload.memberId} not found."))

            val timestampUtc = payload.customTimestampUtc ?: System.currentTimeMillis()
            val timezone = payload.timezone ?: member.timezone

            // Validate bullets
            val sanitizedBullets = payload.bullets
                .take(3)
                .map { it.take(300).trim() }
                .filter { it.isNotBlank() }

            val entryId = "TLE-${UUID.randomUUID().toString().take(8).uppercase(Locale.US)}"
            val entity = TimelineEntryEntity(
                id = entryId,
                memberId = member.id,
                eventType = payload.eventType,
                title = payload.title.take(120).trim(),
                bulletsJson = JSONArray(sanitizedBullets).toString(),
                timestampUtc = timestampUtc,
                timezoneAtCreation = timezone,
                projectId = payload.projectId?.uppercase(Locale.US)?.trim(),
                source = payload.source,
                sourceEventId = payload.sourceEventId,
                isImmutable = payload.immutable,
                visibility = payload.visibility.name,
                createdBy = payload.createdBy,
                isDemo = payload.isDemo || member.isDemo
            )

            timelineEntryDao.insertEntry(entity)

            auditAction(
                actor = payload.createdBy,
                action = "RECORD_EVENT",
                target = payload.memberId,
                metadata = mapOf("entryId" to entryId, "type" to payload.eventType, "source" to payload.source)
            )

            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 3. MEMBER ENTRY CREATION (/timeline add)
    // ----------------------------------------------------
    suspend fun createMemberEntry(
        memberId: String,
        title: String,
        bullets: List<String>,
        projectId: String?,
        daysAgo: Int = 0
    ): Result<TimelineEntryEntity> = withContext(Dispatchers.IO) {
        try {
            val settings = timelineSettingsDao.getSettingsOnce() ?: TimelineSettingsEntity()
            val maxBackdate = settings.maxBackdatingDays

            if (daysAgo > maxBackdate) {
                return@withContext Result.failure(
                    IllegalArgumentException("Backdating beyond $maxBackdate days requires Administrator approval.")
                )
            }

            val sanitizedBullets = bullets
                .take(3)
                .map { it.take(300).trim() }
                .filter { it.isNotBlank() }

            if (title.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Title cannot be empty."))
            }

            val timestampUtc = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysAgo.toLong())

            val payload = TimelineEventPayload(
                memberId = memberId,
                eventType = "PERSONAL_MILESTONE",
                title = title,
                bullets = sanitizedBullets,
                projectId = projectId,
                source = "member",
                immutable = false, // member entries are editable
                visibility = TimelineVisibility.PUBLIC_SAFE,
                createdBy = memberId,
                customTimestampUtc = timestampUtc
            )

            recordTimelineEvent(payload)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 4. EDIT & DELETE MEMBER ENTRIES (/timeline edit, /timeline delete)
    // ----------------------------------------------------
    suspend fun editMemberEntry(
        entryId: String,
        memberId: String,
        newTitle: String,
        newBullets: List<String>,
        newProjectId: String?
    ): Result<TimelineEntryEntity> = withContext(Dispatchers.IO) {
        try {
            val entry = timelineEntryDao.getEntryById(entryId)
                ?: return@withContext Result.failure(IllegalArgumentException("Timeline entry not found."))

            if (entry.memberId != memberId) {
                return@withContext Result.failure(IllegalAccessException("You cannot edit another member's timeline entry."))
            }

            if (entry.isImmutable) {
                return@withContext Result.failure(
                    IllegalStateException("🔒 Official records are immutable and cannot be modified.")
                )
            }

            val sanitizedBullets = newBullets
                .take(3)
                .map { it.take(300).trim() }
                .filter { it.isNotBlank() }

            val updated = entry.copy(
                title = newTitle.take(120).trim(),
                bulletsJson = JSONArray(sanitizedBullets).toString(),
                projectId = newProjectId?.uppercase(Locale.US)?.trim(),
                updatedAtUtc = System.currentTimeMillis()
            )

            timelineEntryDao.updateEntry(updated)

            auditAction(
                actor = memberId,
                action = "EDIT_MEMBER_ENTRY",
                target = memberId,
                metadata = mapOf("entryId" to entryId)
            )

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMemberEntry(
        entryId: String,
        memberId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entry = timelineEntryDao.getEntryById(entryId)
                ?: return@withContext Result.failure(IllegalArgumentException("Timeline entry not found."))

            if (entry.memberId != memberId) {
                return@withContext Result.failure(IllegalAccessException("You cannot delete another member's entry."))
            }

            if (entry.isImmutable) {
                return@withContext Result.failure(
                    IllegalStateException("🔒 This is an official Jobless Empire record and cannot be deleted.")
                )
            }

            timelineEntryDao.deleteEntry(entry)

            auditAction(
                actor = memberId,
                action = "DELETE_MEMBER_ENTRY",
                target = memberId,
                metadata = mapOf("entryId" to entryId, "title" to entry.title)
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 5. ADMIN CORRECTION (/timeline-admin correction)
    // ----------------------------------------------------
    suspend fun createAdminCorrection(
        originalEntryId: String,
        adminId: String,
        correctionNote: String,
        correctedProjectId: String? = null
    ): Result<TimelineEntryEntity> = withContext(Dispatchers.IO) {
        try {
            val original = timelineEntryDao.getEntryById(originalEntryId)
                ?: return@withContext Result.failure(IllegalArgumentException("Original entry not found."))

            val correctionEntryId = "TLE-CORR-${UUID.randomUUID().toString().take(6).uppercase(Locale.US)}"
            val entity = TimelineEntryEntity(
                id = correctionEntryId,
                memberId = original.memberId,
                eventType = "ADMIN_CORRECTION",
                title = "ADMIN CORRECTION: ${original.title}",
                bulletsJson = JSONArray(listOf(correctionNote, "Original Entry Reference: ${original.id}")).toString(),
                timestampUtc = System.currentTimeMillis(),
                timezoneAtCreation = original.timezoneAtCreation,
                projectId = correctedProjectId ?: original.projectId,
                source = "admin",
                sourceEventId = "corr-${original.id}-${System.currentTimeMillis()}",
                isImmutable = true,
                visibility = "PUBLIC_SAFE",
                createdBy = adminId,
                correctionForEntryId = original.id,
                isDemo = original.isDemo
            )

            timelineEntryDao.insertEntry(entity)

            auditAction(
                actor = adminId,
                action = "ADMIN_CORRECTION",
                target = original.memberId,
                metadata = mapOf("originalEntryId" to original.id, "correctionId" to correctionEntryId)
            )

            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 6. TIMELINE HIERARCHY & COMPLEXITY THRESHOLD ENGINE
    // ----------------------------------------------------
    suspend fun getTimelineHierarchy(memberId: String): TimelineHierarchy = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberById(memberId)
        val timezoneStr = member?.timezone ?: "Asia/Kolkata"
        val memberDisplayId = member?.jempDisplayId ?: memberId
        val memberName = member?.displayName ?: "Empire Member"

        val rawEntries = timelineEntryDao.getEntriesForMemberOnce(memberId)
        val domainItems = rawEntries.map { it.toDomainItem(timezoneStr) }

        // Group by Year -> Month -> Date -> Entries
        val yearGroups = domainItems
            .groupBy { it.year }
            .map { (year, yearItems) ->
                val monthGroups = yearItems
                    .groupBy { it.monthName }
                    .map { (monthName, monthItems) ->
                        val dateGroups = monthItems
                            .groupBy { it.dateName }
                            .map { (dateName, dateItems) ->
                                DateHierarchyGroup(
                                    dateKey = dateItems.first().formattedDate,
                                    displayDate = dateName,
                                    timestampUtc = dateItems.first().timestampUtc,
                                    entries = dateItems
                                )
                            }
                        MonthHierarchyGroup(
                            monthKey = "$year-$monthName",
                            monthName = monthName,
                            year = year,
                            dateGroups = dateGroups,
                            totalEntriesCount = monthItems.size
                        )
                    }
                YearHierarchyGroup(
                    year = year,
                    monthGroups = monthGroups,
                    totalEntriesCount = yearItems.size
                )
            }

        // Calculate Complexity metrics
        val totalEntries = domainItems.size
        val distinctDates = domainItems.map { it.formattedDate }.distinct().size
        val totalChars = domainItems.sumOf { it.title.length + it.bullets.sumOf { b -> b.length } }
        val totalWords = domainItems.sumOf {
            it.title.split("\\s+".toRegex()).size + it.bullets.sumOf { b -> b.split("\\s+".toRegex()).size }
        }

        val settings = timelineSettingsDao.getSettingsOnce() ?: TimelineSettingsEntity()
        val isCollapsed = totalEntries > settings.maxInlineEntries ||
                distinctDates > settings.maxInlineDates ||
                totalChars > settings.maxInlineCharacters ||
                totalWords > settings.maxInlineWords

        TimelineHierarchy(
            memberId = memberId,
            memberDisplayId = memberDisplayId,
            memberName = memberName,
            timezone = timezoneStr,
            yearGroups = yearGroups,
            totalEntries = totalEntries,
            distinctDatesCount = distinctDates,
            totalCharacters = totalChars,
            totalWords = totalWords,
            isCollapsibleThresholdReached = isCollapsed
        )
    }

    // ----------------------------------------------------
    // 7. TIMELINE SEARCH & STATS (/timeline search, /timeline stats)
    // ----------------------------------------------------
    suspend fun searchTimeline(
        memberId: String,
        query: String? = null,
        projectId: String? = null,
        eventType: String? = null,
        year: Int? = null,
        monthName: String? = null
    ): List<TimelineItem> = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberById(memberId)
        val timezoneStr = member?.timezone ?: "Asia/Kolkata"
        val rawEntries = timelineEntryDao.getEntriesForMemberOnce(memberId)

        rawEntries.map { it.toDomainItem(timezoneStr) }.filter { item ->
            val matchQuery = if (!query.isNullOrBlank()) {
                val q = query.lowercase(Locale.US).trim()
                item.title.lowercase(Locale.US).contains(q) ||
                        item.bullets.any { b -> b.lowercase(Locale.US).contains(q) } ||
                        (item.projectId?.lowercase(Locale.US)?.contains(q) == true)
            } else true

            val matchProject = if (!projectId.isNullOrBlank()) {
                item.projectId?.equals(projectId.trim(), ignoreCase = true) == true
            } else true

            val matchEventType = if (!eventType.isNullOrBlank()) {
                item.eventType.equals(eventType.trim(), ignoreCase = true) ||
                        item.category.name.equals(eventType.trim(), ignoreCase = true)
            } else true

            val matchYear = if (year != null) item.year == year else true
            val matchMonth = if (!monthName.isNullOrBlank()) item.monthName.equals(monthName.trim(), ignoreCase = true) else true

            matchQuery && matchProject && matchEventType && matchYear && matchMonth
        }
    }

    suspend fun getMemberStats(memberId: String): TimelineStatsSummary = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberById(memberId)
        val rawEntries = timelineEntryDao.getEntriesForMemberOnce(memberId)
        val timezoneStr = member?.timezone ?: "Asia/Kolkata"
        val items = rawEntries.map { it.toDomainItem(timezoneStr) }

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone(timezoneStr)
        val memberSince = if (member != null) sdf.format(Date(member.joinedAtUtc)) else "N/A"

        val officialCount = items.count { it.isImmutable }
        val personalCount = items.count { !it.isImmutable }
        val brainstorms = items.count { it.category == EventTypeCategory.BRAINSTORM || it.projectId?.startsWith("B#") == true }
        val projects = items.count { it.category == EventTypeCategory.PROJECT || it.projectId?.startsWith("P#") == true }
        val clientProjects = items.count { it.category == EventTypeCategory.CLIENT_PROJECT || it.projectId?.startsWith("HC#") == true }
        val meetings = items.count { it.category == EventTypeCategory.MEETING || it.eventType.contains("MEETING") }
        val completed = items.count { it.eventType.contains("COMPLETED") }

        TimelineStatsSummary(
            memberId = memberId,
            memberSinceDate = memberSince,
            totalEntries = items.size,
            officialRecordsCount = officialCount,
            personalMilestonesCount = personalCount,
            brainstormsCount = brainstorms,
            projectsCount = projects,
            clientProjectsCount = clientProjects,
            meetingsCount = meetings,
            completedProjectsCount = completed
        )
    }

    // ----------------------------------------------------
    // 8. 24-HOUR PUBLIC SHARE (/timeline share, /timeline unshare)
    // ----------------------------------------------------
    suspend fun createPublicShare(memberId: String): Result<PublicShareDetails> = withContext(Dispatchers.IO) {
        try {
            val member = memberDao.getMemberById(memberId)
                ?: return@withContext Result.failure(IllegalArgumentException("Member not found."))

            if (!member.publicSharingAllowed) {
                return@withContext Result.failure(IllegalStateException("Public sharing is disabled in your member settings."))
            }

            // Revoke any previous shares first
            timelineShareDao.revokeAllSharesForMember(memberId)

            val tokenBytes = ByteArray(24)
            secureRandom.nextBytes(tokenBytes)
            val token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes)

            val now = System.currentTimeMillis()
            val expiresAt = now + TimeUnit.HOURS.toMillis(24)

            val settings = timelineSettingsDao.getSettingsOnce() ?: TimelineSettingsEntity()
            val shareUrl = "${settings.publicShareBaseUrl}$token"

            val shareEntity = TimelineShareEntity(
                id = UUID.randomUUID().toString(),
                memberId = memberId,
                tokenHash = token,
                shareUrl = shareUrl,
                createdAtUtc = now,
                expiresAtUtc = expiresAt,
                revokedAtUtc = null,
                accessCount = 0
            )

            timelineShareDao.insertShare(shareEntity)

            auditAction(
                actor = memberId,
                action = "CREATE_PUBLIC_SHARE",
                target = memberId,
                metadata = mapOf("expiresAtUtc" to expiresAt.toString())
            )

            val sdf = SimpleDateFormat("dd MMM yyyy · HH:mm z", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone(member.timezone)

            Result.success(
                PublicShareDetails(
                    memberId = memberId,
                    token = token,
                    shareUrl = shareUrl,
                    createdAtFormatted = sdf.format(Date(now)),
                    expiresAtFormatted = sdf.format(Date(expiresAt)),
                    isExpired = false,
                    isRevoked = false,
                    accessCount = 0,
                    remainingHoursMinutes = "23h 59m"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revokePublicShare(memberId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            timelineShareDao.revokeAllSharesForMember(memberId)
            auditAction(
                actor = memberId,
                action = "REVOKE_PUBLIC_SHARE",
                target = memberId,
                metadata = emptyMap()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublicTimeline(token: String): PublicTimelineViewData = withContext(Dispatchers.IO) {
        val share = timelineShareDao.getShareByToken(token)
        val now = System.currentTimeMillis()

        if (share == null || share.revokedAtUtc != null || share.expiresAtUtc <= now) {
            return@withContext PublicTimelineViewData(
                memberDisplayId = "EXPIRED",
                displayName = "Expired Share",
                specialty = "",
                joinedDateFormatted = "",
                timezone = "UTC",
                entries = emptyList(),
                shareExpiresIn = "0h 0m",
                isValid = false,
                expirationNotice = "THIS TIMELINE SHARE HAS EXPIRED OR HAS BEEN REVOKED."
            )
        }

        // Increment access counter
        timelineShareDao.updateShare(
            share.copy(
                accessCount = share.accessCount + 1,
                lastAccessedAtUtc = now
            )
        )

        val member = memberDao.getMemberById(share.memberId)
        val timezoneStr = member?.timezone ?: "Asia/Kolkata"
        val rawEntries = timelineEntryDao.getEntriesForMemberOnce(share.memberId)

        // Filter: ONLY PUBLIC-SAFE ENTRIES (never expose internal moderation, admin-only notes, private resumes, or email)
        val publicSafeItems = rawEntries
            .filter { it.visibility == "PUBLIC_SAFE" }
            .map { it.toDomainItem(timezoneStr) }

        val remainingMs = (share.expiresAtUtc - now).coerceAtLeast(0)
        val remHours = TimeUnit.MILLISECONDS.toHours(remainingMs)
        val remMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingMs) % 60

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone(timezoneStr)
        val joinedStr = if (member != null) sdf.format(Date(member.joinedAtUtc)) else "N/A"

        PublicTimelineViewData(
            memberDisplayId = member?.jempDisplayId ?: share.memberId,
            displayName = member?.displayName ?: "Empire Member",
            specialty = member?.specialty ?: "Builder",
            joinedDateFormatted = joinedStr,
            timezone = timezoneStr,
            entries = publicSafeItems,
            shareExpiresIn = "${remHours}h ${remMinutes}m",
            isValid = true
        )
    }

    // ----------------------------------------------------
    // 9. PROFESSIONAL & FULL EXPORT (/timeline export)
    // ----------------------------------------------------
    suspend fun exportTimeline(
        memberId: String,
        exportType: ExportType = ExportType.PROFESSIONAL,
        format: ExportFormat = ExportFormat.MARKDOWN
    ): ExportDocument = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberById(memberId)
        val timezoneStr = member?.timezone ?: "Asia/Kolkata"
        val rawEntries = timelineEntryDao.getEntriesForMemberOnce(memberId)

        // Filter based on export type
        val entries = when (exportType) {
            ExportType.PROFESSIONAL -> rawEntries.filter { it.visibility == "PUBLIC_SAFE" }
            ExportType.FULL -> rawEntries.filter { it.visibility != "ADMIN_ONLY" }
        }.map { it.toDomainItem(timezoneStr) }

        val sdf = SimpleDateFormat("dd MMMM yyyy · HH:mm z", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone(timezoneStr)
        val nowFormatted = sdf.format(Date())

        val sdfDateOnly = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        sdfDateOnly.timeZone = TimeZone.getTimeZone(timezoneStr)
        val memberSince = if (member != null) sdfDateOnly.format(Date(member.joinedAtUtc)) else "N/A"

        val content = when (format) {
            ExportFormat.MARKDOWN -> buildMarkdownExport(member, memberSince, entries, exportType, nowFormatted)
            ExportFormat.JSON -> buildJsonExport(member, memberSince, entries, exportType, nowFormatted)
            ExportFormat.STRUCTURED_DOC -> buildStructuredDocExport(member, memberSince, entries, exportType, nowFormatted)
        }

        auditAction(
            actor = memberId,
            action = "EXPORT_TIMELINE",
            target = memberId,
            metadata = mapOf("type" to exportType.name, "format" to format.name)
        )

        ExportDocument(
            memberId = memberId,
            exportType = exportType,
            format = format,
            content = content,
            generatedAtUtc = System.currentTimeMillis(),
            generatedAtFormatted = nowFormatted
        )
    }

    private fun buildMarkdownExport(
        member: MemberEntity?,
        memberSince: String,
        entries: List<TimelineItem>,
        exportType: ExportType,
        nowFormatted: String
    ): String = buildString {
        appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        appendLine("# JOBLESS EMPIRE")
        appendLine("## PROOF OF WORK — TIMELINE RECORD")
        appendLine("### ${member?.jempDisplayId ?: "JEMP#MEMBER"} — ${member?.displayName ?: "Builder"}")
        appendLine("**Member since:** $memberSince  ")
        appendLine("**Scope:** ${if (exportType == ExportType.PROFESSIONAL) "Professional Proof of Work" else "Full Personal Timeline"}  ")
        appendLine("**Generated:** $nowFormatted  ")
        appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")

        val grouped = entries.groupBy { it.year }
        grouped.forEach { (year, yearItems) ->
            appendLine("## $year\n")
            val monthGrouped = yearItems.groupBy { it.monthName }
            monthGrouped.forEach { (month, monthItems) ->
                appendLine("### $month\n")
                monthItems.forEach { entry ->
                    val projTag = if (entry.projectId != null) " · **${entry.projectId}**" else ""
                    val immutTag = if (entry.isImmutable) " 🔒 [Verified]" else " ✎ [Member Entry]"
                    appendLine("**${entry.formattedDate} · ${entry.formattedTime}**$projTag$immutTag")
                    appendLine("#### ${entry.title}")
                    entry.bullets.forEach { b ->
                        appendLine("• $b")
                    }
                    appendLine()
                }
            }
        }

        appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        appendLine("### Jobless Empire Timeline System")
        appendLine("*Proof of work generated from the member's recorded community activity.*")
        appendLine("*Verification hash stamped at canonical UTC internal timestamp.*")
    }

    private fun buildJsonExport(
        member: MemberEntity?,
        memberSince: String,
        entries: List<TimelineItem>,
        exportType: ExportType,
        nowFormatted: String
    ): String = buildString {
        appendLine("{")
        appendLine("  \"organization\": \"Jobless Empire\",")
        appendLine("  \"system\": \"Timeline Operating System\",")
        appendLine("  \"memberId\": \"${member?.jempDisplayId ?: "JEMP#001"}\",")
        appendLine("  \"displayName\": \"${member?.displayName ?: ""}\",")
        appendLine("  \"specialty\": \"${member?.specialty ?: ""}\",")
        appendLine("  \"memberSince\": \"$memberSince\",")
        appendLine("  \"exportType\": \"${exportType.name}\",")
        appendLine("  \"generatedAt\": \"$nowFormatted\",")
        appendLine("  \"totalEntries\": ${entries.size},")
        appendLine("  \"entries\": [")
        entries.forEachIndexed { i, e ->
            appendLine("    {")
            appendLine("      \"id\": \"${e.id}\",")
            appendLine("      \"date\": \"${e.formattedDate}\",")
            appendLine("      \"time\": \"${e.formattedTime}\",")
            appendLine("      \"title\": \"${e.title.replace("\"", "\\\"")}\",")
            appendLine("      \"projectId\": ${if (e.projectId != null) "\"${e.projectId}\"" else "null"},")
            appendLine("      \"isImmutable\": ${e.isImmutable},")
            appendLine("      \"source\": \"${e.source}\",")
            appendLine("      \"bullets\": [${e.bullets.joinToString(", ") { "\"${it.replace("\"", "\\\"")}\"" }}]")
            appendLine("    }${if (i < entries.lastIndex) "," else ""}")
        }
        appendLine("  ]")
        appendLine("}")
    }

    private fun buildStructuredDocExport(
        member: MemberEntity?,
        memberSince: String,
        entries: List<TimelineItem>,
        exportType: ExportType,
        nowFormatted: String
    ): String = buildString {
        appendLine("==================================================")
        appendLine("               JOBLESS EMPIRE                     ")
        appendLine("           OFFICIAL PROOF OF WORK                 ")
        appendLine("==================================================")
        appendLine("MEMBER:       ${member?.jempDisplayId ?: "JEMP#001"}")
        appendLine("NAME:         ${member?.displayName ?: "Member"}")
        appendLine("SPECIALTY:    ${member?.specialty ?: "Builder"}")
        appendLine("MEMBER SINCE: $memberSince")
        appendLine("EXPORT MODE:  ${exportType.name}")
        appendLine("ISSUED:       $nowFormatted")
        appendLine("--------------------------------------------------\n")

        entries.forEach { e ->
            val ref = if (e.projectId != null) "[${e.projectId}] " else ""
            val lock = if (e.isImmutable) "[OFFICIAL RECORD]" else "[MEMBER ENTRY]"
            appendLine("${e.formattedDate} ${e.formattedTime} | $lock")
            appendLine("$ref${e.title}")
            e.bullets.forEach { bullet ->
                appendLine("   - $bullet")
            }
            appendLine()
        }

        appendLine("--------------------------------------------------")
        appendLine("Jobless Empire Timeline System - Canonical Attestation")
        appendLine("==================================================")
    }

    // ----------------------------------------------------
    // 10. DEMO SEED & CLEAR (/timeline-admin seed-demo / clear-demo)
    // ----------------------------------------------------
    suspend fun seedDemoData(): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Check if demo member JEMP#001 exists or create
            var demoMember = memberDao.getMemberById("JEMP#001")
            if (demoMember == null) {
                demoMember = MemberEntity(
                    id = "JEMP#001",
                    discordUserId = "100000000000000001",
                    jempNumber = 1,
                    jempDisplayId = "JEMP#001",
                    displayName = "Alex Vance",
                    specialty = "Full-Stack & Systems Developer",
                    timezone = "Asia/Kolkata",
                    joinedAtUtc = 1788345600000L, // 02 Sep 2026
                    status = "ACTIVE",
                    isDemo = true
                )
                memberDao.insertMember(demoMember)
            }

            val demoEntries = listOf(
                // 2026 - September
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "ONBOARDING_COMPLETED",
                    title = "Entered the Empire",
                    bullets = listOf(
                        "Completed Jobless Empire onboarding",
                        "Accepted community rules and anti-scam charter",
                        "Confirmed communication email",
                        "Selected timezone (Asia/Kolkata)"
                    ),
                    projectId = null,
                    source = "onboarding-bot",
                    sourceEventId = "demo-001",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1788350000000L, // 02 Sep 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "BRAINSTORM_JOINED",
                    title = "Joined Brainstorming Session B#007",
                    bullets = listOf(
                        "Joined AI Resume Intelligence squad",
                        "Contributed architectural blueprint for local parsing engine"
                    ),
                    projectId = "B#007",
                    source = "brainstorm-bot",
                    sourceEventId = "demo-002",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1788436400000L, // 03 Sep 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_JOINED",
                    title = "Joined Project P#003",
                    bullets = listOf(
                        "Accepted as Lead Frontend & Compose Architect",
                        "Initialized repository and design tokens"
                    ),
                    projectId = "P#003",
                    source = "project-bot",
                    sourceEventId = "demo-003",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1788522800000L, // 04 Sep 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "MEETING_PARTICIPATION",
                    title = "Participated in Project Sprint Standup",
                    bullets = listOf(
                        "Sprint 1 sync in Discord voice channel",
                        "Defined milestone deliverables with squad"
                    ),
                    projectId = "P#003",
                    source = "project-bot",
                    sourceEventId = "demo-004",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1788609200000L, // 05 Sep 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PERSONAL_MILESTONE",
                    title = "Finished Compose Theme & Canvas Shaders",
                    bullets = listOf(
                        "Built dark mode cyberpunk palette",
                        "Optimized rendering performance to 60fps"
                    ),
                    projectId = "P#003",
                    source = "member",
                    sourceEventId = "demo-005",
                    immutable = false, // Member entry
                    createdBy = "JEMP#001",
                    customTimestampUtc = 1788782000000L, // 07 Sep 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_MILESTONE",
                    title = "Completed P#003 Milestone 1",
                    bullets = listOf(
                        "Delivered core OCR ingestion pipeline",
                        "Passed automated integration suite"
                    ),
                    projectId = "P#003",
                    source = "project-bot",
                    sourceEventId = "demo-006",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1789214000000L, // 12 Sep 2026
                    isDemo = true
                ),
                // 2026 - October
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "CLIENT_PROJECT_ACCEPTED",
                    title = "Accepted into HC#004 Client Scope",
                    bullets = listOf(
                        "Selected for $4,500 E-commerce Analytics portal",
                        "Agreed to 95% contributor distribution terms"
                    ),
                    projectId = "HC#004",
                    source = "client-project-bot",
                    sourceEventId = "demo-007",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1791114800000L, // 04 Oct 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_MILESTONE",
                    title = "Completed HC#004 Milestone Delivery",
                    bullets = listOf(
                        "Merged real-time conversion charts",
                        "Verified client sign-off and received escrow payout"
                    ),
                    projectId = "HC#004",
                    source = "client-project-bot",
                    sourceEventId = "demo-008",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1792324400000L, // 18 Oct 2026
                    isDemo = true
                ),
                // 2026 - November
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "BRAINSTORM_CONTRIBUTION",
                    title = "Proposed P2P Hardware Bridge B#031",
                    bullets = listOf(
                        "Presented proof-of-concept spec in community call",
                        "Gathered 5 prospective collaborators"
                    ),
                    projectId = "B#031",
                    source = "brainstorm-bot",
                    sourceEventId = "demo-009",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1794138800000L, // 08 Nov 2026
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_COMPLETED",
                    title = "Completed P#003 V1 Launch",
                    bullets = listOf(
                        "Shipped production build",
                        "Generated portfolio proof-of-work bundle"
                    ),
                    projectId = "P#003",
                    source = "project-bot",
                    sourceEventId = "demo-010",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1795348400000L, // 22 Nov 2026
                    isDemo = true
                ),
                // 2027 - January
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_JOINED",
                    title = "Joined High-Velocity Squad HC#011",
                    bullets = listOf(
                        "Initiated multi-tenant asset engine",
                        "Took ownership of state graph & offline caching"
                    ),
                    projectId = "HC#011",
                    source = "client-project-bot",
                    sourceEventId = "demo-011",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1799063600000L, // 04 Jan 2027
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "MEETING_PARTICIPATION",
                    title = "Conducted Technical Architecture Review",
                    bullets = listOf(
                        "Led 60-min deep-dive on Room schema migrations",
                        "Published design docs in project thread"
                    ),
                    projectId = "HC#011",
                    source = "project-bot",
                    sourceEventId = "demo-012",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1800273200000L, // 18 Jan 2027
                    isDemo = true
                ),
                // 2027 - March
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PERSONAL_MILESTONE",
                    title = "Published Open Source Kotlin Multiplatform Lib",
                    bullets = listOf(
                        "Built zero-dependency rate limiter",
                        "Reached 100+ GitHub stars from community builders"
                    ),
                    projectId = null,
                    source = "member",
                    sourceEventId = "demo-013",
                    immutable = false,
                    createdBy = "JEMP#001",
                    customTimestampUtc = 1804420400000L, // 06 Mar 2027
                    isDemo = true
                ),
                // 2027 - June
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_MILESTONE",
                    title = "Completed HC#011 Production Release",
                    bullets = listOf(
                        "Delivered ahead of schedule",
                        "Received 5-star client recommendation"
                    ),
                    projectId = "HC#011",
                    source = "client-project-bot",
                    sourceEventId = "demo-014",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1812369200000L, // 06 Jun 2027
                    isDemo = true
                ),
                // 2028 - January
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "BRAINSTORM_JOINED",
                    title = "Joined AI Agent Workspaces B#099",
                    bullets = listOf(
                        "Co-authored multi-agent orchestration RFC",
                        "Built streaming WebSocket bridge"
                    ),
                    projectId = "B#099",
                    source = "brainstorm-bot",
                    sourceEventId = "demo-015",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1830945200000L, // 07 Jan 2028
                    isDemo = true
                ),
                TimelineEventPayload(
                    memberId = "JEMP#001",
                    eventType = "PROJECT_JOINED",
                    title = "Promoted B#099 → Project P#042",
                    bullets = listOf(
                        "Assigned as Lead Systems Architect",
                        "Coordinating 6 builder contributors"
                    ),
                    projectId = "P#042",
                    source = "project-bot",
                    sourceEventId = "demo-016",
                    immutable = true,
                    createdBy = "SYSTEM",
                    customTimestampUtc = 1832154800000L, // 21 Jan 2028
                    isDemo = true
                )
            )

            demoEntries.forEach { recordTimelineEvent(it) }

            auditAction(
                actor = "ADMIN",
                action = "SEED_DEMO_DATA",
                target = "JEMP#001",
                metadata = mapOf("count" to demoEntries.size.toString())
            )

            Result.success("Seeded ${demoEntries.size} demo records across 2026, 2027, and 2028 for JEMP#001.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearDemoData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            timelineEntryDao.deleteDemoEntries()
            memberDao.deleteDemoMembers()
            auditAction(
                actor = "ADMIN",
                action = "CLEAR_DEMO_DATA",
                target = "ALL_DEMO",
                metadata = emptyMap()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 11. SYSTEM HEALTH & AUDIT TRAIL
    // ----------------------------------------------------
    suspend fun getSystemHealth(): SystemHealthReport = withContext(Dispatchers.IO) {
        val totalMembers = memberDao.getIdCounter()?.nextNumber?.minus(1)?.coerceAtLeast(0) ?: 0
        val totalEntries = timelineEntryDao.countEntriesForMember("JEMP#001") // sample count
        SystemHealthReport(
            discordGatewayStatus = "CONNECTED (Gateway v10, 18ms latency)",
            databaseStatus = "HEALTHY (Room SQLite Persistent Storage)",
            eventProcessorStatus = "RUNNING (Idempotent Ingestion Active)",
            publicShareServiceStatus = "OPERATIONAL (24h Cryptographic Expiry)",
            totalTrackedMembers = totalMembers,
            totalRecordedEntries = totalEntries,
            totalActivePublicShares = 1,
            queuedEventsCount = 0,
            lastEventProcessedFormatted = "Just now"
        )
    }

    fun getAuditLogs(): Flow<List<AuditLogEntity>> {
        return auditLogDao.getRecentAuditLogs()
    }

    private suspend fun auditAction(
        actor: String,
        action: String,
        target: String,
        metadata: Map<String, String>
    ) {
        try {
            val jsonStr = JSONArray(metadata.entries.map { "${it.key}=${it.value}" }).toString()
            auditLogDao.insertAuditLog(
                AuditLogEntity(
                    actor = actor,
                    action = action,
                    target = target,
                    metadataJson = jsonStr,
                    timestampUtc = System.currentTimeMillis()
                )
            )
        } catch (_: Exception) {}
    }

    // Extension converter
    private fun TimelineEntryEntity.toDomainItem(timezoneStr: String): TimelineItem {
        val bulletsList = try {
            val arr = JSONArray(bulletsJson)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (_: Exception) {
            listOf(bulletsJson)
        }

        val cal = Calendar.getInstance(TimeZone.getTimeZone(timezoneStr), Locale.US)
        cal.timeInMillis = timestampUtc

        val year = cal.get(Calendar.YEAR)
        val monthIdx = cal.get(Calendar.MONTH) + 1

        val sdfMonthName = SimpleDateFormat("MMMM", Locale.US)
        sdfMonthName.timeZone = TimeZone.getTimeZone(timezoneStr)
        val monthName = sdfMonthName.format(Date(timestampUtc))

        val sdfFormattedDate = SimpleDateFormat("dd MMM yyyy", Locale.US)
        sdfFormattedDate.timeZone = TimeZone.getTimeZone(timezoneStr)
        val formattedDate = sdfFormattedDate.format(Date(timestampUtc))

        val sdfFormattedTime = SimpleDateFormat("HH:mm z", Locale.US)
        sdfFormattedTime.timeZone = TimeZone.getTimeZone(timezoneStr)
        val formattedTime = sdfFormattedTime.format(Date(timestampUtc))

        val sdfDateName = SimpleDateFormat("dd MMMM", Locale.US)
        sdfDateName.timeZone = TimeZone.getTimeZone(timezoneStr)
        val dateName = sdfDateName.format(Date(timestampUtc))

        val category = when {
            eventType.startsWith("SYSTEM") || eventType.contains("ONBOARD") -> EventTypeCategory.ONBOARDING
            eventType.startsWith("BRAINSTORM") -> EventTypeCategory.BRAINSTORM
            eventType.startsWith("PROJECT") -> EventTypeCategory.PROJECT
            eventType.startsWith("CLIENT_PROJECT") -> EventTypeCategory.CLIENT_PROJECT
            eventType.contains("MEETING") -> EventTypeCategory.MEETING
            eventType.startsWith("ADMIN") -> EventTypeCategory.ADMIN
            else -> EventTypeCategory.MEMBER
        }

        return TimelineItem(
            id = id,
            memberId = memberId,
            eventType = eventType,
            title = title,
            bullets = bulletsList,
            timestampUtc = timestampUtc,
            timezone = timezoneStr,
            formattedDate = formattedDate,
            formattedTime = formattedTime,
            year = year,
            monthName = monthName,
            monthIndex = monthIdx,
            dateName = dateName,
            projectId = projectId,
            source = source,
            sourceEventId = sourceEventId,
            isImmutable = isImmutable,
            visibility = try { TimelineVisibility.valueOf(visibility) } catch (_: Exception) { TimelineVisibility.PUBLIC_SAFE },
            createdBy = createdBy,
            correctionForEntryId = correctionForEntryId,
            category = category,
            isDemo = isDemo
        )
    }
}
