package com.lockedin.app.data.mapper

import com.lockedin.app.core.security.CryptoManager
import com.lockedin.app.data.local.converter.CustomField
import com.lockedin.app.data.local.converter.LockedInTypeConverters
import com.lockedin.app.data.local.entity.PasswordEntity
import javax.inject.Inject

/**
 * Decrypted password DTO used at the data layer.
 *
 * Phase 5 will introduce dedicated domain models; this is an internal bridge.
 */
data class PasswordData(
    val id: Long,
    val siteName: String,
    val siteUrl: String,
    val username: String,
    val password: CharArray,
    val faviconUrl: String?,
    val colorLabel: String,
    val category: String,
    val tags: List<String>,
    val notes: String?,
    val customFields: List<CustomField>,
    val totpSecret: String?,
    val passwordStrength: Int,
    val isCompromised: Boolean,
    val compromiseCount: Int,
    val timesUsed: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val lastUsedAt: Long?
)

class PasswordMapper @Inject constructor(
    private val cryptoManager: CryptoManager
) {

    fun toData(entity: PasswordEntity): PasswordData {
        val decryptedBytes = cryptoManager.decrypt(entity.encryptedPassword)
        val passwordChars = decryptedBytes.toString(Charsets.UTF_8).toCharArray()
        decryptedBytes.fill(0)

        val tags = LockedInTypeConverters.toTagsList(entity.tagsJson) ?: emptyList()
        val customFields = LockedInTypeConverters.toCustomFieldsList(entity.encryptedCustomFieldsJson) ?: emptyList()

        val notes = entity.encryptedNotes?.let {
            val bytes = cryptoManager.decrypt(it)
            val text = bytes.toString(Charsets.UTF_8)
            bytes.fill(0)
            text
        }

        val totp = entity.encryptedTotpSecret?.let {
            val bytes = cryptoManager.decrypt(it)
            val text = bytes.toString(Charsets.UTF_8)
            bytes.fill(0)
            text
        }

        return PasswordData(
            id = entity.id,
            siteName = entity.siteName,
            siteUrl = entity.siteUrl,
            username = entity.username,
            password = passwordChars,
            faviconUrl = entity.faviconUrl,
            colorLabel = entity.colorLabel,
            category = entity.category,
            tags = tags,
            notes = notes,
            customFields = customFields,
            totpSecret = totp,
            passwordStrength = entity.passwordStrength,
            isCompromised = entity.isCompromised == 1,
            compromiseCount = entity.compromiseCount,
            timesUsed = entity.timesUsed,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            lastUsedAt = entity.lastUsedAt
        )
    }

    fun fromData(
        data: PasswordData,
        nowMillis: Long
    ): PasswordEntity {
        val passBytes = data.password.concatToString().encodeToByteArray()
        val encryptedPassword = cryptoManager.encrypt(passBytes)
        passBytes.fill(0)
        data.password.fill('\u0000')

        val notesEncrypted = data.notes?.let { note ->
            val bytes = note.encodeToByteArray()
            val enc = cryptoManager.encrypt(bytes)
            bytes.fill(0)
            enc
        }

        val totpEncrypted = data.totpSecret?.let { secret ->
            val bytes = secret.encodeToByteArray()
            val enc = cryptoManager.encrypt(bytes)
            bytes.fill(0)
            enc
        }

        val tagsJson = LockedInTypeConverters.fromTagsList(data.tags)
        val customFieldsJson = LockedInTypeConverters.fromCustomFieldsList(data.customFields)

        return PasswordEntity(
            id = data.id,
            siteName = data.siteName,
            siteUrl = data.siteUrl,
            username = data.username,
            encryptedPassword = encryptedPassword,
            faviconUrl = data.faviconUrl,
            colorLabel = data.colorLabel,
            category = data.category,
            tagsJson = tagsJson,
            encryptedNotes = notesEncrypted,
            encryptedCustomFieldsJson = customFieldsJson,
            encryptedTotpSecret = totpEncrypted,
            passwordStrength = data.passwordStrength,
            isCompromised = if (data.isCompromised) 1 else 0,
            compromiseCount = data.compromiseCount,
            timesUsed = data.timesUsed,
            createdAt = if (data.createdAt == 0L) nowMillis else data.createdAt,
            updatedAt = nowMillis,
            lastUsedAt = data.lastUsedAt
        )
    }
}

