package com.lockedin.app.data.repository

import com.lockedin.app.data.local.dao.PasswordDao
import com.lockedin.app.data.local.entity.PasswordEntity
import com.lockedin.app.data.mapper.PasswordData
import com.lockedin.app.data.mapper.PasswordMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Data-layer repository for password entries.
 *
 * Phase 5 will adapt this to the domain repository interface.
 */
class PasswordRepositoryImpl(
    private val dao: PasswordDao,
    private val mapper: PasswordMapper
) {

    fun getAll(): Flow<List<PasswordData>> =
        dao.getAll().map { list -> list.map { mapper.toData(it) } }

    suspend fun getById(id: Long): PasswordData? = withContext(Dispatchers.IO) {
        dao.getById(id)?.let { mapper.toData(it) }
    }

    fun search(query: String): Flow<List<PasswordData>> {
        val pattern = "%$query%"
        return dao.searchBasic(pattern).map { list ->
            list.map { mapper.toData(it) }
        }
    }

    suspend fun save(data: PasswordData): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val entity: PasswordEntity = mapper.fromData(data, nowMillis = now)
        if (entity.id == 0L) {
            dao.insert(entity)
        } else {
            dao.update(entity)
            entity.id
        }
    }

    suspend fun delete(data: PasswordData) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val entity = mapper.fromData(data, nowMillis = now)
        dao.delete(entity)
    }

    suspend fun incrementUsage(id: Long) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.incrementUsage(id, now)
    }

    suspend fun updateCompromiseState(id: Long, isCompromised: Boolean, count: Int) =
        withContext(Dispatchers.IO) {
            dao.updateCompromiseState(id, if (isCompromised) 1 else 0, count)
        }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        dao.deleteAll()
    }

    suspend fun getAllOnce(): List<PasswordData> = withContext(Dispatchers.IO) {
        dao.getAllOnce().map { mapper.toData(it) }
    }
}

