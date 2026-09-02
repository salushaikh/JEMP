package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BrainstormDao {
    @Query("SELECT * FROM brainstorms WHERE isArchived = 0 ORDER BY brainstormNumber DESC")
    fun getAllActiveBrainstorms(): Flow<List<BrainstormEntity>>

    @Query("SELECT * FROM brainstorms ORDER BY brainstormNumber DESC")
    fun getAllBrainstorms(): Flow<List<BrainstormEntity>>

    @Query("SELECT * FROM brainstorms ORDER BY brainstormNumber DESC")
    suspend fun getAllBrainstormsOnce(): List<BrainstormEntity>

    @Query("SELECT * FROM brainstorms WHERE id = :id LIMIT 1")
    suspend fun getBrainstormById(id: String): BrainstormEntity?

    @Query("SELECT * FROM brainstorms WHERE ownerMemberId = :memberId ORDER BY brainstormNumber DESC")
    fun getBrainstormsByOwner(memberId: String): Flow<List<BrainstormEntity>>

    @Query("SELECT * FROM brainstorms WHERE status = :status ORDER BY brainstormNumber DESC")
    fun getBrainstormsByStatus(status: String): Flow<List<BrainstormEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrainstorm(brainstorm: BrainstormEntity)

    @Update
    suspend fun updateBrainstorm(brainstorm: BrainstormEntity)

    @Delete
    suspend fun deleteBrainstorm(brainstorm: BrainstormEntity)

    @Query("DELETE FROM brainstorms WHERE isDemo = 1")
    suspend fun deleteDemoBrainstorms()

    @Query("SELECT * FROM brainstorm_id_counters WHERE counterName = 'brainstorm_id_counter' LIMIT 1")
    suspend fun getIdCounter(): BrainstormIdCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdCounter(counter: BrainstormIdCounterEntity)
}

@Dao
interface BrainstormContributorDao {
    @Query("SELECT * FROM brainstorm_contributors WHERE brainstormId = :brainstormId AND status = 'ACTIVE' ORDER BY joinedAtUtc ASC")
    fun getActiveContributorsForBrainstorm(brainstormId: String): Flow<List<BrainstormContributorEntity>>

    @Query("SELECT * FROM brainstorm_contributors WHERE brainstormId = :brainstormId ORDER BY joinedAtUtc ASC")
    suspend fun getAllContributorsForBrainstormOnce(brainstormId: String): List<BrainstormContributorEntity>

    @Query("SELECT * FROM brainstorm_contributors WHERE memberId = :memberId AND status = 'ACTIVE'")
    fun getActiveContributionsForMember(memberId: String): Flow<List<BrainstormContributorEntity>>

    @Query("SELECT * FROM brainstorm_contributors WHERE brainstormId = :brainstormId AND memberId = :memberId LIMIT 1")
    suspend fun getContributor(brainstormId: String, memberId: String): BrainstormContributorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributor(contributor: BrainstormContributorEntity)

    @Update
    suspend fun updateContributor(contributor: BrainstormContributorEntity)

    @Query("DELETE FROM brainstorm_contributors WHERE isDemo = 1")
    suspend fun deleteDemoContributors()

    @Query("SELECT COUNT(*) FROM brainstorm_contributors WHERE brainstormId = :brainstormId AND status = 'ACTIVE'")
    suspend fun countActiveContributors(brainstormId: String): Int
}

@Dao
interface BrainstormApplicationDao {
    @Query("SELECT * FROM brainstorm_applications WHERE brainstormId = :brainstormId ORDER BY createdAtUtc DESC")
    fun getApplicationsForBrainstorm(brainstormId: String): Flow<List<BrainstormApplicationEntity>>

    @Query("SELECT * FROM brainstorm_applications WHERE memberId = :memberId ORDER BY createdAtUtc DESC")
    fun getApplicationsForMember(memberId: String): Flow<List<BrainstormApplicationEntity>>

    @Query("SELECT * FROM brainstorm_applications WHERE id = :id LIMIT 1")
    suspend fun getApplicationById(id: String): BrainstormApplicationEntity?

    @Query("SELECT * FROM brainstorm_applications WHERE brainstormId = :brainstormId AND memberId = :memberId AND status = 'PENDING' LIMIT 1")
    suspend fun getPendingApplication(brainstormId: String, memberId: String): BrainstormApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: BrainstormApplicationEntity)

    @Update
    suspend fun updateApplication(application: BrainstormApplicationEntity)

    @Query("DELETE FROM brainstorm_applications WHERE isDemo = 1")
    suspend fun deleteDemoApplications()

    @Query("SELECT COUNT(*) FROM brainstorm_applications WHERE brainstormId = :brainstormId AND status = 'PENDING'")
    suspend fun countPendingApplications(brainstormId: String): Int

    @Query("SELECT COUNT(*) FROM brainstorm_applications WHERE status = 'PENDING'")
    suspend fun countAllPendingApplications(): Int
}

