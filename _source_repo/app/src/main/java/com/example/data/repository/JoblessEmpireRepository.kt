package com.example.data.repository

import android.content.Context
import com.example.data.local.JoblessEmpireDatabase
import com.example.data.local.entities.*
import com.example.data.model.*
import com.example.data.sample.JoblessEmpireData
import com.example.data.service.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class JoblessEmpireRepository private constructor(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database = JoblessEmpireDatabase.getDatabase(context)
    val timelineService = TimelineService(
        memberDao = database.memberDao(),
        timelineEntryDao = database.timelineEntryDao(),
        timelineShareDao = database.timelineShareDao(),
        auditLogDao = database.auditLogDao(),
        timelineSettingsDao = database.timelineSettingsDao()
    )

    val onboardingService = OnboardingMembershipService(
        memberDao = database.memberDao(),
        timelineEntryDao = database.timelineEntryDao(),
        auditLogDao = database.auditLogDao(),
        applicationDao = database.applicationDao(),
        invitationDao = database.invitationDao(),
        onboardingSessionDao = database.onboardingSessionDao(),
        onboardingSettingsDao = database.onboardingSettingsDao(),
        timelineService = timelineService
    )

    val brainstormService = BrainstormService(
        brainstormDao = database.brainstormDao(),
        contributorDao = database.brainstormContributorDao(),
        applicationDao = database.brainstormApplicationDao(),
        decisionDao = database.brainstormDecisionDao(),
        milestoneDao = database.brainstormMilestoneDao(),
        taskDao = database.brainstormTaskDao(),
        resourceDao = database.brainstormResourceDao(),
        pollDao = database.brainstormPollDao(),
        activityDao = database.brainstormActivityDao(),
        settingsDao = database.brainstormSettingsDao(),
        memberDao = database.memberDao(),
        auditLogDao = database.auditLogDao(),
        timelineService = timelineService
    )

    val payrollService = PayrollService(
        paymentStageDao = database.paymentStageDao(),
        paymentProofDao = database.paymentProofDao(),
        proofReviewDao = database.proofReviewDao(),
        distributionProposalDao = database.distributionProposalDao(),
        distributionAllocationDao = database.distributionAllocationDao(),
        distributionReviewDao = database.distributionReviewDao(),
        ownerTransferDao = database.ownerTransferDao(),
        payoutDao = database.payoutDao(),
        disputeDao = database.disputeDao(),
        ledgerDao = database.ledgerDao(),
        auditLogDao = database.financialAuditLogDao(),
        settingsDao = database.financialSettingsDao(),
        idCounterDao = database.payrollIdCounterDao(),
        memberDao = database.memberDao(),
        timelineService = timelineService
    )

    // Payroll & Revenue Bot State
    private val _allPaymentStages = MutableStateFlow<List<PaymentStageEntity>>(emptyList())
    val allPaymentStages: StateFlow<List<PaymentStageEntity>> = _allPaymentStages.asStateFlow()

    private val _selectedStageId = MutableStateFlow<String?>("demo_stage_002")
    val selectedStageId: StateFlow<String?> = _selectedStageId.asStateFlow()

    private val _selectedStageDetail = MutableStateFlow<FinancialStageDetail?>(null)
    val selectedStageDetail: StateFlow<FinancialStageDetail?> = _selectedStageDetail.asStateFlow()

    private val _payrollMetrics = MutableStateFlow(PayrollAdminDashboardMetrics())
    val payrollMetrics: StateFlow<PayrollAdminDashboardMetrics> = _payrollMetrics.asStateFlow()

    private val _payrollHealth = MutableStateFlow(PayrollHealthStatus())
    val payrollHealth: StateFlow<PayrollHealthStatus> = _payrollHealth.asStateFlow()

    private val _memberEarnings = MutableStateFlow<MemberEarningsHistory?>(null)
    val memberEarnings: StateFlow<MemberEarningsHistory?> = _memberEarnings.asStateFlow()

    private val _projectReconciliation = MutableStateFlow<ProjectReconciliationReport?>(null)
    val projectReconciliation: StateFlow<ProjectReconciliationReport?> = _projectReconciliation.asStateFlow()

    private val _allLedgerEntries = MutableStateFlow<List<FinancialLedgerEntity>>(emptyList())
    val allLedgerEntries: StateFlow<List<FinancialLedgerEntity>> = _allLedgerEntries.asStateFlow()

    private val _allFinancialAuditLogs = MutableStateFlow<List<FinancialAuditLogEntity>>(emptyList())
    val allFinancialAuditLogs: StateFlow<List<FinancialAuditLogEntity>> = _allFinancialAuditLogs.asStateFlow()

    private val _allDisputes = MutableStateFlow<List<FinancialDisputeEntity>>(emptyList())
    val allDisputes: StateFlow<List<FinancialDisputeEntity>> = _allDisputes.asStateFlow()

    // Brainstorm Bot State
    private val _dbBrainstorms = MutableStateFlow<List<BrainstormEntity>>(emptyList())
    val dbBrainstorms: StateFlow<List<BrainstormEntity>> = _dbBrainstorms.asStateFlow()

    private val _selectedBrainstormId = MutableStateFlow<String?>("B#001")
    val selectedBrainstormId: StateFlow<String?> = _selectedBrainstormId.asStateFlow()

    private val _selectedBrainstormDetail = MutableStateFlow<BrainstormDetailModel?>(null)
    val selectedBrainstormDetail: StateFlow<BrainstormDetailModel?> = _selectedBrainstormDetail.asStateFlow()

    private val _brainstormMetrics = MutableStateFlow(BrainstormAdminDashboardMetrics())
    val brainstormMetrics: StateFlow<BrainstormAdminDashboardMetrics> = _brainstormMetrics.asStateFlow()

    private val _brainstormHealth = MutableStateFlow(BrainstormHealthStatus())
    val brainstormHealth: StateFlow<BrainstormHealthStatus> = _brainstormHealth.asStateFlow()

    private val _brainstormReconcileReport = MutableStateFlow<BrainstormReconciliationReport?>(null)
    val brainstormReconcileReport: StateFlow<BrainstormReconciliationReport?> = _brainstormReconcileReport.asStateFlow()

    private val _brainstormSearchQuery = MutableStateFlow("")
    val brainstormSearchQuery: StateFlow<String> = _brainstormSearchQuery.asStateFlow()

    private val _brainstormFilterStatus = MutableStateFlow<String?>(null)
    val brainstormFilterStatus: StateFlow<String?> = _brainstormFilterStatus.asStateFlow()

    private val _brainstormFilterSkill = MutableStateFlow<String?>(null)
    val brainstormFilterSkill: StateFlow<String?> = _brainstormFilterSkill.asStateFlow()

    // Discord Simulation & Onboarding State
    private val _activePersona = MutableStateFlow(DiscordPersonaType.APPROVED_APPLICANT)
    val activePersona: StateFlow<DiscordPersonaType> = _activePersona.asStateFlow()

    private val _onboardingProgress = MutableStateFlow<OnboardingProgressState?>(null)
    val onboardingProgress: StateFlow<OnboardingProgressState?> = _onboardingProgress.asStateFlow()

    private val _allApplicationsList = MutableStateFlow<List<ApplicationEntity>>(emptyList())
    val allApplicationsList: StateFlow<List<ApplicationEntity>> = _allApplicationsList.asStateFlow()

    private val _onboardingDashboard = MutableStateFlow<OnboardingDashboardMetrics?>(null)
    val onboardingDashboard: StateFlow<OnboardingDashboardMetrics?> = _onboardingDashboard.asStateFlow()

    private val _memberCard = MutableStateFlow<MemberCardSummary?>(null)
    val memberCard: StateFlow<MemberCardSummary?> = _memberCard.asStateFlow()

    // Core Sample Data from Landing Page
    private val _brainstorms = MutableStateFlow(JoblessEmpireData.sampleBrainstorms)
    val brainstorms: StateFlow<List<BrainstormSession>> = _brainstorms.asStateFlow()

    private val _projects = MutableStateFlow(JoblessEmpireData.sampleProjects)
    val projects: StateFlow<List<OngoingProject>> = _projects.asStateFlow()

    private val _clientProjects = MutableStateFlow(JoblessEmpireData.sampleClientProjects)
    val clientProjects: StateFlow<List<ClientProject>> = _clientProjects.asStateFlow()

    private val _memberProfiles = MutableStateFlow(JoblessEmpireData.memberProfiles)
    val memberProfiles: StateFlow<Map<String, MemberProfile>> = _memberProfiles.asStateFlow()

    // Timeline Bot State
    private val _selectedMemberId = MutableStateFlow("JEMP#001")
    val selectedMemberId: StateFlow<String> = _selectedMemberId.asStateFlow()

    private val _allMembers = MutableStateFlow<List<MemberEntity>>(emptyList())
    val allMembers: StateFlow<List<MemberEntity>> = _allMembers.asStateFlow()

    private val _timelineHierarchy = MutableStateFlow<TimelineHierarchy?>(null)
    val timelineHierarchy: StateFlow<TimelineHierarchy?> = _timelineHierarchy.asStateFlow()

    private val _timelineStats = MutableStateFlow<TimelineStatsSummary?>(null)
    val timelineStats: StateFlow<TimelineStatsSummary?> = _timelineStats.asStateFlow()

    private val _viewMode = MutableStateFlow(TimelineViewMode.STANDARD)
    val viewMode: StateFlow<TimelineViewMode> = _viewMode.asStateFlow()

    private val _selectedYear = MutableStateFlow<Int?>(null)
    val selectedYear: StateFlow<Int?> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow<String?>(null)
    val selectedMonth: StateFlow<String?> = _selectedMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    private val _activePublicShare = MutableStateFlow<PublicShareDetails?>(null)
    val activePublicShare: StateFlow<PublicShareDetails?> = _activePublicShare.asStateFlow()

    private val _publicTimelinePreview = MutableStateFlow<PublicTimelineViewData?>(null)
    val publicTimelinePreview: StateFlow<PublicTimelineViewData?> = _publicTimelinePreview.asStateFlow()

    private val _exportDocument = MutableStateFlow<ExportDocument?>(null)
    val exportDocument: StateFlow<ExportDocument?> = _exportDocument.asStateFlow()

    private val _systemHealth = MutableStateFlow(SystemHealthReport())
    val systemHealth: StateFlow<SystemHealthReport> = _systemHealth.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntity>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntity>> = _auditLogs.asStateFlow()

    private val _botTerminalOutput = MutableStateFlow<String?>(null)
    val botTerminalOutput: StateFlow<String?> = _botTerminalOutput.asStateFlow()

    private val _applications = MutableStateFlow<List<ApplicationSubmission>>(emptyList())
    val applications: StateFlow<List<ApplicationSubmission>> = _applications.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _latestSubmittedApplication = MutableStateFlow<ApplicationSubmission?>(null)
    val latestSubmittedApplication: StateFlow<ApplicationSubmission?> = _latestSubmittedApplication.asStateFlow()

    private val _submissionErrorMessage = MutableStateFlow<String?>(null)
    val submissionErrorMessage: StateFlow<String?> = _submissionErrorMessage.asStateFlow()

    // Revenue Simulator State
    private val _simulatedBudget = MutableStateFlow(5000)
    val simulatedBudget: StateFlow<Int> = _simulatedBudget.asStateFlow()

    private val _isCharityModelActive = MutableStateFlow(false)
    val isCharityModelActive: StateFlow<Boolean> = _isCharityModelActive.asStateFlow()

    init {
        scope.launch {
            // Seed initial demo data in Room if database is fresh
            timelineService.seedDemoData()
            onboardingService.seedDemoOnboardingData()
            brainstormService.seedDemoData()
            payrollService.seedDemoData()
            refreshTimelineData()
            refreshOnboardingData()
            refreshBrainstormData()
            refreshPayrollData()
            refreshHealthAndLogs()
            selectDiscordPersona(DiscordPersonaType.APPROVED_APPLICANT)
        }
    }

    fun selectDiscordPersona(persona: DiscordPersonaType) {
        _activePersona.value = persona
        scope.launch {
            val progress = onboardingService.handleDiscordJoin(persona.discordUserId, persona.discordUsername)
            _onboardingProgress.value = progress
            if (progress.assignedJempId != null) {
                _selectedMemberId.value = progress.assignedJempId
                val card = onboardingService.getMemberCard(progress.assignedJempId)
                _memberCard.value = card
                refreshTimelineData()
            }
        }
    }

    suspend fun refreshOnboardingData() {
        val applications = database.applicationDao().getAllApplicationsOnce()
        _allApplicationsList.value = applications
        val metrics = onboardingService.getDashboardData()
        _onboardingDashboard.value = metrics
    }

    fun verifyEmail(email: String, onResult: (EmailVerificationResult) -> Unit = {}) {
        val current = _activePersona.value
        scope.launch {
            val res = onboardingService.verifyApplicationEmail(current.discordUserId, email)
            if (res.isSuccess) {
                val updatedProgress = onboardingService.handleDiscordJoin(current.discordUserId, current.discordUsername)
                _onboardingProgress.value = updatedProgress
            }
            refreshOnboardingData()
            onResult(res)
        }
    }

    fun confirmNameStep(useSubmittedName: Boolean, correctionNote: String? = null, onResult: (Boolean) -> Unit = {}) {
        val current = _activePersona.value
        scope.launch {
            val res = onboardingService.completeStep1ConfirmName(current.discordUserId, useSubmittedName, correctionNote)
            if (res.isSuccess) {
                _onboardingProgress.value = res.getOrNull()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun selectTimezoneStep(timezone: String, onResult: (Boolean) -> Unit = {}) {
        val current = _activePersona.value
        scope.launch {
            val res = onboardingService.completeStep2SelectTimezone(current.discordUserId, timezone)
            if (res.isSuccess) {
                _onboardingProgress.value = res.getOrNull()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun acceptRulesStep(agree: Boolean, onResult: (Boolean) -> Unit = {}) {
        val current = _activePersona.value
        scope.launch {
            val res = onboardingService.completeStep3AcceptRules(current.discordUserId, agree)
            if (res.isSuccess) {
                _onboardingProgress.value = res.getOrNull()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun acknowledgeSafetyStep(understand: Boolean, onResult: (Boolean) -> Unit = {}) {
        val current = _activePersona.value
        scope.launch {
            val res = onboardingService.completeStep4AcknowledgeSafety(current.discordUserId, understand)
            if (res.isSuccess) {
                _onboardingProgress.value = res.getOrNull()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun activateMembershipStep(onResult: (Result<MemberCardSummary>) -> Unit = {}) {
        val current = _activePersona.value
        scope.launch {
            val res = onboardingService.completeOnboardingAndActivate(current.discordUserId)
            if (res.isSuccess) {
                val summary = res.getOrThrow()
                _memberCard.value = summary
                _selectedMemberId.value = summary.jempId
                val updatedProgress = onboardingService.handleDiscordJoin(current.discordUserId, current.discordUsername)
                _onboardingProgress.value = updatedProgress
                refreshTimelineData()
                refreshOnboardingData()
            }
            onResult(res)
        }
    }

    fun adminApproveApplication(appIdOrEmail: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.approveApplication(appIdOrEmail, "ADMIN_CONSOLE")
            val msg = if (res.isSuccess) {
                val app = res.getOrThrow()
                "✅ Approved ${app.applicationId} (${app.fullName}). Generated invite code: `${app.inviteCode}`. Normalized email whitelisted."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            onComplete(msg)
        }
    }

    fun adminRejectApplication(appIdOrEmail: String, reason: String? = null, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.rejectApplication(appIdOrEmail, "ADMIN_CONSOLE", reason)
            val msg = if (res.isSuccess) {
                val app = res.getOrThrow()
                "🔴 Rejected ${app.applicationId} (${app.fullName}). Application review updated."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            onComplete(msg)
        }
    }

    fun adminWaitlistApplication(appIdOrEmail: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.waitlistApplication(appIdOrEmail, "ADMIN_CONSOLE")
            val msg = if (res.isSuccess) {
                val app = res.getOrThrow()
                "🟡 Waitlisted ${app.applicationId} (${app.fullName})."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            onComplete(msg)
        }
    }

    fun adminWhitelistEmail(email: String, applicantName: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.whitelistEmailDirectly(email, applicantName, "ADMIN_CONSOLE")
            val msg = if (res.isSuccess) {
                val app = res.getOrThrow()
                "✅ Whitelisted `$email` for **${app.fullName}** (${app.applicationId}). Invite code: `${app.inviteCode}`"
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            onComplete(msg)
        }
    }

    fun adminRevokeApplication(appIdOrEmail: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.revokeApplication(appIdOrEmail, "ADMIN_CONSOLE")
            val msg = if (res.isSuccess) {
                val app = res.getOrThrow()
                "⛔ Revoked approval for ${app.applicationId}."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            onComplete(msg)
        }
    }

    fun adminSuspendMember(discordUserIdOrJempId: String, reason: String? = null, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.suspendMember(discordUserIdOrJempId, "ADMIN_CONSOLE", reason)
            val msg = if (res.isSuccess) {
                "⛔ Suspended member $discordUserIdOrJempId. Access to normal channels locked."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            selectDiscordPersona(_activePersona.value)
            onComplete(msg)
        }
    }

    fun adminRestoreMember(discordUserIdOrJempId: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.restoreMember(discordUserIdOrJempId, "ADMIN_CONSOLE")
            val msg = if (res.isSuccess) {
                "🟢 Restored member $discordUserIdOrJempId to ACTIVE status."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            selectDiscordPersona(_activePersona.value)
            onComplete(msg)
        }
    }

    fun adminTerminateMember(discordUserIdOrJempId: String, reason: String? = null, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.terminateMember(discordUserIdOrJempId, "ADMIN_CONSOLE", reason)
            val msg = if (res.isSuccess) {
                "🔴 Terminated member $discordUserIdOrJempId permanently. Anti-scam zero tolerance applied."
            } else {
                "⚠️ Error: ${res.exceptionOrNull()?.message}"
            }
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            selectDiscordPersona(_activePersona.value)
            onComplete(msg)
        }
    }

    fun adminReconcileMember(discordUserId: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val res = onboardingService.reconcileMember(discordUserId, "ADMIN_CONSOLE")
            val msg = res.getOrDefault("Reconciliation completed.")
            _botTerminalOutput.value = msg
            refreshOnboardingData()
            onComplete(msg)
        }
    }

    fun selectMember(memberId: String) {
        _selectedMemberId.value = memberId
        _selectedYear.value = null
        _selectedMonth.value = null
        _selectedDate.value = null
        scope.launch {
            refreshTimelineData()
        }
    }

    fun setViewMode(mode: TimelineViewMode) {
        _viewMode.value = mode
    }

    fun selectYear(year: Int?) {
        _selectedYear.value = year
        _selectedMonth.value = null
        _selectedDate.value = null
    }

    fun selectMonth(month: String?) {
        _selectedMonth.value = month
        _selectedDate.value = null
    }

    fun selectDate(dateKey: String?) {
        _selectedDate.value = dateKey
    }

    fun resetHierarchyBreadcrumbs() {
        _selectedYear.value = null
        _selectedMonth.value = null
        _selectedDate.value = null
    }

    suspend fun refreshTimelineData() {
        val memberId = _selectedMemberId.value
        val hierarchy = timelineService.getTimelineHierarchy(memberId)
        val stats = timelineService.getMemberStats(memberId)
        _timelineHierarchy.value = hierarchy
        _timelineStats.value = stats
    }

    suspend fun refreshHealthAndLogs() {
        _systemHealth.value = timelineService.getSystemHealth()
    }

    fun executeSlashCommand(commandText: String, onComplete: (String) -> Unit = {}) {
        scope.launch {
            val cmd = commandText.trim()
            val memberId = _selectedMemberId.value
            val currentPersona = _activePersona.value

            when {
                cmd == "/timeline" || cmd.startsWith("/timeline ") && !cmd.startsWith("/timeline-admin") -> {
                    handleMemberSlashCommand(cmd, memberId, onComplete)
                }
                cmd.startsWith("/timeline-admin") -> {
                    handleAdminSlashCommand(cmd, memberId, onComplete)
                }
                cmd == "/membership" || cmd.startsWith("/membership ") -> {
                    handleMembershipSlashCommand(cmd, currentPersona, onComplete)
                }
                cmd.startsWith("/onboarding-admin") -> {
                    handleOnboardingAdminSlashCommand(cmd, onComplete)
                }
                cmd.startsWith("/brainstorm") -> {
                    executeBrainstormBotCommand(cmd)
                    onComplete(_botTerminalOutput.value ?: "Brainstorm command executed.")
                }
                cmd.startsWith("/payroll") || cmd.startsWith("/payroll-admin") -> {
                    val out = payrollService.executePayrollSlashCommand(cmd, memberId)
                    _botTerminalOutput.value = out
                    refreshPayrollData()
                    onComplete(out)
                }
                else -> {
                    val result = "⚠️ Unknown command `$cmd`. Use `/membership help`, `/timeline help`, `/brainstorm help`, or `/payroll help` to view all commands."
                    _botTerminalOutput.value = result
                    onComplete(result)
                }
            }
            refreshTimelineData()
            refreshOnboardingData()
            refreshHealthAndLogs()
        }
    }

    private suspend fun handleMembershipSlashCommand(
        cmd: String,
        persona: DiscordPersonaType,
        onComplete: (String) -> Unit
    ) {
        when {
            cmd == "/membership" || cmd == "/membership status" -> {
                val card = onboardingService.getMemberCard(persona.discordUserId)
                if (card != null) {
                    val result = "🪪 **JOBLESS EMPIRE IDENTITY CARD**\n" +
                            "• Member ID: **${card.jempId}**\n" +
                            "• Status: **${card.status}**\n" +
                            "• Name: ${card.displayName}\n" +
                            "• Roles: ${card.assignedRoles.joinToString(", ")}\n" +
                            "• Timezone: ${card.timezone}\n" +
                            "• Member Since: ${card.joinedDateFormatted}\n" +
                            "• Verified Timeline Records: ${card.totalTimelineRecords}\n" +
                            "• Application ID: ${card.applicationId}"
                    _botTerminalOutput.value = result
                    onComplete(result)
                } else {
                    val session = database.onboardingSessionDao().getSessionByDiscordUserId(persona.discordUserId)
                    val result = if (session != null) {
                        "⏳ **Membership In Progress**\n• State: ${session.membershipState}\n• Step: ${session.currentStep}/5\n• Roles: ${session.assignedRolesJson}\nUse `/membership verify` or check #welcome to complete onboarding."
                    } else {
                        "🔒 **Unverified Joiner**\nPlease start in `#welcome` and verify your approved application email."
                    }
                    _botTerminalOutput.value = result
                    onComplete(result)
                }
            }
            cmd == "/membership verify" -> {
                val result = "📩 **Verification Portal**\nEnter your email in the `#welcome` channel modal to verify your application."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd.startsWith("/membership timezone") -> {
                val tzArg = cmd.removePrefix("/membership timezone").trim()
                if (tzArg.isBlank()) {
                    val currentTz = onboardingService.getMemberTimezone(persona.discordUserId)
                    val result = "🌐 **Current Timezone:** `$currentTz`\nTo update, run: `/membership timezone Asia/Kolkata` or `America/New_York`"
                    _botTerminalOutput.value = result
                    onComplete(result)
                } else {
                    val res = onboardingService.updateMemberTimezone(persona.discordUserId, tzArg)
                    val msg = if (res.isSuccess) {
                        "✅ Updated member timezone to `$tzArg`."
                    } else {
                        "⚠️ Failed to update timezone: ${res.exceptionOrNull()?.message}"
                    }
                    _botTerminalOutput.value = msg
                    onComplete(msg)
                }
            }
            cmd == "/membership help" -> {
                val result = "ℹ️ **Jobless Empire Membership Commands**\n" +
                        "`/membership` — View your private JEMP ID card\n" +
                        "`/membership verify` — Begin/resume email verification\n" +
                        "`/membership timezone [IANA]` — Set or view your display timezone\n" +
                        "`/membership status` — Quick check of active status\n" +
                        "`/timeline` — View your immutable proof-of-work timeline"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            else -> {
                val result = "Executed `$cmd`."
                _botTerminalOutput.value = result
                onComplete(result)
            }
        }
    }

    private suspend fun handleOnboardingAdminSlashCommand(
        cmd: String,
        onComplete: (String) -> Unit
    ) {
        when {
            cmd == "/onboarding-admin" || cmd == "/onboarding-admin dashboard" -> {
                val data = onboardingService.getDashboardData()
                _onboardingDashboard.value = data
                val result = "📊 **ONBOARDING & MEMBERSHIP DASHBOARD**\n" +
                        "• Pending Applications: ${data.pendingApplicationsCount}\n" +
                        "• Approved & Invited: ${data.approvedApplicationsCount}\n" +
                        "• Waitlisted: ${data.waitlistedApplicationsCount}\n" +
                        "• Rejected: ${data.rejectedApplicationsCount}\n" +
                        "• Active Members: ${data.activeMembersCount}\n" +
                        "• Onboarding In-Flight: ${data.onboardingMembersCount}\n" +
                        "• Suspended: ${data.suspendedMembersCount}\n" +
                        "• Terminated: ${data.terminatedMembersCount}"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd.startsWith("/onboarding-admin approve") -> {
                val target = cmd.removePrefix("/onboarding-admin approve").trim().removePrefix("email:").trim()
                adminApproveApplication(target, onComplete)
            }
            cmd.startsWith("/onboarding-admin reject") -> {
                val target = cmd.removePrefix("/onboarding-admin reject").trim().removePrefix("email:").trim()
                adminRejectApplication(target, "Administrative review", onComplete)
            }
            cmd.startsWith("/onboarding-admin waitlist") -> {
                val target = cmd.removePrefix("/onboarding-admin waitlist").trim().removePrefix("email:").trim()
                adminWaitlistApplication(target, onComplete)
            }
            cmd.startsWith("/onboarding-admin whitelist") -> {
                val target = cmd.removePrefix("/onboarding-admin whitelist").trim().removePrefix("email:").trim()
                adminWhitelistEmail(target, "Whitelisted Candidate", onComplete)
            }
            cmd.startsWith("/onboarding-admin revoke") -> {
                val target = cmd.removePrefix("/onboarding-admin revoke").trim().removePrefix("email:").trim()
                adminRevokeApplication(target, onComplete)
            }
            cmd.startsWith("/onboarding-admin suspend") -> {
                val target = cmd.removePrefix("/onboarding-admin suspend").trim().removePrefix("@").trim()
                adminSuspendMember(target, "Administrative action", onComplete)
            }
            cmd.startsWith("/onboarding-admin restore") -> {
                val target = cmd.removePrefix("/onboarding-admin restore").trim().removePrefix("@").trim()
                adminRestoreMember(target, onComplete)
            }
            cmd.startsWith("/onboarding-admin terminate") -> {
                val target = cmd.removePrefix("/onboarding-admin terminate").trim().removePrefix("@").trim()
                adminTerminateMember(target, "Violation of zero-tolerance policy", onComplete)
            }
            cmd.startsWith("/onboarding-admin reconcile") -> {
                val target = cmd.removePrefix("/onboarding-admin reconcile").trim().removePrefix("@").trim()
                adminReconcileMember(target, onComplete)
            }
            cmd == "/onboarding-admin health" -> {
                val health = timelineService.getSystemHealth()
                _systemHealth.value = health
                val result = "🟢 **Onboarding Gateway Health**\n• Gateway: ${health.discordGatewayStatus}\n• Database: ${health.databaseStatus}\n• Event Processor: ${health.eventProcessorStatus}\n• Role Dispatcher: ACTIVE"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/onboarding-admin seed-demo" -> {
                val res = onboardingService.seedDemoOnboardingData()
                val msg = res.getOrDefault("Seeded onboarding demo applications.")
                _botTerminalOutput.value = msg
                refreshOnboardingData()
                onComplete(msg)
            }
            cmd == "/onboarding-admin clear-demo" -> {
                onboardingService.clearDemoOnboardingData()
                val msg = "🧹 Cleared onboarding demo applications."
                _botTerminalOutput.value = msg
                refreshOnboardingData()
                onComplete(msg)
            }
            else -> {
                val result = "Executed admin command: `$cmd`."
                _botTerminalOutput.value = result
                onComplete(result)
            }
        }
    }

    private suspend fun handleMemberSlashCommand(cmd: String, memberId: String, onComplete: (String) -> Unit) {
        when {
            cmd == "/timeline" -> {
                resetHierarchyBreadcrumbs()
                val result = "✅ Displaying timeline for **$memberId** in ephemeral view."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline view compact" -> {
                _viewMode.value = TimelineViewMode.COMPACT
                val result = "✅ View mode set to **COMPACT**."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline view standard" -> {
                _viewMode.value = TimelineViewMode.STANDARD
                val result = "✅ View mode set to **STANDARD**."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline view detailed" -> {
                _viewMode.value = TimelineViewMode.DETAILED
                val result = "✅ View mode set to **DETAILED**."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline stats" -> {
                val stats = timelineService.getMemberStats(memberId)
                _timelineStats.value = stats
                val result = "📊 **$memberId Stats Summary**\n• Records: ${stats.totalEntries}\n• Official: ${stats.officialRecordsCount}\n• Projects: ${stats.projectsCount}\n• Brainstorms: ${stats.brainstormsCount}\n• Milestones: ${stats.personalMilestonesCount}"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline share" -> {
                val shareRes = timelineService.createPublicShare(memberId)
                if (shareRes.isSuccess) {
                    val share = shareRes.getOrNull()
                    _activePublicShare.value = share
                    val result = "🔗 **24-Hour Public Share Link Created!**\nURL: ${share?.shareUrl}\nExpires in: 24 hours (Auto-expiring cryptographic token)"
                    _botTerminalOutput.value = result
                    onComplete(result)
                } else {
                    val err = "⚠️ Failed to create share: ${shareRes.exceptionOrNull()?.message}"
                    _botTerminalOutput.value = err
                    onComplete(err)
                }
            }
            cmd == "/timeline unshare" -> {
                timelineService.revokePublicShare(memberId)
                _activePublicShare.value = null
                _publicTimelinePreview.value = null
                val result = "🔒 **Public timeline share link revoked immediately.**"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd.startsWith("/timeline export") -> {
                val isFull = cmd.contains("full", ignoreCase = true)
                val format = when {
                    cmd.contains("json", ignoreCase = true) -> ExportFormat.JSON
                    cmd.contains("doc", ignoreCase = true) -> ExportFormat.STRUCTURED_DOC
                    else -> ExportFormat.MARKDOWN
                }
                val exportType = if (isFull) ExportType.FULL else ExportType.PROFESSIONAL
                val doc = timelineService.exportTimeline(memberId, exportType, format)
                _exportDocument.value = doc
                val result = "📥 **Timeline Export Generated (${format.name} · ${exportType.name})**"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline help" -> {
                val result = "ℹ️ **Jobless Empire Timeline Bot Commands**\n`/timeline` — View interactive timeline\n`/timeline view [compact|standard|detailed]` — Switch density\n`/timeline add` — Add member milestone\n`/timeline edit` — Edit personal entry\n`/timeline delete` — Delete personal entry\n`/timeline search [query]` — Search timeline\n`/timeline stats` — View activity summary\n`/timeline share` — 24h public link\n`/timeline unshare` — Revoke public link\n`/timeline export` — Export proof document\n`/timeline settings` — Adjust preferences"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            else -> {
                val result = "Executed `$cmd` for **$memberId**."
                _botTerminalOutput.value = result
                onComplete(result)
            }
        }
    }

    private suspend fun handleAdminSlashCommand(cmd: String, memberId: String, onComplete: (String) -> Unit) {
        when {
            cmd == "/timeline-admin seed-demo" -> {
                val res = timelineService.seedDemoData()
                val result = res.getOrDefault("Demo data seeded.")
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline-admin clear-demo" -> {
                timelineService.clearDemoData()
                val result = "🧹 Demo records removed cleanly without affecting production data."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline-admin health" -> {
                val health = timelineService.getSystemHealth()
                _systemHealth.value = health
                val result = "🟢 **System Health: Operational**\n• Gateway: ${health.discordGatewayStatus}\n• Database: ${health.databaseStatus}\n• Event Processor: ${health.eventProcessorStatus}\n• Public Shares: ${health.publicShareServiceStatus}"
                _botTerminalOutput.value = result
                onComplete(result)
            }
            cmd == "/timeline-admin audit" -> {
                val result = "📜 Fetched audit logs. Inspect the Admin Audit Hub below."
                _botTerminalOutput.value = result
                onComplete(result)
            }
            else -> {
                val result = "Executed admin command: `$cmd`."
                _botTerminalOutput.value = result
                onComplete(result)
            }
        }
    }

    // Ingest event simulation for external bots
    suspend fun simulateExternalBotEvent(
        source: String,
        eventType: String,
        title: String,
        bullets: List<String>,
        projectId: String?,
        sourceEventId: String
    ): Result<TimelineEntryEntity> {
        val memberId = _selectedMemberId.value
        val payload = TimelineEventPayload(
            memberId = memberId,
            eventType = eventType,
            title = title,
            bullets = bullets,
            projectId = projectId,
            source = source,
            sourceEventId = sourceEventId,
            immutable = true,
            createdBy = source
        )
        val res = timelineService.recordTimelineEvent(payload)
        refreshTimelineData()
        refreshHealthAndLogs()
        return res
    }

    fun setSimulatedBudget(budget: Int) {
        _simulatedBudget.value = budget.coerceIn(500, 50000)
    }

    fun toggleCharityModel(active: Boolean) {
        _isCharityModelActive.value = active
    }

    suspend fun submitApplication(
        fullName: String,
        email: String,
        resumeFileName: String,
        resumeFileSize: String,
        isCurrentlyUnemployed: Boolean,
        consentChecked: Boolean
    ): Result<ApplicationSubmission> {
        _isSubmitting.value = true
        _submissionErrorMessage.value = null

        val res = onboardingService.submitApplication(
            fullName = fullName,
            email = email,
            resumeFileName = resumeFileName,
            resumeFileSize = resumeFileSize,
            isCurrentlyUnemployed = isCurrentlyUnemployed,
            consentGiven = consentChecked
        )

        if (res.isFailure) {
            _isSubmitting.value = false
            val error = res.exceptionOrNull()?.message ?: "Application failed to submit."
            _submissionErrorMessage.value = error
            return Result.failure(IllegalArgumentException(error))
        }

        val entity = res.getOrThrow()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm z", Locale.US)
        val timestamp = dateFormat.format(Date(entity.submittedAtUtc))

        val submission = ApplicationSubmission(
            id = entity.applicationId,
            fullName = entity.fullName,
            email = entity.email,
            resumeFileName = entity.resumeFileName,
            resumeFileSize = entity.resumeFileSize,
            isCurrentlyUnemployed = entity.isCurrentlyUnemployed,
            consentChecked = entity.consentGiven,
            timestamp = timestamp,
            assignedJempId = entity.applicationId,
            reviewStatus = ApplicationReviewStatus.PENDING
        )

        _applications.update { listOf(submission) + it }
        _latestSubmittedApplication.value = submission
        _isSubmitting.value = false

        refreshOnboardingData()
        refreshTimelineData()
        return Result.success(submission)
    }

    fun dismissLatestSubmission() {
        _latestSubmittedApplication.value = null
    }

    fun toggle24hShare(memberId: String) {
        scope.launch {
            val currentShare = _activePublicShare.value
            if (currentShare != null && !currentShare.isExpired && !currentShare.isRevoked) {
                timelineService.revokePublicShare(memberId)
                _activePublicShare.value = null
            } else {
                val res = timelineService.createPublicShare(memberId)
                _activePublicShare.value = res.getOrNull()
            }
            refreshTimelineData()
        }
    }

    fun addCustomTimelineMilestone(
        memberId: String,
        title: String,
        bullets: List<String>,
        relatedProject: String? = null
    ) {
        scope.launch {
            timelineService.createMemberEntry(
                memberId = memberId,
                title = title,
                bullets = bullets,
                projectId = relatedProject,
                daysAgo = 0
            )
            refreshTimelineData()
        }
    }

    fun addCustomTimelineMilestone(
        memberId: String,
        title: String,
        description: String,
        relatedProject: String? = null
    ) {
        addCustomTimelineMilestone(
            memberId = memberId,
            title = title,
            bullets = listOf(description),
            relatedProject = relatedProject
        )
    }

    fun setBrainstormSearchQuery(query: String) {
        _brainstormSearchQuery.value = query
    }

    fun setBrainstormFilterStatus(status: String?) {
        _brainstormFilterStatus.value = status
    }

    fun setBrainstormFilterSkill(skill: String?) {
        _brainstormFilterSkill.value = skill
    }

    fun selectBrainstorm(id: String?) {
        _selectedBrainstormId.value = id
        scope.launch {
            refreshBrainstormDetail(id)
        }
    }

    suspend fun refreshBrainstormData() {
        val list = database.brainstormDao().getAllBrainstormsOnce()
        _dbBrainstorms.value = list
        val currentSelected = _selectedBrainstormId.value ?: list.firstOrNull()?.id
        if (currentSelected != null) {
            refreshBrainstormDetail(currentSelected)
        }
        calculateBrainstormMetrics(list)
        _brainstormHealth.value = brainstormService.getHealthReport()
    }

    private suspend fun calculateBrainstormMetrics(list: List<BrainstormEntity>) {
        val total = list.size
        val active = list.count { it.status == "BRAINSTORMING" }
        val cooking = list.count { it.status == "COOKING" }
        val paused = list.count { it.status == "PAUSED" }
        val cancelled = list.count { it.status == "CANCELLED" }
        val alive = list.count { it.status == "IT_S_ALIVE" }
        val pendingApps = database.brainstormApplicationDao().countAllPendingApplications()
        val now = System.currentTimeMillis()
        val act24h = database.brainstormActivityDao().countActivitySince(now - TimeUnit.HOURS.toMillis(24))
        val act7d = database.brainstormActivityDao().countActivitySince(now - TimeUnit.DAYS.toMillis(7))
        val inactiveWarn = list.count { (now - it.lastMeaningfulActivityAtUtc) > TimeUnit.DAYS.toMillis(14) && it.status != "IT_S_ALIVE" && it.status != "CANCELLED" }

        _brainstormMetrics.value = BrainstormAdminDashboardMetrics(
            totalBrainstorms = total,
            activeCount = active,
            cookingCount = cooking,
            pausedCount = paused,
            cancelledCount = cancelled,
            aliveCount = alive,
            pendingApplicationsCount = pendingApps,
            activeContributorsCount = list.sumOf { database.brainstormContributorDao().countActiveContributors(it.id) },
            activityLast24h = act24h,
            activityLast7d = act7d,
            inactiveWarningsCount = inactiveWarn,
            conversionsCount = alive
        )
    }

    private suspend fun refreshBrainstormDetail(brainstormId: String?) {
        if (brainstormId == null) {
            _selectedBrainstormDetail.value = null
            return
        }

        val entity = database.brainstormDao().getBrainstormById(brainstormId)
        if (entity == null) {
            _selectedBrainstormDetail.value = null
            return
        }

        val rawSkills = try {
            val arr = org.json.JSONArray(entity.desiredSkillsJson)
            List(arr.length()) { arr.getString(it) }
        } catch (e: Exception) { emptyList<String>() }

        val rawTags = try {
            val arr = org.json.JSONArray(entity.tagsJson)
            List(arr.length()) { arr.getString(it) }
        } catch (e: Exception) { emptyList<String>() }

        val rawContributors = database.brainstormContributorDao().getAllContributorsForBrainstormOnce(brainstormId)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val contributorsList = rawContributors.map {
            BrainstormContributorItem(
                id = it.id,
                brainstormId = it.brainstormId,
                memberId = it.memberId,
                displayName = it.displayName,
                role = try { ContributorRole.valueOf(it.role) } catch (e: Exception) { ContributorRole.CONTRIBUTOR },
                status = it.status,
                joinedDateFormatted = sdf.format(Date(it.joinedAtUtc)),
                isDemo = it.isDemo
            )
        }

        val rawApplications = database.brainstormApplicationDao().getApplicationById(brainstormId)
        // Load all applications for this brainstorm
        val allApps = database.brainstormApplicationDao().getApplicationById(brainstormId)
        // Since getApplicationsForBrainstorm returns a Flow, let's query via DAO query or collector
        val applicationList = mutableListOf<BrainstormApplicationItem>()
        // Query applications directly
        val rawAppsList = mutableListOf<BrainstormApplicationEntity>()
        // Let's get applications from database query
        val appEntities = try {
            // query once
            val raw = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_applications WHERE brainstormId = ? ORDER BY createdAtUtc DESC",
                arrayOf(brainstormId)
            )
            val items = mutableListOf<BrainstormApplicationEntity>()
            while (raw.moveToNext()) {
                val id = raw.getString(raw.getColumnIndexOrThrow("id"))
                val bsId = raw.getString(raw.getColumnIndexOrThrow("brainstormId"))
                val mId = raw.getString(raw.getColumnIndexOrThrow("memberId"))
                val dId = raw.getString(raw.getColumnIndexOrThrow("discordUserId"))
                val dName = raw.getString(raw.getColumnIndexOrThrow("displayName"))
                val reason = raw.getString(raw.getColumnIndexOrThrow("reason"))
                val skills = raw.getString(raw.getColumnIndexOrThrow("skills"))
                val exp = raw.getString(raw.getColumnIndexOrThrow("experience"))
                val avail = raw.getString(raw.getColumnIndexOrThrow("availability"))
                val port = raw.getString(raw.getColumnIndexOrThrow("portfolio"))
                val status = raw.getString(raw.getColumnIndexOrThrow("status"))
                val created = raw.getLong(raw.getColumnIndexOrThrow("createdAtUtc"))
                val revAt = if (raw.isNull(raw.getColumnIndexOrThrow("reviewedAtUtc"))) null else raw.getLong(raw.getColumnIndexOrThrow("reviewedAtUtc"))
                val revBy = if (raw.isNull(raw.getColumnIndexOrThrow("reviewedBy"))) null else raw.getString(raw.getColumnIndexOrThrow("reviewedBy"))
                val decReason = if (raw.isNull(raw.getColumnIndexOrThrow("decisionReason"))) null else raw.getString(raw.getColumnIndexOrThrow("decisionReason"))
                val isDemo = raw.getInt(raw.getColumnIndexOrThrow("isDemo")) == 1
                items.add(BrainstormApplicationEntity(id, bsId, mId, dId, dName, reason, skills, exp, avail, port, status, created, revAt, revBy, decReason, isDemo))
            }
            raw.close()
            items
        } catch (e: Exception) {
            emptyList<BrainstormApplicationEntity>()
        }

        val applicationsDomain = appEntities.map {
            BrainstormApplicationItem(
                id = it.id,
                brainstormId = it.brainstormId,
                memberId = it.memberId,
                displayName = it.displayName,
                reason = it.reason,
                skills = it.skills,
                experience = it.experience,
                availability = it.availability,
                portfolio = it.portfolio,
                status = it.status,
                createdDateFormatted = sdf.format(Date(it.createdAtUtc)),
                decisionReason = it.decisionReason,
                isDemo = it.isDemo
            )
        }

        // Decisions
        val decisionsDomain = mutableListOf<BrainstormDecisionItem>()
        try {
            val cur = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_decisions WHERE brainstormId = ? ORDER BY decisionNumber ASC",
                arrayOf(brainstormId)
            )
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndexOrThrow("id"))
                val num = cur.getInt(cur.getColumnIndexOrThrow("decisionNumber"))
                val text = cur.getString(cur.getColumnIndexOrThrow("decisionText"))
                val reason = cur.getString(cur.getColumnIndexOrThrow("reason"))
                val mId = cur.getString(cur.getColumnIndexOrThrow("madeByMemberId"))
                val mName = cur.getString(cur.getColumnIndexOrThrow("madeByDisplayName"))
                val ts = cur.getLong(cur.getColumnIndexOrThrow("timestampUtc"))
                decisionsDomain.add(BrainstormDecisionItem(id, brainstormId, num, text, reason, mId, mName, sdf.format(Date(ts))))
            }
            cur.close()
        } catch (e: Exception) {}

        // Milestones
        val milestonesDomain = mutableListOf<BrainstormMilestoneItem>()
        try {
            val cur = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_milestones WHERE brainstormId = ? ORDER BY createdAtUtc ASC",
                arrayOf(brainstormId)
            )
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndexOrThrow("id"))
                val title = cur.getString(cur.getColumnIndexOrThrow("title"))
                val desc = cur.getString(cur.getColumnIndexOrThrow("description"))
                val createdBy = cur.getString(cur.getColumnIndexOrThrow("createdByMemberId"))
                val isComp = cur.getInt(cur.getColumnIndexOrThrow("isCompleted")) == 1
                val compAt = if (cur.isNull(cur.getColumnIndexOrThrow("completedAtUtc"))) null else cur.getLong(cur.getColumnIndexOrThrow("completedAtUtc"))
                val compBy = if (cur.isNull(cur.getColumnIndexOrThrow("completedByMemberId"))) null else cur.getString(cur.getColumnIndexOrThrow("completedByMemberId"))
                milestonesDomain.add(BrainstormMilestoneItem(id, brainstormId, title, desc, createdBy, isComp, compAt?.let { sdf.format(Date(it)) }, compBy))
            }
            cur.close()
        } catch (e: Exception) {}

        // Tasks
        val tasksDomain = mutableListOf<BrainstormTaskItem>()
        try {
            val cur = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_tasks WHERE brainstormId = ? ORDER BY createdAtUtc DESC",
                arrayOf(brainstormId)
            )
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndexOrThrow("id"))
                val title = cur.getString(cur.getColumnIndexOrThrow("title"))
                val desc = cur.getString(cur.getColumnIndexOrThrow("description"))
                val aId = if (cur.isNull(cur.getColumnIndexOrThrow("assigneeMemberId"))) null else cur.getString(cur.getColumnIndexOrThrow("assigneeMemberId"))
                val aName = if (cur.isNull(cur.getColumnIndexOrThrow("assigneeDisplayName"))) null else cur.getString(cur.getColumnIndexOrThrow("assigneeDisplayName"))
                val priority = cur.getString(cur.getColumnIndexOrThrow("priority"))
                val status = cur.getString(cur.getColumnIndexOrThrow("status"))
                val dueDate = if (cur.isNull(cur.getColumnIndexOrThrow("dueDate"))) null else cur.getString(cur.getColumnIndexOrThrow("dueDate"))
                tasksDomain.add(BrainstormTaskItem(id, brainstormId, title, desc, aId, aName, priority, status, dueDate))
            }
            cur.close()
        } catch (e: Exception) {}

        // Resources
        val resourcesDomain = mutableListOf<BrainstormResourceItem>()
        try {
            val cur = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_resources WHERE brainstormId = ? ORDER BY createdAtUtc DESC",
                arrayOf(brainstormId)
            )
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndexOrThrow("id"))
                val name = cur.getString(cur.getColumnIndexOrThrow("name"))
                val url = cur.getString(cur.getColumnIndexOrThrow("url"))
                val desc = cur.getString(cur.getColumnIndexOrThrow("description"))
                val cat = cur.getString(cur.getColumnIndexOrThrow("category"))
                val isPriv = cur.getInt(cur.getColumnIndexOrThrow("isPrivate")) == 1
                val addedBy = cur.getString(cur.getColumnIndexOrThrow("addedByMemberId"))
                resourcesDomain.add(BrainstormResourceItem(id, brainstormId, name, url, desc, cat, isPriv, addedBy))
            }
            cur.close()
        } catch (e: Exception) {}

        // Polls
        val pollsDomain = mutableListOf<BrainstormPollItem>()
        val currentUserId = _selectedMemberId.value
        try {
            val cur = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_polls WHERE brainstormId = ? ORDER BY createdAtUtc DESC",
                arrayOf(brainstormId)
            )
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndexOrThrow("id"))
                val q = cur.getString(cur.getColumnIndexOrThrow("question"))
                val optJson = cur.getString(cur.getColumnIndexOrThrow("optionsJson"))
                val votesJson = cur.getString(cur.getColumnIndexOrThrow("votesJson"))
                val isClosed = cur.getInt(cur.getColumnIndexOrThrow("isClosed")) == 1
                val promId = if (cur.isNull(cur.getColumnIndexOrThrow("promotedToDecisionId"))) null else cur.getString(cur.getColumnIndexOrThrow("promotedToDecisionId"))

                val opts = try {
                    val arr = org.json.JSONArray(optJson)
                    List(arr.length()) { arr.getString(it) }
                } catch (e: Exception) { emptyList<String>() }

                val votesObj = try { org.json.JSONObject(votesJson) } catch (e: Exception) { org.json.JSONObject() }
                val voteCounts = mutableMapOf<Int, Int>()
                var userVoted: Int? = null
                val keys = votesObj.keys()
                var totalV = 0
                while (keys.hasNext()) {
                    val memberKey = keys.next()
                    val optIdx = votesObj.getInt(memberKey)
                    voteCounts[optIdx] = (voteCounts[optIdx] ?: 0) + 1
                    totalV++
                    if (memberKey == currentUserId) {
                        userVoted = optIdx
                    }
                }

                pollsDomain.add(BrainstormPollItem(id, brainstormId, q, opts, voteCounts, userVoted, totalV, isClosed, promId))
            }
            cur.close()
        } catch (e: Exception) {}

        // Activity
        val recentActivityList = mutableListOf<BrainstormActivityEntity>()
        try {
            val cur = database.openHelper.readableDatabase.query(
                "SELECT * FROM brainstorm_activity WHERE brainstormId = ? ORDER BY timestampUtc DESC LIMIT 20",
                arrayOf(brainstormId)
            )
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndexOrThrow("id"))
                val type = cur.getString(cur.getColumnIndexOrThrow("activityType"))
                val aId = cur.getString(cur.getColumnIndexOrThrow("actorMemberId"))
                val aName = cur.getString(cur.getColumnIndexOrThrow("actorDisplayName"))
                val title = cur.getString(cur.getColumnIndexOrThrow("title"))
                val desc = cur.getString(cur.getColumnIndexOrThrow("description"))
                val ts = cur.getLong(cur.getColumnIndexOrThrow("timestampUtc"))
                val isDemo = cur.getInt(cur.getColumnIndexOrThrow("isDemo")) == 1
                recentActivityList.add(BrainstormActivityEntity(id, brainstormId, type, aId, aName, title, desc, ts, isDemo))
            }
            cur.close()
        } catch (e: Exception) {}

        val isOwner = entity.ownerMemberId == currentUserId
        val isContributor = contributorsList.any { it.memberId == currentUserId && it.status == "ACTIVE" }
        val hasPendingApp = applicationsDomain.any { it.memberId == currentUserId && it.status == "PENDING" }

        _selectedBrainstormDetail.value = BrainstormDetailModel(
            brainstorm = entity,
            desiredSkills = rawSkills,
            tags = rawTags,
            contributors = contributorsList,
            applications = applicationsDomain,
            decisions = decisionsDomain,
            milestones = milestonesDomain,
            tasks = tasksDomain,
            resources = resourcesDomain,
            polls = pollsDomain,
            recentActivity = recentActivityList,
            isOwner = isOwner,
            isContributor = isContributor,
            hasPendingApplication = hasPendingApp,
            isLinkedProjectActive = entity.linkedProjectId != null
        )
    }

    // Action Methods for UI and Commands
    fun createBrainstorm(
        draft: BrainstormCreationDraft,
        onResult: (Result<BrainstormEntity>) -> Unit = {}
    ) {
        scope.launch {
            val creatorId = _selectedMemberId.value
            val res = brainstormService.createBrainstorm(creatorId, draft)
            if (res.isSuccess) {
                val entity = res.getOrThrow()
                _selectedBrainstormId.value = entity.id
                refreshBrainstormData()
                refreshTimelineData()
            }
            onResult(res)
        }
    }

    fun submitBrainstormApplication(
        brainstormId: String,
        reason: String,
        skills: String,
        experience: String,
        availability: String,
        portfolio: String,
        onResult: (Result<BrainstormApplicationEntity>) -> Unit = {}
    ) {
        scope.launch {
            val applicantId = _selectedMemberId.value
            val res = brainstormService.submitApplication(
                brainstormId = brainstormId,
                applicantMemberId = applicantId,
                reason = reason,
                skills = skills,
                experience = experience,
                availability = availability,
                portfolio = portfolio
            )
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun acceptBrainstormApplication(
        applicationId: String,
        onResult: (Result<BrainstormContributorEntity>) -> Unit = {}
    ) {
        scope.launch {
            val reviewerId = _selectedMemberId.value
            val res = brainstormService.acceptApplication(applicationId, reviewerId)
            if (res.isSuccess) {
                refreshBrainstormData()
                refreshTimelineData()
            }
            onResult(res)
        }
    }

    fun rejectBrainstormApplication(
        applicationId: String,
        decisionReason: String? = null,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val reviewerId = _selectedMemberId.value
            val res = brainstormService.rejectApplication(applicationId, reviewerId, decisionReason)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun withdrawBrainstormApplication(
        applicationId: String,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val memberId = _selectedMemberId.value
            val res = brainstormService.withdrawApplication(applicationId, memberId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun changeBrainstormStatus(
        brainstormId: String,
        newStatus: BrainstormStatus,
        reason: String,
        onResult: (Result<BrainstormEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.changeStatus(brainstormId, newStatus, reason, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun reviveBrainstorm(
        brainstormId: String,
        reason: String,
        onResult: (Result<BrainstormEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.reviveBrainstorm(brainstormId, reason, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun archiveBrainstorm(
        brainstormId: String,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.archiveBrainstorm(brainstormId, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun restoreBrainstorm(
        brainstormId: String,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.restoreBrainstorm(brainstormId, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun convertBrainstormToProject(
        brainstormId: String,
        projectTitle: String?,
        onResult: (Result<String>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.convertToProject(brainstormId, projectTitle, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
                refreshTimelineData()
            }
            onResult(res)
        }
    }

    fun recordBrainstormDecision(
        brainstormId: String,
        decisionText: String,
        reason: String,
        onResult: (Result<BrainstormDecisionEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.recordDecision(brainstormId, decisionText, reason, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun createBrainstormMilestone(
        brainstormId: String,
        title: String,
        description: String,
        onResult: (Result<BrainstormMilestoneEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.createMilestone(brainstormId, title, description, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun completeBrainstormMilestone(
        milestoneId: String,
        onResult: (Result<BrainstormMilestoneEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.completeMilestone(milestoneId, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
                refreshTimelineData()
            }
            onResult(res)
        }
    }

    fun createBrainstormTask(
        brainstormId: String,
        title: String,
        description: String,
        assigneeMemberId: String?,
        priority: String = "MEDIUM",
        dueDate: String? = null,
        onResult: (Result<BrainstormTaskEntity>) -> Unit = {}
    ) {
        scope.launch {
            val res = brainstormService.createTask(brainstormId, title, description, assigneeMemberId, priority, dueDate)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun updateBrainstormTaskStatus(
        taskId: String,
        newStatus: String,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val res = brainstormService.updateTaskStatus(taskId, newStatus)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun addBrainstormResource(
        brainstormId: String,
        name: String,
        url: String,
        description: String,
        category: String,
        isPrivate: Boolean,
        onResult: (Result<BrainstormResourceEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.addResource(brainstormId, name, url, description, category, isPrivate, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun createBrainstormPoll(
        brainstormId: String,
        question: String,
        options: List<String>,
        onResult: (Result<BrainstormPollEntity>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.createPoll(brainstormId, question, options, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun voteBrainstormPoll(
        pollId: String,
        optionIndex: Int,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.votePoll(pollId, actorId, optionIndex)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun removeBrainstormContributor(
        brainstormId: String,
        targetMemberId: String,
        reason: String,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.removeContributor(brainstormId, targetMemberId, actorId, reason)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun changeBrainstormContributorRole(
        brainstormId: String,
        targetMemberId: String,
        newRole: ContributorRole,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val res = brainstormService.changeContributorRole(brainstormId, targetMemberId, newRole, actorId)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun transferBrainstormOwnership(
        brainstormId: String,
        newOwnerId: String,
        reason: String,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        scope.launch {
            val currentOwnerId = _selectedMemberId.value
            val res = brainstormService.transferOwnership(brainstormId, currentOwnerId, newOwnerId, reason)
            if (res.isSuccess) {
                refreshBrainstormData()
            }
            onResult(res)
        }
    }

    fun reconcileBrainstormSystem() {
        scope.launch {
            val report = brainstormService.reconcileSystem()
            _brainstormReconcileReport.value = report
            _botTerminalOutput.value = "RECONCILIATION REPORT:\n${report.details.joinToString("\n")}"
        }
    }

    fun seedBrainstormDemoData() {
        scope.launch {
            val res = brainstormService.seedDemoData()
            _botTerminalOutput.value = res.getOrNull() ?: "Error seeding brainstorm demo."
            refreshBrainstormData()
            refreshTimelineData()
        }
    }

    fun clearBrainstormDemoData() {
        scope.launch {
            val res = brainstormService.clearDemoData()
            if (res.isSuccess) {
                _botTerminalOutput.value = "Demo brainstorm data cleared successfully."
                refreshBrainstormData()
                refreshTimelineData()
            }
        }
    }

    fun postBrainstormDiscussionMessage(
        brainstormId: String,
        categoryTag: String, // e.g. "💡 Idea", "🎨 Design", "💻 Technical"
        messageText: String
    ) {
        scope.launch {
            val actorId = _selectedMemberId.value
            val member = database.memberDao().getMemberById(actorId)
            brainstormService.recordActivity(
                brainstormId = brainstormId,
                activityType = "MESSAGE_ACTIVITY",
                actorMemberId = actorId,
                actorDisplayName = member?.displayName ?: "Builder",
                title = categoryTag,
                description = messageText
            )
            refreshBrainstormData()
        }
    }

    fun executeBrainstormBotCommand(commandInput: String) {
        val trimmed = commandInput.trim()
        val parts = trimmed.split("\\s+".toRegex())
        val root = parts.getOrNull(0)?.lowercase(Locale.US) ?: ""
        val sub = parts.getOrNull(1)?.lowercase(Locale.US) ?: ""

        scope.launch {
            when {
                trimmed.startsWith("/brainstorm browse") || trimmed.startsWith("/brainstorm list") -> {
                    _botTerminalOutput.value = "📋 BRAINSTORMING CLUB — DIRECTORY:\n" +
                            _dbBrainstorms.value.joinToString("\n") { "[${it.id}] ${it.title} (${it.status}) • Owner: ${it.ownerMemberId}" }
                }
                trimmed.startsWith("/brainstorm view") -> {
                    val idArg = parts.getOrNull(2)?.uppercase(Locale.US) ?: _selectedBrainstormId.value ?: "B#001"
                    _selectedBrainstormId.value = idArg
                    refreshBrainstormDetail(idArg)
                    val detail = _selectedBrainstormDetail.value
                    if (detail != null) {
                        _botTerminalOutput.value = """
                            |💡 ${detail.brainstorm.id} — ${detail.brainstorm.title}
                            |STATUS: ${detail.brainstorm.status}
                            |OWNER: ${detail.brainstorm.ownerDisplayName} (${detail.brainstorm.ownerMemberId})
                            |ABOUT: ${detail.brainstorm.description}
                            |PROBLEM: ${detail.brainstorm.problem}
                            |DIRECTION: ${detail.brainstorm.direction}
                            |COMMITMENT: ${detail.brainstorm.commitmentLevel}
                            |CONTRIBUTORS (${detail.contributors.size}): ${detail.contributors.joinToString(", ") { "${it.displayName} [${it.role.name}]" }}
                            |DECISIONS (${detail.decisions.size}) • MILESTONES (${detail.milestones.size})
                        """.trimMargin()
                    } else {
                        _botTerminalOutput.value = "❌ Brainstorm $idArg not found."
                    }
                }
                trimmed.startsWith("/brainstorm stats") -> {
                    val m = _brainstormMetrics.value
                    _botTerminalOutput.value = """
                        |📊 BRAINSTORMING CLUB ANALYTICS:
                        |Total Ideas: ${m.totalBrainstorms}
                        |Active / Brainstorming: ${m.activeCount}
                        |Cooking (Gaining Velocity): ${m.cookingCount}
                        |Paused: ${m.pausedCount}
                        |It's Alive (Promoted to P#): ${m.aliveCount}
                        |Pending Applications: ${m.pendingApplicationsCount}
                        |Active Contributors: ${m.activeContributorsCount}
                        |Activity (Last 7d): ${m.activityLast7d} events
                    """.trimMargin()
                }
                trimmed.startsWith("/brainstorm my-brainstorms") -> {
                    val currentId = _selectedMemberId.value
                    val owned = _dbBrainstorms.value.filter { it.ownerMemberId == currentId }
                    _botTerminalOutput.value = "💡 YOUR BRAINSTORMS (${owned.size}):\n" +
                            owned.joinToString("\n") { "[${it.id}] ${it.title} • Status: ${it.status}" }
                }
                trimmed.startsWith("/brainstorm my-contributions") || trimmed.startsWith("/brainstorm contribution-history") -> {
                    val currentId = _selectedMemberId.value
                    val contributions = _dbBrainstorms.value.filter { bs ->
                        database.brainstormContributorDao().getContributor(bs.id, currentId)?.status == "ACTIVE"
                    }
                    _botTerminalOutput.value = "👥 ACTIVE CONTRIBUTIONS (${contributions.size}):\n" +
                            contributions.joinToString("\n") { "[${it.id}] ${it.title} (${it.status})" }
                }
                trimmed.startsWith("/brainstorm-admin dashboard") -> {
                    val m = _brainstormMetrics.value
                    _botTerminalOutput.value = """
                        |==================================================
                        |      BRAINSTORMING CLUB — ADMIN DASHBOARD        
                        |==================================================
                        |Total Brainstorms:   ${m.totalBrainstorms}
                        |Active:              ${m.activeCount}
                        |Cooking:             ${m.cookingCount}
                        |Paused:              ${m.pausedCount}
                        |Cancelled:           ${m.cancelledCount}
                        |It's Alive:          ${m.aliveCount}
                        |Pending Review:      ${m.pendingApplicationsCount}
                        |Active Contributors: ${m.activeContributorsCount}
                        |Inactive Warnings:   ${m.inactiveWarningsCount}
                        |Conversions:         ${m.conversionsCount}
                    """.trimMargin()
                }
                trimmed.startsWith("/brainstorm-admin health") -> {
                    val h = brainstormService.getHealthReport()
                    _botTerminalOutput.value = """
                        |==================================================
                        |       BRAINSTORM BOT HEALTH CHECK               
                        |==================================================
                        |DATABASE:        ${h.databaseStatus}
                        |DISCORD API:     ${h.discordApiStatus}
                        |TIMELINE ENGINE: ${h.timelineEngineStatus}
                        |MEMBER SERVICE:  ${h.memberServiceStatus}
                        |EVENT QUEUE:     ${h.eventQueueStatus}
                        |CONFIG:          ${h.configurationStatus}
                        |TIME:            ${h.checkedAtFormatted}
                    """.trimMargin()
                }
                trimmed.startsWith("/brainstorm-admin reconcile") -> {
                    reconcileBrainstormSystem()
                }
                trimmed.startsWith("/brainstorm-admin seed-demo") -> {
                    seedBrainstormDemoData()
                }
                trimmed.startsWith("/brainstorm-admin clear-demo") -> {
                    clearBrainstormDemoData()
                }
                trimmed.startsWith("/brainstorm help") -> {
                    _botTerminalOutput.value = """
                        |💡 BRAINSTORMING CLUB BOT COMMANDS:
                        |/brainstorm create — Open interactive creation wizard
                        |/brainstorm browse — Browse all ideas by tag, skill, or status
                        |/brainstorm view [B#ID] — Inspect full brainstorm card & workspace
                        |/brainstorm apply — Submit contributor application
                        |/brainstorm status [STATUS] [REASON] — Change brainstorm lifecycle state
                        |/brainstorm decision [TEXT] [REASON] — Log permanent decision
                        |/brainstorm milestone [TITLE] — Register lightweight milestone
                        |/brainstorm task [TITLE] — Create task item
                        |/brainstorm convert — Promote B# into active Project P#
                        |/brainstorm stats — View community-wide metrics
                        |/brainstorm contribution-history — View your active squads & attestations
                        |/brainstorm-admin dashboard — Admin metrics & moderation
                        |/brainstorm-admin health — Infrastructure health report
                        |/brainstorm-admin reconcile — Check & repair discrepancies
                    """.trimMargin()
                }
                else -> {
                    _botTerminalOutput.value = "🤖 BrainstormBot: Received '$trimmed'. Use '/brainstorm help' for list of slash commands."
                }
            }
        }
    }

    // ----------------------------------------------------
    // PAYROLL & REVENUE BOT REPOSITORY OPERATIONS
    // ----------------------------------------------------
    fun refreshPayrollData() {
        scope.launch {
            val stages = database.paymentStageDao().getAllStagesFlow().first()
            _allPaymentStages.value = stages

            val currentSelectedId = _selectedStageId.value ?: stages.firstOrNull()?.id
            if (currentSelectedId != null) {
                selectPaymentStage(currentSelectedId)
            }

            val metrics = payrollService.getAdminDashboardMetrics()
            _payrollMetrics.value = metrics

            val health = payrollService.getHealthStatus()
            _payrollHealth.value = health

            val curMemberId = _selectedMemberId.value
            val earnings = payrollService.getMemberEarningsHistory(curMemberId)
            _memberEarnings.value = earnings

            val reconcile = payrollService.reconcileProject("P#014")
            _projectReconciliation.value = reconcile

            val ledger = database.ledgerDao().getAllLedgerEntriesFlow().first()
            _allLedgerEntries.value = ledger

            val audit = database.financialAuditLogDao().getAllAuditLogsFlow().first()
            _allFinancialAuditLogs.value = audit

            val disputes = database.disputeDao().getAllDisputesFlow().first()
            _allDisputes.value = disputes
        }
    }

    fun selectPaymentStage(stageId: String) {
        _selectedStageId.value = stageId
        scope.launch {
            val detail = payrollService.getFinancialStageDetail(stageId)
            _selectedStageDetail.value = detail
        }
    }

    fun createPaymentStage(
        projectId: String,
        projectTitle: String,
        type: PaymentStageType,
        description: String,
        quotedAmount: Double,
        currency: String = "INR",
        clientName: String? = null,
        invoiceNumber: String? = null,
        onComplete: (String) -> Unit = {}
    ) {
        scope.launch {
            val ownerId = _selectedMemberId.value
            val ownerName = _allMembers.value.find { it.id == ownerId }?.displayName ?: "Alex"
            val stageId = payrollService.createPaymentStage(
                projectId = projectId,
                projectTitle = projectTitle,
                ownerMemberId = ownerId,
                ownerName = ownerName,
                type = type,
                description = description,
                quotedAmount = quotedAmount,
                currency = currency,
                clientName = clientName,
                invoiceNumber = invoiceNumber
            )
            refreshPayrollData()
            selectPaymentStage(stageId)
            onComplete(stageId)
        }
    }

    fun submitPaymentProof(
        stageId: String,
        amount: Double,
        currency: String,
        paymentMethod: PaymentMethodType,
        paymentDate: String,
        invoiceReference: String,
        transactionReference: String,
        description: String,
        attachments: List<EvidenceAttachment>,
        previousIssueAddressed: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                val memberName = _allMembers.value.find { it.id == memberId }?.displayName ?: "Alex"
                payrollService.submitPaymentProof(
                    stageId = stageId,
                    amount = amount,
                    currency = currency,
                    paymentMethod = paymentMethod,
                    paymentDate = paymentDate,
                    invoiceReference = invoiceReference,
                    transactionReference = transactionReference,
                    description = description,
                    attachments = attachments,
                    submittedByMemberId = memberId,
                    submittedByName = memberName,
                    previousIssueAddressed = previousIssueAddressed
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun reviewPaymentProof(
        proofId: String,
        decision: ReviewDecision,
        reason: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                val member = _allMembers.value.find { it.id == memberId }
                val reviewerName = member?.displayName ?: "Sarah"
                val reviewerRole = if (memberId == "JEMP#001") "@Admin" else "@Project Contributor"
                payrollService.reviewPaymentProof(
                    proofId = proofId,
                    reviewerMemberId = memberId,
                    reviewerName = reviewerName,
                    reviewerRole = reviewerRole,
                    decision = decision,
                    reason = reason
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun createDistributionProposal(
        stageId: String,
        rationale: String,
        allocationsList: List<Triple<String, String, Double>>,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                val memberName = _allMembers.value.find { it.id == memberId }?.displayName ?: "Alex"
                payrollService.createDistributionProposal(
                    stageId = stageId,
                    rationale = rationale,
                    allocationsList = allocationsList,
                    submittedByMemberId = memberId,
                    submittedByName = memberName
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun reviewDistributionProposal(
        proposalId: String,
        decision: ReviewDecision,
        reason: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                val reviewerName = _allMembers.value.find { it.id == memberId }?.displayName ?: "John"
                payrollService.reviewDistributionProposal(
                    proposalId = proposalId,
                    reviewerMemberId = memberId,
                    reviewerName = reviewerName,
                    decision = decision,
                    reason = reason
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun submitOwnerTransfer(
        stageId: String,
        paymentMode: PaymentMethodType,
        submittedAmount: Double,
        transactionReference: String,
        cryptoHash: String? = null,
        notes: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                payrollService.submitOwnerTransfer(
                    stageId = stageId,
                    paymentMode = paymentMode,
                    submittedAmount = submittedAmount,
                    transactionReference = transactionReference,
                    cryptoHash = cryptoHash,
                    notes = notes,
                    submittedByMemberId = memberId
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun verifyAdminTransferReceipt(
        stageId: String,
        isVerified: Boolean,
        actualReceivedAmount: Double,
        rejectionOrNotes: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                payrollService.verifyAdminTransferReceipt(
                    stageId = stageId,
                    adminMemberId = "JEMP#001",
                    adminName = "Admin",
                    isVerified = isVerified,
                    actualReceivedAmount = actualReceivedAmount,
                    rejectionOrNotes = rejectionOrNotes
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun processSendPayout(
        payoutId: String,
        paymentMethod: PaymentMethodType,
        transactionReference: String,
        cryptoHash: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                payrollService.processSendPayout(
                    payoutId = payoutId,
                    adminMemberId = "JEMP#001",
                    adminName = "Admin",
                    paymentMethod = paymentMethod,
                    transactionReference = transactionReference,
                    cryptoHash = cryptoHash
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun confirmEmployeePayout(
        payoutId: String,
        isConfirmed: Boolean,
        disputeReason: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                val memberName = _allMembers.value.find { it.id == memberId }?.displayName ?: "Member"
                payrollService.confirmEmployeePayout(
                    payoutId = payoutId,
                    memberId = memberId,
                    memberName = memberName,
                    isConfirmed = isConfirmed,
                    disputeReason = disputeReason
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun createFinancialDispute(
        projectId: String,
        stageId: String,
        type: DisputeType,
        reason: String,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                val memberId = _selectedMemberId.value
                val memberName = _allMembers.value.find { it.id == memberId }?.displayName ?: "Member"
                payrollService.createDispute(
                    projectId = projectId,
                    stageId = stageId,
                    createdByMemberId = memberId,
                    createdByName = memberName,
                    type = type,
                    reason = reason
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun resolveFinancialDispute(
        disputeId: String,
        resolutionReason: String,
        newStatus: DisputeStatus = DisputeStatus.RESOLVED,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                payrollService.resolveDispute(
                    disputeId = disputeId,
                    adminMemberId = "JEMP#001",
                    adminName = "Admin",
                    resolutionReason = resolutionReason,
                    newStatus = newStatus
                )
                refreshPayrollData()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun setFinancialStageLock(stageId: String, isLocked: Boolean, reason: String?) {
        scope.launch {
            payrollService.setStageLock(
                stageId = stageId,
                isLocked = isLocked,
                reason = reason,
                adminMemberId = "JEMP#001",
                adminName = "Admin"
            )
            refreshPayrollData()
        }
    }

    fun seedPayrollDemoData() {
        scope.launch {
            payrollService.seedDemoData()
            refreshPayrollData()
            _botTerminalOutput.value = "✅ Successfully seeded realistic multi-stage payroll demo scenario (P#014 with PAY#001..PAY#004)."
        }
    }

    fun clearPayrollDemoData() {
        scope.launch {
            payrollService.clearDemoData()
            refreshPayrollData()
            _botTerminalOutput.value = "🧹 Cleared all payroll demo data."
        }
    }

    fun executePayrollSlash(command: String) {
        scope.launch {
            val output = payrollService.executePayrollSlashCommand(command, _selectedMemberId.value)
            _botTerminalOutput.value = output
            refreshPayrollData()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: JoblessEmpireRepository? = null

        fun getInstance(context: Context): JoblessEmpireRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = JoblessEmpireRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
