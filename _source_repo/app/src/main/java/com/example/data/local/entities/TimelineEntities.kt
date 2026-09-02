package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timeline_entries",
    indices = [
        Index(value = ["memberId"]),
        Index(value = ["source", "sourceEventId"], unique = true),
        Index(value = ["timestampUtc"]),
        Index(value = ["projectId"])
    ]
)
data class TimelineEntryEntity(
    @PrimaryKey
    val id: String, // e.g. "TLE-987654"
    val memberId: String, // e.g. "JEMP#001"
    val eventType: String, // SYSTEM_MEMBER_JOINED, ONBOARDING_COMPLETED, PROJECT_JOINED, etc.
    val title: String,
    val bulletsJson: String, // JSON array of 1-3 bullet points
    val timestampUtc: Long,
    val timezoneAtCreation: String,
    val projectId: String? = null, // e.g. "B#007", "P#003", "HC#004"
    val source: String, // "onboarding-bot", "brainstorm-bot", "project-bot", "client-project-bot", "admin", "member"
    val sourceEventId: String? = null, // for idempotency deduplication
    val isImmutable: Boolean = true, // true for system & admin, false for member entries
    val visibility: String = "PUBLIC_SAFE", // PUBLIC_SAFE, PRIVATE_MEMBER, ADMIN_ONLY
    val createdBy: String, // e.g. "SYSTEM", "JEMP#001", "ADMIN_123"
    val correctionForEntryId: String? = null, // if this is an ADMIN_CORRECTION
    val isDemo: Boolean = false,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val updatedAtUtc: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "timeline_shares",
    indices = [
        Index(value = ["memberId"]),
        Index(value = ["tokenHash"], unique = true)
    ]
)
data class TimelineShareEntity(
    @PrimaryKey
    val id: String, // UUID
    val memberId: String,
    val tokenHash: String, // SHA-256 or secure random token
    val shareUrl: String,
    val createdAtUtc: Long,
    val expiresAtUtc: Long, // 24 hours after creation
    val revokedAtUtc: Long? = null,
    val accessCount: Int = 0,
    val lastAccessedAtUtc: Long? = null
)

@Entity(
    tableName = "audit_logs",
    indices = [
        Index(value = ["timestampUtc"]),
        Index(value = ["target"])
    ]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actor: String, // e.g. "JEMP#001", "ADMIN#SYSTEM"
    val action: String, // "VIEW_TIMELINE", "ADD_ENTRY", "EDIT_ENTRY", "DELETE_ENTRY", "CREATE_SHARE", "REVOKE_SHARE", "ADMIN_CORRECTION", "SEED_DEMO"
    val target: String, // e.g. "JEMP#001"
    val metadataJson: String,
    val timestampUtc: Long = System.currentTimeMillis()
)

@Entity(tableName = "timeline_settings")
data class TimelineSettingsEntity(
    @PrimaryKey
    val guildId: String = "DEFAULT_GUILD",
    val adminRole: String = "Jobless Empire Admin",
    val timelineChannel: String = "#timeline-attestations",
    val maxInlineEntries: Int = 15,
    val maxInlineDates: Int = 5,
    val maxInlineCharacters: Int = 3000,
    val maxInlineWords: Int = 200,
    val defaultTimezone: String = "Asia/Kolkata",
    val publicShareBaseUrl: String = "https://timeline.joblessempire.com/share/",
    val maxBackdatingDays: Int = 30,
    val isSetupComplete: Boolean = true
)
