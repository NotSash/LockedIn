package com.lockedin.app.data.mapper

import com.lockedin.app.core.security.CryptoManager
import com.lockedin.app.data.local.converter.CharacterTypes
import com.lockedin.app.data.local.converter.LockedInTypeConverters
import com.lockedin.app.data.local.entity.HistoryEntity
import javax.inject.Inject

data class PasswordHistoryData(
    val id: Long,
    val password: CharArray,
    val strength: Int,
    val length: Int,
    val characterTypes: CharacterTypes,
    val wasSaved: Boolean,
    val createdAt: Long
)

class HistoryMapper @Inject constructor(
    private val cryptoManager: CryptoManager
) {

    fun toData(entity: HistoryEntity): PasswordHistoryData {
        val decrypted = cryptoManager.decrypt(entity.encryptedPassword)
        val chars = decrypted.toString(Charsets.UTF_8).toCharArray()
        decrypted.fill(0)

        val types = LockedInTypeConverters.toCharacterTypes(entity.characterTypesJson)
            ?: CharacterTypes(upper = true, lower = true, num = true, sym = true)

        return PasswordHistoryData(
            id = entity.id,
            password = chars,
            strength = entity.strength,
            length = entity.length,
            characterTypes = types,
            wasSaved = entity.wasSaved == 1,
            createdAt = entity.createdAt
        )
    }

    fun fromData(data: PasswordHistoryData): HistoryEntity {
        val bytes = data.password.concatToString().encodeToByteArray()
        val encrypted = cryptoManager.encrypt(bytes)
        bytes.fill(0)
        data.password.fill('\u0000')

        val typesJson = LockedInTypeConverters.fromCharacterTypes(data.characterTypes) ?: "{}"

        return HistoryEntity(
            id = data.id,
            encryptedPassword = encrypted,
            strength = data.strength,
            length = data.length,
            characterTypesJson = typesJson,
            wasSaved = if (data.wasSaved) 1 else 0,
            createdAt = data.createdAt
        )
    }
}

