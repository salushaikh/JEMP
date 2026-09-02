package com.example.data.model

enum class MembershipState(val label: String, val badgeColorHex: Long) {
    APPLIED("APPLIED", 0xFF9E9E9E),
    APPROVED("APPROVED", 0xFF10B981),
    INVITED("INVITED", 0xFF06B6D4),
    JOINED_UNVERIFIED("JOINED (UNVERIFIED)", 0xFFF59E0B),
    EMAIL_VERIFIED("EMAIL VERIFIED", 0xFF3B82F6),
    ONBOARDING("ONBOARDING", 0xFF8B5CF6),
    ACTIVE("ACTIVE", 0xFF10B981),
    SUSPENDED("SUSPENDED", 0xFFEF4444),
    TERMINATED("TERMINATED", 0xFF991B1B),
    LEFT("LEFT", 0xFF6B7280),
    REJOIN_PENDING("REJOIN PENDING", 0xFFF59E0B)
}

data class EmailVerificationResult(
    val isSuccess: Boolean,
    val applicantName: String? = null,
    val applicationId: String? = null,
    val errorMessage: String? = null,
    val isRateLimited: Boolean = false,
    val retryAfterSeconds: Int = 0,
    val alreadyLinkedToDifferentUser: Boolean = false
)

data class OnboardingProgressState(
    val discordUserId: String,
    val discordUsername: String,
    val applicationId: String?,
    val applicantName: String,
    val verifiedEmail: String?,
    val currentStep: Int, // 0: Welcome, 1: Confirm Name, 2: Timezone, 3: Rules, 4: Safety, 5: Complete
    val totalSteps: Int = 5,
    val selectedTimezone: String,
    val nameCorrectionRequested: Boolean,
    val rulesAccepted: Boolean,
    val safetyAcknowledged: Boolean,
    val assignedJempId: String?,
    val membershipState: MembershipState,
    val assignedRoles: List<String>,
    val canAccessGeneralChannels: Boolean,
    val isRateLimited: Boolean = false,
    val retryAfterSeconds: Int = 0
)

data class MemberCardSummary(
    val jempId: String,
    val displayName: String,
    val specialty: String,
    val status: String,
    val joinedDateFormatted: String,
    val timezone: String,
    val applicationId: String,
    val onboardingState: String,
    val totalTimelineRecords: Int,
    val assignedRoles: List<String>
)

data class OnboardingDashboardMetrics(
    val pendingApplicationsCount: Int,
    val approvedApplicationsCount: Int,
    val waitlistedApplicationsCount: Int,
    val rejectedApplicationsCount: Int,
    val activeMembersCount: Int,
    val onboardingMembersCount: Int,
    val suspendedMembersCount: Int,
    val terminatedMembersCount: Int,
    val recentActivity: List<OnboardingActivityLogItem>
)

data class OnboardingActivityLogItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestampFormatted: String,
    val tag: String,
    val isHighlighted: Boolean = false
)

enum class DiscordPersonaType(
    val title: String,
    val discordUserId: String,
    val discordUsername: String,
    val subtitle: String
) {
    APPROVED_APPLICANT(
        "Approved Applicant (Alex Demo)",
        "100000000000000001",
        "AlexVance",
        "Approved application awaiting email verification & onboarding"
    ),
    UNKNOWN_JOINER(
        "Unknown / Unwhitelisted Joiner",
        "999999999999999999",
        "GuestBuilder",
        "Tests non-enumerating error and rate-limiting"
    ),
    ACTIVE_MEMBER(
        "Active Member (JEMP#001 Alex Vance)",
        "100000000000000001",
        "AlexVance",
        "Already active JEMP member (tests rejoin & member commands)"
    ),
    ACTIVE_MEMBER_2(
        "Active Member (JEMP#014 Marcus Chen)",
        "200000000000000014",
        "MarcusChen",
        "Product lead member profile"
    ),
    SUSPENDED_MEMBER(
        "Suspended Member (John Doe)",
        "300000000000000099",
        "SuspendedUser",
        "Tests access denial and suspension card"
    ),
    ADMIN_USER(
        "Administrator (@JEMP Admin)",
        "800000000000000001",
        "EmpireAdmin",
        "Full access to #applications and /onboarding-admin hub"
    );

    val displayName: String get() = title
    val roleDescription: String get() = subtitle

    companion object {
        val ADMINISTRATOR = ADMIN_USER
    }
}

data class DiscordServerChannel(
    val id: String,
    val name: String,
    val category: String,
    val isPrivateAdminOnly: Boolean = false,
    val requiresActiveMembership: Boolean = true,
    val iconSymbol: String = "#",
    val unreadCount: Int = 0
)

data class CommunityRuleItem(
    val number: Int,
    val title: String,
    val description: String
)
