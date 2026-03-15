package com.lockedin.app.data.repository

import com.lockedin.app.data.local.dao.HistoryDao
import com.lockedin.app.data.local.entity.HistoryEntity
import com.lockedin.app.data.mapper.HistoryMapper
import com.lockedin.app.data.mapper.PasswordHistoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val dao: HistoryDao,
    private val mapper: HistoryMapper
) {

    fun getRecent(limit: Int = 100): Flow<List<PasswordHistoryData>> =
        dao.getRecent(limit).map { list -> list.map { mapper.toData(it) } }

    suspend fun add(entry: PasswordHistoryData) = withContext(Dispatchers.IO) {
        val entity: HistoryEntity = mapper.fromData(entry)
        dao.insert(entity)
        dao.trimToMax(100)
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }
}