@Dao
interface BrainstormDecisionDao {
    @Query("SELECT * FROM brainstorm_decisions WHERE brainstormId = :brainstormId ORDER BY decisionNumber ASC")
    fun getDecisionsForBrainstorm(brainstormId: String): Flow<List<BrainstormDecisionEntity>>

    @Query("SELECT COUNT(*) FROM brainstorm_decisions WHERE brainstormId = :brainstormId")
    suspend fun countDecisionsForBrainstorm(brainstormId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecision(decision: BrainstormDecisionEntity)

    @Query("DELETE FROM brainstorm_decisions WHERE isDemo = 1")
    suspend fun deleteDemoDecisions()
}

@Dao
interface BrainstormMilestoneDao {
    @Query("SELECT * FROM brainstorm_milestones WHERE brainstormId = :brainstormId ORDER BY createdAtUtc ASC")
    fun getMilestonesForBrainstorm(brainstormId: String): Flow<List<BrainstormMilestoneEntity>>

    @Query("SELECT * FROM brainstorm_milestones WHERE id = :id LIMIT 1")
    suspend fun getMilestoneById(id: String): BrainstormMilestoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: BrainstormMilestoneEntity)

    @Update
    suspend fun updateMilestone(milestone: BrainstormMilestoneEntity)

    @Query("DELETE FROM brainstorm_milestones WHERE isDemo = 1")
    suspend fun deleteDemoMilestones()
}

@Dao
interface BrainstormTaskDao {
    @Query("SELECT * FROM brainstorm_tasks WHERE brainstormId = :brainstormId ORDER BY createdAtUtc DESC")
    fun getTasksForBrainstorm(brainstormId: String): Flow<List<BrainstormTaskEntity>>

    @Query("SELECT * FROM brainstorm_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): BrainstormTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: BrainstormTaskEntity)

    @Update
    suspend fun updateTask(task: BrainstormTaskEntity)

    @Delete
    suspend fun deleteTask(task: BrainstormTaskEntity)

    @Query("DELETE FROM brainstorm_tasks WHERE isDemo = 1")
    suspend fun deleteDemoTasks()
}

@Dao
interface BrainstormResourceDao {
    @Query("SELECT * FROM brainstorm_resources WHERE brainstormId = :brainstormId ORDER BY createdAtUtc DESC")
    fun getResourcesForBrainstorm(brainstormId: String): Flow<List<BrainstormResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: BrainstormResourceEntity)

    @Delete
    suspend fun deleteResource(resource: BrainstormResourceEntity)

    @Query("DELETE FROM brainstorm_resources WHERE isDemo = 1")
    suspend fun deleteDemoResources()
}

@Dao
interface BrainstormPollDao {
    @Query("SELECT * FROM brainstorm_polls WHERE brainstormId = :brainstormId ORDER BY createdAtUtc DESC")
    fun getPollsForBrainstorm(brainstormId: String): Flow<List<BrainstormPollEntity>>

    @Query("SELECT * FROM brainstorm_polls WHERE id = :id LIMIT 1")
    suspend fun getPollById(id: String): BrainstormPollEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoll(poll: BrainstormPollEntity)

    @Update
    suspend fun updatePoll(poll: BrainstormPollEntity)

    @Query("DELETE FROM brainstorm_polls WHERE isDemo = 1")
    suspend fun deleteDemoPolls()
}

@Dao
interface BrainstormActivityDao {
    @Query("SELECT * FROM brainstorm_activity WHERE brainstormId = :brainstormId ORDER BY timestampUtc DESC LIMIT 50")
    fun getActivityForBrainstorm(brainstormId: String): Flow<List<BrainstormActivityEntity>>

    @Query("SELECT * FROM brainstorm_activity ORDER BY timestampUtc DESC LIMIT 100")
    fun getRecentGlobalActivity(): Flow<List<BrainstormActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: BrainstormActivityEntity)

    @Query("DELETE FROM brainstorm_activity WHERE isDemo = 1")
    suspend fun deleteDemoActivity()

    @Query("SELECT COUNT(*) FROM brainstorm_activity WHERE timestampUtc > :sinceUtc")
    suspend fun countActivitySince(sinceUtc: Long): Int
}

@Dao
interface BrainstormSettingsDao {
    @Query("SELECT * FROM brainstorm_settings WHERE guildId = :guildId LIMIT 1")
    fun getSettings(guildId: String = "DEFAULT_GUILD"): Flow<BrainstormSettingsEntity?>

    @Query("SELECT * FROM brainstorm_settings WHERE guildId = :guildId LIMIT 1")
    suspend fun getSettingsOnce(guildId: String = "DEFAULT_GUILD"): BrainstormSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: BrainstormSettingsEntity)
}
