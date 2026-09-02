package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "applications",
    indices = [
        Index(value = ["normalizedEmail"], unique = true),
        Index(value = ["status"]),
        Index(value = ["submittedAtUtc"])
    ]
)
data class ApplicationEntity(
    @PrimaryKey
    val applicationId: String, // e.g. "APP#0042" or "APP-8F3K9L2A"
    val fullName: String,
    val email: String, // Original email as submitted
    val normalizedEmail: String, // Trimmed, lowercased for exact matching
    val resumeFileName: String,
    val resumeFileSize: String,
    val isCurrentlyUnemployed: Boolean,
    val consentGiven: Boolean = true,
    val status: String = "PENDING", // PENDING, APPROVED, WAITLISTED, REJECTED, REVOKED
    val submittedAtUtc: Long = System.currentTimeMillis(),
    val reviewedAtUtc: Long? = null,
    val reviewedByAdmin: String? = null,
    val linkedDiscordUserId: String? = null,
    val inviteCode: String? = null,
    val rejectionReason: String? = null,
    val adminNotes: String? = null,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "invitations",
    indices = [
        Index(value = ["applicationId"]),
        Index(value = ["inviteCode"], unique = true)
    ]
)
data class InvitationEntity(
    @PrimaryKey
    val inviteCode: String, // e.g. "JEMP-INV-8F3K"
    val applicationId: String,
    val createdBy: String,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val expiresAtUtc: Long = System.currentTimeMillis() + (72 * 3600 * 1000L), // 72 hours
    val maxUses: Int = 1,
    val useCount: Int = 0,
    val isUsed: Boolean = false,
    val usedByDiscordUserId: String? = null,
    val usedAtUtc: Long? = null,
    val isRevoked: Boolean = false,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "onboarding_sessions",
    indices = [
        Index(value = ["applicationId"]),
        Index(value = ["membershipState"]),
        Index(value = ["assignedJempId"])
    ]
)
data class OnboardingSessionEntity(
    @PrimaryKey
    val discordUserId: String, // e.g. "100000000000000001"
    val discordUsername: String = "BuilderUser",
    val applicationId: String? = null,
    val verifiedEmail: String? = null,
    val confirmedName: String? = null,
    val nameCorrectionRequested: Boolean = false,
    val nameCorrectionNote: String? = null,
    val selectedTimezone: String = "Asia/Kolkata",
    val rulesAccepted: Boolean = false,
    val rulesAcceptedAtUtc: Long? = null,
    val safetyAcknowledged: Boolean = false,
    val safetyAcknowledgedAtUtc: Long? = null,
    val assignedJempId: String? = null, // e.g. "JEMP#014"
    val currentStep: Int = 0, // 0: Welcome/Email, 1: Name, 2: Timezone, 3: Rules, 4: Safety, 5: Completed
    val membershipState: String = "JOINED_UNVERIFIED", // JOINED_UNVERIFIED, EMAIL_VERIFIED, ONBOARDING, ACTIVE, SUSPENDED, TERMINATED, LEFT, REJOIN_PENDING
    val verificationAttempts: Int = 0,
    val lastAttemptUtc: Long = 0L,
    val rateLimitedUntilUtc: Long = 0L,
    val startedAtUtc: Long = System.currentTimeMillis(),
    val completedAtUtc: Long? = null,
    val updatedAtUtc: Long = System.currentTimeMillis(),
    val assignedRolesJson: String = "[\"@Unverified\"]", // JSON array of role names
    val suspensionReason: String? = null,
    val terminationReason: String? = null,
    val isDemo: Boolean = false
)

@Entity(tableName = "onboarding_settings")
data class OnboardingSettingsEntity(
    @PrimaryKey
    val guildId: String = "DEFAULT_GUILD",
    val applicationsChannel: String = "#applications",
    val onboardingChannel: String = "#welcome",
    val unverifiedRole: String = "@Unverified",
    val memberRole: String = "@Member",
    val adminRole: String = "@JEMP Admin",
    val suspendedRole: String = "@Suspended",
    val terminatedRole: String = "@Terminated",
    val nicknameFormat: String = "JEMP#%03d • %s",
    val autoSetNickname: Boolean = true,
    val maxVerificationAttempts: Int = 5,
    val rateLimitMinutes: Int = 15,
    val inviteExpiryHours: Int = 72,
    val isConfigured: Boolean = true
)
