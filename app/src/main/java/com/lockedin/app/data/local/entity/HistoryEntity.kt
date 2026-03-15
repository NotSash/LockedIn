package com.lockedin.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "encrypted_password")
    val encryptedPassword: String,
    @ColumnInfo(name = "strength")
    val strength: Int,
    @ColumnInfo(name = "length")
    val length: Int,
    @ColumnInfo(name = "character_types")
    val characterTypesJson: String,
    @ColumnInfo(name = "was_saved")
    val wasSaved: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)

