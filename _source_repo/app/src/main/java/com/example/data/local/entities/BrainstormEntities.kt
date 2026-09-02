package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brainstorms")
data class BrainstormEntity(
    @PrimaryKey
    val id: String, // e.g. "B#001"
    val brainstormNumber: Int, // e.g. 1
    val title: String,
    val description: String,
    val problem: String,
    val direction: String,
    val targetAudience: String,
    val desiredSkillsJson: String, // JSON array e.g. ["Developer", "Designer"]
    val commitmentLevel: String, // e.g. "Serious build candidate"
    val tagsJson: String, // JSON array e.g. ["AI", "SaaS", "Career"]
    val ownerMemberId: String, // e.g. "JEMP#014"
    val ownerDiscordId: String,
    val ownerDisplayName: String,
    val discordThreadId: String = "",
    val status: String = "BRAINSTORMING", // BRAINSTORMING, COOKING, PAUSED, CANCELLED, IT_S_ALIVE
    val statusReason: String? = null,
    val statusChangedBy: String? = null,
    val statusChangedAtUtc: Long? = null,
    val linkedProjectId: String? = null, // e.g. "P#014"
    val createdAtUtc: Long = System.currentTimeMillis(),
    val updatedAtUtc: Long = System.currentTimeMillis(),
    val lastMeaningfulActivityAtUtc: Long = System.currentTimeMillis(),
    val archivedAtUtc: Long? = null,
    val cancelledAtUtc: Long? = null,
    val isArchived: Boolean = false,
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_contributors")
data class BrainstormContributorEntity(
    @PrimaryKey
    val id: String, // UUID
    val brainstormId: String, // e.g. "B#001"
    val memberId: String, // e.g. "JEMP#031"
    val discordUserId: String,
    val displayName: String,
    val role: String = "CONTRIBUTOR", // OWNER, CORE_CONTRIBUTOR, CONTRIBUTOR, ADVISOR, OBSERVER
    val status: String = "ACTIVE", // ACTIVE, REMOVED, LEFT, SUSPENDED
    val joinedAtUtc: Long = System.currentTimeMillis(),
    val leftAtUtc: Long? = null,
    val removedBy: String? = null,
    val removalReason: String? = null,
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_applications")
data class BrainstormApplicationEntity(
    @PrimaryKey
    val id: String, // UUID
    val brainstormId: String,
    val memberId: String,
    val discordUserId: String,
    val displayName: String,
    val reason: String,
    val skills: String,
    val experience: String,
    val availability: String,
    val portfolio: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, WITHDRAWN
    val createdAtUtc: Long = System.currentTimeMillis(),
    val reviewedAtUtc: Long? = null,
    val reviewedBy: String? = null,
    val decisionReason: String? = null,
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_decisions")
data class BrainstormDecisionEntity(
    @PrimaryKey
    val id: String,
    val brainstormId: String,
    val decisionNumber: Int,
    val decisionText: String,
    val reason: String,
    val madeByMemberId: String,
    val madeByDisplayName: String,
    val timestampUtc: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_milestones")
data class BrainstormMilestoneEntity(
    @PrimaryKey
    val id: String,
    val brainstormId: String,
    val title: String,
    val description: String,
    val createdByMemberId: String,
    val isCompleted: Boolean = false,
    val completedAtUtc: Long? = null,
    val completedByMemberId: String? = null,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_tasks")
data class BrainstormTaskEntity(
    @PrimaryKey
    val id: String,
    val brainstormId: String,
    val title: String,
    val description: String,
    val assigneeMemberId: String? = null,
    val assigneeDisplayName: String? = null,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val status: String = "TODO", // TODO, IN_PROGRESS, BLOCKED, DONE
    val dueDate: String? = null,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val updatedAtUtc: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_resources")
data class BrainstormResourceEntity(
    @PrimaryKey
    val id: String,
    val brainstormId: String,
    val name: String,
    val url: String,
    val description: String,
    val category: String = "OTHER", // DESIGN, CODE, RESEARCH, DOCUMENTATION, REFERENCE, OTHER
    val isPrivate: Boolean = false,
    val addedByMemberId: String,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_polls")
data class BrainstormPollEntity(
    @PrimaryKey
    val id: String,
    val brainstormId: String,
    val question: String,
    val optionsJson: String, // JSON array of options
    val votesJson: String, // JSON map of memberId -> optionIndex
    val createdByMemberId: String,
    val isClosed: Boolean = false,
    val promotedToDecisionId: String? = null,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_activity")
data class BrainstormActivityEntity(
    @PrimaryKey
    val id: String,
    val brainstormId: String,
    val activityType: String = "MESSAGE_ACTIVITY", // MESSAGE_ACTIVITY, MEANINGFUL_ACTIVITY, OFFICIAL_EVENT
    val actorMemberId: String,
    val actorDisplayName: String,
    val title: String,
    val description: String,
    val timestampUtc: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "brainstorm_settings")
data class BrainstormSettingsEntity(
    @PrimaryKey
    val guildId: String = "DEFAULT_GUILD",
    val categoryId: String = "109876543210987654",
    val hubChannelId: String = "109876543210987655",
    val feedChannelId: String = "109876543210987656",
    val ownerRoleId: String = "109876543210987660",
    val contributorRoleId: String = "109876543210987661",
    val inactivityWarningDays: Int = 14,
    val autoPauseDays: Int = 30,
    val archiveReviewDays: Int = 60,
    val allowMemberCreation: Boolean = true,
    val allowMemberApply: Boolean = true
)

@Entity(tableName = "brainstorm_id_counters")
data class BrainstormIdCounterEntity(
    @PrimaryKey
    val counterName: String = "brainstorm_id_counter",
    val nextNumber: Int = 1
)
