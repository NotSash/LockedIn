package com.lockedin.app.data.repository

import com.lockedin.app.data.mapper.PasswordData
import com.lockedin.app.domain.model.CustomField
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.model.PasswordStrength
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Bridges the data-layer PasswordRepositoryImpl to the domain PasswordRepository interface.
 */
class PasswordRepositoryAdapter(
    private val impl: PasswordRepositoryImpl
) : PasswordRepository {

    override fun getAll(): Flow<List<PasswordEntry>> =
        impl.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllOnce(): List<PasswordEntry> =
        impl.getAllOnce().map { it.toDomain() }

    override suspend fun getById(id: Long): PasswordEntry? =
        impl.getById(id)?.toDomain()

    override fun search(query: String): Flow<List<PasswordEntry>> =
        impl.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun save(entry: PasswordEntry): Long =
        impl.save(entry.toData())

    override suspend fun delete(entry: PasswordEntry) {
        impl.delete(entry.toData())
    }

    override suspend fun incrementUsage(id: Long) {
        impl.incrementUsage(id)
    }

    override suspend fun updateCompromiseState(id: Long, isCompromised: Boolean, count: Int) {
        impl.updateCompromiseState(id, isCompromised, count)
    }

    override suspend fun deleteAll() {
        impl.deleteAll()
    }
}

private fun PasswordData.toDomain(): PasswordEntry =
    PasswordEntry(
        id = id,
        siteName = siteName,
        siteUrl = siteUrl,
        username = username,
        password = password.copyOf(),
        faviconUrl = faviconUrl,
        colorLabelHex = colorLabel,
        category = category,
        tags = tags,
        notes = notes,
        customFields = customFields.map { CustomField(label = it.label, value = it.value) },
        totpSecret = totpSecret,
        strengthScore = passwordStrength,
        strengthBucket = PasswordStrength.fromScore(passwordStrength),
        isCompromised = isCompromised,
        compromiseCount = compromiseCount,
        timesUsed = timesUsed,
        createdAtMillis = createdAt,
        updatedAtMillis = updatedAt,
        lastUsedAtMillis = lastUsedAt
    )

private fun PasswordEntry.toData(): PasswordData =
    PasswordData(
        id = id,
        siteName = siteName,
        siteUrl = siteUrl,
        username = username,
        password = password.copyOf(),
        faviconUrl = faviconUrl,
        colorLabel = colorLabelHex,
        category = category,
        tags = tags,
        notes = notes,
        customFields = customFields.map {
            com.lockedin.app.data.local.converter.CustomField(
                label = it.label,
                value = it.value
            )
        },
        totpSecret = totpSecret,
        passwordStrength = strengthScore,
        isCompromised = isCompromised,
        compromiseCount = compromiseCount,
        timesUsed = timesUsed,
        createdAt = createdAtMillis,
        updatedAt = updatedAtMillis,
        lastUsedAt = lastUsedAtMillis
    )

