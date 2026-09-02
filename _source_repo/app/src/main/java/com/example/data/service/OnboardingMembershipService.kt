package com.example.data.service

import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OnboardingMembershipService(
    private val memberDao: MemberDao,
    private val timelineEntryDao: TimelineEntryDao,
    private val auditLogDao: AuditLogDao,
    private val applicationDao: ApplicationDao,
    private val invitationDao: InvitationDao,
    private val onboardingSessionDao: OnboardingSessionDao,
    private val onboardingSettingsDao: OnboardingSettingsDao,
    private val timelineService: TimelineService
) {
    private val secureRandom = SecureRandom()

    // ----------------------------------------------------
    // 1. EMAIL NORMALIZATION & VALIDATION
    // ----------------------------------------------------
    fun normalizeEmail(email: String): String {
        return email.trim().lowercase(Locale.US)
    }

    private fun isValidEmailSyntax(email: String): Boolean {
        val trimmed = email.trim()
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return trimmed.matches(regex)
    }

    fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return "••••••••"
        val name = parts[0]
        val domain = parts[1]
        val maskedName = if (name.length <= 2) {
            name.first() + "•••"
        } else {
            name.take(2) + "•••••"
        }
        return "$maskedName@$domain"
    }

    // ----------------------------------------------------
    // 2. APPLICATION LIFECYCLE (WEBSITE & ADMIN)
    // ----------------------------------------------------
    suspend fun submitApplication(
        fullName: String,
        email: String,
        resumeFileName: String,
        resumeFileSize: String,
        isCurrentlyUnemployed: Boolean,
        consentGiven: Boolean,
        isDemo: Boolean = false
    ): Result<ApplicationEntity> = withContext(Dispatchers.IO) {
        try {
            if (fullName.trim().length < 2) {
                return@withContext Result.failure(IllegalArgumentException("Please enter your full name."))
            }

            if (!isValidEmailSyntax(email)) {
                return@withContext Result.failure(IllegalArgumentException("Please provide a valid active email address."))
            }

            if (!consentGiven) {
                return@withContext Result.failure(IllegalArgumentException("Consent confirmation is required."))
            }

            val normalized = normalizeEmail(email)
            val existing = applicationDao.getApplicationByNormalizedEmail(normalized)
            if (existing != null) {
                return@withContext Result.success(existing)
            }

            val appId = "APP#${String.format(Locale.US, "%04d", (1..9999).random())}"
            val entity = ApplicationEntity(
                applicationId = appId,
                fullName = fullName.trim(),
                email = email.trim(),
                normalizedEmail = normalized,
                resumeFileName = resumeFileName,
                resumeFileSize = resumeFileSize,
                isCurrentlyUnemployed = isCurrentlyUnemployed,
                consentGiven = consentGiven,
                status = "PENDING",
                submittedAtUtc = System.currentTimeMillis(),
                isDemo = isDemo
            )

            applicationDao.insertApplication(entity)

            auditAction(
                actor = "WEBSITE_PORTAL",
                action = "APPLICATION_SUBMITTED",
                target = appId,
                metadata = mapOf("applicant" to fullName.trim(), "emailMasked" to maskEmail(normalized))
            )

            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveApplication(
        applicationIdOrEmail: String,
        adminId: String = "ADMIN_CONSOLE",
        adminNotes: String? = null
    ): Result<ApplicationEntity> = withContext(Dispatchers.IO) {
        try {
            val app = if (applicationIdOrEmail.contains("@")) {
                applicationDao.getApplicationByNormalizedEmail(normalizeEmail(applicationIdOrEmail))
            } else {
                applicationDao.getApplicationById(applicationIdOrEmail.trim())
            } ?: return@withContext Result.failure(IllegalArgumentException("Application $applicationIdOrEmail not found."))

            val inviteCode = "JEMP-INV-${UUID.randomUUID().toString().take(6).uppercase(Locale.US)}"
            val now = System.currentTimeMillis()

            val updatedApp = app.copy(
                status = "APPROVED",
                reviewedAtUtc = now,
                reviewedByAdmin = adminId,
                inviteCode = inviteCode,
                adminNotes = adminNotes ?: app.adminNotes
            )

            val inviteEntity = InvitationEntity(
                inviteCode = inviteCode,
                applicationId = app.applicationId,
                createdBy = adminId,
                createdAtUtc = now,
                expiresAtUtc = now + TimeUnit.HOURS.toMillis(72),
                maxUses = 1,
                useCount = 0,
                isUsed = false,
                isDemo = app.isDemo
            )

            applicationDao.updateApplication(updatedApp)
            invitationDao.insertInvitation(inviteEntity)

            auditAction(
                actor = adminId,
                action = "APPLICATION_APPROVED",
                target = app.applicationId,
                metadata = mapOf(
                    "emailMasked" to maskEmail(app.normalizedEmail),
                    "inviteCode" to inviteCode
                )
            )

            Result.success(updatedApp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectApplication(
        applicationIdOrEmail: String,
        adminId: String = "ADMIN_CONSOLE",
        rejectionReason: String? = null
    ): Result<ApplicationEntity> = withContext(Dispatchers.IO) {
        try {
            val app = if (applicationIdOrEmail.contains("@")) {
                applicationDao.getApplicationByNormalizedEmail(normalizeEmail(applicationIdOrEmail))
            } else {
                applicationDao.getApplicationById(applicationIdOrEmail.trim())
            } ?: return@withContext Result.failure(IllegalArgumentException("Application not found."))

            val updated = app.copy(
                status = "REJECTED",
                reviewedAtUtc = System.currentTimeMillis(),
                reviewedByAdmin = adminId,
                rejectionReason = rejectionReason ?: "Application does not meet current criteria"
            )

            applicationDao.updateApplication(updated)

            auditAction(
                actor = adminId,
                action = "APPLICATION_REJECTED",
                target = app.applicationId,
                metadata = mapOf("reason" to (rejectionReason ?: "Standard"))
            )

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun waitlistApplication(
        applicationIdOrEmail: String,
        adminId: String = "ADMIN_CONSOLE"
    ): Result<ApplicationEntity> = withContext(Dispatchers.IO) {
        try {
            val app = if (applicationIdOrEmail.contains("@")) {
                applicationDao.getApplicationByNormalizedEmail(normalizeEmail(applicationIdOrEmail))
            } else {
                applicationDao.getApplicationById(applicationIdOrEmail.trim())
            } ?: return@withContext Result.failure(IllegalArgumentException("Application not found."))

            val updated = app.copy(
                status = "WAITLISTED",
                reviewedAtUtc = System.currentTimeMillis(),
                reviewedByAdmin = adminId
            )

            applicationDao.updateApplication(updated)

            auditAction(
                actor = adminId,
                action = "APPLICATION_WAITLISTED",
                target = app.applicationId,
                metadata = emptyMap()
            )

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun whitelistEmailDirectly(
        email: String,
        applicantName: String,
        adminId: String = "ADMIN_CONSOLE"
    ): Result<ApplicationEntity> = withContext(Dispatchers.IO) {
        try {
            val normalized = normalizeEmail(email)
            val existing = applicationDao.getApplicationByNormalizedEmail(normalized)
            if (existing != null) {
                return@withContext approveApplication(existing.applicationId, adminId)
            }

            val appId = "APP#${String.format(Locale.US, "%04d", (1..9999).random())}"
            val inviteCode = "JEMP-INV-${UUID.randomUUID().toString().take(6).uppercase(Locale.US)}"
            val now = System.currentTimeMillis()

            val app = ApplicationEntity(
                applicationId = appId,
                fullName = applicantName.trim(),
                email = email.trim(),
                normalizedEmail = normalized,
                resumeFileName = "Manual_Whitelist_Profile.pdf",
                resumeFileSize = "1.0 MB",
                isCurrentlyUnemployed = true,
                consentGiven = true,
                status = "APPROVED",
                submittedAtUtc = now,
                reviewedAtUtc = now,
                reviewedByAdmin = adminId,
                inviteCode = inviteCode
            )

            applicationDao.insertApplication(app)
            invitationDao.insertInvitation(
                InvitationEntity(
                    inviteCode = inviteCode,
                    applicationId = appId,
                    createdBy = adminId,
                    createdAtUtc = now,
                    expiresAtUtc = now + TimeUnit.HOURS.toMillis(72)
                )
            )

            auditAction(
                actor = adminId,
                action = "EMAIL_WHITELISTED_MANUALLY",
                target = appId,
                metadata = mapOf("emailMasked" to maskEmail(normalized))
            )

            Result.success(app)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revokeApplication(
        applicationIdOrEmail: String,
        adminId: String = "ADMIN_CONSOLE"
    ): Result<ApplicationEntity> = withContext(Dispatchers.IO) {
        try {
            val app = if (applicationIdOrEmail.contains("@")) {
                applicationDao.getApplicationByNormalizedEmail(normalizeEmail(applicationIdOrEmail))
            } else {
                applicationDao.getApplicationById(applicationIdOrEmail.trim())
            } ?: return@withContext Result.failure(IllegalArgumentException("Application not found."))

            val updated = app.copy(
                status = "REVOKED",
                reviewedAtUtc = System.currentTimeMillis(),
                reviewedByAdmin = adminId
            )
            applicationDao.updateApplication(updated)

            auditAction(
                actor = adminId,
                action = "APPLICATION_APPROVAL_REVOKED",
                target = app.applicationId,
                metadata = emptyMap()
            )

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 3. DISCORD JOIN EVENT & SESSION MANAGEMENT
    // ----------------------------------------------------
    suspend fun handleDiscordJoin(
        discordUserId: String,
        discordUsername: String
    ): OnboardingProgressState = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // 1. Check if user is already an active/existing member in canonical `members` table
        val existingMember = memberDao.getMemberByDiscordUserId(discordUserId)
        if (existingMember != null) {
            val membershipState = when (existingMember.status) {
                "ACTIVE" -> MembershipState.ACTIVE
                "SUSPENDED" -> MembershipState.SUSPENDED
                "TERMINATED" -> MembershipState.TERMINATED
                else -> MembershipState.ACTIVE
            }

            val roles = when (membershipState) {
                MembershipState.ACTIVE -> listOf("@Member")
                MembershipState.SUSPENDED -> listOf("@Suspended")
                MembershipState.TERMINATED -> listOf("@Terminated")
                else -> listOf("@Member")
            }

            // Sync session
            val session = OnboardingSessionEntity(
                discordUserId = discordUserId,
                discordUsername = discordUsername,
                assignedJempId = existingMember.id,
                confirmedName = existingMember.displayName,
                selectedTimezone = existingMember.timezone,
                rulesAccepted = true,
                safetyAcknowledged = true,
                currentStep = 5,
                membershipState = membershipState.name,
                assignedRolesJson = JSONArray(roles).toString(),
                completedAtUtc = existingMember.joinedAtUtc,
                updatedAtUtc = now
            )
            onboardingSessionDao.insertSession(session)

            auditAction(
                actor = "DISCORD_GATEWAY",
                action = "MEMBER_REJOINED",
                target = existingMember.id,
                metadata = mapOf("discordUserId" to discordUserId, "status" to existingMember.status)
            )

            return@withContext session.toProgressState(existingMember.displayName)
        }

        // 2. Check if existing onboarding session exists to resume progress
        val existingSession = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
        if (existingSession != null) {
            val app = if (existingSession.applicationId != null) {
                applicationDao.getApplicationById(existingSession.applicationId)
            } else null

            val applicantName = existingSession.confirmedName
                ?: app?.fullName
                ?: discordUsername

            return@withContext existingSession.toProgressState(applicantName)
        }

        // 3. Brand new joiner: Assign @Unverified role and initialize step 0
        val newSession = OnboardingSessionEntity(
            discordUserId = discordUserId,
            discordUsername = discordUsername,
            currentStep = 0,
            membershipState = "JOINED_UNVERIFIED",
            assignedRolesJson = JSONArray(listOf("@Unverified")).toString(),
            startedAtUtc = now,
            updatedAtUtc = now
        )
        onboardingSessionDao.insertSession(newSession)

        auditAction(
            actor = "DISCORD_GATEWAY",
            action = "USER_JOINED_UNVERIFIED",
            target = discordUserId,
            metadata = mapOf("username" to discordUsername)
        )

        newSession.toProgressState("Applicant")
    }

    // ----------------------------------------------------
    // 4. EMAIL VERIFICATION & GATEKEEPER
    // ----------------------------------------------------
    suspend fun verifyApplicationEmail(
        discordUserId: String,
        enteredEmail: String
    ): EmailVerificationResult = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
            ?: OnboardingSessionEntity(discordUserId = discordUserId)

        // 1. Rate Limiting Check
        if (session.rateLimitedUntilUtc > now) {
            val retrySec = ((session.rateLimitedUntilUtc - now) / 1000).toInt().coerceAtLeast(1)
            return@withContext EmailVerificationResult(
                isSuccess = false,
                isRateLimited = true,
                retryAfterSeconds = retrySec,
                errorMessage = "Too many verification attempts. Please wait before trying again."
            )
        }

        val attempts = session.verificationAttempts + 1
        if (attempts >= 5) {
            // Apply 15 minute exponential rate limit
            val lockUntil = now + TimeUnit.MINUTES.toMillis(15)
            onboardingSessionDao.updateSession(
                session.copy(
                    verificationAttempts = attempts,
                    lastAttemptUtc = now,
                    rateLimitedUntilUtc = lockUntil
                )
            )

            auditAction(
                actor = discordUserId,
                action = "VERIFICATION_RATE_LIMITED",
                target = discordUserId,
                metadata = mapOf("attempts" to attempts.toString())
            )

            return@withContext EmailVerificationResult(
                isSuccess = false,
                isRateLimited = true,
                retryAfterSeconds = 900,
                errorMessage = "Too many verification attempts. Please wait 15 minutes before trying again."
            )
        }

        val normalized = normalizeEmail(enteredEmail)
        if (!isValidEmailSyntax(enteredEmail)) {
            onboardingSessionDao.updateSession(
                session.copy(verificationAttempts = attempts, lastAttemptUtc = now)
            )
            return@withContext EmailVerificationResult(
                isSuccess = false,
                errorMessage = "Please enter a valid email format."
            )
        }

        // 2. Query Approved Applications for Normalized Match
        val approvedApp = applicationDao.getApplicationByNormalizedEmail(normalized)

        if (approvedApp == null || approvedApp.status != "APPROVED") {
            onboardingSessionDao.updateSession(
                session.copy(verificationAttempts = attempts, lastAttemptUtc = now)
            )

            auditAction(
                actor = discordUserId,
                action = "VERIFICATION_FAILED_EMAIL_NOT_WHITELISTED",
                target = discordUserId,
                metadata = mapOf("emailMasked" to maskEmail(normalized))
            )

            // Non-enumerating error message
            return@withContext EmailVerificationResult(
                isSuccess = false,
                errorMessage = "We couldn't find an approved Jobless Empire application using that email.\nAre you sure you entered the same email you used when applying?\n\nPlease check for:\n• spelling mistakes\n• extra spaces\n• a different email address\n\nIf you applied using the correct email, reply to the invitation/application email explaining the issue."
            )
        }

        // 3. Anti-Account Hijacking Check
        if (approvedApp.linkedDiscordUserId != null && approvedApp.linkedDiscordUserId != discordUserId) {
            auditAction(
                actor = discordUserId,
                action = "VERIFICATION_HIJACK_ALERT",
                target = approvedApp.applicationId,
                metadata = mapOf(
                    "existingDiscordId" to approvedApp.linkedDiscordUserId,
                    "attemptingDiscordId" to discordUserId
                )
            )

            return@withContext EmailVerificationResult(
                isSuccess = false,
                alreadyLinkedToDifferentUser = true,
                errorMessage = "This application is already associated with another Discord account. Please contact Jobless Empire administration if you believe this is an error."
            )
        }

        // 4. Verification Success -> Link Application to Discord ID and transition to Step 1
        val updatedApp = approvedApp.copy(linkedDiscordUserId = discordUserId)
        applicationDao.updateApplication(updatedApp)

        val updatedSession = session.copy(
            applicationId = approvedApp.applicationId,
            verifiedEmail = normalized,
            confirmedName = approvedApp.fullName,
            currentStep = 1,
            membershipState = "EMAIL_VERIFIED",
            verificationAttempts = 0,
            rateLimitedUntilUtc = 0L,
            updatedAtUtc = now
        )
        onboardingSessionDao.insertSession(updatedSession)

        auditAction(
            actor = discordUserId,
            action = "EMAIL_VERIFICATION_SUCCESS",
            target = approvedApp.applicationId,
            metadata = mapOf("emailMasked" to maskEmail(normalized), "applicant" to approvedApp.fullName)
        )

        EmailVerificationResult(
            isSuccess = true,
            applicantName = approvedApp.fullName,
            applicationId = approvedApp.applicationId
        )
    }

    // ----------------------------------------------------
    // 5. INTERACTIVE 5-STEP ONBOARDING
    // ----------------------------------------------------
    // STEP 1 — CONFIRM NAME
    suspend fun completeStep1ConfirmName(
        discordUserId: String,
        useSubmittedName: Boolean,
        correctionNote: String? = null
    ): Result<OnboardingProgressState> = withContext(Dispatchers.IO) {
        try {
            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
                ?: return@withContext Result.failure(IllegalStateException("Session not found."))

            val updatedSession = session.copy(
                nameCorrectionRequested = !useSubmittedName,
                nameCorrectionNote = if (!useSubmittedName) correctionNote else null,
                currentStep = 2,
                membershipState = "ONBOARDING",
                updatedAtUtc = System.currentTimeMillis()
            )
            onboardingSessionDao.updateSession(updatedSession)

            auditAction(
                actor = discordUserId,
                action = "ONBOARDING_STEP1_NAME_CONFIRMED",
                target = session.applicationId ?: discordUserId,
                metadata = mapOf("correctionRequested" to (!useSubmittedName).toString())
            )

            Result.success(updatedSession.toProgressState(session.confirmedName ?: "Applicant"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // STEP 2 — SELECT TIMEZONE
    suspend fun completeStep2SelectTimezone(
        discordUserId: String,
        ianaTimezone: String
    ): Result<OnboardingProgressState> = withContext(Dispatchers.IO) {
        try {
            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
                ?: return@withContext Result.failure(IllegalStateException("Session not found."))

            val validTz = try {
                TimeZone.getTimeZone(ianaTimezone).id
            } catch (_: Exception) {
                "Asia/Kolkata"
            }

            val updatedSession = session.copy(
                selectedTimezone = validTz,
                currentStep = 3,
                updatedAtUtc = System.currentTimeMillis()
            )
            onboardingSessionDao.updateSession(updatedSession)

            auditAction(
                actor = discordUserId,
                action = "ONBOARDING_STEP2_TIMEZONE_SELECTED",
                target = session.applicationId ?: discordUserId,
                metadata = mapOf("timezone" to validTz)
            )

            Result.success(updatedSession.toProgressState(session.confirmedName ?: "Applicant"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // STEP 3 — COMMUNITY RULES
    suspend fun completeStep3AcceptRules(
        discordUserId: String,
        agreeAccepted: Boolean
    ): Result<OnboardingProgressState> = withContext(Dispatchers.IO) {
        try {
            if (!agreeAccepted) {
                return@withContext Result.failure(IllegalArgumentException("You must read and agree to the Jobless Empire community rules."))
            }

            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
                ?: return@withContext Result.failure(IllegalStateException("Session not found."))

            val now = System.currentTimeMillis()
            val updatedSession = session.copy(
                rulesAccepted = true,
                rulesAcceptedAtUtc = now,
                currentStep = 4,
                updatedAtUtc = now
            )
            onboardingSessionDao.updateSession(updatedSession)

            auditAction(
                actor = discordUserId,
                action = "ONBOARDING_STEP3_RULES_ACCEPTED",
                target = session.applicationId ?: discordUserId,
                metadata = emptyMap()
            )

            Result.success(updatedSession.toProgressState(session.confirmedName ?: "Applicant"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // STEP 4 — SAFETY ACKNOWLEDGMENT
    suspend fun completeStep4AcknowledgeSafety(
        discordUserId: String,
        understandAccepted: Boolean
    ): Result<OnboardingProgressState> = withContext(Dispatchers.IO) {
        try {
            if (!understandAccepted) {
                return@withContext Result.failure(IllegalArgumentException("You must acknowledge the project safety and communication policy."))
            }

            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
                ?: return@withContext Result.failure(IllegalStateException("Session not found."))

            val now = System.currentTimeMillis()
            val updatedSession = session.copy(
                safetyAcknowledged = true,
                safetyAcknowledgedAtUtc = now,
                currentStep = 5,
                updatedAtUtc = now
            )
            onboardingSessionDao.updateSession(updatedSession)

            auditAction(
                actor = discordUserId,
                action = "ONBOARDING_STEP4_SAFETY_ACKNOWLEDGED",
                target = session.applicationId ?: discordUserId,
                metadata = emptyMap()
            )

            Result.success(updatedSession.toProgressState(session.confirmedName ?: "Applicant"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // STEP 5 — MEMBER ID ATOMIC ASSIGNMENT & MEMBERSHIP ACTIVATION
    suspend fun completeOnboardingAndActivate(
        discordUserId: String
    ): Result<MemberCardSummary> = withContext(Dispatchers.IO) {
        try {
            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
                ?: return@withContext Result.failure(IllegalStateException("Session not found."))

            val displayName = session.confirmedName ?: "Builder"
            val timezone = session.selectedTimezone

            // Call canonical TimelineService atomic member ID allocation
            val memberRes = timelineService.assignMemberId(
                discordUserId = discordUserId,
                displayName = displayName,
                specialty = "Empire Builder / Contributor",
                timezone = timezone,
                isDemo = session.isDemo
            )

            val member = memberRes.getOrThrow()
            val now = System.currentTimeMillis()

            // Update session state to ACTIVE & assign @Member role
            val updatedSession = session.copy(
                assignedJempId = member.id,
                membershipState = "ACTIVE",
                currentStep = 5,
                assignedRolesJson = JSONArray(listOf("@Member")).toString(),
                completedAtUtc = now,
                updatedAtUtc = now
            )
            onboardingSessionDao.updateSession(updatedSession)

            // Mark invitation as used
            if (session.applicationId != null) {
                val invite = invitationDao.getInvitationByApplicationId(session.applicationId)
                if (invite != null) {
                    invitationDao.updateInvitation(
                        invite.copy(
                            isUsed = true,
                            useCount = invite.useCount + 1,
                            usedByDiscordUserId = discordUserId,
                            usedAtUtc = now
                        )
                    )
                }
            }

            auditAction(
                actor = "ONBOARDING_ENGINE",
                action = "MEMBERSHIP_ACTIVATED",
                target = member.id,
                metadata = mapOf(
                    "discordUserId" to discordUserId,
                    "jempId" to member.id,
                    "name" to displayName
                )
            )

            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone(timezone)

            Result.success(
                MemberCardSummary(
                    jempId = member.id,
                    displayName = member.displayName,
                    specialty = member.specialty,
                    status = "ACTIVE",
                    joinedDateFormatted = sdf.format(Date(member.joinedAtUtc)),
                    timezone = member.timezone,
                    applicationId = session.applicationId ?: "APP#0000",
                    onboardingState = "COMPLETE",
                    totalTimelineRecords = 1,
                    assignedRoles = listOf("@Member")
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 6. MEMBER PROFILE & TIMEZONE UPDATE
    // ----------------------------------------------------
    suspend fun getMemberCard(discordUserIdOrJempId: String): MemberCardSummary? = withContext(Dispatchers.IO) {
        val member = if (discordUserIdOrJempId.startsWith("JEMP#")) {
            memberDao.getMemberById(discordUserIdOrJempId)
        } else {
            memberDao.getMemberByDiscordUserId(discordUserIdOrJempId)
        } ?: return@withContext null

        val session = onboardingSessionDao.getSessionByDiscordUserId(member.discordUserId)
        val timelineCount = timelineEntryDao.countEntriesForMember(member.id)

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone(member.timezone)

        MemberCardSummary(
            jempId = member.id,
            displayName = member.displayName,
            specialty = member.specialty,
            status = member.status,
            joinedDateFormatted = sdf.format(Date(member.joinedAtUtc)),
            timezone = member.timezone,
            applicationId = session?.applicationId ?: "APP#0042",
            onboardingState = if (session?.completedAtUtc != null) "Complete" else "In Progress",
            totalTimelineRecords = timelineCount,
            assignedRoles = listOf(if (member.status == "ACTIVE") "@Member" else "@${member.status}")
        )
    }

    suspend fun updateMemberTimezone(
        discordUserId: String,
        newTimezone: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val member = memberDao.getMemberByDiscordUserId(discordUserId)
                ?: return@withContext Result.failure(IllegalArgumentException("Member not found."))

            val validTz = try {
                TimeZone.getTimeZone(newTimezone).id
            } catch (_: Exception) {
                "Asia/Kolkata"
            }

            memberDao.updateMember(member.copy(timezone = validTz, updatedAtUtc = System.currentTimeMillis()))

            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)
            if (session != null) {
                onboardingSessionDao.updateSession(session.copy(selectedTimezone = validTz))
            }

            auditAction(
                actor = member.id,
                action = "TIMEZONE_UPDATED",
                target = member.id,
                metadata = mapOf("newTimezone" to validTz)
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 7. ADMIN CONTROLS (SUSPEND, RESTORE, TERMINATE, RECONCILE)
    // ----------------------------------------------------
    suspend fun suspendMember(
        discordUserIdOrJempId: String,
        adminId: String = "ADMIN_CONSOLE",
        reason: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val member = if (discordUserIdOrJempId.startsWith("JEMP#")) {
                memberDao.getMemberById(discordUserIdOrJempId)
            } else {
                memberDao.getMemberByDiscordUserId(discordUserIdOrJempId)
            } ?: return@withContext Result.failure(IllegalArgumentException("Member not found."))

            memberDao.updateMember(member.copy(status = "SUSPENDED", updatedAtUtc = System.currentTimeMillis()))

            val session = onboardingSessionDao.getSessionByDiscordUserId(member.discordUserId)
            if (session != null) {
                onboardingSessionDao.updateSession(
                    session.copy(
                        membershipState = "SUSPENDED",
                        assignedRolesJson = JSONArray(listOf("@Suspended")).toString(),
                        suspensionReason = reason ?: "Administrative review"
                    )
                )
            }

            auditAction(
                actor = adminId,
                action = "MEMBER_SUSPENDED",
                target = member.id,
                metadata = mapOf("reason" to (reason ?: "Policy enforcement"))
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreMember(
        discordUserIdOrJempId: String,
        adminId: String = "ADMIN_CONSOLE"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val member = if (discordUserIdOrJempId.startsWith("JEMP#")) {
                memberDao.getMemberById(discordUserIdOrJempId)
            } else {
                memberDao.getMemberByDiscordUserId(discordUserIdOrJempId)
            } ?: return@withContext Result.failure(IllegalArgumentException("Member not found."))

            memberDao.updateMember(member.copy(status = "ACTIVE", updatedAtUtc = System.currentTimeMillis()))

            val session = onboardingSessionDao.getSessionByDiscordUserId(member.discordUserId)
            if (session != null) {
                onboardingSessionDao.updateSession(
                    session.copy(
                        membershipState = "ACTIVE",
                        assignedRolesJson = JSONArray(listOf("@Member")).toString(),
                        suspensionReason = null
                    )
                )
            }

            auditAction(
                actor = adminId,
                action = "MEMBER_RESTORED",
                target = member.id,
                metadata = emptyMap()
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun terminateMember(
        discordUserIdOrJempId: String,
        adminId: String = "ADMIN_CONSOLE",
        reason: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val member = if (discordUserIdOrJempId.startsWith("JEMP#")) {
                memberDao.getMemberById(discordUserIdOrJempId)
            } else {
                memberDao.getMemberByDiscordUserId(discordUserIdOrJempId)
            } ?: return@withContext Result.failure(IllegalArgumentException("Member not found."))

            memberDao.updateMember(member.copy(status = "TERMINATED", updatedAtUtc = System.currentTimeMillis()))

            val session = onboardingSessionDao.getSessionByDiscordUserId(member.discordUserId)
            if (session != null) {
                onboardingSessionDao.updateSession(
                    session.copy(
                        membershipState = "TERMINATED",
                        assignedRolesJson = JSONArray(listOf("@Terminated")).toString(),
                        terminationReason = reason ?: "Violation of anti-scam charter"
                    )
                )
            }

            auditAction(
                actor = adminId,
                action = "MEMBER_TERMINATED",
                target = member.id,
                metadata = mapOf("reason" to (reason ?: "Irrevocable violation"))
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reconcileMember(
        discordUserId: String,
        adminId: String = "ADMIN_CONSOLE"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val member = memberDao.getMemberByDiscordUserId(discordUserId)
            val session = onboardingSessionDao.getSessionByDiscordUserId(discordUserId)

            if (member != null && session == null) {
                // Reconstruct session
                val reconstructed = OnboardingSessionEntity(
                    discordUserId = discordUserId,
                    assignedJempId = member.id,
                    confirmedName = member.displayName,
                    selectedTimezone = member.timezone,
                    rulesAccepted = true,
                    safetyAcknowledged = true,
                    currentStep = 5,
                    membershipState = member.status,
                    assignedRolesJson = JSONArray(listOf(if (member.status == "ACTIVE") "@Member" else "@${member.status}")).toString(),
                    completedAtUtc = member.joinedAtUtc
                )
                onboardingSessionDao.insertSession(reconstructed)
                return@withContext Result.success("Reconciled: Restored missing session for active member ${member.id}.")
            }

            if (session != null && session.assignedJempId != null && member == null) {
                // Fix orphaned member record
                val memberRes = timelineService.assignMemberId(
                    discordUserId = discordUserId,
                    displayName = session.confirmedName ?: "Builder",
                    timezone = session.selectedTimezone
                )
                return@withContext Result.success("Reconciled: Created missing member record ${memberRes.getOrNull()?.id} for session.")
            }

            Result.success("Reconciled: State for $discordUserId is consistent.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetOnboarding(
        discordUserId: String,
        adminId: String = "ADMIN_CONSOLE"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            onboardingSessionDao.deleteSession(discordUserId)
            auditAction(
                actor = adminId,
                action = "RESET_ONBOARDING_SESSION",
                target = discordUserId,
                metadata = emptyMap()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // 8. SHARED MEMBERSHIP QUERIES (FOR FUTURE BOTS)
    // ----------------------------------------------------
    suspend fun isActiveMember(discordUserId: String): Boolean = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberByDiscordUserId(discordUserId)
        member != null && member.status == "ACTIVE"
    }

    suspend fun getMemberByDiscordId(discordUserId: String): MemberEntity? = withContext(Dispatchers.IO) {
        memberDao.getMemberByDiscordUserId(discordUserId)
    }

    suspend fun getMemberByJempId(jempId: String): MemberEntity? = withContext(Dispatchers.IO) {
        memberDao.getMemberById(jempId)
    }

    suspend fun getMembershipStatus(discordUserId: String): String = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberByDiscordUserId(discordUserId)
        member?.status ?: "UNKNOWN"
    }

    suspend fun getMemberTimezone(discordUserId: String): String = withContext(Dispatchers.IO) {
        val member = memberDao.getMemberByDiscordUserId(discordUserId)
        member?.timezone ?: "Asia/Kolkata"
    }

    // ----------------------------------------------------
    // 9. ADMIN DASHBOARD & SYSTEM HEALTH
    // ----------------------------------------------------
    suspend fun getDashboardData(): OnboardingDashboardMetrics = withContext(Dispatchers.IO) {
        val pendingCount = applicationDao.countApplicationsByStatus("PENDING")
        val approvedCount = applicationDao.countApplicationsByStatus("APPROVED")
        val waitlistedCount = applicationDao.countApplicationsByStatus("WAITLISTED")
        val rejectedCount = applicationDao.countApplicationsByStatus("REJECTED")

        val activeCount = onboardingSessionDao.countSessionsByState("ACTIVE")
        val onboardingCount = onboardingSessionDao.countSessionsByState("ONBOARDING") +
                onboardingSessionDao.countSessionsByState("EMAIL_VERIFIED") +
                onboardingSessionDao.countSessionsByState("JOINED_UNVERIFIED")
        val suspendedCount = onboardingSessionDao.countSessionsByState("SUSPENDED")
        val terminatedCount = onboardingSessionDao.countSessionsByState("TERMINATED")

        val recentApps = applicationDao.getAllApplicationsOnce().take(5)
        val activity = recentApps.map { app ->
            val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.US)
            val tag = when (app.status) {
                "APPROVED" -> "🟢 APPROVED"
                "WAITLISTED" -> "🟡 WAITLISTED"
                "REJECTED" -> "🔴 REJECTED"
                else -> "⏳ PENDING"
            }
            OnboardingActivityLogItem(
                id = app.applicationId,
                title = "${app.fullName} (${app.applicationId})",
                subtitle = "Status: ${app.status} • ${maskEmail(app.normalizedEmail)}",
                timestampFormatted = sdf.format(Date(app.submittedAtUtc)),
                tag = tag,
                isHighlighted = app.status == "APPROVED"
            )
        }

        OnboardingDashboardMetrics(
            pendingApplicationsCount = pendingCount,
            approvedApplicationsCount = approvedCount,
            waitlistedApplicationsCount = waitlistedCount,
            rejectedApplicationsCount = rejectedCount,
            activeMembersCount = activeCount.coerceAtLeast(1),
            onboardingMembersCount = onboardingCount,
            suspendedMembersCount = suspendedCount,
            terminatedMembersCount = terminatedCount,
            recentActivity = activity
        )
    }

    fun getAllApplicationsFlow(): Flow<List<ApplicationEntity>> {
        return applicationDao.getAllApplicationsFlow()
    }

    // ----------------------------------------------------
    // 10. DEMO SEED & CLEANUP
    // ----------------------------------------------------
    suspend fun seedDemoOnboardingData(): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Seed Demo Applications using reserved .invalid domain
            val demoApps = listOf(
                ApplicationEntity(
                    applicationId = "APP#0042",
                    fullName = "Saleha Shaikh",
                    email = "saleha.shaikh@example.invalid",
                    normalizedEmail = "saleha.shaikh@example.invalid",
                    resumeFileName = "Saleha_Shaikh_FullStack_2026.pdf",
                    resumeFileSize = "1.8 MB",
                    isCurrentlyUnemployed = true,
                    status = "APPROVED",
                    submittedAtUtc = 1788220000000L,
                    reviewedAtUtc = 1788230000000L,
                    reviewedByAdmin = "ADMIN#001",
                    inviteCode = "JEMP-INV-0042",
                    isDemo = true
                ),
                ApplicationEntity(
                    applicationId = "APP#0043",
                    fullName = "Alex Demo",
                    email = "alex.demo@example.invalid",
                    normalizedEmail = "alex.demo@example.invalid",
                    resumeFileName = "Alex_Demo_Systems_Architect.pdf",
                    resumeFileSize = "2.1 MB",
                    isCurrentlyUnemployed = true,
                    status = "APPROVED",
                    submittedAtUtc = 1788224000000L,
                    reviewedAtUtc = 1788232000000L,
                    reviewedByAdmin = "ADMIN#001",
                    inviteCode = "JEMP-INV-0043",
                    isDemo = true
                ),
                ApplicationEntity(
                    applicationId = "APP#0044",
                    fullName = "Sarah Jenkins",
                    email = "sarah.dev@example.invalid",
                    normalizedEmail = "sarah.dev@example.invalid",
                    resumeFileName = "Sarah_Jenkins_UI_Design.pdf",
                    resumeFileSize = "3.2 MB",
                    isCurrentlyUnemployed = true,
                    status = "PENDING",
                    submittedAtUtc = 1788240000000L,
                    isDemo = true
                ),
                ApplicationEntity(
                    applicationId = "APP#0045",
                    fullName = "Marcus Chen",
                    email = "marcus.product@example.invalid",
                    normalizedEmail = "marcus.product@example.invalid",
                    resumeFileName = "Marcus_Chen_PM_Lead.pdf",
                    resumeFileSize = "1.5 MB",
                    isCurrentlyUnemployed = true,
                    status = "APPROVED",
                    submittedAtUtc = 1788245000000L,
                    reviewedAtUtc = 1788248000000L,
                    reviewedByAdmin = "ADMIN#001",
                    linkedDiscordUserId = "200000000000000014",
                    inviteCode = "JEMP-INV-0045",
                    isDemo = true
                ),
                ApplicationEntity(
                    applicationId = "APP#0046",
                    fullName = "Vikram Patel",
                    email = "vikram.ai@example.invalid",
                    normalizedEmail = "vikram.ai@example.invalid",
                    resumeFileName = "Vikram_Patel_ML_Engineer.pdf",
                    resumeFileSize = "2.8 MB",
                    isCurrentlyUnemployed = false,
                    status = "WAITLISTED",
                    submittedAtUtc = 1788250000000L,
                    reviewedAtUtc = 1788255000000L,
                    reviewedByAdmin = "ADMIN#001",
                    isDemo = true
                )
            )

            demoApps.forEach { applicationDao.insertApplication(it) }

            // Seed demo active member (JEMP#014)
            if (memberDao.getMemberById("JEMP#014") == null) {
                memberDao.insertMember(
                    MemberEntity(
                        id = "JEMP#014",
                        discordUserId = "200000000000000014",
                        jempNumber = 14,
                        jempDisplayId = "JEMP#014",
                        displayName = "Marcus Chen",
                        specialty = "Product Strategy & Technical Architecture",
                        timezone = "Asia/Kolkata",
                        joinedAtUtc = 1788300000000L,
                        status = "ACTIVE",
                        isDemo = true
                    )
                )
            }

            auditAction(
                actor = "ADMIN",
                action = "SEED_ONBOARDING_DEMO",
                target = "SYSTEM",
                metadata = mapOf("count" to demoApps.size.toString())
            )

            Result.success("Seeded ${demoApps.size} demo applications and onboarding states.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearDemoOnboardingData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            applicationDao.deleteDemoApplications()
            invitationDao.deleteDemoInvitations()
            onboardingSessionDao.deleteDemoSessions()
            auditAction(
                actor = "ADMIN",
                action = "CLEAR_ONBOARDING_DEMO",
                target = "SYSTEM",
                metadata = emptyMap()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    private fun OnboardingSessionEntity.toProgressState(applicantName: String): OnboardingProgressState {
        val state = try {
            MembershipState.valueOf(membershipState)
        } catch (_: Exception) {
            MembershipState.JOINED_UNVERIFIED
        }

        val roles = try {
            val arr = JSONArray(assignedRolesJson)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (_: Exception) {
            listOf("@Unverified")
        }

        val now = System.currentTimeMillis()
        val isRateLimited = rateLimitedUntilUtc > now
        val retrySec = if (isRateLimited) ((rateLimitedUntilUtc - now) / 1000).toInt().coerceAtLeast(1) else 0

        return OnboardingProgressState(
            discordUserId = discordUserId,
            discordUsername = discordUsername,
            applicationId = applicationId,
            applicantName = applicantName,
            verifiedEmail = verifiedEmail,
            currentStep = currentStep,
            selectedTimezone = selectedTimezone,
            nameCorrectionRequested = nameCorrectionRequested,
            rulesAccepted = rulesAccepted,
            safetyAcknowledged = safetyAcknowledged,
            assignedJempId = assignedJempId,
            membershipState = state,
            assignedRoles = roles,
            canAccessGeneralChannels = state == MembershipState.ACTIVE,
            isRateLimited = isRateLimited,
            retryAfterSeconds = retrySec
        )
    }
}
