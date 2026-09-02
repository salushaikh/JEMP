package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        MemberEntity::class,
        IdCounterEntity::class,
        TimelineEntryEntity::class,
        TimelineShareEntity::class,
        AuditLogEntity::class,
        TimelineSettingsEntity::class,
        ApplicationEntity::class,
        InvitationEntity::class,
        OnboardingSessionEntity::class,
        OnboardingSettingsEntity::class,
        BrainstormEntity::class,
        BrainstormContributorEntity::class,
        BrainstormApplicationEntity::class,
        BrainstormDecisionEntity::class,
        BrainstormMilestoneEntity::class,
        BrainstormTaskEntity::class,
        BrainstormResourceEntity::class,
        BrainstormPollEntity::class,
        BrainstormActivityEntity::class,
        BrainstormSettingsEntity::class,
        BrainstormIdCounterEntity::class,
        PaymentStageEntity::class,
        PaymentProofEntity::class,
        ProofReviewEntity::class,
        EvidenceRequestEntity::class,
        DistributionProposalEntity::class,
        DistributionAllocationEntity::class,
        DistributionReviewEntity::class,
        OwnerTransferEntity::class,
        PayoutEntity::class,
        PayoutConfirmationEntity::class,
        FinancialDisputeEntity::class,
        FinancialLedgerEntity::class,
        FinancialAuditLogEntity::class,
        FinancialSettingsEntity::class,
        MemberPaymentMethodEntity::class,
        PayrollIdCounterEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class JoblessEmpireDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun timelineEntryDao(): TimelineEntryDao
    abstract fun timelineShareDao(): TimelineShareDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun timelineSettingsDao(): TimelineSettingsDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun invitationDao(): InvitationDao
    abstract fun onboardingSessionDao(): OnboardingSessionDao
    abstract fun onboardingSettingsDao(): OnboardingSettingsDao

    abstract fun brainstormDao(): BrainstormDao
    abstract fun brainstormContributorDao(): BrainstormContributorDao
    abstract fun brainstormApplicationDao(): BrainstormApplicationDao
    abstract fun brainstormDecisionDao(): BrainstormDecisionDao
    abstract fun brainstormMilestoneDao(): BrainstormMilestoneDao
    abstract fun brainstormTaskDao(): BrainstormTaskDao
    abstract fun brainstormResourceDao(): BrainstormResourceDao
    abstract fun brainstormPollDao(): BrainstormPollDao
    abstract fun brainstormActivityDao(): BrainstormActivityDao
    abstract fun brainstormSettingsDao(): BrainstormSettingsDao

    abstract fun paymentStageDao(): PaymentStageDao
    abstract fun paymentProofDao(): PaymentProofDao
    abstract fun proofReviewDao(): ProofReviewDao
    abstract fun distributionProposalDao(): DistributionProposalDao
    abstract fun distributionAllocationDao(): DistributionAllocationDao
    abstract fun distributionReviewDao(): DistributionReviewDao
    abstract fun ownerTransferDao(): OwnerTransferDao
    abstract fun payoutDao(): PayoutDao
    abstract fun disputeDao(): FinancialDisputeDao
    abstract fun ledgerDao(): FinancialLedgerDao
    abstract fun financialAuditLogDao(): FinancialAuditLogDao
    abstract fun financialSettingsDao(): FinancialSettingsDao
    abstract fun memberPaymentMethodDao(): MemberPaymentMethodDao
    abstract fun payrollIdCounterDao(): PayrollIdCounterDao

    companion object {
        @Volatile
        private var INSTANCE: JoblessEmpireDatabase? = null

        fun getDatabase(context: Context): JoblessEmpireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JoblessEmpireDatabase::class.java,
                    "jobless_empire_timeline.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
