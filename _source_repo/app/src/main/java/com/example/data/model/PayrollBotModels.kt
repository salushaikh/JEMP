package com.example.data.model

import java.text.SimpleDateFormat
import java.util.*

enum class PaymentStageType(val label: String, val badgeColorHex: Long) {
    ADVANCE("Advance Payment", 0xFF06B6D4),
    MILESTONE("Milestone Payment", 0xFF3B82F6),
    FINAL_PAYMENT("Final Project Completion", 0xFF10B981),
    REVISION("Scope Revision", 0xFFF59E0B),
    MAINTENANCE("Monthly Maintenance", 0xFF8B5CF6),
    ADDITIONAL_SCOPE("Additional Scope", 0xFFEC4899),
    REFUND("Client Refund", 0xFFEF4444),
    CHARGEBACK("Payment Chargeback", 0xFFDC2626),
    OTHER("Other Financial Unit", 0xFF6B7280)
}

enum class PaymentStageStatus(val label: String, val badgeColorHex: Long) {
    DRAFT("Draft", 0xFF6B7280),
    AWAITING_CLIENT_PAYMENT("Awaiting Client Payment", 0xFFF59E0B),
    PAYMENT_REPORTED("Payment Reported", 0xFF06B6D4),
    PROOF_SUBMITTED("Proof Submitted", 0xFF3B82F6),
    PROOF_UNDER_REVIEW("Proof Under Review", 0xFFF59E0B),
    PROOF_NEEDS_MORE_EVIDENCE("Needs More Evidence", 0xFFEF4444),
    PROOF_REJECTED("Proof Rejected", 0xFFDC2626),
    PROOF_APPROVED("Proof Approved", 0xFF10B981),
    DISTRIBUTION_DRAFT("Distribution Draft", 0xFF6B7280),
    DISTRIBUTION_UNDER_REVIEW("Distribution Under Review", 0xFFF59E0B),
    DISTRIBUTION_NEEDS_REVISION("Distribution Needs Revision", 0xFFEF4444),
    DISTRIBUTION_APPROVED("Distribution Approved", 0xFF10B981),
    AWAITING_OWNER_TRANSFER("Awaiting Owner Transfer", 0xFFF59E0B),
    TRANSFER_SUBMITTED("Transfer Submitted", 0xFF06B6D4),
    TRANSFER_UNDER_REVIEW("Transfer Under Review", 0xFFF59E0B),
    TRANSFER_REQUIRES_ACTION("Transfer Action Needed", 0xFFEF4444),
    TRANSFER_APPROVED("Transfer Verified", 0xFF10B981),
    PAYROLL_PROCESSING("Payroll Processing", 0xFF3B82F6),
    PARTIALLY_PAID("Partially Paid", 0xFFF59E0B),
    PAYROLL_COMPLETE("Payroll Sent", 0xFF10B981),
    EMPLOYEE_CONFIRMATION_PENDING("Confirmations Pending", 0xFFF59E0B),
    CLOSED("Financial Stage Closed", 0xFF10B981),
    DISPUTED("Stage Disputed", 0xFFDC2626),
    LOCKED("Stage Locked", 0xFFEF4444),
    REFUNDED("Refunded", 0xFF6B7280),
    CANCELLED("Cancelled", 0xFF6B7280)
}

enum class ReviewDecision(val label: String) {
    APPROVED("Approved"),
    NOT_SATISFIED("Not Satisfied"),
    REQUEST_MORE_EVIDENCE("Request More Evidence")
}

