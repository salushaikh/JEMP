package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey
    val id: String, // e.g. "JEMP#001"
    val discordUserId: String, // e.g. "123456789012345678"
    val jempNumber: Int, // e.g. 1
    val jempDisplayId: String, // "JEMP#001"
    val displayName: String,
    val specialty: String,
    val timezone: String, // e.g. "Asia/Kolkata", "America/New_York", "UTC"
    val joinedAtUtc: Long,
    val status: String = "ACTIVE", // ACTIVE, SUSPENDED, TERMINATED, DEPARTED
    val defaultViewMode: String = "STANDARD", // COMPACT, STANDARD, DETAILED
    val defaultExportType: String = "PROFESSIONAL", // PROFESSIONAL, FULL
    val notificationsEnabled: Boolean = true,
    val publicSharingAllowed: Boolean = true,
    val isDemo: Boolean = false,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val updatedAtUtc: Long = System.currentTimeMillis()
)

@Entity(tableName = "id_counters")
data class IdCounterEntity(
    @PrimaryKey
    val counterName: String = "member_id_counter",
    val nextNumber: Int = 1
)
