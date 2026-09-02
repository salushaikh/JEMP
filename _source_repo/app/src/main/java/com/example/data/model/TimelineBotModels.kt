package com.example.data.model

import java.text.SimpleDateFormat
import java.util.*

enum class EventTypeCategory(val displayName: String, val iconSymbol: String, val defaultImmutable: Boolean) {
    SYSTEM("System Record", "⚙", true),
    ONBOARDING("Onboarding", "🚀", true),
    BRAINSTORM("Brainstorming (B#)", "💡", true),
    PROJECT("Ongoing Project (P#)", "🏗", true),
    CLIENT_PROJECT("Client Project (HC#)", "💼", true),
    MEETING("Meeting Participation", "🎙", true),
    MEMBER("Member Milestone", "✎", false),
    ADMIN("Admin Action", "🔒", true)
}

enum class TimelineViewMode {
    COMPACT,
    STANDARD,
    DETAILED
}

enum class TimelineVisibility {
    PUBLIC_SAFE,
    PRIVATE_MEMBER,
    ADMIN_ONLY
}

enum class ExportType {
    PROFESSIONAL,
    FULL
}

enum class ExportFormat {
    MARKDOWN,
    JSON,
    STRUCTURED_DOC
}

data class TimelineEventPayload(
    val memberId: String,
    val eventType: String,
    val title: String,
    val bullets: List<String>,
    val projectId: String? = null, // e.g. "B#007", "P#003", "HC#004"
    val source: String, // "onboarding-bot", "brainstorm-bot", "project-bot", "client-project-bot", "admin", "member"
    val sourceEventId: String? = null,
    val immutable: Boolean = true,
    val visibility: TimelineVisibility = TimelineVisibility.PUBLIC_SAFE,
    val createdBy: String = "SYSTEM",
    val customTimestampUtc: Long? = null,
    val timezone: String? = null,
    val isDemo: Boolean = false
)

data class TimelineItem(
    val id: String,
    val memberId: String,
    val eventType: String,
    val title: String,
    val bullets: List<String>,
    val timestampUtc: Long,
    val timezone: String,
    val formattedDate: String, // e.g. "02 Sep 2026"
    val formattedTime: String, // e.g. "20:42 IST"
    val year: Int,
    val monthName: String, // e.g. "September"
    val monthIndex: Int, // 1-12
    val dateName: String, // e.g. "02 September 2026"
    val projectId: String? = null,
    val source: String,
    val sourceEventId: String? = null,
    val isImmutable: Boolean,
    val visibility: TimelineVisibility,
    val createdBy: String,
    val correctionForEntryId: String? = null,
    val category: EventTypeCategory,
    val isDemo: Boolean = false
)

data class DateHierarchyGroup(
    val dateKey: String, // e.g. "02 Sep 2026"
    val displayDate: String, // "02 September"
    val timestampUtc: Long,
    val entries: List<TimelineItem>
)

data class MonthHierarchyGroup(
    val monthKey: String, // e.g. "2026-09"
    val monthName: String, // "September"
    val year: Int,
    val dateGroups: List<DateHierarchyGroup>,
    val totalEntriesCount: Int
)

data class YearHierarchyGroup(
    val year: Int,
    val monthGroups: List<MonthHierarchyGroup>,
    val totalEntriesCount: Int
)

data class TimelineHierarchy(
    val memberId: String,
    val memberDisplayId: String,
    val memberName: String,
    val timezone: String,
    val yearGroups: List<YearHierarchyGroup>,
    val totalEntries: Int,
    val distinctDatesCount: Int,
    val totalCharacters: Int,
    val totalWords: Int,
    val isCollapsibleThresholdReached: Boolean
)

data class TimelineComplexity(
    val totalEntries: Int,
    val distinctDatesCount: Int,
    val totalCharacters: Int,
    val totalWords: Int,
    val isAboveThreshold: Boolean,
    val reason: String
)

data class TimelineStatsSummary(
    val memberId: String,
    val memberSinceDate: String,
    val totalEntries: Int,
    val officialRecordsCount: Int,
    val personalMilestonesCount: Int,
    val brainstormsCount: Int,
    val projectsCount: Int,
    val clientProjectsCount: Int,
    val meetingsCount: Int,
    val completedProjectsCount: Int
)

data class PublicShareDetails(
    val memberId: String,
    val token: String,
    val shareUrl: String,
    val createdAtFormatted: String,
    val expiresAtFormatted: String,
    val isExpired: Boolean,
    val isRevoked: Boolean,
    val accessCount: Int,
    val remainingHoursMinutes: String
)

data class PublicTimelineViewData(
    val memberDisplayId: String,
    val displayName: String,
    val specialty: String,
    val joinedDateFormatted: String,
    val timezone: String,
    val entries: List<TimelineItem>,
    val shareExpiresIn: String,
    val isValid: Boolean,
    val expirationNotice: String? = null
)

data class ExportDocument(
    val memberId: String,
    val exportType: ExportType,
    val format: ExportFormat,
    val content: String,
    val generatedAtUtc: Long,
    val generatedAtFormatted: String
)

data class SystemHealthReport(
    val discordGatewayStatus: String = "CONNECTED (Gateway v10, 18ms latency)",
    val databaseStatus: String = "HEALTHY (Room SQLite Persistent Storage)",
    val eventProcessorStatus: String = "RUNNING (Idempotent Ingestion Active)",
    val publicShareServiceStatus: String = "OPERATIONAL (24h Cryptographic Expiry)",
    val totalTrackedMembers: Int = 0,
    val totalRecordedEntries: Int = 0,
    val totalActivePublicShares: Int = 0,
    val queuedEventsCount: Int = 0,
    val lastEventProcessedFormatted: String = "Just now"
)

data class SlashCommandInfo(
    val name: String,
    val syntax: String,
    val description: String,
    val category: String, // "Member", "Admin"
    val exampleUsage: String
)
