package com.example.data.model

enum class BrainstormStatus(val label: String) {
    BRAINSTORMING("BRAINSTORMING"),
    COOKING("COOKING"),
    PAUSED("PAUSED"),
    CANCELLED("CANCELLED"),
    IT_S_ALIVE("IT'S ALIVE")
}

enum class ProjectStatus(val label: String) {
    IN_PROGRESS("IN PROGRESS"),
    PAUSED("PAUSED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED")
}

enum class ClientProjectStatus(val label: String) {
    OPEN_FOR_APPLICATIONS("OPEN FOR APPLICATIONS"),
    REVIEWING_APPLICANTS("REVIEWING APPLICANTS"),
    CONVERTED_TO_PROJECT("CONVERTED (P#)"),
    COMPLETED("COMPLETED")
}

enum class TimelineCategory(val label: String) {
    ONBOARDING("ONBOARDING"),
    BRAINSTORM("BRAINSTORM"),
    PROJECT("PROJECT"),
    CLIENT_WORK("CLIENT WORK"),
    MEETING("MEETING"),
    MILESTONE("MILESTONE")
}

enum class ApplicationReviewStatus(val label: String) {
    PENDING("PENDING REVIEW"),
    APPROVED("APPROVED & INVITED"),
    REJECTED("REJECTED"),
    WAITLISTED("WAITLISTED")
}

data class ProjectMilestone(
    val title: String,
    val isCompleted: Boolean,
    val date: String,
    val assigneeId: String
)

data class BrainstormSession(
    val id: String, // e.g. "B#007"
    val title: String,
    val description: String,
    val category: String,
    val ownerId: String, // e.g. "JEMP#014"
    val status: BrainstormStatus,
    val contributorsCount: Int,
    val applicationsCount: Int,
    val linkedProjectId: String? = null, // e.g. "P#003"
    val recentActivity: String,
    val tags: List<String>
)

data class OngoingProject(
    val id: String, // e.g. "P#003"
    val title: String,
    val description: String,
    val ownerId: String,
    val contributors: List<String>,
    val linkedBrainstormId: String? = null,
    val linkedClientId: String? = null,
    val status: ProjectStatus,
    val statusReason: String? = null,
    val milestones: List<ProjectMilestone>,
    val latestMeeting: String? = null
)

data class ClientProject(
    val id: String, // e.g. "HC#004"
    val title: String,
    val description: String,
    val clientRequirements: String,
    val budgetUsd: Int,
    val expectedTimeline: String,
    val requiredSkills: List<String>,
    val revShareSplit: String = "5% Admin • 95% Contributor Pool",
    val ownerId: String,
    val status: ClientProjectStatus,
    val linkedProjectId: String? = null // e.g. "P#002"
)

data class TimelineEntry(
    val id: String,
    val memberId: String, // "JEMP#001"
    val date: String,
    val title: String,
    val linkedReferenceId: String? = null, // "B#014", "P#008", "HC#004"
    val details: List<String>,
    val isSystemGenerated: Boolean = true,
    val category: TimelineCategory
)

data class MemberProfile(
    val id: String, // "JEMP#014"
    val name: String,
    val specialty: String,
    val joinedDate: String,
    val timezone: String,
    val timeline: List<TimelineEntry>,
    val isPublic24hShared: Boolean = false,
    val shareExpiresInSeconds: Long = 86400L
)

data class ApplicationSubmission(
    val id: String,
    val fullName: String,
    val email: String,
    val resumeFileName: String,
    val resumeFileSize: String,
    val isCurrentlyUnemployed: Boolean,
    val consentChecked: Boolean,
    val timestamp: String,
    val assignedJempId: String,
    val reviewStatus: ApplicationReviewStatus = ApplicationReviewStatus.PENDING
)

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String = "General"
)

data class BuilderRoleInfo(
    val title: String,
    val description: String,
    val iconName: String,
    val skills: List<String>
)
