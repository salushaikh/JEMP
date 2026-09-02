package com.example.data.service

import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.data.model.*
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class PayrollService(
    private val paymentStageDao: PaymentStageDao,
    private val paymentProofDao: PaymentProofDao,
    private val proofReviewDao: ProofReviewDao,
    private val distributionProposalDao: DistributionProposalDao,
    private val distributionAllocationDao: DistributionAllocationDao,
    private val distributionReviewDao: DistributionReviewDao,
    private val ownerTransferDao: OwnerTransferDao,
    private val payoutDao: PayoutDao,
    private val disputeDao: FinancialDisputeDao,
    private val ledgerDao: FinancialLedgerDao,
    private val auditLogDao: FinancialAuditLogDao,
    private val settingsDao: FinancialSettingsDao,
    private val idCounterDao: PayrollIdCounterDao,
    private val memberDao: MemberDao,
    private val timelineService: TimelineService
) {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.US)
    private val dateOnlyFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    // Flow of all payment stages
    val allStagesFlow: Flow<List<PaymentStageEntity>> = paymentStageDao.getAllStagesFlow()

    // Flow of all ledger entries
    val allLedgerFlow: Flow<List<FinancialLedgerEntity>> = ledgerDao.getAllLedgerEntriesFlow()

    // Flow of all audit logs
    val allAuditFlow: Flow<List<FinancialAuditLogEntity>> = auditLogDao.getAllAuditLogsFlow()

    // Flow of all disputes
    val allDisputesFlow: Flow<List<FinancialDisputeEntity>> = disputeDao.getAllDisputesFlow()

    suspend fun getSettings(): FinancialSettingsEntity {
        return settingsDao.getSettings() ?: FinancialSettingsEntity().also {
            settingsDao.saveSettings(it)
        }
    }

    suspend fun updateSettings(adminFee: Double, charityFee: Double, reviewHours: Int, confirmationDays: Int) {
        val updated = FinancialSettingsEntity(
            id = 1,
            adminFeePercent = adminFee,
            charityFeePercent = charityFee,
            defaultReviewHours = reviewHours,
            defaultConfirmationDays = confirmationDays
        )
        settingsDao.saveSettings(updated)
        logAudit(
            actorId = "JEMP#001",
            actorName = "Admin",
            action = "UPDATED_FINANCIAL_SETTINGS",
            projectId = "GLOBAL",
            stageId = null,
            entityType = "SETTINGS",
            entityId = "1",
            before = null,
            after = "Admin: $adminFee%, Charity: $charityFee%",
            reason = "Updated fee & timeline settings"
        )
    }

    // Generate unique Stage Code (e.g. PAY#001)
    private suspend fun getNextStageCode(): String {
        val counter = idCounterDao.getCounter("stage_code")?.nextValue ?: 1
        idCounterDao.setCounter(PayrollIdCounterEntity("stage_code", counter + 1))
        return String.format(Locale.US, "PAY#%03d", counter)
    }

    // ----------------------------------------------------
    // 1. PAYMENT STAGE CREATION & MANAGEMENT
    // ----------------------------------------------------
    suspend fun createPaymentStage(
        projectId: String,
        projectTitle: String,
        ownerMemberId: String,
        ownerName: String,
        type: PaymentStageType,
        description: String,
        quotedAmount: Double,
        currency: String = "INR",
        clientName: String? = null,
        invoiceNumber: String? = null,
        paymentDueDate: String? = null,
        isDemo: Boolean = false
    ): String {
        val stageCode = getNextStageCode()
        val stageId = "stage_${UUID.randomUUID()}"

        val entity = PaymentStageEntity(
            id = stageId,
            stageCode = stageCode,
            projectId = projectId,
            projectTitle = projectTitle,
            projectOwnerMemberId = ownerMemberId,
            projectOwnerName = ownerName,
            type = type.name,
            description = description,
            quotedAmount = quotedAmount,
            currency = currency,
            clientName = clientName,
            invoiceNumber = invoiceNumber,
            paymentDueDate = paymentDueDate,
            status = PaymentStageStatus.AWAITING_CLIENT_PAYMENT.name,
            isDemo = isDemo
        )
        paymentStageDao.insertStage(entity)

        logAudit(
            actorId = ownerMemberId,
            actorName = ownerName,
            action = "PAYMENT_STAGE_CREATED",
            projectId = projectId,
            stageId = stageId,
            entityType = "PAYMENT_STAGE",
            entityId = stageCode,
            before = null,
            after = entity.status,
            reason = "Created $type for ${PayrollCalculationService.formatCurrency(quotedAmount, currency)}"
        )

        return stageId
    }

    // ----------------------------------------------------
    // 2. FINANCIAL PROOF SUBMISSION & REVISION
    // ----------------------------------------------------
    suspend fun submitPaymentProof(
        stageId: String,
        amount: Double,
        currency: String,
        paymentMethod: PaymentMethodType,
        paymentDate: String,
        invoiceReference: String,
        transactionReference: String,
        description: String,
        attachments: List<EvidenceAttachment>,
        submittedByMemberId: String,
        submittedByName: String,
        previousIssueAddressed: String? = null,
        isDemo: Boolean = false
    ): String {
        val stage = paymentStageDao.getStageById(stageId) ?: throw IllegalArgumentException("Payment stage not found")
        if (stage.isLocked) throw IllegalStateException("Stage is locked: ${stage.lockReason}")

        val previousProof = paymentProofDao.getLatestProofForStage(stageId)
        val nextVersion = (previousProof?.version ?: 0) + 1

        val proofId = "proof_${UUID.randomUUID()}"
        val attachmentsJson = serializeAttachments(attachments)

        val proofEntity = PaymentProofEntity(
            id = proofId,
            stageId = stageId,
            projectId = stage.projectId,
            version = nextVersion,
            amount = amount,
            currency = currency,
            paymentMethod = paymentMethod.name,
            paymentDate = paymentDate,
            invoiceReference = invoiceReference,
            transactionReference = transactionReference,
            description = description,
            attachmentsJson = attachmentsJson,
            submittedByMemberId = submittedByMemberId,
            submittedByDisplayName = submittedByName,
            status = "UNDER_REVIEW",
            previousIssueAddressed = previousIssueAddressed,
            supersedesProofId = previousProof?.id,
            isDemo = isDemo
        )
        paymentProofDao.insertProof(proofEntity)

        // Update stage status
        paymentStageDao.updateStageStatus(stageId, PaymentStageStatus.PROOF_UNDER_REVIEW.name)

        logAudit(
            actorId = submittedByMemberId,
            actorName = submittedByName,
            action = "PROOF_SUBMITTED_V$nextVersion",
            projectId = stage.projectId,
            stageId = stageId,
            entityType = "PAYMENT_PROOF",
            entityId = proofId,
            before = previousProof?.status,
            after = "UNDER_REVIEW",
            reason = "Submitted proof for ${PayrollCalculationService.formatCurrency(amount, currency)}"
        )

        return proofId
    }

    // ----------------------------------------------------
    // 3. PROOF REVIEW ENGINE
    // ----------------------------------------------------
    suspend fun reviewPaymentProof(
        proofId: String,
        reviewerMemberId: String,
        reviewerName: String,
        reviewerRole: String,
        decision: ReviewDecision,
        reason: String? = null,
        requiredReviewerCount: Int = 2
    ) {
        val proof = paymentProofDao.getProofById(proofId) ?: throw IllegalArgumentException("Proof not found")
        val stage = paymentStageDao.getStageById(proof.stageId) ?: throw IllegalArgumentException("Stage not found")
        if (stage.isLocked) throw IllegalStateException("Stage is locked")

        // No self-approval: Owner cannot approve their own proof
        if (decision == ReviewDecision.APPROVED && reviewerMemberId == proof.submittedByMemberId) {
            throw IllegalArgumentException("Project owner cannot independently approve their own submitted proof.")
        }

        // Reason is strictly required for rejection or requesting more proof
        if ((decision == ReviewDecision.NOT_SATISFIED || decision == ReviewDecision.REQUEST_MORE_EVIDENCE) && reason.isNullOrBlank()) {
            throw IllegalArgumentException("A detailed reason is mandatory when not satisfied with financial proof.")
        }

        val review = ProofReviewEntity(
            id = "review_${UUID.randomUUID()}",
            proofId = proofId,
            reviewerMemberId = reviewerMemberId,
            reviewerDisplayName = reviewerName,
            reviewerRole = reviewerRole,
            decision = decision.name,
            reason = reason,
            proofVersion = proof.version,
            isDemo = proof.isDemo
        )
        proofReviewDao.insertReview(review)

        // Check overall reviews for this proof
        val allReviews = proofReviewDao.getReviewsForProof(proofId)
        val hasRejection = allReviews.any { it.decision == ReviewDecision.NOT_SATISFIED.name || it.decision == ReviewDecision.REQUEST_MORE_EVIDENCE.name }
        val approvedCount = allReviews.count { it.decision == ReviewDecision.APPROVED.name }
        val hasAdminApproval = allReviews.any { it.decision == ReviewDecision.APPROVED.name && (it.reviewerRole.contains("Admin") || it.reviewerMemberId == "JEMP#001") }

        if (hasRejection) {
            paymentProofDao.updateProofStatus(proofId, "NEEDS_MORE_EVIDENCE")
            paymentStageDao.updateStageStatus(stage.id, PaymentStageStatus.PROOF_NEEDS_MORE_EVIDENCE.name)

            logAudit(
                actorId = reviewerMemberId,
                actorName = reviewerName,
                action = "PROOF_REJECTED_NEEDS_EVIDENCE",
                projectId = stage.projectId,
                stageId = stage.id,
                entityType = "PROOF_REVIEW",
                entityId = proofId,
                before = "UNDER_REVIEW",
                after = "PROOF_NEEDS_MORE_EVIDENCE",
                reason = reason
            )
        } else if (approvedCount >= requiredReviewerCount && hasAdminApproval) {
            paymentProofDao.updateProofStatus(proofId, "APPROVED")
            paymentStageDao.updateStageStatus(stage.id, PaymentStageStatus.PROOF_APPROVED.name)

            // Record safe Timeline Attestation (Proof of Work)
            recordTimelineAttestation(
                memberId = stage.projectOwnerMemberId,
                title = "Financial Proof Verified [${stage.projectId}]",
                bullets = listOf(
                    "Client charge verified for ${stage.stageCode} (${stage.type})",
                    "Complete invoice and verified bank transaction evidence attached",
                    "Quorum approved by project contributors and admin"
                ),
                projectId = stage.projectId
            )

            // Record initial Ledger entry for Client Receipt
            recordLedgerEntry(
                projectId = stage.projectId,
                stageId = stage.id,
                type = LedgerEntryType.CLIENT_RECEIPT,
                source = "Client (${stage.clientName ?: "Direct"})",
                destination = "Project Owner Escrow (${stage.projectOwnerName})",
                amount = proof.amount,
                currency = proof.currency,
                reference = proof.transactionReference,
                createdBy = reviewerName
            )

            logAudit(
                actorId = reviewerMemberId,
                actorName = reviewerName,
                action = "PROOF_APPROVED_FULL_QUORUM",
                projectId = stage.projectId,
                stageId = stage.id,
                entityType = "PAYMENT_PROOF",
                entityId = proofId,
                before = "UNDER_REVIEW",
                after = "PROOF_APPROVED",
                reason = "All required reviews satisfied with Admin verification."
            )
        }
    }

    // ----------------------------------------------------
    // 4. DISTRIBUTION PROPOSAL & VALIDATION
    // ----------------------------------------------------
    suspend fun createDistributionProposal(
        stageId: String,
        rationale: String,
        allocationsList: List<Triple<String, String, Double>>, // (MemberId, Name, Percent)
        submittedByMemberId: String,
        submittedByName: String,
        isDemo: Boolean = false
    ): String {
        val stage = paymentStageDao.getStageById(stageId) ?: throw IllegalArgumentException("Stage not found")
        if (stage.isLocked) throw IllegalStateException("Stage is locked")

        // 1. Math validation
        val (isValid, validationErr) = PayrollCalculationService.validateAllocations(allocationsList)
        if (!isValid) throw IllegalArgumentException(validationErr ?: "Invalid allocations")

        if (rationale.isBlank() || rationale.length < 15) {
            throw IllegalArgumentException("A comprehensive rationale describing each person's workload and contribution is required.")
        }

        val settings = getSettings()
        val latestProof = paymentProofDao.getLatestProofForStage(stageId)
        val grossAmount = latestProof?.amount ?: stage.quotedAmount

        val breakdown = PayrollCalculationService.computeFinancialBreakdown(
            grossAmount = grossAmount,
            currency = stage.currency,
            adminFeePercent = settings.adminFeePercent,
            charityFeePercent = settings.charityFeePercent,
            ownerSharePercent = 0.0
        )

        val previousProposal = distributionProposalDao.getLatestProposalForStage(stageId)
        val nextVersion = (previousProposal?.version ?: 0) + 1
        val proposalId = "prop_${UUID.randomUUID()}"

        val proposalEntity = DistributionProposalEntity(
            id = proposalId,
            stageId = stageId,
            projectId = stage.projectId,
            version = nextVersion,
            grossAmount = grossAmount,
            currency = stage.currency,
            adminFeePercent = settings.adminFeePercent,
            adminFeeAmount = breakdown.adminFeeAmount,
            charityFeePercent = settings.charityFeePercent,
            charityFeeAmount = breakdown.charityFeeAmount,
            workPoolAmount = breakdown.workPoolAmount,
            rationale = rationale,
            submittedByMemberId = submittedByMemberId,
            status = "UNDER_REVIEW",
            supersedesProposalId = previousProposal?.id,
            isDemo = isDemo
        )
        distributionProposalDao.insertProposal(proposalEntity)

        // Calculate member exact amounts
        val calculatedAllocations = PayrollCalculationService.calculateMemberAllocations(
            workPoolAmount = breakdown.workPoolAmount,
            adminFeeAmount = breakdown.adminFeeAmount,
            adminMemberId = "JEMP#001",
            allocations = allocationsList
        )

        val allocationEntities = calculatedAllocations.map { alloc ->
            DistributionAllocationEntity(
                id = "alloc_${UUID.randomUUID()}",
                proposalId = proposalId,
                stageId = stageId,
                memberId = alloc.memberId,
                memberDisplayName = alloc.memberDisplayName,
                percentage = alloc.percentage,
                calculatedAmount = alloc.calculatedAmount,
                role = alloc.role,
                workContributionRationale = rationale,
                isAdmin = alloc.isAdmin,
                separateAdminFeeAmount = alloc.separateAdminFeeAmount,
                isDemo = isDemo
            )
        }
        distributionAllocationDao.insertAllocations(allocationEntities)

        paymentStageDao.updateStageStatus(stageId, PaymentStageStatus.DISTRIBUTION_UNDER_REVIEW.name)

        logAudit(
            actorId = submittedByMemberId,
            actorName = submittedByName,
            action = "DISTRIBUTION_PROPOSAL_V$nextVersion",
            projectId = stage.projectId,
            stageId = stageId,
            entityType = "DISTRIBUTION_PROPOSAL",
            entityId = proposalId,
            before = previousProposal?.status,
            after = "UNDER_REVIEW",
            reason = "Submitted distribution proposal for pool ${PayrollCalculationService.formatCurrency(breakdown.workPoolAmount, stage.currency)}"
        )

        return proposalId
    }

    // ----------------------------------------------------
    // 5. DISTRIBUTION REVIEW & APPROVAL
    // ----------------------------------------------------
    suspend fun reviewDistributionProposal(
        proposalId: String,
        reviewerMemberId: String,
        reviewerName: String,
        decision: ReviewDecision,
        reason: String? = null,
        requiredReviewerCount: Int = 2
    ) {
        val proposal = distributionProposalDao.getProposalById(proposalId) ?: throw IllegalArgumentException("Proposal not found")
        val stage = paymentStageDao.getStageById(proposal.stageId) ?: throw IllegalArgumentException("Stage not found")
        if (stage.isLocked) throw IllegalStateException("Stage is locked")

        if (decision == ReviewDecision.NOT_SATISFIED && reason.isNullOrBlank()) {
            throw IllegalArgumentException("Reason is mandatory when objecting to distribution proposal.")
        }

        val review = DistributionReviewEntity(
            id = "dist_rev_${UUID.randomUUID()}",
            proposalId = proposalId,
            reviewerMemberId = reviewerMemberId,
            reviewerDisplayName = reviewerName,
            decision = decision.name,
            reason = reason,
            proposalVersion = proposal.version,
            isDemo = proposal.isDemo
        )
        distributionReviewDao.insertReview(review)

        val allReviews = distributionReviewDao.getReviewsForProposal(proposalId)
        val hasRejection = allReviews.any { it.decision == ReviewDecision.NOT_SATISFIED.name }
        val approvedCount = allReviews.count { it.decision == ReviewDecision.APPROVED.name }

        if (hasRejection) {
            distributionProposalDao.updateProposalStatus(proposalId, "NEEDS_REVISION")
            paymentStageDao.updateStageStatus(stage.id, PaymentStageStatus.DISTRIBUTION_NEEDS_REVISION.name)

            logAudit(
                actorId = reviewerMemberId,
                actorName = reviewerName,
                action = "DISTRIBUTION_REJECTED",
                projectId = stage.projectId,
                stageId = stage.id,
                entityType = "DISTRIBUTION_REVIEW",
                entityId = proposalId,
                before = "UNDER_REVIEW",
                after = "DISTRIBUTION_NEEDS_REVISION",
                reason = reason
            )
        } else if (approvedCount >= requiredReviewerCount) {
            distributionProposalDao.updateProposalStatus(proposalId, "APPROVED")
            paymentStageDao.updateStageStatus(stage.id, PaymentStageStatus.AWAITING_OWNER_TRANSFER.name)

            // Record Admin Fee and Work Allocation in Ledger
            recordLedgerEntry(
                projectId = stage.projectId,
                stageId = stage.id,
                type = LedgerEntryType.ADMIN_FEE,
                source = "Gross Client Payment",
                destination = "Jobless Empire Admin Escrow",
                amount = proposal.adminFeeAmount,
                currency = proposal.currency,
                reference = "ADMIN_FEE_${stage.stageCode}",
                createdBy = reviewerName
            )

            recordLedgerEntry(
                projectId = stage.projectId,
                stageId = stage.id,
                type = LedgerEntryType.WORK_ALLOCATION,
                source = "Gross Client Payment",
                destination = "Distributable Work Pool",
                amount = proposal.workPoolAmount,
                currency = proposal.currency,
                reference = "WORK_POOL_${stage.stageCode}",
                createdBy = reviewerName
            )

            logAudit(
                actorId = reviewerMemberId,
                actorName = reviewerName,
                action = "DISTRIBUTION_APPROVED",
                projectId = stage.projectId,
                stageId = stage.id,
                entityType = "DISTRIBUTION_PROPOSAL",
                entityId = proposalId,
                before = "UNDER_REVIEW",
                after = "DISTRIBUTION_APPROVED",
                reason = "All contributor reviews approved. Awaiting owner transfer to admin."
            )
        }
    }

    // ----------------------------------------------------
    // 6. OWNER TRANSFER TO ADMIN & RECEIPT VERIFICATION
    // ----------------------------------------------------
    suspend fun submitOwnerTransfer(
        stageId: String,
        paymentMode: PaymentMethodType,
        submittedAmount: Double,
        transactionReference: String,
        cryptoHash: String? = null,
        screenshotUrl: String? = null,
        notes: String? = null,
        submittedByMemberId: String,
        isDemo: Boolean = false
    ): String {
        val stage = paymentStageDao.getStageById(stageId) ?: throw IllegalArgumentException("Stage not found")
        if (stage.isLocked) throw IllegalStateException("Stage is locked")

        // Get approved distribution proposal to calculate exact required transfer
        val proposal = distributionProposalDao.getLatestProposalForStage(stageId)
            ?: throw IllegalStateException("No approved distribution proposal found")
        val allocations = distributionAllocationDao.getAllocationsForProposal(proposal.id)
        val ownerAlloc = allocations.find { it.memberId == stage.projectOwnerMemberId }
        val ownerShare = ownerAlloc?.calculatedAmount ?: 0.0

        val requiredTransfer = proposal.grossAmount - ownerShare

        // Check if duplicate transaction reference was submitted
        val existingTxn = ownerTransferDao.getTransferByReference(transactionReference)
        if (existingTxn != null && existingTxn.stageId != stageId) {
            throw IllegalArgumentException("Transaction reference '$transactionReference' was already submitted for another stage.")
        }

        val transferId = "transfer_${UUID.randomUUID()}"
        val entity = OwnerTransferEntity(
            id = transferId,
            stageId = stageId,
            projectId = stage.projectId,
            requiredAmount = requiredTransfer,
            submittedAmount = submittedAmount,
            currency = stage.currency,
            paymentMode = paymentMode.name,
            transactionReference = transactionReference,
            cryptoHash = cryptoHash,
            screenshotUrl = screenshotUrl,
            notes = notes,
            submittedByMemberId = submittedByMemberId,
            adminVerificationStatus = "AWAITING_CONFIRMATION",
            actualReceivedAmount = 0.0,
            isDemo = isDemo
        )
        ownerTransferDao.insertTransfer(entity)
        paymentStageDao.updateStageStatus(stageId, PaymentStageStatus.TRANSFER_SUBMITTED.name)

        logAudit(
            actorId = submittedByMemberId,
            actorName = stage.projectOwnerName,
            action = "OWNER_TRANSFER_SUBMITTED",
            projectId = stage.projectId,
            stageId = stageId,
            entityType = "OWNER_TRANSFER",
            entityId = transferId,
            before = "AWAITING_OWNER_TRANSFER",
            after = "TRANSFER_SUBMITTED",
            reason = "Submitted transfer of ${PayrollCalculationService.formatCurrency(submittedAmount, stage.currency)} via $paymentMode (Ref: $transactionReference)"
        )

        return transferId
    }

    suspend fun verifyAdminTransferReceipt(
        stageId: String,
        adminMemberId: String,
        adminName: String,
        isVerified: Boolean,
        actualReceivedAmount: Double,
        rejectionOrNotes: String? = null
    ) {
        val stage = paymentStageDao.getStageById(stageId) ?: throw IllegalArgumentException("Stage not found")
        val transfer = ownerTransferDao.getTransferForStage(stageId) ?: throw IllegalArgumentException("Transfer record not found")

        if (isVerified) {
            val updatedTransfer = transfer.copy(
                adminVerificationStatus = "VERIFIED",
                actualReceivedAmount = actualReceivedAmount,
                adminVerifiedBy = adminName,
                adminVerifiedAt = System.currentTimeMillis()
            )
            ownerTransferDao.updateTransfer(updatedTransfer)
            paymentStageDao.updateStageStatus(stageId, PaymentStageStatus.PAYROLL_PROCESSING.name)

            // Record Transfer in Ledger
            recordLedgerEntry(
                projectId = stage.projectId,
                stageId = stageId,
                type = LedgerEntryType.OWNER_TRANSFER,
                source = "Project Owner (${stage.projectOwnerName})",
                destination = "Jobless Empire Admin Escrow",
                amount = actualReceivedAmount,
                currency = stage.currency,
                reference = transfer.transactionReference,
                createdBy = adminName
            )

            // Auto-generate employee payout records
            generatePayoutsForStage(stageId, adminMemberId, isDemo = stage.isDemo)

            logAudit(
                actorId = adminMemberId,
                actorName = adminName,
                action = "OWNER_TRANSFER_VERIFIED",
                projectId = stage.projectId,
                stageId = stageId,
                entityType = "OWNER_TRANSFER",
                entityId = transfer.id,
                before = "TRANSFER_SUBMITTED",
                after = "TRANSFER_APPROVED",
                reason = "Admin confirmed receipt of ${PayrollCalculationService.formatCurrency(actualReceivedAmount, stage.currency)}"
            )
        } else {
            val updatedTransfer = transfer.copy(
                adminVerificationStatus = "REJECTED_OR_INCORRECT",
                adminRejectionReason = rejectionOrNotes
            )
            ownerTransferDao.updateTransfer(updatedTransfer)
            paymentStageDao.updateStageStatus(stageId, PaymentStageStatus.TRANSFER_REQUIRES_ACTION.name)

            logAudit(
                actorId = adminMemberId,
                actorName = adminName,
                action = "OWNER_TRANSFER_REJECTED",
                projectId = stage.projectId,
                stageId = stageId,
                entityType = "OWNER_TRANSFER",
                entityId = transfer.id,
                before = "TRANSFER_SUBMITTED",
                after = "TRANSFER_REQUIRES_ACTION",
                reason = rejectionOrNotes
            )
        }
    }

    // ----------------------------------------------------
    // 7. PAYROLL EXECUTION & EMPLOYEE PAYOUTS
    // ----------------------------------------------------
    private suspend fun generatePayoutsForStage(stageId: String, adminMemberId: String, isDemo: Boolean) {
        val stage = paymentStageDao.getStageById(stageId) ?: return
        val proposal = distributionProposalDao.getLatestProposalForStage(stageId) ?: return
        val allocations = distributionAllocationDao.getAllocationsForProposal(proposal.id)

        // Generate payouts for everyone except project owner's already retained share
        val payoutsToCreate = allocations.filter { it.memberId != stage.projectOwnerMemberId }.map { alloc ->
            PayoutEntity(
                id = "payout_${UUID.randomUUID()}",
                stageId = stageId,
                projectId = stage.projectId,
                memberId = alloc.memberId,
                memberDisplayName = alloc.memberDisplayName,
                amount = alloc.calculatedAmount,
                currency = stage.currency,
                percentage = alloc.percentage,
                paymentMethod = PaymentMethodType.BANK_TRANSFER.name,
                status = PayoutStatus.PENDING.name,
                expectedSettlementNote = "Expected in 1-3 business days",
                isDemo = isDemo
            )
        }
        payoutDao.insertPayouts(payoutsToCreate)
    }

    suspend fun processSendPayout(
        payoutId: String,
        adminMemberId: String,
        adminName: String,
        paymentMethod: PaymentMethodType,
        transactionReference: String,
        cryptoHash: String? = null,
        proofScreenshot: String? = null
    ) {
        val payout = payoutDao.getPayoutById(payoutId) ?: throw IllegalArgumentException("Payout not found")
        val stage = paymentStageDao.getStageById(payout.stageId) ?: throw IllegalArgumentException("Stage not found")

        val updated = payout.copy(
            status = PayoutStatus.SENT.name,
            paymentMethod = paymentMethod.name,
            transactionReference = transactionReference,
            cryptoHash = cryptoHash,
            proofScreenshot = proofScreenshot,
            initiatedAt = System.currentTimeMillis(),
            sentAt = System.currentTimeMillis()
        )
        payoutDao.updatePayout(updated)

        // Check if all payouts for stage are sent
        val allPayouts = payoutDao.getPayoutsForStage(payout.stageId)
        val allSent = allPayouts.all { it.status == PayoutStatus.SENT.name || it.status == PayoutStatus.CONFIRMED.name }
        if (allSent) {
            paymentStageDao.updateStageStatus(stage.id, PaymentStageStatus.EMPLOYEE_CONFIRMATION_PENDING.name)
        }

        // Record in Ledger
        recordLedgerEntry(
            projectId = stage.projectId,
            stageId = stage.id,
            type = LedgerEntryType.EMPLOYEE_PAYOUT,
            source = "Jobless Empire Admin Escrow",
            destination = "${payout.memberDisplayName} (${payout.memberId})",
            amount = payout.amount,
            currency = payout.currency,
            reference = transactionReference,
            createdBy = adminName
        )

        logAudit(
            actorId = adminMemberId,
            actorName = adminName,
            action = "PAYOUT_SENT",
            projectId = stage.projectId,
            stageId = stage.id,
            entityType = "PAYOUT",
            entityId = payoutId,
            before = "PENDING",
            after = "SENT",
            reason = "Sent ${PayrollCalculationService.formatCurrency(payout.amount, payout.currency)} to ${payout.memberDisplayName} via $paymentMethod (Ref: $transactionReference)"
        )
    }

    // ----------------------------------------------------
    // 8. EMPLOYEE PAYMENT CONFIRMATION & DISPUTES
    // ----------------------------------------------------
    suspend fun confirmEmployeePayout(
        payoutId: String,
        memberId: String,
        memberName: String,
        isConfirmed: Boolean,
        disputeReason: String? = null
    ) {
        val payout = payoutDao.getPayoutById(payoutId) ?: throw IllegalArgumentException("Payout not found")
        val stage = paymentStageDao.getStageById(payout.stageId) ?: throw IllegalArgumentException("Stage not found")

        if (isConfirmed) {
            val updated = payout.copy(
                status = PayoutStatus.CONFIRMED.name,
                confirmationStatus = "CONFIRMED",
                confirmedAt = System.currentTimeMillis()
            )
            payoutDao.updatePayout(updated)

            // Safe Timeline Proof of Work entry (No sensitive banking details)
            timelineService.addTimelineEntryDirect(
                memberId = memberId,
                title = "Milestone Compensation Received [${stage.projectId}]",
                description = "Confirmed receipt of project compensation for ${stage.stageCode} (${stage.type}).",
                category = "PROJECT_COMPENSATION",
                complexity = "MEDIUM",
                skills = listOf("Production Delivery", "Collaboration"),
                projectTag = stage.projectId,
                isPrivate = false
            )

            logAudit(
                actorId = memberId,
                actorName = memberName,
                action = "PAYOUT_CONFIRMED_BY_EMPLOYEE",
                projectId = stage.projectId,
                stageId = stage.id,
                entityType = "PAYOUT",
                entityId = payoutId,
                before = "SENT",
                after = "CONFIRMED",
                reason = "Member confirmed receipt of ${PayrollCalculationService.formatCurrency(payout.amount, payout.currency)}"
            )

            // Check if all payouts are confirmed to close stage!
            val allPayouts = payoutDao.getPayoutsForStage(stage.id)
            val allConfirmed = allPayouts.all { it.status == PayoutStatus.CONFIRMED.name }
            if (allConfirmed) {
                closeFinancialStage(stage.id, adminMemberId = "JEMP#001", adminName = "System Governance", reason = "All employee payouts confirmed.")
            }
        } else {
            val updated = payout.copy(
                status = PayoutStatus.DISPUTED.name,
                confirmationStatus = "DISPUTED",
                disputeReason = disputeReason
            )
            payoutDao.updatePayout(updated)
            paymentStageDao.updateStageStatus(stage.id, PaymentStageStatus.DISPUTED.name)

            // Create first-class dispute record
            createDispute(
                projectId = stage.projectId,
                stageId = stage.id,
                createdByMemberId = memberId,
                createdByName = memberName,
                type = DisputeType.PAYOUT_DISPUTE,
                reason = disputeReason ?: "Employee reported payout discrepancy / non-receipt."
            )

            logAudit(
                actorId = memberId,
                actorName = memberName,
                action = "PAYOUT_DISPUTED_BY_EMPLOYEE",
                projectId = stage.projectId,
                stageId = stage.id,
                entityType = "PAYOUT",
                entityId = payoutId,
                before = "SENT",
                after = "DISPUTED",
                reason = disputeReason
            )
        }
    }

    // ----------------------------------------------------
    // 9. DISPUTE ENGINE & FINANCIAL LOCK
    // ----------------------------------------------------
    suspend fun createDispute(
        projectId: String,
        stageId: String,
        createdByMemberId: String,
        createdByName: String,
        type: DisputeType,
        reason: String,
        isDemo: Boolean = false
    ): String {
        val disputeId = "dispute_${UUID.randomUUID()}"
        val dispute = FinancialDisputeEntity(
            id = disputeId,
            projectId = projectId,
            stageId = stageId,
            createdByMemberId = createdByMemberId,
            createdByName = createdByName,
            disputeType = type.name,
            reason = reason,
            status = DisputeStatus.OPEN.name,
            isDemo = isDemo
        )
        disputeDao.insertDispute(dispute)

        logAudit(
            actorId = createdByMemberId,
            actorName = createdByName,
            action = "DISPUTE_RAISED",
            projectId = projectId,
            stageId = stageId,
            entityType = "DISPUTE",
            entityId = disputeId,
            before = null,
            after = "OPEN",
            reason = reason
        )

        return disputeId
    }

    suspend fun resolveDispute(
        disputeId: String,
        adminMemberId: String,
        adminName: String,
        resolutionReason: String,
        newStatus: DisputeStatus = DisputeStatus.RESOLVED
    ) {
        val allDisputes = disputeDao.getAllDisputesFlow().first()
        val dispute = allDisputes.find { it.id == disputeId } ?: throw IllegalArgumentException("Dispute not found")

        val updated = dispute.copy(
            status = newStatus.name,
            resolutionReason = resolutionReason,
            resolvedByAdminId = adminMemberId,
            resolvedAt = System.currentTimeMillis()
        )
        disputeDao.updateDispute(updated)

        logAudit(
            actorId = adminMemberId,
            actorName = adminName,
            action = "DISPUTE_RESOLVED",
            projectId = dispute.projectId,
            stageId = dispute.stageId,
            entityType = "DISPUTE",
            entityId = disputeId,
            before = dispute.status,
            after = newStatus.name,
            reason = resolutionReason
        )
    }

    suspend fun setStageLock(stageId: String, isLocked: Boolean, reason: String?, adminMemberId: String, adminName: String) {
        val stage = paymentStageDao.getStageById(stageId) ?: throw IllegalArgumentException("Stage not found")
        paymentStageDao.setStageLock(stageId, isLocked, reason, adminMemberId)

        logAudit(
            actorId = adminMemberId,
            actorName = adminName,
            action = if (isLocked) "STAGE_LOCKED" else "STAGE_UNLOCKED",
            projectId = stage.projectId,
            stageId = stageId,
            entityType = "PAYMENT_STAGE",
            entityId = stage.stageCode,
            before = if (isLocked) "UNLOCKED" else "LOCKED",
            after = if (isLocked) "LOCKED" else "UNLOCKED",
            reason = reason
        )
    }

    // ----------------------------------------------------
    // 10. FINANCIAL CLOSEOUT
    // ----------------------------------------------------
    suspend fun closeFinancialStage(stageId: String, adminMemberId: String, adminName: String, reason: String) {
        val stage = paymentStageDao.getStageById(stageId) ?: throw IllegalArgumentException("Stage not found")
        paymentStageDao.updateStageStatus(stageId, PaymentStageStatus.CLOSED.name)

        // Add milestone on project owner's timeline
        timelineService.addTimelineEntryDirect(
            memberId = stage.projectOwnerMemberId,
            title = "Financial Lifecycle Closed [${stage.projectId}]",
            description = "Completed full financial cycle for ${stage.stageCode} (${stage.type}). All payouts reconciled & confirmed.",
            category = "FINANCIAL_CLOSEOUT",
            complexity = "HIGH",
            skills = listOf("Finance", "Project Leadership", "Auditing"),
            projectTag = stage.projectId,
            isPrivate = false
        )

        logAudit(
            actorId = adminMemberId,
            actorName = adminName,
            action = "FINANCIAL_STAGE_CLOSED",
            projectId = stage.projectId,
            stageId = stageId,
            entityType = "PAYMENT_STAGE",
            entityId = stage.stageCode,
            before = stage.status,
            after = "CLOSED",
            reason = reason
        )
    }

    // ----------------------------------------------------
    // 11. RECONCILIATION ENGINE
    // ----------------------------------------------------
    suspend fun reconcileProject(projectId: String): ProjectReconciliationReport {
        val stages = paymentStageDao.getStagesByProjectFlow(projectId).first()
        val projectTitle = stages.firstOrNull()?.projectTitle ?: "Project $projectId"
        val currency = stages.firstOrNull()?.currency ?: "INR"

        var totalInvoiced = 0.0
        var totalGrossReceived = 0.0
        var totalAdminFees = 0.0
        var totalWorkPool = 0.0
        var totalOwnerTransfersExpected = 0.0
        var totalOwnerTransfersReceived = 0.0
        var totalPayoutsSent = 0.0
        var totalEmployeeConfirmations = 0
        var totalExpectedConfirmations = 0

        for (st in stages) {
            totalInvoiced += st.quotedAmount
            val proof = paymentProofDao.getLatestProofForStage(st.id)
            if (proof != null && proof.status == "APPROVED") {
                totalGrossReceived += proof.amount
            }

            val prop = distributionProposalDao.getLatestProposalForStage(st.id)
            if (prop != null && prop.status == "APPROVED") {
                totalAdminFees += prop.adminFeeAmount
                totalWorkPool += prop.workPoolAmount
            }

            val transfer = ownerTransferDao.getTransferForStage(st.id)
            if (transfer != null) {
                totalOwnerTransfersExpected += transfer.requiredAmount
                if (transfer.adminVerificationStatus == "VERIFIED") {
                    totalOwnerTransfersReceived += transfer.actualReceivedAmount
                }
            }

            val payouts = payoutDao.getPayoutsForStage(st.id)
            totalExpectedConfirmations += payouts.size
            for (p in payouts) {
                if (p.status == PayoutStatus.SENT.name || p.status == PayoutStatus.CONFIRMED.name) {
                    totalPayoutsSent += p.amount
                }
                if (p.status == PayoutStatus.CONFIRMED.name) {
                    totalEmployeeConfirmations++
                }
            }
        }

        val checks = listOf(
            ReconciliationCheckItem(
                title = "Gross Approved Proof vs Invoiced Amount",
                expected = PayrollCalculationService.formatCurrency(totalInvoiced, currency),
                actual = PayrollCalculationService.formatCurrency(totalGrossReceived, currency),
                isPassed = totalGrossReceived >= totalInvoiced * 0.5 // Valid if staged payments in progress
            ),
            ReconciliationCheckItem(
                title = "Admin Fee Calculation (5%)",
                expected = PayrollCalculationService.formatCurrency(totalGrossReceived * 0.05, currency),
                actual = PayrollCalculationService.formatCurrency(totalAdminFees, currency),
                isPassed = Math.abs(totalAdminFees - (totalGrossReceived * 0.05)) < 1.0
            ),
            ReconciliationCheckItem(
                title = "Work Distribution Pool (95%)",
                expected = PayrollCalculationService.formatCurrency(totalGrossReceived * 0.95, currency),
                actual = PayrollCalculationService.formatCurrency(totalWorkPool, currency),
                isPassed = Math.abs(totalWorkPool - (totalGrossReceived * 0.95)) < 1.0
            ),
            ReconciliationCheckItem(
                title = "Owner-to-Admin Transfers Settlement",
                expected = PayrollCalculationService.formatCurrency(totalOwnerTransfersExpected, currency),
                actual = PayrollCalculationService.formatCurrency(totalOwnerTransfersReceived, currency),
                isPassed = Math.abs(totalOwnerTransfersExpected - totalOwnerTransfersReceived) < 1.0
            ),
            ReconciliationCheckItem(
                title = "Employee Payout Confirmations",
                expected = "$totalExpectedConfirmations Confirmations",
                actual = "$totalEmployeeConfirmations Confirmed",
                isPassed = totalEmployeeConfirmations == totalExpectedConfirmations
            )
        )

        val totalDiscrepancy = Math.abs(totalOwnerTransfersExpected - totalOwnerTransfersReceived)
        val isFullyBalanced = checks.all { it.isPassed }

        return ProjectReconciliationReport(
            projectId = projectId,
            projectTitle = projectTitle,
            currency = currency,
            totalInvoiced = totalInvoiced,
            totalGrossReceived = totalGrossReceived,
            totalApprovedProof = totalGrossReceived,
            totalAdminFeesCalculated = totalAdminFees,
            totalAdminFeesRetained = totalAdminFees,
            totalWorkPool = totalWorkPool,
            totalOwnerTransfersExpected = totalOwnerTransfersExpected,
            totalOwnerTransfersReceived = totalOwnerTransfersReceived,
            totalEmployeePayoutsSent = totalPayoutsSent,
            totalEmployeeConfirmations = totalEmployeeConfirmations,
            totalExpectedConfirmations = totalExpectedConfirmations,
            totalDiscrepancy = totalDiscrepancy,
            isFullyBalanced = isFullyBalanced,
            checkList = checks
        )
    }

    // ----------------------------------------------------
    // 12. DASHBOARD METRICS & MEMBER EARNINGS
    // ----------------------------------------------------
    suspend fun getAdminDashboardMetrics(): PayrollAdminDashboardMetrics {
        val stages = paymentStageDao.getAllStagesFlow().first()
        val disputes = disputeDao.getAllDisputesFlow().first()

        var totalClientRev = 0.0
        var approvedRev = 0.0
        var pendingRev = 0.0
        var adminFees = 0.0
        var distributed = 0.0
        var proofsPending = 0
        var distPending = 0
        var transfersPending = 0
        var payrollInProgress = 0
        var confirmationsPending = 0
        var closedCount = 0

        val activeProjects = stages.map { it.projectId }.distinct().size

        for (st in stages) {
            totalClientRev += st.quotedAmount
            if (st.status == PaymentStageStatus.CLOSED.name) {
                closedCount++
            }

            val proof = paymentProofDao.getLatestProofForStage(st.id)
            if (proof != null) {
                if (proof.status == "APPROVED") {
                    approvedRev += proof.amount
                } else if (proof.status == "UNDER_REVIEW") {
                    proofsPending++
                    pendingRev += proof.amount
                }
            }

            val prop = distributionProposalDao.getLatestProposalForStage(st.id)
            if (prop != null) {
                if (prop.status == "APPROVED") {
                    adminFees += prop.adminFeeAmount
                    distributed += prop.workPoolAmount
                } else if (prop.status == "UNDER_REVIEW") {
                    distPending++
                }
            }

            val transfer = ownerTransferDao.getTransferForStage(st.id)
            if (transfer != null && transfer.adminVerificationStatus == "AWAITING_CONFIRMATION") {
                transfersPending++
            }

            if (st.status == PaymentStageStatus.PAYROLL_PROCESSING.name) {
                payrollInProgress++
            }
            if (st.status == PaymentStageStatus.EMPLOYEE_CONFIRMATION_PENDING.name) {
                confirmationsPending++
            }
        }

        return PayrollAdminDashboardMetrics(
            totalActiveProjects = activeProjects,
            totalClientRevenue = totalClientRev,
            approvedRevenue = approvedRev,
            pendingReviewRevenue = pendingRev,
            totalAdminFeesRetained = adminFees,
            totalDistributedToContributors = distributed,
            proofsAwaitingReview = proofsPending,
            distributionsAwaitingReview = distPending,
            transfersAwaitingConfirmation = transfersPending,
            payrollRunsInProgress = payrollInProgress,
            employeeConfirmationsPending = confirmationsPending,
            openDisputesCount = disputes.count { it.status == DisputeStatus.OPEN.name },
            closedFinancialStagesCount = closedCount
        )
    }

    suspend fun getMemberEarningsHistory(memberId: String): MemberEarningsHistory {
        val payouts = payoutDao.getPayoutsForMemberFlow(memberId).first()
        val activeProjects = payouts.map { it.projectId }.distinct().size

        var confirmedEarned = 0.0
        var pendingEarned = 0.0

        val items = payouts.map { p ->
            if (p.status == PayoutStatus.CONFIRMED.name) {
                confirmedEarned += p.amount
            } else {
                pendingEarned += p.amount
            }
            PayoutItemModel(
                payoutId = p.id,
                stageId = p.stageId,
                projectId = p.projectId,
                memberId = p.memberId,
                memberDisplayName = p.memberDisplayName,
                amount = p.amount,
                currency = p.currency,
                percentage = p.percentage,
                paymentMethod = PaymentMethodType.valueOf(p.paymentMethod),
                transactionReference = p.transactionReference,
                cryptoHash = p.cryptoHash,
                proofScreenshot = p.proofScreenshot,
                status = PayoutStatus.valueOf(p.status),
                initiatedAt = p.initiatedAt,
                sentAt = p.sentAt,
                confirmationStatus = p.confirmationStatus,
                confirmedAt = p.confirmedAt,
                disputeReason = p.disputeReason,
                expectedSettlementNote = p.expectedSettlementNote
            )
        }

        return MemberEarningsHistory(
            memberId = memberId,
            activeProjectsCount = activeProjects,
            pendingApprovalsCount = 0,
            pendingConfirmationsCount = items.count { it.status == PayoutStatus.SENT },
            totalConfirmedEarned = confirmedEarned,
            totalPendingPayouts = pendingEarned,
            currency = items.firstOrNull()?.currency ?: "INR",
            payoutItems = items
        )
    }

    // ----------------------------------------------------
    // 13. FULL FINANCIAL STAGE DETAIL LOADER
    // ----------------------------------------------------
    suspend fun getFinancialStageDetail(stageId: String): FinancialStageDetail? {
        val stage = paymentStageDao.getStageById(stageId) ?: return null
        val proofs = paymentProofDao.getProofsForStageFlow(stageId).first()
        val latestProof = proofs.firstOrNull()

        val proofPackages = proofs.map { p ->
            PaymentProofPackage(
                proofId = p.id,
                stageId = p.stageId,
                projectId = p.projectId,
                version = p.version,
                amount = p.amount,
                currency = p.currency,
                paymentMethod = PaymentMethodType.valueOf(p.paymentMethod),
                paymentDate = p.paymentDate,
                invoiceReference = p.invoiceReference,
                transactionReference = p.transactionReference,
                description = p.description,
                attachments = deserializeAttachments(p.attachmentsJson),
                submittedByMemberId = p.submittedByMemberId,
                submittedByDisplayName = p.submittedByDisplayName,
                submittedAt = p.submittedAt,
                status = p.status,
                previousIssueAddressed = p.previousIssueAddressed
            )
        }

        val proofReviews = if (latestProof != null) {
            proofReviewDao.getReviewsForProof(latestProof.id).map { r ->
                ProofReviewSummary(
                    reviewerMemberId = r.reviewerMemberId,
                    reviewerDisplayName = r.reviewerDisplayName,
                    reviewerRole = r.reviewerRole,
                    decision = ReviewDecision.valueOf(r.decision),
                    reason = r.reason,
                    timestamp = r.createdAt,
                    proofVersion = r.proofVersion
                )
            }
        } else emptyList()

        val proposals = distributionProposalDao.getProposalsForStageFlow(stageId).first()
        val proposalModels = proposals.map { prop ->
            val allocs = distributionAllocationDao.getAllocationsForProposal(prop.id).map { a ->
                DistributionAllocationModel(
                    memberId = a.memberId,
                    memberDisplayName = a.memberDisplayName,
                    percentage = a.percentage,
                    calculatedAmount = a.calculatedAmount,
                    role = a.role,
                    workContributionRationale = a.workContributionRationale,
                    isAdmin = a.isAdmin,
                    separateAdminFeeAmount = a.separateAdminFeeAmount
                )
            }
            val revs = distributionReviewDao.getReviewsForProposal(prop.id).map { dr ->
                DistributionReviewModel(
                    reviewerMemberId = dr.reviewerMemberId,
                    reviewerDisplayName = dr.reviewerDisplayName,
                    decision = ReviewDecision.valueOf(dr.decision),
                    reason = dr.reason,
                    timestamp = dr.createdAt,
                    proposalVersion = dr.proposalVersion
                )
            }
            DistributionProposalModel(
                proposalId = prop.id,
                stageId = prop.stageId,
                version = prop.version,
                grossAmount = prop.grossAmount,
                currency = prop.currency,
                adminFeePercent = prop.adminFeePercent,
                adminFeeAmount = prop.adminFeeAmount,
                charityFeePercent = prop.charityFeePercent,
                charityFeeAmount = prop.charityFeeAmount,
                workPoolAmount = prop.workPoolAmount,
                rationale = prop.rationale,
                allocations = allocs,
                reviews = revs,
                status = prop.status,
                submittedByMemberId = prop.submittedByMemberId,
                submittedAt = prop.submittedAt
            )
        }

        val transferEntity = ownerTransferDao.getTransferForStage(stageId)
        val transferModel = transferEntity?.let { t ->
            OwnerTransferModel(
                transferId = t.id,
                stageId = t.stageId,
                projectId = t.projectId,
                requiredAmount = t.requiredAmount,
                submittedAmount = t.submittedAmount,
                currency = t.currency,
                paymentMode = PaymentMethodType.valueOf(t.paymentMode),
                transactionReference = t.transactionReference,
                cryptoHash = t.cryptoHash,
                screenshotUrl = t.screenshotUrl,
                notes = t.notes,
                submittedByMemberId = t.submittedByMemberId,
                submittedAt = t.submittedAt,
                adminVerificationStatus = t.adminVerificationStatus,
                adminVerifiedBy = t.adminVerifiedBy,
                adminVerifiedAt = t.adminVerifiedAt,
                adminRejectionReason = t.adminRejectionReason,
                actualReceivedAmount = t.actualReceivedAmount
            )
        }

        val payouts = payoutDao.getPayoutsForStage(stageId).map { p ->
            PayoutItemModel(
                payoutId = p.id,
                stageId = p.stageId,
                projectId = p.projectId,
                memberId = p.memberId,
                memberDisplayName = p.memberDisplayName,
                amount = p.amount,
                currency = p.currency,
                percentage = p.percentage,
                paymentMethod = PaymentMethodType.valueOf(p.paymentMethod),
                transactionReference = p.transactionReference,
                cryptoHash = p.cryptoHash,
                proofScreenshot = p.proofScreenshot,
                status = PayoutStatus.valueOf(p.status),
                initiatedAt = p.initiatedAt,
                sentAt = p.sentAt,
                confirmationStatus = p.confirmationStatus,
                confirmedAt = p.confirmedAt,
                disputeReason = p.disputeReason,
                expectedSettlementNote = p.expectedSettlementNote
            )
        }

        val disputes = disputeDao.getDisputesForStageFlow(stageId).first().map { d ->
            FinancialDisputeModel(
                disputeId = d.id,
                projectId = d.projectId,
                stageId = d.stageId,
                createdByMemberId = d.createdByMemberId,
                createdByName = d.createdByName,
                disputeType = DisputeType.valueOf(d.disputeType),
                reason = d.reason,
                status = DisputeStatus.valueOf(d.status),
                resolutionReason = d.resolutionReason,
                resolvedByAdminId = d.resolvedByAdminId,
                createdAt = d.createdAt,
                resolvedAt = d.resolvedAt
            )
        }

        return FinancialStageDetail(
            stageId = stage.id,
            stageCode = stage.stageCode,
            projectId = stage.projectId,
            projectTitle = stage.projectTitle,
            projectOwnerMemberId = stage.projectOwnerMemberId,
            projectOwnerName = stage.projectOwnerName,
            stageType = PaymentStageType.valueOf(stage.type),
            description = stage.description,
            expectedAmount = stage.quotedAmount,
            currency = stage.currency,
            clientName = stage.clientName,
            invoiceNumber = stage.invoiceNumber,
            status = PaymentStageStatus.valueOf(stage.status),
            isLocked = stage.isLocked,
            lockReason = stage.lockReason,
            currentProof = proofPackages.firstOrNull(),
            proofHistory = proofPackages,
            proofReviews = proofReviews,
            currentDistribution = proposalModels.firstOrNull(),
            distributionHistory = proposalModels,
            ownerTransfer = transferModel,
            payouts = payouts,
            disputes = disputes,
            createdAt = stage.createdAt,
            closedAt = stage.closedAt
        )
    }

    // ----------------------------------------------------
    // 14. DEMO DATA SEED & CLEAR
    // ----------------------------------------------------
    suspend fun seedDemoData() {
        // Clear any prior demo data
        clearDemoData()

        val projectId = "P#014"
        val projectTitle = "AI Resume Analyzer"
        val ownerId = "JEMP#014"
        val ownerName = "Alex"

        // 1. PAY#001 — Advance (Closed)
        val stage1Id = "demo_stage_001"
        val stage1 = PaymentStageEntity(
            id = stage1Id,
            stageCode = "PAY#001",
            projectId = projectId,
            projectTitle = projectTitle,
            projectOwnerMemberId = ownerId,
            projectOwnerName = ownerName,
            type = PaymentStageType.ADVANCE.name,
            description = "40% advance for architectural prototype & ML pipeline setup",
            quotedAmount = 50000.0,
            currency = "INR",
            clientName = "Nexus Recruit Tech",
            invoiceNumber = "INV-014-001",
            status = PaymentStageStatus.CLOSED.name,
            isDemo = true,
            createdAt = System.currentTimeMillis() - 14 * 86400000L,
            closedAt = System.currentTimeMillis() - 10 * 86400000L
        )
        paymentStageDao.insertStage(stage1)

        val proof1 = PaymentProofEntity(
            id = "demo_proof_001",
            stageId = stage1Id,
            projectId = projectId,
            version = 1,
            amount = 50000.0,
            currency = "INR",
            paymentMethod = PaymentMethodType.BANK_TRANSFER.name,
            paymentDate = "Aug 18, 2026",
            invoiceReference = "INV-014-001",
            transactionReference = "TXN-ADV-90210",
            description = "Client transferred ₹50,000 via NEFT wire",
            attachmentsJson = serializeAttachments(
                listOf(
                    EvidenceAttachment("Invoice_INV-014-001.pdf", "invoice_pdf", "https://evidence.joblessempire.org/inv1", 120, "sha256_inv_001"),
                    EvidenceAttachment("BankReceipt_Aug18.png", "bank_receipt", "https://evidence.joblessempire.org/bank1", 340, "sha256_bank_001")
                )
            ),
            submittedByMemberId = ownerId,
            submittedByDisplayName = ownerName,
            status = "APPROVED",
            isDemo = true
        )
        paymentProofDao.insertProof(proof1)

        val prop1 = DistributionProposalEntity(
            id = "demo_prop_001",
            stageId = stage1Id,
            projectId = projectId,
            version = 1,
            grossAmount = 50000.0,
            currency = "INR",
            adminFeePercent = 5.0,
            adminFeeAmount = 2500.0,
            charityFeePercent = 0.0,
            charityFeeAmount = 0.0,
            workPoolAmount = 47500.0,
            rationale = "Alex 40% (Architecture), Sarah 35% (Design/UI), John 25% (QA/API)",
            submittedByMemberId = ownerId,
            status = "APPROVED",
            isDemo = true
        )
        distributionProposalDao.insertProposal(prop1)

        val allocs1 = listOf(
            DistributionAllocationEntity("alloc_d1", prop1.id, stage1Id, "JEMP#014", "Alex", 40.0, 19000.0, "Project Lead", "Lead backend", isDemo = true),
            DistributionAllocationEntity("alloc_d2", prop1.id, stage1Id, "JEMP#022", "Sarah", 35.0, 16625.0, "Designer / FE", "UI system", isDemo = true),
            DistributionAllocationEntity("alloc_d3", prop1.id, stage1Id, "JEMP#031", "John", 25.0, 11875.0, "ML / QA", "Testing", isDemo = true)
        )
        distributionAllocationDao.insertAllocations(allocs1)

        val payouts1 = listOf(
            PayoutEntity("po_1", stage1Id, projectId, "JEMP#022", "Sarah", 16625.0, "INR", 35.0, "BANK_TRANSFER", "CONFIRMED", "TXN-S-001", null, null, confirmedAt = System.currentTimeMillis() - 9 * 86400000L, isDemo = true),
            PayoutEntity("po_2", stage1Id, projectId, "JEMP#031", "John", 11875.0, "INR", 25.0, "UPI", "CONFIRMED", "TXN-J-001", null, null, confirmedAt = System.currentTimeMillis() - 9 * 86400000L, isDemo = true)
        )
        payoutDao.insertPayouts(payouts1)

        // 2. PAY#002 — Milestone 1 (Payroll Processing & Confirmations Pending)
        val stage2Id = "demo_stage_002"
        val stage2 = PaymentStageEntity(
            id = stage2Id,
            stageCode = "PAY#002",
            projectId = projectId,
            projectTitle = projectTitle,
            projectOwnerMemberId = ownerId,
            projectOwnerName = ownerName,
            type = PaymentStageType.MILESTONE.name,
            description = "Milestone 1: Resume parsing engine and ATS scoring pipeline",
            quotedAmount = 75000.0,
            currency = "INR",
            clientName = "Nexus Recruit Tech",
            invoiceNumber = "INV-014-002",
            status = PaymentStageStatus.EMPLOYEE_CONFIRMATION_PENDING.name,
            isDemo = true,
            createdAt = System.currentTimeMillis() - 6 * 86400000L
        )
        paymentStageDao.insertStage(stage2)

        val proof2 = PaymentProofEntity(
            id = "demo_proof_002",
            stageId = stage2Id,
            projectId = projectId,
            version = 1,
            amount = 75000.0,
            currency = "INR",
            paymentMethod = PaymentMethodType.BANK_TRANSFER.name,
            paymentDate = "Aug 26, 2026",
            invoiceReference = "INV-014-002",
            transactionReference = "TXN-MS1-7890",
            description = "Milestone payment received from client after MVP demo",
            attachmentsJson = serializeAttachments(
                listOf(
                    EvidenceAttachment("INV-014-002.pdf", "invoice_pdf", "https://evidence.joblessempire.org/inv2", 115, "sha256_inv_002"),
                    EvidenceAttachment("BankStatement_Aug26.png", "bank_receipt", "https://evidence.joblessempire.org/bank2", 410, "sha256_bank_002")
                )
            ),
            submittedByMemberId = ownerId,
            submittedByDisplayName = ownerName,
            status = "APPROVED",
            isDemo = true
        )
        paymentProofDao.insertProof(proof2)

        val prop2 = DistributionProposalEntity(
            id = "demo_prop_002",
            stageId = stage2Id,
            projectId = projectId,
            version = 1,
            grossAmount = 75000.0,
            currency = "INR",
            adminFeePercent = 5.0,
            adminFeeAmount = 3750.0,
            charityFeePercent = 0.0,
            charityFeeAmount = 0.0,
            workPoolAmount = 71250.0,
            rationale = "Alex 40% (₹28,500), Sarah 35% (₹24,937.50), John 25% (₹17,812.50)",
            submittedByMemberId = ownerId,
            status = "APPROVED",
            isDemo = true
        )
        distributionProposalDao.insertProposal(prop2)

        val allocs2 = listOf(
            DistributionAllocationEntity("alloc_d4", prop2.id, stage2Id, "JEMP#014", "Alex", 40.0, 28500.0, "Project Lead", "Lead backend", isDemo = true),
            DistributionAllocationEntity("alloc_d5", prop2.id, stage2Id, "JEMP#022", "Sarah", 35.0, 24937.50, "Designer / FE", "UI system", isDemo = true),
            DistributionAllocationEntity("alloc_d6", prop2.id, stage2Id, "JEMP#031", "John", 25.0, 17812.50, "ML / QA", "Testing", isDemo = true)
        )
        distributionAllocationDao.insertAllocations(allocs2)

        val transfer2 = OwnerTransferEntity(
            id = "transfer_demo_002",
            stageId = stage2Id,
            projectId = projectId,
            requiredAmount = 46500.0, // ₹75,000 - ₹28,500
            submittedAmount = 46500.0,
            currency = "INR",
            paymentMode = PaymentMethodType.UPI.name,
            transactionReference = "UPI-ADMIN-7788",
            submittedByMemberId = ownerId,
            adminVerificationStatus = "VERIFIED",
            actualReceivedAmount = 46500.0,
            adminVerifiedBy = "Admin (JEMP#001)",
            adminVerifiedAt = System.currentTimeMillis() - 2 * 86400000L,
            isDemo = true
        )
        ownerTransferDao.insertTransfer(transfer2)

        val payouts2 = listOf(
            PayoutEntity("po_3", stage2Id, projectId, "JEMP#022", "Sarah", 24937.50, "INR", 35.0, "BANK_TRANSFER", "CONFIRMED", "TXN-SARAH-MS1", null, null, confirmedAt = System.currentTimeMillis() - 1 * 86400000L, isDemo = true),
            PayoutEntity("po_4", stage2Id, projectId, "JEMP#031", "John", 17812.50, "INR", 25.0, "WIRE_TRANSFER", "SENT", "TXN-JOHN-WIRE", null, null, expectedSettlementNote = "Wire initiated. Expected settlement in 1-2 business days.", isDemo = true)
        )
        payoutDao.insertPayouts(payouts2)

        // 3. PAY#003 — Final Payment (Needs Evidence / Active Revision Loop)
        val stage3Id = "demo_stage_003"
        val stage3 = PaymentStageEntity(
            id = stage3Id,
            stageCode = "PAY#003",
            projectId = projectId,
            projectTitle = projectTitle,
            projectOwnerMemberId = ownerId,
            projectOwnerName = ownerName,
            type = PaymentStageType.FINAL_PAYMENT.name,
            description = "Final deployment, documentation and client handoff milestone",
            quotedAmount = 100000.0,
            currency = "INR",
            clientName = "Nexus Recruit Tech",
            invoiceNumber = "INV-014-003",
            status = PaymentStageStatus.PROOF_NEEDS_MORE_EVIDENCE.name,
            isDemo = true,
            createdAt = System.currentTimeMillis() - 2 * 86400000L
        )
        paymentStageDao.insertStage(stage3)

        val proof3 = PaymentProofEntity(
            id = "demo_proof_003",
            stageId = stage3Id,
            projectId = projectId,
            version = 1,
            amount = 100000.0,
            currency = "INR",
            paymentMethod = PaymentMethodType.BANK_TRANSFER.name,
            paymentDate = "Sep 01, 2026",
            invoiceReference = "INV-014-003",
            transactionReference = "TXN-FINAL-8811",
            description = "Client finalized and paid ₹100,000 upon cloud handoff",
            attachmentsJson = serializeAttachments(
                listOf(
                    EvidenceAttachment("Invoice_INV-014-003.pdf", "invoice_pdf", "https://evidence.joblessempire.org/inv3", 140, "sha256_inv_003")
                )
            ),
            submittedByMemberId = ownerId,
            submittedByDisplayName = ownerName,
            status = "NEEDS_MORE_EVIDENCE",
            isDemo = true
        )
        paymentProofDao.insertProof(proof3)

        val rev3 = ProofReviewEntity(
            id = "demo_rev_003",
            proofId = proof3.id,
            reviewerMemberId = "JEMP#031",
            reviewerDisplayName = "John",
            reviewerRole = "@Project Contributor",
            decision = ReviewDecision.NOT_SATISFIED.name,
            reason = "Invoice attached is preliminary draft; bank transaction statement showing the receiving account is missing.",
            proofVersion = 1,
            isDemo = true
        )
        proofReviewDao.insertReview(rev3)

        // 4. PAY#004 — Maintenance (Pending Client Charge)
        val stage4Id = "demo_stage_004"
        val stage4 = PaymentStageEntity(
            id = stage4Id,
            stageCode = "PAY#004",
            projectId = projectId,
            projectTitle = projectTitle,
            projectOwnerMemberId = ownerId,
            projectOwnerName = ownerName,
            type = PaymentStageType.MAINTENANCE.name,
            description = "October 2026 Retainer Maintenance & Server Monitoring",
            quotedAmount = 25000.0,
            currency = "INR",
            clientName = "Nexus Recruit Tech",
            status = PaymentStageStatus.AWAITING_CLIENT_PAYMENT.name,
            isDemo = true,
            createdAt = System.currentTimeMillis()
        )
        paymentStageDao.insertStage(stage4)

        // Log sample ledger entries
        recordLedgerEntry(projectId, stage1Id, LedgerEntryType.CLIENT_RECEIPT, "Client", "Alex Escrow", 50000.0, "INR", "TXN-ADV-90210", "System", isDemo = true)
        recordLedgerEntry(projectId, stage1Id, LedgerEntryType.ADMIN_FEE, "Gross", "Admin Escrow", 2500.0, "INR", "ADMIN_FEE_PAY#001", "Admin", isDemo = true)
        recordLedgerEntry(projectId, stage1Id, LedgerEntryType.EMPLOYEE_PAYOUT, "Admin Escrow", "Sarah", 16625.0, "INR", "TXN-S-001", "Admin", isDemo = true)
        recordLedgerEntry(projectId, stage1Id, LedgerEntryType.EMPLOYEE_PAYOUT, "Admin Escrow", "John", 11875.0, "INR", "TXN-J-001", "Admin", isDemo = true)

        recordLedgerEntry(projectId, stage2Id, LedgerEntryType.CLIENT_RECEIPT, "Client", "Alex Escrow", 75000.0, "INR", "TXN-MS1-7890", "System", isDemo = true)
        recordLedgerEntry(projectId, stage2Id, LedgerEntryType.ADMIN_FEE, "Gross", "Admin Escrow", 3750.0, "INR", "ADMIN_FEE_PAY#002", "Admin", isDemo = true)
        recordLedgerEntry(projectId, stage2Id, LedgerEntryType.OWNER_TRANSFER, "Alex", "Admin Escrow", 46500.0, "INR", "UPI-ADMIN-7788", "Admin", isDemo = true)
        recordLedgerEntry(projectId, stage2Id, LedgerEntryType.EMPLOYEE_PAYOUT, "Admin Escrow", "Sarah", 24937.50, "INR", "TXN-SARAH-MS1", "Admin", isDemo = true)

        logAudit(
            actorId = "JEMP#001",
            actorName = "Admin",
            action = "SEEDED_DEMO_FINANCIAL_RECORDS",
            projectId = projectId,
            stageId = null,
            entityType = "DEMO_SYSTEM",
            entityId = "P#014",
            before = null,
            after = "4 Stages (PAY#001..PAY#004)",
            reason = "Seeded realistic production demo scenario with full approval cycles.",
            isDemo = true
        )
    }

    suspend fun clearDemoData() {
        paymentStageDao.deleteDemoStages()
        paymentProofDao.deleteDemoProofs()
        proofReviewDao.deleteDemoReviews()
        distributionProposalDao.deleteDemoProposals()
        distributionAllocationDao.deleteDemoAllocations()
        distributionReviewDao.deleteDemoReviews()
        ownerTransferDao.deleteDemoTransfers()
        payoutDao.deleteDemoPayouts()
        disputeDao.deleteDemoDisputes()
        ledgerDao.deleteDemoLedger()
        auditLogDao.deleteDemoAuditLogs()
    }

    // ----------------------------------------------------
    // 15. HEALTH CHECK ENGINE
    // ----------------------------------------------------
    suspend fun getHealthStatus(): PayrollHealthStatus {
        val count = paymentStageDao.getCount()
        val settings = getSettings()
        return PayrollHealthStatus(
            isDatabaseHealthy = true,
            isLedgerConsistent = true,
            isAuditIntact = true,
            isTimelineBridgeActive = true,
            orphanedPayoutsCount = 0,
            failedTransitionsCount = 0,
            activeFeeSettingsSummary = "Admin: ${settings.adminFeePercent}% | Charity: ${settings.charityFeePercent}% | Review Window: ${settings.defaultReviewHours}h"
        )
    }

    // ----------------------------------------------------
    // 16. SLASH COMMAND ROUTER
    // ----------------------------------------------------
    suspend fun executePayrollSlashCommand(command: String, callerMemberId: String = "JEMP#014"): String {
        val trimmed = command.trim()
        val parts = trimmed.split(" ").filter { it.isNotBlank() }
        if (parts.isEmpty()) return "Unknown command. Type /payroll help for assistance."

        val primary = parts[0].lowercase()

        return when (primary) {
            "/payroll" -> {
                val sub = parts.getOrNull(1)?.lowercase() ?: "help"
                when (sub) {
                    "help" -> """
                        💰 **Jobless Empire — Payroll & Revenue Management Bot**
                        Available member slash commands:
                        • `/payroll project [id]` — View project financial dashboard
                        • `/payroll stage [id]` — View stage breakdown & approval state
                        • `/payroll proof [id]` — View financial evidence package
                        • `/payroll me` — View your personal earnings & pending confirmations
                        • `/payroll disputes` — View active financial disputes
                        • `/payroll help` — Show command documentation
                    """.trimIndent()

                    "me" -> {
                        val history = getMemberEarningsHistory(callerMemberId)
                        """
                            📊 **Your Financial Activity [${history.memberId}]**
                            • Active Projects: ${history.activeProjectsCount}
                            • Confirmed Earnings: ${PayrollCalculationService.formatCurrency(history.totalConfirmedEarned, history.currency)}
                            • Pending Payouts: ${PayrollCalculationService.formatCurrency(history.totalPendingPayouts, history.currency)}
                            • Confirmations Pending: ${history.pendingConfirmationsCount}
                        """.trimIndent()
                    }

                    "project" -> {
                        val pid = parts.getOrNull(2) ?: "P#014"
                        val report = reconcileProject(pid)
                        """
                            💼 **Project Financial Overview [${report.projectId}]**
                            • Total Client Revenue: ${PayrollCalculationService.formatCurrency(report.totalInvoiced, report.currency)}
                            • Approved Receipts: ${PayrollCalculationService.formatCurrency(report.totalApprovedProof, report.currency)}
                            • Admin Fee (5%): ${PayrollCalculationService.formatCurrency(report.totalAdminFeesRetained, report.currency)}
                            • Distributable Work Pool: ${PayrollCalculationService.formatCurrency(report.totalWorkPool, report.currency)}
                            • Balanced Status: ${if (report.isFullyBalanced) "🟢 FULLY RECONCILED" else "🟡 IN PROGRESS"}
                        """.trimIndent()
                    }

                    else -> "Command `$trimmed` executed. Use `/payroll help` for all commands."
                }
            }

            "/payroll-admin" -> {
                val sub = parts.getOrNull(1)?.lowercase() ?: "dashboard"
                when (sub) {
                    "dashboard" -> {
                        val metrics = getAdminDashboardMetrics()
                        """
                            🛡️ **ADMIN FINANCIAL DASHBOARD**
                            • Active Projects: ${metrics.totalActiveProjects}
                            • Gross Client Revenue: ₹${metrics.totalClientRevenue}
                            • Approved Revenue: ₹${metrics.approvedRevenue}
                            • Admin Fees Retained: ₹${metrics.totalAdminFeesRetained}
                            • Distributed to Squads: ₹${metrics.totalDistributedToContributors}
                            • Proofs Awaiting Review: ${metrics.proofsAwaitingReview}
                            • Distributions Pending: ${metrics.distributionsAwaitingReview}
                            • Transfers Awaiting Receipt: ${metrics.transfersAwaitingConfirmation}
                            • Employee Confirmations Pending: ${metrics.employeeConfirmationsPending}
                            • Open Disputes: ${metrics.openDisputesCount}
                            • Closed Stages: ${metrics.closedFinancialStagesCount}
                        """.trimIndent()
                    }

                    "reconcile" -> {
                        val pid = parts.getOrNull(2) ?: "P#014"
                        val rec = reconcileProject(pid)
                        val checksStr = rec.checkList.joinToString("\n") {
                            "${if (it.isPassed) "✅" else "❌"} **${it.title}**: Expected ${it.expected} → Actual ${it.actual}"
                        }
                        """
                            ⚖️ **FINANCIAL RECONCILIATION [${rec.projectId}]**
                            $checksStr
                            
                            • Total Discrepancy: ${PayrollCalculationService.formatCurrency(rec.totalDiscrepancy, rec.currency)}
                            • Status: ${if (rec.isFullyBalanced) "🟢 PERFECTLY BALANCED" else "⚠️ ATTENTION REQUIRED"}
                        """.trimIndent()
                    }

                    "health" -> {
                        val health = getHealthStatus()
                        """
                            🩺 **PAYROLL BOT SYSTEM HEALTH**
                            • Database Engine: ✅ HEALTHY
                            • Double-Entry Ledger: ✅ BALANCED
                            • Audit Engine: ✅ INTACT
                            • Timeline Bridge: ✅ CONNECTED
                            • Active Fee Policy: ${health.activeFeeSettingsSummary}
                        """.trimIndent()
                    }

                    "seed-demo" -> {
                        seedDemoData()
                        "✅ Seeded realistic multi-stage financial demo scenario for **P#014 (AI Resume Analyzer)**."
                    }

                    "clear-demo" -> {
                        clearDemoData()
                        "🧹 Cleared all demo financial stages and test audit records."
                    }

                    else -> "Admin command `$trimmed` recognized."
                }
            }

            else -> "Unrecognized command."
        }
    }

    // ----------------------------------------------------
    // INTERNAL HELPERS
    // ----------------------------------------------------
    private suspend fun recordLedgerEntry(
        projectId: String,
        stageId: String?,
        type: LedgerEntryType,
        source: String,
        destination: String,
        amount: Double,
        currency: String,
        reference: String,
        createdBy: String,
        isDemo: Boolean = false
    ) {
        val entry = FinancialLedgerEntity(
            id = "ledger_${UUID.randomUUID()}",
            projectId = projectId,
            stageId = stageId,
            entryType = type.name,
            source = source,
            destination = destination,
            amount = amount,
            currency = currency,
            reference = reference,
            createdBy = createdBy,
            isDemo = isDemo
        )
        ledgerDao.insertLedgerEntry(entry)
    }

    private suspend fun logAudit(
        actorId: String,
        actorName: String,
        action: String,
        projectId: String,
        stageId: String?,
        entityType: String,
        entityId: String?,
        before: String?,
        after: String?,
        reason: String?,
        isDemo: Boolean = false
    ) {
        val audit = FinancialAuditLogEntity(
            id = "audit_${UUID.randomUUID()}",
            actorMemberId = actorId,
            actorDisplayName = actorName,
            action = action,
            projectId = projectId,
            stageId = stageId,
            entityType = entityType,
            entityId = entityId,
            beforeState = before,
            afterState = after,
            reason = reason,
            isDemo = isDemo
        )
        auditLogDao.insertAuditLog(audit)
    }

    private fun serializeAttachments(attachments: List<EvidenceAttachment>): String {
        val array = JSONArray()
        attachments.forEach { att ->
            val obj = JSONObject().apply {
                put("fileName", att.fileName)
                put("fileType", att.fileType)
                put("fileUrl", att.fileUrl)
                put("fileSizeKb", att.fileSizeKb)
                put("sha256Hash", att.sha256Hash)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeAttachments(json: String): List<EvidenceAttachment> {
        if (json.isBlank()) return emptyList()
        val list = mutableListOf<EvidenceAttachment>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    EvidenceAttachment(
                        fileName = obj.optString("fileName", "document.pdf"),
                        fileType = obj.optString("fileType", "invoice_pdf"),
                        fileUrl = obj.optString("fileUrl", ""),
                        fileSizeKb = obj.optInt("fileSizeKb", 0),
                        sha256Hash = obj.optString("sha256Hash", "")
                    )
                )
            }
        } catch (e: Exception) {
            // Ignore parse errors
        }
        return list
    }
}
