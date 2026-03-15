package com.lockedin.app.data.repository

import com.lockedin.app.data.mapper.HistoryMapper
import com.lockedin.app.data.mapper.PasswordHistoryData
import com.lockedin.app.domain.model.PasswordHistory
import com.lockedin.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Bridges HistoryRepositoryImpl to the domain HistoryRepository interface.
 */
class HistoryRepositoryAdapter(
    private val impl: HistoryRepositoryImpl,
    private val mapper: HistoryMapper
) : HistoryRepository {

    override fun getRecent(limit: Int): Flow<List<PasswordHistory>> =
        impl.getRecent(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun add(entry: PasswordHistory) {
        impl.add(entry.toData())
    }

    override suspend fun delete(id: Long) {
        impl.delete(id)
    }

    override suspend fun clearAll() {
        impl.clearAll()
    }

    private fun PasswordHistoryData.toDomain(): PasswordHistory =
        PasswordHistory(
            id = id,
            password = password.copyOf(),
            strengthScore = strength,
            length = length,
            characterTypes = com.lockedin.app.domain.model.CharacterTypes(
                upper = characterTypes.upper,
                lower = characterTypes.lower,
                numbers = characterTypes.num,
                symbols = characterTypes.sym
            ),
            wasSavedToVault = wasSaved,
            createdAtMillis = createdAt
        )

    private fun PasswordHistory.toData(): PasswordHistoryData =
        PasswordHistoryData(
            id = id,
            password = password.copyOf(),
            strength = strengthScore,
            length = length,
            characterTypes = com.lockedin.app.data.local.converter.CharacterTypes(
                upper = characterTypes.upper,
                lower = characterTypes.lower,
                num = characterTypes.numbers,
                sym = characterTypes.symbols
            ),
            wasSaved = wasSavedToVault,
            createdAt = createdAtMillis
        )
}