enum class PaymentMethodType(val label: String) {
    BANK_TRANSFER("Bank Transfer"),
    WIRE_TRANSFER("Wire Transfer"),
    UPI("UPI (Instant)"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    WISE("Wise (TransferWise)"),
    PAYONEER("Payoneer"),
    CRYPTO("Cryptocurrency (USDT/ETH/BTC)"),
    CASH("Direct Cash / Cheque"),
    OTHER("Other Payment Provider")
}

enum class PayoutStatus(val label: String, val badgeColorHex: Long) {
    PENDING("Pending", 0xFF6B7280),
    INITIATED("Initiated", 0xFF06B6D4),
    PROCESSING("Processing (1-3 Days)", 0xFFF59E0B),
    SENT("Payment Sent", 0xFF3B82F6),
    PARTIALLY_SENT("Partially Sent", 0xFFF59E0B),
    FAILED("Payment Failed", 0xFFDC2626),
    RETRY_REQUIRED("Retry Required", 0xFFEF4444),
    CONFIRMED("Confirmed by Member", 0xFF10B981),
    DISPUTED("Disputed by Member", 0xFFDC2626)
}

enum class DisputeType(val label: String) {
    PROOF_DISPUTE("Proof Authenticity / Clarity"),
    DISTRIBUTION_DISPUTE("Work Share / Fairness"),
    TRANSFER_DISPUTE("Owner-to-Admin Transfer"),
    PAYOUT_DISPUTE("Payout Non-Receipt / Mismatch"),
    AMOUNT_DISPUTE("Incorrect Invoiced / Received Amount"),
    CURRENCY_DISPUTE("Exchange Rate Discrepancy"),
    OTHER("General Financial Dispute")
}

enum class DisputeStatus(val label: String, val badgeColorHex: Long) {
    OPEN("Open", 0xFFDC2626),
    UNDER_REVIEW("Under Admin Review", 0xFFF59E0B),
    WAITING_FOR_EVIDENCE("Waiting For Evidence", 0xFF06B6D4),
    RESOLVED("Resolved by Admin", 0xFF10B981),
    REJECTED("Dispute Dismissed", 0xFF6B7280),
    ESCALATED("Escalated to Governance", 0xFF8B5CF6)
}

enum class LedgerEntryType(val label: String) {
    CLIENT_RECEIPT("Client Receipt"),
    ADMIN_FEE("Admin Fee Allocation"),
    CHARITY_FEE("Charity Allocation"),
    WORK_ALLOCATION("Work Distribution Pool"),
    OWNER_TRANSFER("Owner-to-Admin Transfer"),
    EMPLOYEE_PAYOUT("Employee Compensation Payout"),
    REFUND("Client Refund"),
    ADJUSTMENT("Financial Adjustment / Correction")
}

data class FinancialCalculationResult(
    val grossAmount: Double,
    val currency: String,
    val adminFeePercent: Double,
    val adminFeeAmount: Double,
    val charityFeePercent: Double,
    val charityFeeAmount: Double,
    val workPoolAmount: Double,
    val ownerSharePercent: Double,
    val ownerShareAmount: Double,
    val requiredTransferToAdmin: Double,
    val teamPayoutPool: Double,
    val roundingDifference: Double = 0.0
)

data class PaymentProofPackage(
    val proofId: String,
    val stageId: String,
    val projectId: String,
    val version: Int,
    val amount: Double,
    val currency: String,
    val paymentMethod: PaymentMethodType,
    val paymentDate: String,
    val invoiceReference: String,
    val transactionReference: String,
    val description: String,
    val attachments: List<EvidenceAttachment>,
    val submittedByMemberId: String,
    val submittedByDisplayName: String,
    val submittedAt: Long,
    val status: String,
    val previousIssueAddressed: String? = null
)

data class EvidenceAttachment(
    val fileName: String,
    val fileType: String, // "invoice_pdf", "bank_receipt", "transaction_screenshot", "crypto_tx_hash", "statement"
    val fileUrl: String,
    val fileSizeKb: Int,
    val sha256Hash: String
)

data class ProofReviewSummary(
    val reviewerMemberId: String,
    val reviewerDisplayName: String,
    val reviewerRole: String,
    val decision: ReviewDecision,
    val reason: String?,
    val timestamp: Long,
    val proofVersion: Int
)

data class DistributionProposalModel(
    val proposalId: String,
    val stageId: String,
    val version: Int,
    val grossAmount: Double,
    val currency: String,
    val adminFeePercent: Double,
    val adminFeeAmount: Double,
    val charityFeePercent: Double,
    val charityFeeAmount: Double,
    val workPoolAmount: Double,
    val rationale: String,
    val allocations: List<DistributionAllocationModel>,
    val reviews: List<DistributionReviewModel>,
    val status: String,
    val submittedByMemberId: String,
    val submittedAt: Long
)

data class DistributionAllocationModel(
    val memberId: String,
    val memberDisplayName: String,
    val percentage: Double,
    val calculatedAmount: Double,
    val role: String,
    val workContributionRationale: String,
    val isAdmin: Boolean = false,
    val separateAdminFeeAmount: Double = 0.0
)

data class DistributionReviewModel(
    val reviewerMemberId: String,
    val reviewerDisplayName: String,
    val decision: ReviewDecision,
    val reason: String?,
    val timestamp: Long,
    val proposalVersion: Int
)

data class OwnerTransferModel(
    val transferId: String,
    val stageId: String,
    val projectId: String,
    val requiredAmount: Double,
    val submittedAmount: Double,
    val currency: String,
    val paymentMode: PaymentMethodType,
    val transactionReference: String,
    val cryptoHash: String?,
    val screenshotUrl: String?,
    val notes: String?,
    val submittedByMemberId: String,
    val submittedAt: Long,
    val adminVerificationStatus: String,
    val adminVerifiedBy: String?,
    val adminVerifiedAt: Long?,
    val adminRejectionReason: String?,
    val actualReceivedAmount: Double = 0.0
)

data class PayoutItemModel(
    val payoutId: String,
    val stageId: String,
    val projectId: String,
    val memberId: String,
    val memberDisplayName: String,
    val amount: Double,
    val currency: String,
    val percentage: Double,
    val paymentMethod: PaymentMethodType,
    val transactionReference: String?,
    val cryptoHash: String?,
    val proofScreenshot: String?,
    val status: PayoutStatus,
    val initiatedAt: Long?,
    val sentAt: Long?,
    val confirmationStatus: String?,
    val confirmedAt: Long?,
    val disputeReason: String?,
    val expectedSettlementNote: String?
)

data class FinancialStageDetail(
    val stageId: String,
    val stageCode: String, // e.g. "PAY#004" or "P#014-F01"
    val projectId: String,
    val projectTitle: String,
    val projectOwnerMemberId: String,
    val projectOwnerName: String,
    val stageType: PaymentStageType,
    val description: String,
    val expectedAmount: Double,
    val currency: String,
    val clientName: String?,
    val invoiceNumber: String?,
    val status: PaymentStageStatus,
    val isLocked: Boolean,
    val lockReason: String?,
    val currentProof: PaymentProofPackage?,
    val proofHistory: List<PaymentProofPackage>,
    val proofReviews: List<ProofReviewSummary>,
    val currentDistribution: DistributionProposalModel?,
    val distributionHistory: List<DistributionProposalModel>,
    val ownerTransfer: OwnerTransferModel?,
    val payouts: List<PayoutItemModel>,
    val disputes: List<FinancialDisputeModel>,
    val createdAt: Long,
    val closedAt: Long?
)

data class FinancialDisputeModel(
    val disputeId: String,
    val projectId: String,
    val stageId: String,
    val createdByMemberId: String,
    val createdByName: String,
    val disputeType: DisputeType,
    val reason: String,
    val status: DisputeStatus,
    val resolutionReason: String?,
    val resolvedByAdminId: String?,
    val createdAt: Long,
    val resolvedAt: Long?
)

data class FinancialLedgerModel(
    val ledgerId: String,
    val projectId: String,
    val stageId: String?,
    val entryType: LedgerEntryType,
    val source: String,
    val destination: String,
    val amount: Double,
    val currency: String,
    val reference: String,
    val createdBy: String,
    val createdAt: Long
)

data class FinancialAuditLogModel(
    val auditId: String,
    val actorMemberId: String,
    val actorDisplayName: String,
    val action: String,
    val projectId: String,
    val stageId: String?,
    val beforeState: String?,
    val afterState: String?,
    val reason: String?,
    val timestamp: Long
)

data class PayrollAdminDashboardMetrics(
    val totalActiveProjects: Int = 0,
    val totalClientRevenue: Double = 0.0,
    val approvedRevenue: Double = 0.0,
    val pendingReviewRevenue: Double = 0.0,
    val totalAdminFeesRetained: Double = 0.0,
    val totalDistributedToContributors: Double = 0.0,
    val proofsAwaitingReview: Int = 0,
    val distributionsAwaitingReview: Int = 0,
    val transfersAwaitingConfirmation: Int = 0,
    val payrollRunsInProgress: Int = 0,
    val employeeConfirmationsPending: Int = 0,
    val openDisputesCount: Int = 0,
    val closedFinancialStagesCount: Int = 0
)

data class MemberEarningsHistory(
    val memberId: String,
    val activeProjectsCount: Int,
    val pendingApprovalsCount: Int,
    val pendingConfirmationsCount: Int,
    val totalConfirmedEarned: Double,
    val totalPendingPayouts: Double,
    val currency: String,
    val payoutItems: List<PayoutItemModel>
)

data class ProjectReconciliationReport(
    val projectId: String,
    val projectTitle: String,
    val currency: String,
    val totalInvoiced: Double,
    val totalGrossReceived: Double,
    val totalApprovedProof: Double,
    val totalAdminFeesCalculated: Double,
    val totalAdminFeesRetained: Double,
    val totalWorkPool: Double,
    val totalOwnerTransfersExpected: Double,
    val totalOwnerTransfersReceived: Double,
    val totalEmployeePayoutsSent: Double,
    val totalEmployeeConfirmations: Int,
    val totalExpectedConfirmations: Int,
    val totalDiscrepancy: Double,
    val isFullyBalanced: Boolean,
    val checkList: List<ReconciliationCheckItem>
)

data class ReconciliationCheckItem(
    val title: String,
    val expected: String,
    val actual: String,
    val isPassed: Boolean,
    val notes: String? = null
)

data class PayrollHealthStatus(
    val isDatabaseHealthy: Boolean = true,
    val isLedgerConsistent: Boolean = true,
    val isAuditIntact: Boolean = true,
    val isTimelineBridgeActive: Boolean = true,
    val orphanedPayoutsCount: Int = 0,
    val failedTransitionsCount: Int = 0,
    val activeFeeSettingsSummary: String = "Admin: 5.00% | Charity: 0.00%"
)
