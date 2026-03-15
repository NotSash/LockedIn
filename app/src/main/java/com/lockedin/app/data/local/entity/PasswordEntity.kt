package com.lockedin.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password_entries")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "site_name")
    val siteName: String,
    @ColumnInfo(name = "site_url")
    val siteUrl: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "encrypted_password")
    val encryptedPassword: String,
    @ColumnInfo(name = "favicon_url")
    val faviconUrl: String? = null,
    @ColumnInfo(name = "color_label")
    val colorLabel: String = "#7C4DFF",
    @ColumnInfo(name = "category")
    val category: String = "Other",
    @ColumnInfo(name = "tags")
    val tagsJson: String? = null,
    @ColumnInfo(name = "encrypted_notes")
    val encryptedNotes: String? = null,
    @ColumnInfo(name = "encrypted_custom_fields")
    val encryptedCustomFieldsJson: String? = null,
    @ColumnInfo(name = "encrypted_totp_secret")
    val encryptedTotpSecret: String? = null,
    @ColumnInfo(name = "password_strength")
    val passwordStrength: Int = 0,
    @ColumnInfo(name = "is_compromised")
    val isCompromised: Int = 0,
    @ColumnInfo(name = "compromise_count")
    val compromiseCount: Int = 0,
    @ColumnInfo(name = "times_used")
    val timesUsed: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null
)

