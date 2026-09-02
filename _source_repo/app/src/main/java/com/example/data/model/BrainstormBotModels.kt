package com.example.data.model

import com.example.data.local.entities.*
import org.json.JSONArray
import org.json.JSONObject

enum class ContributorRole(val label: String, val badgeColorHex: Long) {
    OWNER("OWNER", 0xFFF59E0B),
    CORE_CONTRIBUTOR("CORE CONTRIBUTOR", 0xFF06B6D4),
    CONTRIBUTOR("CONTRIBUTOR", 0xFF10B981),
    ADVISOR("ADVISOR", 0xFF8B5CF6),
    OBSERVER("OBSERVER", 0xFF6B7280)
}

enum class BrainstormCommitment(val label: String) {
    CASUAL("Casual brainstorming"),
    RESEARCH("Research & exploration"),
    EXPERIMENT("Short experiment"),
    SERIOUS_BUILD("Serious build candidate"),
    STARTUP("Founder/startup exploration")
}

data class BrainstormCreationDraft(
    val title: String = "",
    val description: String = "",
    val problem: String = "",
    val direction: String = "",
    val targetAudience: String = "",
    val desiredSkills: List<String> = emptyList(),
    val commitmentLevel: String = BrainstormCommitment.SERIOUS_BUILD.label,
    val tags: List<String> = emptyList()
)

data class BrainstormContributorItem(
    val id: String,
    val brainstormId: String,
    val memberId: String,
    val displayName: String,
    val role: ContributorRole,
    val status: String,
    val joinedDateFormatted: String,
    val isDemo: Boolean
)

data class BrainstormApplicationItem(
    val id: String,
    val brainstormId: String,
    val memberId: String,
    val displayName: String,
    val reason: String,
    val skills: String,
    val experience: String,
    val availability: String,
    val portfolio: String,
    val status: String,
    val createdDateFormatted: String,
    val decisionReason: String?,
    val isDemo: Boolean
)

data class BrainstormDecisionItem(
    val id: String,
    val brainstormId: String,
    val decisionNumber: Int,
    val decisionText: String,
    val reason: String,
    val madeByMemberId: String,
    val madeByDisplayName: String,
    val formattedDate: String
)

data class BrainstormMilestoneItem(
    val id: String,
    val brainstormId: String,
    val title: String,
    val description: String,
    val createdByMemberId: String,
    val isCompleted: Boolean,
    val completedDateFormatted: String?,
    val completedByMemberId: String?
)

data class BrainstormTaskItem(
    val id: String,
    val brainstormId: String,
    val title: String,
    val description: String,
    val assigneeMemberId: String?,
    val assigneeDisplayName: String?,
    val priority: String,
    val status: String,
    val dueDate: String?
)

data class BrainstormResourceItem(
    val id: String,
    val brainstormId: String,
    val name: String,
    val url: String,
    val description: String,
    val category: String,
    val isPrivate: Boolean,
    val addedByMemberId: String
)

data class BrainstormPollItem(
    val id: String,
    val brainstormId: String,
    val question: String,
    val options: List<String>,
    val voteCounts: Map<Int, Int>,
    val userVotedOptionIndex: Int?,
    val totalVotes: Int,
    val isClosed: Boolean,
    val promotedToDecisionId: String?
)

data class BrainstormDetailModel(
    val brainstorm: BrainstormEntity,
    val desiredSkills: List<String>,
    val tags: List<String>,
    val contributors: List<BrainstormContributorItem>,
    val applications: List<BrainstormApplicationItem>,
    val decisions: List<BrainstormDecisionItem>,
    val milestones: List<BrainstormMilestoneItem>,
    val tasks: List<BrainstormTaskItem>,
    val resources: List<BrainstormResourceItem>,
    val polls: List<BrainstormPollItem>,
    val recentActivity: List<BrainstormActivityEntity>,
    val isOwner: Boolean,
    val isContributor: Boolean,
    val hasPendingApplication: Boolean,
    val isLinkedProjectActive: Boolean
)

data class BrainstormAdminDashboardMetrics(
    val totalBrainstorms: Int = 0,
    val activeCount: Int = 0,
    val cookingCount: Int = 0,
    val pausedCount: Int = 0,
    val cancelledCount: Int = 0,
    val aliveCount: Int = 0,
    val pendingApplicationsCount: Int = 0,
    val activeContributorsCount: Int = 0,
    val activityLast24h: Int = 0,
    val activityLast7d: Int = 0,
    val inactiveWarningsCount: Int = 0,
    val conversionsCount: Int = 0
)

data class BrainstormReconciliationReport(
    val checkedTimestampFormatted: String,
    val totalInconsistenciesFound: Int,
    val orphanedThreadsFound: Int = 0,
    val suspendedMembersWithAccessCount: Int = 0,
    val missingTimelineEventsCount: Int = 0,
    val invalidProjectLinksCount: Int = 0,
    val details: List<String>,
    val isHealthy: Boolean
)

data class BrainstormHealthStatus(
    val databaseStatus: String = "HEALTHY (Room SQLite Persistent Storage)",
    val discordApiStatus: String = "CONNECTED (Gateway v10, Permissions Verified)",
    val timelineEngineStatus: String = "CONNECTED (Shared Canonical Engine)",
    val memberServiceStatus: String = "ACTIVE (Membership Attestation Online)",
    val eventQueueStatus: String = "ONLINE (Idempotent Ingestion)",
    val configurationStatus: String = "OPTIMAL",
    val checkedAtFormatted: String = "Just now"
)
