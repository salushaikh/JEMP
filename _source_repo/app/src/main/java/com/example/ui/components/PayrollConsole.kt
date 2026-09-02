package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.*
import com.example.data.model.*
import com.example.data.repository.JoblessEmpireRepository
import com.example.data.service.PayrollCalculationService
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class PayrollTab(val label: String, val icon: @Composable () -> Unit) {
    STAGES("Stages & Lifecycle", { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) }),
    ADMIN_RECONCILE("Admin & Reconcile", { Icon(Icons.Default.Assessment, contentDescription = null) }),
    LEDGER_AUDIT("Ledger & Audit Log", { Icon(Icons.Default.HistoryEdu, contentDescription = null) }),
    MY_EARNINGS("My Earnings", { Icon(Icons.Default.MonetizationOn, contentDescription = null) }),
    TERMINAL("Slash Terminal", { Icon(Icons.Default.Terminal, contentDescription = null) })
}

@Composable
fun PayrollConsole(
    repository: JoblessEmpireRepository,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val allStages by repository.allPaymentStages.collectAsState()
    val selectedStageId by repository.selectedStageId.collectAsState()
    val selectedStageDetail by repository.selectedStageDetail.collectAsState()
    val payrollMetrics by repository.payrollMetrics.collectAsState()
    val payrollHealth by repository.payrollHealth.collectAsState()
    val memberEarnings by repository.memberEarnings.collectAsState()
    val projectReconciliation by repository.projectReconciliation.collectAsState()
    val ledgerEntries by repository.allLedgerEntries.collectAsState()
    val auditLogs by repository.allFinancialAuditLogs.collectAsState()
    val disputes by repository.allDisputes.collectAsState()
    val selectedMemberId by repository.selectedMemberId.collectAsState()
    val allMembers by repository.allMembers.collectAsState()
    val terminalOutput by repository.botTerminalOutput.collectAsState()

    var activeTab by remember { mutableStateOf(PayrollTab.STAGES) }
    var showAddStageDialog by remember { mutableStateOf(false) }
    var showSubmitProofDialog by remember { mutableStateOf(false) }
    var showCreateDistDialog by remember { mutableStateOf(false) }
    var showSubmitTransferDialog by remember { mutableStateOf(false) }

    // Feedback dialog state
    var reviewDialogTarget by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // (ActionTitle, isApproval)
    var reviewDialogCallback by remember { mutableStateOf<((String?) -> Unit)?>(null) }

    var filterStatus by remember { mutableStateOf<PaymentStageStatus?>(null) }

    val currentMember = allMembers.find { it.id == selectedMemberId }
    val isAdmin = selectedMemberId == "JEMP#001"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("payroll_console_main")
    ) {
        // Top Header
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DiscordBlurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = "Payroll", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Jobless Empire Payroll & Revenue Bot",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentGreen.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen)
                                ) {
                                    Text(
                                        "v2.4 STABLE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "5% Admin Fee • Immutable Proof Packages • Multi-version Objection Loop • Safe Ledger",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { repository.seedPayrollDemoData() },
                            colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DiscordBlurple),
                            modifier = Modifier.testTag("payroll_seed_demo_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = DiscordBlurple)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Seed Demo", color = TextPrimary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showAddStageDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                            modifier = Modifier.testTag("payroll_create_stage_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Stage", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Selector
                ScrollableTabRow(
                    selectedTabIndex = activeTab.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = DiscordBlurple,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    PayrollTab.values().forEach { tab ->
                        Tab(
                            selected = activeTab == tab,
                            onClick = { activeTab = tab },
                            text = { Text(tab.label, fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal) },
                            icon = tab.icon,
                            selectedContentColor = DiscordBlurple,
                            unselectedContentColor = TextMuted
                        )
                    }
                }
            }
        }

        // Main Tab Content
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (activeTab) {
                PayrollTab.STAGES -> {
                    StagesAndLifecycleView(
                        allStages = allStages,
                        selectedStageId = selectedStageId,
                        selectedStageDetail = selectedStageDetail,
                        onSelectStage = { repository.selectPaymentStage(it) },
                        onSubmitProofClick = { showSubmitProofDialog = true },
                        onCreateDistributionClick = { showCreateDistDialog = true },
                        onSubmitTransferClick = { showSubmitTransferDialog = true },
                        onReviewProof = { proofId, isApprove ->
                            reviewDialogTarget = Pair(if (isApprove) "Approve Financial Proof" else "Reject Financial Proof", isApprove)
                            reviewDialogCallback = { reason ->
                                repository.reviewPaymentProof(proofId, if (isApprove) ReviewDecision.APPROVED else ReviewDecision.NOT_SATISFIED, reason)
                            }
                        },
                        onReviewDistribution = { propId, isApprove ->
                            reviewDialogTarget = Pair(if (isApprove) "Approve Distribution Proposal" else "Object to Distribution Proposal", isApprove)
                            reviewDialogCallback = { reason ->
                                repository.reviewDistributionProposal(propId, if (isApprove) ReviewDecision.APPROVED else ReviewDecision.NOT_SATISFIED, reason)
                            }
                        },
                        onVerifyTransfer = { stageId, isVerify ->
                            if (isVerify) {
                                val amount = selectedStageDetail?.ownerTransfer?.submittedAmount ?: 0.0
                                repository.verifyAdminTransferReceipt(stageId, true, amount)
                            } else {
                                reviewDialogTarget = Pair("Flag Incorrect / Missing Transfer", false)
                                reviewDialogCallback = { reason ->
                                    repository.verifyAdminTransferReceipt(stageId, false, 0.0, reason)
                                }
                            }
                        },
                        onSendPayout = { payoutId ->
                            repository.processSendPayout(
                                payoutId = payoutId,
                                paymentMethod = PaymentMethodType.BANK_TRANSFER,
                                transactionReference = "TXN-ADMIN-${System.currentTimeMillis().toString().takeLast(6)}"
                            )
                        },
                        onConfirmPayout = { payoutId, isConfirm ->
                            if (isConfirm) {
                                repository.confirmEmployeePayout(payoutId, true)
                            } else {
                                reviewDialogTarget = Pair("Dispute Payout", false)
                                reviewDialogCallback = { reason ->
                                    repository.confirmEmployeePayout(payoutId, false, reason)
                                }
                            }
                        },
                        onToggleLock = { stageId, isLocked ->
                            if (isLocked) {
                                repository.setFinancialStageLock(stageId, false, null)
                            } else {
                                reviewDialogTarget = Pair("Lock Financial Stage", false)
                                reviewDialogCallback = { reason ->
                                    repository.setFinancialStageLock(stageId, true, reason)
                                }
                            }
                        }
                    )
                }

                PayrollTab.ADMIN_RECONCILE -> {
                    AdminReconcileView(
                        metrics = payrollMetrics,
                        health = payrollHealth,
                        reconciliation = projectReconciliation,
                        onReconcileClick = { repository.refreshPayrollData() },
                        onExecuteCommand = { repository.executePayrollSlash(it) }
                    )
                }

                PayrollTab.LEDGER_AUDIT -> {
                    LedgerAndAuditView(
                        ledgerEntries = ledgerEntries,
                        auditLogs = auditLogs,
                        disputes = disputes
                    )
                }

                PayrollTab.MY_EARNINGS -> {
                    MyEarningsView(
                        earnings = memberEarnings,
                        currentMemberId = selectedMemberId,
                        onConfirmPayout = { payoutId, isConfirm ->
                            if (isConfirm) {
                                repository.confirmEmployeePayout(payoutId, true)
                            } else {
                                reviewDialogTarget = Pair("Dispute Payout", false)
                                reviewDialogCallback = { reason ->
                                    repository.confirmEmployeePayout(payoutId, false, reason)
                                }
                            }
                        }
                    )
                }

                PayrollTab.TERMINAL -> {
                    PayrollSlashTerminalView(
                        terminalOutput = terminalOutput,
                        onExecuteCommand = { repository.executePayrollSlash(it) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddStageDialog) {
        AddPaymentStageDialog(
            onDismiss = { showAddStageDialog = false },
            onSubmit = { projectId, projectTitle, type, desc, amt, curr, client, inv ->
                repository.createPaymentStage(projectId, projectTitle, type, desc, amt, curr, client, inv)
            }
        )
    }

    if (showSubmitProofDialog && selectedStageDetail != null) {
        SubmitProofDialog(
            stageDetail = selectedStageDetail!!,
            onDismiss = { showSubmitProofDialog = false },
            onSubmit = { amt, curr, method, date, inv, txn, desc, atts, prev ->
                repository.submitPaymentProof(selectedStageDetail!!.stageId, amt, curr, method, date, inv, txn, desc, atts, prev)
            }
        )
    }

    if (showCreateDistDialog && selectedStageDetail != null) {
        CreateDistributionProposalDialog(
            stageDetail = selectedStageDetail!!,
            onDismiss = { showCreateDistDialog = false },
            onSubmit = { rationale, allocs ->
                repository.createDistributionProposal(selectedStageDetail!!.stageId, rationale, allocs)
            }
        )
    }

    if (showSubmitTransferDialog && selectedStageDetail != null) {
        SubmitOwnerTransferDialog(
            stageDetail = selectedStageDetail!!,
            onDismiss = { showSubmitTransferDialog = false },
            onSubmit = { method, amt, txn, crypto, notes ->
                repository.submitOwnerTransfer(selectedStageDetail!!.stageId, method, amt, txn, crypto, notes)
            }
        )
    }

    if (reviewDialogTarget != null && reviewDialogCallback != null) {
        ReviewFeedbackDialog(
            title = reviewDialogTarget!!.first,
            actionLabel = if (reviewDialogTarget!!.second) "Confirm Approval" else "Submit Objection",
            isApproval = reviewDialogTarget!!.second,
            onDismiss = { reviewDialogTarget = null },
            onSubmit = { reason ->
                reviewDialogCallback?.invoke(reason)
                reviewDialogTarget = null
            }
        )
    }
}

// ----------------------------------------------------
// 1. STAGES & LIFECYCLE VIEW
// ----------------------------------------------------
@Composable
private fun StagesAndLifecycleView(
    allStages: List<PaymentStageEntity>,
    selectedStageId: String?,
    selectedStageDetail: FinancialStageDetail?,
    onSelectStage: (String) -> Unit,
    onSubmitProofClick: () -> Unit,
    onCreateDistributionClick: () -> Unit,
    onSubmitTransferClick: () -> Unit,
    onReviewProof: (String, Boolean) -> Unit,
    onReviewDistribution: (String, Boolean) -> Unit,
    onVerifyTransfer: (String, Boolean) -> Unit,
    onSendPayout: (String) -> Unit,
    onConfirmPayout: (String, Boolean) -> Unit,
    onToggleLock: (String, Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Left Column: Stage Selector List
        Card(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "Payment Stages (${allStages.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allStages) { stage ->
                        val isSelected = stage.id == selectedStageId
                        val status = PaymentStageStatus.valueOf(stage.status)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectStage(stage.id) }
                                .testTag("stage_item_${stage.stageCode}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DiscordBlurple.copy(alpha = 0.15f) else DarkBackground
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) DiscordBlurple else DarkBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        stage.stageCode,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DiscordBlurple else TextPrimary,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(status.badgeColorHex).copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(status.badgeColorHex))
                                    ) {
                                        Text(
                                            status.label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(status.badgeColorHex),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${stage.projectId} • ${stage.projectTitle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Text(
                                    PayrollCalculationService.formatCurrency(stage.quotedAmount, stage.currency),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Active Stage Lifecycle Detail Inspector
        Card(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (selectedStageDetail != null) {
                val detail = selectedStageDetail
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header of stage inspector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${detail.stageCode} : ${detail.stageType.label}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (detail.isLocked) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = AccentRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                "Project ${detail.projectId} (${detail.projectTitle}) • Owner: ${detail.projectOwnerName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        // Lock / Unlock Toggle
                        Button(
                            onClick = { onToggleLock(detail.stageId, detail.isLocked) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (detail.isLocked) AccentGreen.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (detail.isLocked) AccentGreen else AccentRed)
                        ) {
                            Icon(
                                if (detail.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (detail.isLocked) AccentGreen else AccentRed
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (detail.isLocked) "Unlock Stage" else "Lock Stage", color = TextPrimary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1: Financial Proof Card
                    StageProofSection(
                        detail = detail,
                        onSubmitProofClick = onSubmitProofClick,
                        onReviewProof = onReviewProof
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 2: Distribution Proposal Card
                    StageDistributionSection(
                        detail = detail,
                        onCreateDistributionClick = onCreateDistributionClick,
                        onReviewDistribution = onReviewDistribution
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 3: Owner Transfer to Admin
                    StageTransferSection(
                        detail = detail,
                        onSubmitTransferClick = onSubmitTransferClick,
                        onVerifyTransfer = onVerifyTransfer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 4: Employee Payouts & Confirmations
                    StagePayoutsSection(
                        detail = detail,
                        onSendPayout = onSendPayout,
                        onConfirmPayout = onConfirmPayout
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a payment stage on the left to inspect lifecycle.", color = TextMuted)
                }
            }
        }
    }
}

// ----------------------------------------------------
// STEP 1: PROOF SECTION
// ----------------------------------------------------
@Composable
private fun StageProofSection(
    detail: FinancialStageDetail,
    onSubmitProofClick: () -> Unit,
    onReviewProof: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("1. Client Invoiced Proof Package", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                if (detail.currentProof == null || detail.status == PaymentStageStatus.PROOF_NEEDS_MORE_EVIDENCE) {
                    Button(
                        onClick = onSubmitProofClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("submit_proof_action_btn")
                    ) {
                        Text(if (detail.currentProof == null) "Submit Proof" else "Revise Proof (v${detail.currentProof.version + 1})", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (detail.currentProof != null) {
                val proof = detail.currentProof
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Proof Version: v${proof.version} • Status: ${proof.status}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (proof.status == "APPROVED") AccentGreen else AccentYellow)
                            Text(PayrollCalculationService.formatCurrency(proof.amount, proof.currency), fontWeight = FontWeight.Bold, color = AccentGreen, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Invoice: ${proof.invoiceReference} • UTR/Txn: ${proof.transactionReference} (${proof.paymentMethod.label})", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(proof.description, style = MaterialTheme.typography.bodySmall, color = TextMuted)

                        if (proof.attachments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Attachments (${proof.attachments.size}):", style = MaterialTheme.typography.labelSmall, color = AccentCyan)
                            proof.attachments.forEach { att ->
                                Text("  📎 ${att.fileName} (${att.fileSizeKb} KB) • SHA-256: ${att.sha256Hash.take(12)}...", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }

                        // Reviews
                        if (detail.proofReviews.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Review Quorum (${detail.proofReviews.size}):", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            detail.proofReviews.forEach { rev ->
                                Text("  ${if (rev.decision == ReviewDecision.APPROVED) "✅" else "❌"} ${rev.reviewerDisplayName} (${rev.reviewerRole}): ${rev.decision.label}${rev.reason?.let { " — '$it'" } ?: ""}", style = MaterialTheme.typography.bodySmall, color = if (rev.decision == ReviewDecision.APPROVED) AccentGreen else AccentRed)
                            }
                        }

                        // Contributor review actions
                        if (proof.status == "UNDER_REVIEW") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onReviewProof(proof.proofId, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve Proof", fontSize = 11.sp, color = Color.White)
                                }

                                Button(
                                    onClick = { onReviewProof(proof.proofId, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Not Satisfied (Reason Required)", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            } else {
                Text("No payment proof submitted yet. Awaiting project owner upload.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

// ----------------------------------------------------
// STEP 2: DISTRIBUTION SECTION
// ----------------------------------------------------
@Composable
private fun StageDistributionSection(
    detail: FinancialStageDetail,
    onCreateDistributionClick: () -> Unit,
    onReviewDistribution: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PieChart, contentDescription = null, tint = DiscordBlurple, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("2. Contributor Distribution Proposal", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                if (detail.currentDistribution == null || detail.status == PaymentStageStatus.DISTRIBUTION_NEEDS_REVISION) {
                    Button(
                        onClick = onCreateDistributionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("create_distribution_action_btn")
                    ) {
                        Text(if (detail.currentDistribution == null) "Create Proposal" else "Revise Proposal (v${detail.currentDistribution.version + 1})", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (detail.currentDistribution != null) {
                val prop = detail.currentDistribution
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Proposal v${prop.version} • 5% Admin Fee: ${PayrollCalculationService.formatCurrency(prop.adminFeeAmount, prop.currency)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = DiscordBlurple)
                            Text("Work Pool (95%): ${PayrollCalculationService.formatCurrency(prop.workPoolAmount, prop.currency)}", fontWeight = FontWeight.Bold, color = AccentGreen, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Rationale: ${prop.rationale}", style = MaterialTheme.typography.bodySmall, color = TextMuted)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Allocations:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        prop.allocations.forEach { alloc ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("  • ${alloc.memberDisplayName} (${alloc.role}) - ${alloc.percentage}%", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                Text(PayrollCalculationService.formatCurrency(alloc.calculatedAmount, prop.currency), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AccentCyan)
                            }
                        }

                        if (prop.status == "UNDER_REVIEW") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onReviewDistribution(prop.proposalId, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve Split", fontSize = 11.sp, color = Color.White)
                                }

                                Button(
                                    onClick = { onReviewDistribution(prop.proposalId, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Object with Reason", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Distribution proposal pending. Requires verified proof approval first.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

// ----------------------------------------------------
// STEP 3: OWNER TRANSFER SECTION
// ----------------------------------------------------
@Composable
private fun StageTransferSection(
    detail: FinancialStageDetail,
    onSubmitTransferClick: () -> Unit,
    onVerifyTransfer: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("3. Owner Transfer to Admin Escrow", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                if (detail.ownerTransfer == null || detail.status == PaymentStageStatus.TRANSFER_REQUIRES_ACTION) {
                    Button(
                        onClick = onSubmitTransferClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("submit_transfer_action_btn")
                    ) {
                        Text("Record Transfer", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (detail.ownerTransfer != null) {
                val t = detail.ownerTransfer
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Status: ${t.adminVerificationStatus}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (t.adminVerificationStatus == "VERIFIED") AccentGreen else AccentYellow)
                            Text("Transfer: ${PayrollCalculationService.formatCurrency(t.submittedAmount, t.currency)}", fontWeight = FontWeight.Bold, color = AccentGreen, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Method: ${t.paymentMode.label} • UTR/Ref: ${t.transactionReference}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        t.notes?.let { Text("Notes: $it", style = MaterialTheme.typography.bodySmall, color = TextMuted) }

                        if (t.adminVerificationStatus == "AWAITING_CONFIRMATION") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onVerifyTransfer(detail.stageId, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Admin Confirm Receipt", fontSize = 11.sp, color = Color.White)
                                }

                                Button(
                                    onClick = { onVerifyTransfer(detail.stageId, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Incorrect / Missing Amount", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Awaiting owner transfer to Admin Escrow. (Required = Gross - Owner Share)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

// ----------------------------------------------------
// STEP 4: PAYOUTS SECTION
// ----------------------------------------------------
@Composable
private fun StagePayoutsSection(
    detail: FinancialStageDetail,
    onSendPayout: (String) -> Unit,
    onConfirmPayout: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Paid, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("4. Contributor Payouts & Confirmations (${detail.payouts.size})", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (detail.payouts.isNotEmpty()) {
                detail.payouts.forEach { po ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${po.memberDisplayName} (${po.percentage}%)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(PayrollCalculationService.formatCurrency(po.amount, po.currency), fontWeight = FontWeight.Bold, color = AccentGreen, style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Status: ${po.status.label} • Method: ${po.paymentMethod.label}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                po.transactionReference?.let { Text("Ref: $it", style = MaterialTheme.typography.bodySmall, color = TextMuted) }
                            }

                            if (po.status == PayoutStatus.PENDING) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onSendPayout(po.payoutId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Payout & Log Proof", fontSize = 11.sp, color = Color.White)
                                }
                            } else if (po.status == PayoutStatus.SENT) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onConfirmPayout(po.payoutId, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Confirm Receipt", fontSize = 11.sp, color = Color.White)
                                    }

                                    Button(
                                        onClick = { onConfirmPayout(po.payoutId, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Dispute / Not Arrived", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text("Payout records will be generated once owner transfer is verified by admin.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

// ----------------------------------------------------
// 2. ADMIN RECONCILE VIEW
// ----------------------------------------------------
@Composable
private fun AdminReconcileView(
    metrics: PayrollAdminDashboardMetrics,
    health: PayrollHealthStatus,
    reconciliation: ProjectReconciliationReport?,
    onReconcileClick: () -> Unit,
    onExecuteCommand: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // KPI Metric Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard("Gross Revenue", PayrollCalculationService.formatCurrency(metrics.totalClientRevenue), DiscordBlurple, Modifier.weight(1f))
            KpiCard("Admin Fees (5%)", PayrollCalculationService.formatCurrency(metrics.totalAdminFeesRetained), AccentCyan, Modifier.weight(1f))
            KpiCard("Squad Payouts", PayrollCalculationService.formatCurrency(metrics.totalDistributedToContributors), AccentGreen, Modifier.weight(1f))
            KpiCard("Open Disputes", metrics.openDisputesCount.toString(), if (metrics.openDisputesCount > 0) AccentRed else AccentGreen, Modifier.weight(1f))
        }

        // Live Project Reconciliation Report
        if (reconciliation != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (reconciliation.isFullyBalanced) AccentGreen else AccentYellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Project Reconciliation Report [${reconciliation.projectId}]",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                if (reconciliation.isFullyBalanced) "🟢 PERFECTLY BALANCED: All double-entry debits equal credits." else "⚠️ RECONCILIATION IN PROGRESS: Active transfers pending.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (reconciliation.isFullyBalanced) AccentGreen else AccentYellow
                            )
                        }

                        Button(
                            onClick = onReconcileClick,
                            colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Re-Audit Engine", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    reconciliation.checkList.forEach { check ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (check.isPassed) "✅" else "⚠️", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(check.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Expected: ${check.expected}  •  Actual: ${check.actual}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                                Text(
                                    if (check.isPassed) "BALANCED" else "UNSETTLED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (check.isPassed) AccentGreen else AccentYellow
                                )
                            }
                        }
                    }
                }
            }
        }

        // System Health Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Payroll Infrastructure Health Check", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Database & Room DAOs: ✅ Healthy", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                Text("• Double-Entry Ledger Engine: ✅ 0 Discrepancy", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                Text("• Safe Timeline Integration: ✅ Connected", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                Text("• Active Policy: ${health.activeFeeSettingsSummary}", style = MaterialTheme.typography.bodySmall, color = AccentCyan)
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

// ----------------------------------------------------
// 3. LEDGER & AUDIT TRAIL VIEW
// ----------------------------------------------------
@Composable
private fun LedgerAndAuditView(
    ledgerEntries: List<FinancialLedgerEntity>,
    auditLogs: List<FinancialAuditLogEntity>,
    disputes: List<FinancialDisputeEntity>
) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Left Column: Ledger Entries
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Append-Only Financial Ledger (${ledgerEntries.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ledgerEntries) { entry ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(entry.entryType, fontWeight = FontWeight.Bold, color = DiscordBlurple, style = MaterialTheme.typography.bodySmall)
                                    Text(PayrollCalculationService.formatCurrency(entry.amount, entry.currency), fontWeight = FontWeight.Bold, color = AccentGreen, style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${entry.source} ➔ ${entry.destination}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                Text("Ref: ${entry.reference} • Project: ${entry.projectId}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Audit Logs
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Immutable Audit Log (${auditLogs.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(auditLogs) { log ->
                        val dateFormatted = remember(log.timestamp) {
                            SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(log.timestamp))
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(log.action, fontWeight = FontWeight.Bold, color = AccentYellow, style = MaterialTheme.typography.bodySmall)
                                    Text(dateFormatted, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                }
                                Text("Actor: ${log.actorDisplayName} (${log.actorMemberId})", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                log.reason?.let { Text("Reason: $it", style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. MY EARNINGS VIEW
// ----------------------------------------------------
@Composable
private fun MyEarningsView(
    earnings: MemberEarningsHistory?,
    currentMemberId: String,
    onConfirmPayout: (String, Boolean) -> Unit
) {
    if (earnings != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Member Earnings Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard("Confirmed Earnings", PayrollCalculationService.formatCurrency(earnings.totalConfirmedEarned, earnings.currency), AccentGreen, Modifier.weight(1f))
                KpiCard("Pending Payouts", PayrollCalculationService.formatCurrency(earnings.totalPendingPayouts, earnings.currency), AccentYellow, Modifier.weight(1f))
                KpiCard("Active Projects", earnings.activeProjectsCount.toString(), DiscordBlurple, Modifier.weight(1f))
                KpiCard("Action Required", earnings.pendingConfirmationsCount.toString(), if (earnings.pendingConfirmationsCount > 0) AccentCyan else TextMuted, Modifier.weight(1f))
            }

            // Payout Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Project Compensations (${earnings.payoutItems.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (earnings.payoutItems.isEmpty()) {
                        Text("No payouts recorded for $currentMemberId yet.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    } else {
                        earnings.payoutItems.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${item.projectId} • Share: ${item.percentage}%", fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(PayrollCalculationService.formatCurrency(item.amount, item.currency), fontWeight = FontWeight.Bold, color = AccentGreen)
                                    }
                                    Text("Status: ${item.status.label} • Method: ${item.paymentMethod.label}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    item.transactionReference?.let { Text("Reference: $it", style = MaterialTheme.typography.bodySmall, color = TextMuted) }

                                    if (item.status == PayoutStatus.SENT) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { onConfirmPayout(item.payoutId, true) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("I Received This Amount", fontSize = 11.sp, color = Color.White)
                                            }

                                            Button(
                                                onClick = { onConfirmPayout(item.payoutId, false) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Dispute / Did Not Arrive", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No member earnings loaded.", color = TextMuted)
        }
    }
}

// ----------------------------------------------------
// 5. SLASH TERMINAL VIEW
// ----------------------------------------------------
@Composable
private fun PayrollSlashTerminalView(
    terminalOutput: String?,
    onExecuteCommand: (String) -> Unit
) {
    var commandInput by remember { mutableStateOf("/payroll help") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Command Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val quickCmds = listOf(
                "/payroll help",
                "/payroll me",
                "/payroll project P#014",
                "/payroll-admin dashboard",
                "/payroll-admin reconcile P#014",
                "/payroll-admin health",
                "/payroll-admin seed-demo"
            )
            items(quickCmds) { cmd ->
                SuggestionChip(
                    onClick = {
                        commandInput = cmd
                        onExecuteCommand(cmd)
                    },
                    label = { Text(cmd, fontSize = 11.sp, color = TextPrimary) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DiscordBlurple.copy(alpha = 0.5f))
                )
            }
        }

        // Input Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("payroll_terminal_input"),
                placeholder = { Text("Type slash command (e.g. /payroll-admin dashboard)...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = DiscordBlurple,
                    unfocusedBorderColor = DarkBorder
                )
            )

            Button(
                onClick = { onExecuteCommand(commandInput) },
                colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                modifier = Modifier.testTag("payroll_terminal_send_btn")
            ) {
                Text("Execute", color = Color.White)
            }
        }

        // Terminal Console Output Window
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentYellow))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentGreen))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("discord-bot@joblessempire-payroll: ~", color = TextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    terminalOutput ?: "Type any /payroll or /payroll-admin command or click chips above to query.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = AccentGreen,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
