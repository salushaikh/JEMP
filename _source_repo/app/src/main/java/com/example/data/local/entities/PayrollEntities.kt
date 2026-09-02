package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_stages",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["stageCode"], unique = true),
        Index(value = ["status"])
    ]
)
data class PaymentStageEntity(
    @PrimaryKey
    val id: String, // e.g. "stage_001"
    val stageCode: String, // e.g. "PAY#001" or "P#014-F01"
    val projectId: String, // e.g. "P#014"
    val projectTitle: String,
    val projectOwnerMemberId: String,
    val projectOwnerName: String,
    val type: String, // e.g. "ADVANCE", "MILESTONE", "FINAL_PAYMENT", etc.
    val description: String,
    val quotedAmount: Double,
    val currency: String,
    val clientName: String? = null,
    val clientReference: String? = null,
    val invoiceNumber: String? = null,
    val invoiceDate: String? = null,
    val paymentDueDate: String? = null,
    val status: String, // e.g. "AWAITING_CLIENT_PAYMENT", "PROOF_SUBMITTED", "PROOF_APPROVED", etc.
    val isLocked: Boolean = false,
    val lockReason: String? = null,
    val lockedByAdminId: String? = null,
    val isDemo: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null
)

@Entity(
    tableName = "payment_proofs",
    indices = [
        Index(value = ["stageId"]),
        Index(value = ["projectId"]),
        Index(value = ["version"])
    ]
)
data class PaymentProofEntity(
    @PrimaryKey
    val id: String,
    val stageId: String,
    val projectId: String,
    val version: Int,
    val amount: Double,
    val currency: String,
    val paymentMethod: String,
    val paymentDate: String,
    val invoiceReference: String,
    val transactionReference: String,
    val description: String,
    val attachmentsJson: String, // JSON array of EvidenceAttachment
    val submittedByMemberId: String,
    val submittedByDisplayName: String,
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String, // e.g. "UNDER_REVIEW", "APPROVED", "REJECTED", "NEEDS_MORE_EVIDENCE"
    val previousIssueAddressed: String? = null,
    val supersedesProofId: String? = null,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "proof_reviews",
    indices = [
        Index(value = ["proofId"]),
        Index(value = ["reviewerMemberId"])
    ]
)
data class ProofReviewEntity(
    @PrimaryKey
    val id: String,
    val proofId: String,
    val reviewerMemberId: String,
    val reviewerDisplayName: String,
    val reviewerRole: String, // e.g. "@Project Contributor", "@Admin", "@Finance Admin"
    val decision: String, // "APPROVED", "NOT_SATISFIED", "REQUEST_MORE_EVIDENCE"
    val reason: String? = null,
    val proofVersion: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(
    tableName = "evidence_requests",
    indices = [
        Index(value = ["stageId"]),
        Index(value = ["proofId"])
    ]
)
data class EvidenceRequestEntity(
    @PrimaryKey
    val id: String,
    val stageId: String,
    val proofId: String,
    val requestedByMemberId: String,
    val requestedByName: String,
    val specificEvidenceRequired: String,
    val deadlineDate: String?,
    val status: String, // "PENDING", "FULFILLED", "EXPIRED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "distribution_proposals",
    indices = [
        Index(value = ["stageId"]),
        Index(value = ["version"])
    ]
)
data class DistributionProposalEntity(
    @PrimaryKey
    val id: String,
    val stageId: String,
    val projectId: String,
    val version: Int,
    val grossAmount: Double,
    val currency: String,
    val adminFeePercent: Double,
    val adminFeeAmount: Double,
    val charityFeePercent: Double,
    val charityFeeAmount: Double,
    val workPoolAmount: Double,
    val rationale: String,
    val submittedByMemberId: String,
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String, // "UNDER_REVIEW", "APPROVED", "NEEDS_REVISION"
    val supersedesProposalId: String? = null,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "distribution_allocations",
    indices = [
        Index(value = ["proposalId"]),
        Index(value = ["memberId"])
    ]
)
data class DistributionAllocationEntity(
    @PrimaryKey
    val id: String,
    val proposalId: String,
    val stageId: String,
    val memberId: String,
    val memberDisplayName: String,
    val percentage: Double,
    val calculatedAmount: Double,
    val role: String,
    val workContributionRationale: String,
    val isAdmin: Boolean = false,
    val separateAdminFeeAmount: Double = 0.0,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "distribution_reviews",
    indices = [
        Index(value = ["proposalId"]),
        Index(value = ["reviewerMemberId"])
    ]
)
data class DistributionReviewEntity(
    @PrimaryKey
    val id: String,
    val proposalId: String,
    val reviewerMemberId: String,
    val reviewerDisplayName: String,
    val decision: String, // "APPROVED", "NOT_SATISFIED"
    val reason: String? = null,
    val proposalVersion: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(
    tableName = "owner_transfers",
    indices = [
        Index(value = ["stageId"]),
        Index(value = ["projectId"]),
        Index(value = ["transactionReference"])
    ]
)
data class OwnerTransferEntity(
    @PrimaryKey
    val id: String,
    val stageId: String,
    val projectId: String,
    val requiredAmount: Double,
    val submittedAmount: Double,
    val currency: String,
    val paymentMode: String,
    val transactionReference: String,
    val cryptoHash: String? = null,
    val screenshotUrl: String? = null,
    val notes: String? = null,
    val submittedByMemberId: String,
    val submittedAt: Long = System.currentTimeMillis(),
    val adminVerificationStatus: String, // "AWAITING_CONFIRMATION", "VERIFIED", "REJECTED_OR_INCORRECT", "PARTIALLY_RECEIVED", "PENDING_SETTLEMENT"
    val actualReceivedAmount: Double = 0.0,
    val adminVerifiedBy: String? = null,
    val adminVerifiedAt: Long? = null,
    val adminRejectionReason: String? = null,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "payouts",
    indices = [
        Index(value = ["stageId"]),
        Index(value = ["projectId"]),
        Index(value = ["memberId"]),
        Index(value = ["status"])
    ]
)
data class PayoutEntity(
    @PrimaryKey
    val id: String,
    val stageId: String,
    val projectId: String,
    val memberId: String,
    val memberDisplayName: String,
    val amount: Double,
    val currency: String,
    val percentage: Double,
    val paymentMethod: String,
    val status: String, // "PENDING", "INITIATED", "PROCESSING", "SENT", "CONFIRMED", "DISPUTED", "FAILED"
    val transactionReference: String? = null,
    val cryptoHash: String? = null,
    val proofScreenshot: String? = null,
    val initiatedAt: Long? = null,
    val sentAt: Long? = null,
    val confirmedAt: Long? = null,
    val confirmationStatus: String? = null,
    val disputeReason: String? = null,
    val expectedSettlementNote: String? = null,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "payout_confirmations",
    indices = [
        Index(value = ["payoutId"]),
        Index(value = ["memberId"])
    ]
)
data class PayoutConfirmationEntity(
    @PrimaryKey
    val id: String,
    val payoutId: String,
    val stageId: String,
    val memberId: String,
    val status: String, // "CONFIRMED", "DISPUTED"
    val reason: String? = null,
    val confirmedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "financial_disputes",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["stageId"]),
        Index(value = ["status"])
    ]
)
data class FinancialDisputeEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val stageId: String,
    val createdByMemberId: String,
    val createdByName: String,
    val disputeType: String,
    val reason: String,
    val status: String, // "OPEN", "UNDER_REVIEW", "WAITING_FOR_EVIDENCE", "RESOLVED", "REJECTED", "ESCALATED"
    val resolutionReason: String? = null,
    val resolvedByAdminId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val isDemo: Boolean = false
)

