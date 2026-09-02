package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY jempNumber ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: String): MemberEntity?

    @Query("SELECT * FROM members WHERE discordUserId = :discordUserId LIMIT 1")
    suspend fun getMemberByDiscordUserId(discordUserId: String): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Query("DELETE FROM members WHERE isDemo = 1")
    suspend fun deleteDemoMembers()

    @Query("SELECT * FROM id_counters WHERE counterName = 'member_id_counter' LIMIT 1")
    suspend fun getIdCounter(): IdCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdCounter(counter: IdCounterEntity)
}

@Dao
interface TimelineEntryDao {
    @Query("SELECT * FROM timeline_entries WHERE memberId = :memberId ORDER BY timestampUtc ASC")
    fun getEntriesForMember(memberId: String): Flow<List<TimelineEntryEntity>>

    @Query("SELECT * FROM timeline_entries WHERE memberId = :memberId ORDER BY timestampUtc ASC")
    suspend fun getEntriesForMemberOnce(memberId: String): List<TimelineEntryEntity>

    @Query("SELECT * FROM timeline_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: String): TimelineEntryEntity?

    @Query("SELECT * FROM timeline_entries WHERE source = :source AND sourceEventId = :sourceEventId LIMIT 1")
    suspend fun getEntryBySourceAndEventId(source: String, sourceEventId: String): TimelineEntryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(entry: TimelineEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: TimelineEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: TimelineEntryEntity)

    @Query("DELETE FROM timeline_entries WHERE id = :id")
    suspend fun deleteEntryById(id: String)

    @Query("DELETE FROM timeline_entries WHERE isDemo = 1")
    suspend fun deleteDemoEntries()

    @Query("SELECT COUNT(*) FROM timeline_entries WHERE memberId = :memberId")
    suspend fun countEntriesForMember(memberId: String): Int
}

@Dao
interface TimelineShareDao {
    @Query("SELECT * FROM timeline_shares WHERE memberId = :memberId AND revokedAtUtc IS NULL AND expiresAtUtc > :currentTimeUtc ORDER BY createdAtUtc DESC LIMIT 1")
    suspend fun getActiveShareForMember(memberId: String, currentTimeUtc: Long = System.currentTimeMillis()): TimelineShareEntity?

    @Query("SELECT * FROM timeline_shares WHERE tokenHash = :tokenHash LIMIT 1")
    suspend fun getShareByToken(tokenHash: String): TimelineShareEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShare(share: TimelineShareEntity)

    @Update
    suspend fun updateShare(share: TimelineShareEntity)

    @Query("UPDATE timeline_shares SET revokedAtUtc = :revokedAt WHERE memberId = :memberId AND revokedAtUtc IS NULL")
    suspend fun revokeAllSharesForMember(memberId: String, revokedAt: Long = System.currentTimeMillis())
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestampUtc DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert
    suspend fun insertAuditLog(log: AuditLogEntity)
}

@Dao
interface TimelineSettingsDao {
    @Query("SELECT * FROM timeline_settings WHERE guildId = :guildId LIMIT 1")
    fun getSettings(guildId: String = "DEFAULT_GUILD"): Flow<TimelineSettingsEntity?>

    @Query("SELECT * FROM timeline_settings WHERE guildId = :guildId LIMIT 1")
    suspend fun getSettingsOnce(guildId: String = "DEFAULT_GUILD"): TimelineSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: TimelineSettingsEntity)
}
