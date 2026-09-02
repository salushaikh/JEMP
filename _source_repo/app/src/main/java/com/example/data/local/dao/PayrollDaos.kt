package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentStageDao {
    @Query("SELECT * FROM payment_stages ORDER BY createdAt DESC")
    fun getAllStagesFlow(): Flow<List<PaymentStageEntity>>

    @Query("SELECT * FROM payment_stages WHERE projectId = :projectId ORDER BY createdAt ASC")
    fun getStagesByProjectFlow(projectId: String): Flow<List<PaymentStageEntity>>

    @Query("SELECT * FROM payment_stages WHERE id = :id LIMIT 1")
    suspend fun getStageById(id: String): PaymentStageEntity?

    @Query("SELECT * FROM payment_stages WHERE stageCode = :stageCode LIMIT 1")
    suspend fun getStageByCode(stageCode: String): PaymentStageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: PaymentStageEntity)

    @Update
    suspend fun updateStage(stage: PaymentStageEntity)

    @Query("UPDATE payment_stages SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStageStatus(id: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE payment_stages SET isLocked = :isLocked, lockReason = :lockReason, lockedByAdminId = :adminId, updatedAt = :timestamp WHERE id = :id")
    suspend fun setStageLock(id: String, isLocked: Boolean, lockReason: String?, adminId: String?, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM payment_stages WHERE isDemo = 1")
    suspend fun deleteDemoStages()

    @Query("SELECT COUNT(*) FROM payment_stages")
    suspend fun getCount(): Int
}

@Dao
interface PaymentProofDao {
    @Query("SELECT * FROM payment_proofs WHERE stageId = :stageId ORDER BY version DESC")
    fun getProofsForStageFlow(stageId: String): Flow<List<PaymentProofEntity>>

    @Query("SELECT * FROM payment_proofs WHERE stageId = :stageId ORDER BY version DESC LIMIT 1")
    suspend fun getLatestProofForStage(stageId: String): PaymentProofEntity?

    @Query("SELECT * FROM payment_proofs WHERE id = :id LIMIT 1")
    suspend fun getProofById(id: String): PaymentProofEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProof(proof: PaymentProofEntity)

    @Update
    suspend fun updateProof(proof: PaymentProofEntity)

    @Query("UPDATE payment_proofs SET status = :status WHERE id = :id")
    suspend fun updateProofStatus(id: String, status: String)

    @Query("DELETE FROM payment_proofs WHERE isDemo = 1")
    suspend fun deleteDemoProofs()
}

@Dao
interface ProofReviewDao {
    @Query("SELECT * FROM proof_reviews WHERE proofId = :proofId ORDER BY createdAt ASC")
    fun getReviewsForProofFlow(proofId: String): Flow<List<ProofReviewEntity>>

    @Query("SELECT * FROM proof_reviews WHERE proofId = :proofId")
    suspend fun getReviewsForProof(proofId: String): List<ProofReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ProofReviewEntity)

    @Query("DELETE FROM proof_reviews WHERE isDemo = 1")
    suspend fun deleteDemoReviews()
}

@Dao
interface DistributionProposalDao {
    @Query("SELECT * FROM distribution_proposals WHERE stageId = :stageId ORDER BY version DESC")
    fun getProposalsForStageFlow(stageId: String): Flow<List<DistributionProposalEntity>>

    @Query("SELECT * FROM distribution_proposals WHERE stageId = :stageId ORDER BY version DESC LIMIT 1")
    suspend fun getLatestProposalForStage(stageId: String): DistributionProposalEntity?

    @Query("SELECT * FROM distribution_proposals WHERE id = :id LIMIT 1")
    suspend fun getProposalById(id: String): DistributionProposalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: DistributionProposalEntity)

    @Update
    suspend fun updateProposal(proposal: DistributionProposalEntity)

    @Query("UPDATE distribution_proposals SET status = :status WHERE id = :id")
    suspend fun updateProposalStatus(id: String, status: String)

    @Query("DELETE FROM distribution_proposals WHERE isDemo = 1")
    suspend fun deleteDemoProposals()
}

@Dao
interface DistributionAllocationDao {
    @Query("SELECT * FROM distribution_allocations WHERE proposalId = :proposalId")
    fun getAllocationsForProposalFlow(proposalId: String): Flow<List<DistributionAllocationEntity>>

    @Query("SELECT * FROM distribution_allocations WHERE proposalId = :proposalId")
    suspend fun getAllocationsForProposal(proposalId: String): List<DistributionAllocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocations(allocations: List<DistributionAllocationEntity>)

    @Query("DELETE FROM distribution_allocations WHERE isDemo = 1")
    suspend fun deleteDemoAllocations()
}

@Dao
interface DistributionReviewDao {
    @Query("SELECT * FROM distribution_reviews WHERE proposalId = :proposalId")
    fun getReviewsForProposalFlow(proposalId: String): Flow<List<DistributionReviewEntity>>