@Entity(
    tableName = "financial_ledger",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["stageId"]),
        Index(value = ["entryType"])
    ]
)
data class FinancialLedgerEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val stageId: String?,
    val entryType: String,
    val source: String,
    val destination: String,
    val amount: Double,
    val currency: String,
    val reference: String,
    val reversalOfId: String? = null,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(
    tableName = "financial_audit_log",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["stageId"]),
        Index(value = ["actorMemberId"])
    ]
)
data class FinancialAuditLogEntity(
    @PrimaryKey
    val id: String,
    val actorMemberId: String,
    val actorDisplayName: String,
    val action: String,
    val projectId: String,
    val stageId: String?,
    val entityType: String,
    val entityId: String?,
    val beforeState: String?,
    val afterState: String?,
    val reason: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

@Entity(tableName = "financial_settings")
data class FinancialSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val adminFeePercent: Double = 5.0,
    val charityFeePercent: Double = 0.0,
    val defaultReviewHours: Int = 48,
    val defaultConfirmationDays: Int = 7,
    val maxProofVersions: Int = 20,
    val autoEscalateDisputeThreshold: Int = 3
)

@Entity(
    tableName = "member_payment_methods",
    indices = [
        Index(value = ["memberId"])
    ]
)
data class MemberPaymentMethodEntity(
    @PrimaryKey
    val id: String,
    val memberId: String,
    val methodType: String,
    val providerName: String,
    val maskedAccountInfo: String, // e.g. "••••4321" or "alex@upi"
    val currency: String = "INR",
    val isVerified: Boolean = true,
    val isDefault: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payroll_id_counters")
data class PayrollIdCounterEntity(
    @PrimaryKey
    val counterName: String,
    val nextValue: Int
)
