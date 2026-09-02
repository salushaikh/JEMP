package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications ORDER BY submittedAtUtc DESC")
    fun getAllApplicationsFlow(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications ORDER BY submittedAtUtc DESC")
    suspend fun getAllApplicationsOnce(): List<ApplicationEntity>

    @Query("SELECT * FROM applications WHERE applicationId = :appId LIMIT 1")
    suspend fun getApplicationById(appId: String): ApplicationEntity?

    @Query("SELECT * FROM applications WHERE normalizedEmail = :email LIMIT 1")
    suspend fun getApplicationByNormalizedEmail(email: String): ApplicationEntity?

    @Query("SELECT * FROM applications WHERE status = :status ORDER BY submittedAtUtc DESC")
    suspend fun getApplicationsByStatus(status: String): List<ApplicationEntity>

    @Query("SELECT * FROM applications WHERE linkedDiscordUserId = :discordUserId LIMIT 1")
    suspend fun getApplicationByDiscordUserId(discordUserId: String): ApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity)

    @Update
    suspend fun updateApplication(application: ApplicationEntity)

    @Query("DELETE FROM applications WHERE isDemo = 1")
    suspend fun deleteDemoApplications()

    @Query("SELECT COUNT(*) FROM applications WHERE status = :status")
    suspend fun countApplicationsByStatus(status: String): Int
}

@Dao
interface InvitationDao {
    @Query("SELECT * FROM invitations WHERE inviteCode = :code LIMIT 1")
    suspend fun getInvitationByCode(code: String): InvitationEntity?

    @Query("SELECT * FROM invitations WHERE applicationId = :appId ORDER BY createdAtUtc DESC LIMIT 1")
    suspend fun getInvitationByApplicationId(appId: String): InvitationEntity?

    @Query("SELECT * FROM invitations ORDER BY createdAtUtc DESC")
    suspend fun getAllInvitations(): List<InvitationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: InvitationEntity)

    @Update
    suspend fun updateInvitation(invitation: InvitationEntity)

    @Query("DELETE FROM invitations WHERE isDemo = 1")
    suspend fun deleteDemoInvitations()
}

@Dao
interface OnboardingSessionDao {
    @Query("SELECT * FROM onboarding_sessions WHERE discordUserId = :discordUserId LIMIT 1")
    suspend fun getSessionByDiscordUserId(discordUserId: String): OnboardingSessionEntity?

    @Query("SELECT * FROM onboarding_sessions WHERE discordUserId = :discordUserId LIMIT 1")
    fun getSessionFlow(discordUserId: String): Flow<OnboardingSessionEntity?>

    @Query("SELECT * FROM onboarding_sessions ORDER BY updatedAtUtc DESC")
    fun getAllSessionsFlow(): Flow<List<OnboardingSessionEntity>>

    @Query("SELECT * FROM onboarding_sessions ORDER BY updatedAtUtc DESC")
    suspend fun getAllSessionsOnce(): List<OnboardingSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: OnboardingSessionEntity)

    @Update
    suspend fun updateSession(session: OnboardingSessionEntity)

    @Query("DELETE FROM onboarding_sessions WHERE discordUserId = :discordUserId")
    suspend fun deleteSession(discordUserId: String)

    @Query("DELETE FROM onboarding_sessions WHERE isDemo = 1")
    suspend fun deleteDemoSessions()

    @Query("SELECT COUNT(*) FROM onboarding_sessions WHERE membershipState = :state")
    suspend fun countSessionsByState(state: String): Int
}

@Dao
interface OnboardingSettingsDao {
    @Query("SELECT * FROM onboarding_settings WHERE guildId = :guildId LIMIT 1")
    fun getSettingsFlow(guildId: String = "DEFAULT_GUILD"): Flow<OnboardingSettingsEntity?>

    @Query("SELECT * FROM onboarding_settings WHERE guildId = :guildId LIMIT 1")
    suspend fun getSettingsOnce(guildId: String = "DEFAULT_GUILD"): OnboardingSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: OnboardingSettingsEntity)
}
