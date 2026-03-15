package com.lockedin.app.domain.repository

import com.lockedin.app.domain.model.PasswordHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getRecent(limit: Int = 100): Flow<List<PasswordHistory>>

    suspend fun add(entry: PasswordHistory)

    suspend fun delete(id: Long)

    suspend fun clearAll()
}