    @Query("SELECT * FROM distribution_reviews WHERE proposalId = :proposalId")
    suspend fun getReviewsForProposal(proposalId: String): List<DistributionReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: DistributionReviewEntity)

    @Query("DELETE FROM distribution_reviews WHERE isDemo = 1")
    suspend fun deleteDemoReviews()
}

@Dao
interface OwnerTransferDao {
    @Query("SELECT * FROM owner_transfers WHERE stageId = :stageId ORDER BY submittedAt DESC LIMIT 1")
    fun getTransferForStageFlow(stageId: String): Flow<OwnerTransferEntity?>

    @Query("SELECT * FROM owner_transfers WHERE stageId = :stageId ORDER BY submittedAt DESC LIMIT 1")
    suspend fun getTransferForStage(stageId: String): OwnerTransferEntity?

    @Query("SELECT * FROM owner_transfers WHERE transactionReference = :reference LIMIT 1")
    suspend fun getTransferByReference(reference: String): OwnerTransferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: OwnerTransferEntity)

    @Update
    suspend fun updateTransfer(transfer: OwnerTransferEntity)

    @Query("DELETE FROM owner_transfers WHERE isDemo = 1")
    suspend fun deleteDemoTransfers()
}

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payouts WHERE stageId = :stageId ORDER BY memberDisplayName ASC")
    fun getPayoutsForStageFlow(stageId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts WHERE stageId = :stageId")
    suspend fun getPayoutsForStage(stageId: String): List<PayoutEntity>

    @Query("SELECT * FROM payouts WHERE memberId = :memberId ORDER BY initiatedAt DESC")
    fun getPayoutsForMemberFlow(memberId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts WHERE id = :id LIMIT 1")
    suspend fun getPayoutById(id: String): PayoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayouts(payouts: List<PayoutEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)

    @Update
    suspend fun updatePayout(payout: PayoutEntity)

    @Query("DELETE FROM payouts WHERE isDemo = 1")
    suspend fun deleteDemoPayouts()
}

@Dao
interface FinancialDisputeDao {
    @Query("SELECT * FROM financial_disputes ORDER BY createdAt DESC")
    fun getAllDisputesFlow(): Flow<List<FinancialDisputeEntity>>

    @Query("SELECT * FROM financial_disputes WHERE stageId = :stageId ORDER BY createdAt DESC")
    fun getDisputesForStageFlow(stageId: String): Flow<List<FinancialDisputeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: FinancialDisputeEntity)

    @Update
    suspend fun updateDispute(dispute: FinancialDisputeEntity)

    @Query("DELETE FROM financial_disputes WHERE isDemo = 1")
    suspend fun deleteDemoDisputes()
}

@Dao
interface FinancialLedgerDao {
    @Query("SELECT * FROM financial_ledger ORDER BY createdAt DESC")
    fun getAllLedgerEntriesFlow(): Flow<List<FinancialLedgerEntity>>

    @Query("SELECT * FROM financial_ledger WHERE projectId = :projectId ORDER BY createdAt ASC")
    fun getLedgerForProjectFlow(projectId: String): Flow<List<FinancialLedgerEntity>>

    @Query("SELECT * FROM financial_ledger WHERE projectId = :projectId")
    suspend fun getLedgerForProject(projectId: String): List<FinancialLedgerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: FinancialLedgerEntity)

    @Query("DELETE FROM financial_ledger WHERE isDemo = 1")
    suspend fun deleteDemoLedger()
}

@Dao
interface FinancialAuditLogDao {
    @Query("SELECT * FROM financial_audit_log ORDER BY timestamp DESC LIMIT 200")
    fun getAllAuditLogsFlow(): Flow<List<FinancialAuditLogEntity>>

    @Query("SELECT * FROM financial_audit_log WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getAuditLogsForProjectFlow(projectId: String): Flow<List<FinancialAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: FinancialAuditLogEntity)

    @Query("DELETE FROM financial_audit_log WHERE isDemo = 1")
    suspend fun deleteDemoAuditLogs()
}

@Dao
interface FinancialSettingsDao {
    @Query("SELECT * FROM financial_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): FinancialSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: FinancialSettingsEntity)
}

@Dao
interface MemberPaymentMethodDao {
    @Query("SELECT * FROM member_payment_methods WHERE memberId = :memberId")
    fun getMethodsForMemberFlow(memberId: String): Flow<List<MemberPaymentMethodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMethod(method: MemberPaymentMethodEntity)
}

@Dao
interface PayrollIdCounterDao {
    @Query("SELECT * FROM payroll_id_counters WHERE counterName = :name LIMIT 1")
    suspend fun getCounter(name: String): PayrollIdCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCounter(counter: PayrollIdCounterEntity)
}
