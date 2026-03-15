package com.lockedin.app.domain.repository

import com.lockedin.app.domain.model.PasswordEntry
import kotlinx.coroutines.flow.Flow

/**
 * Domain-facing contract for password storage and retrieval.
 */
interface PasswordRepository {

    fun getAll(): Flow<List<PasswordEntry>>

    /**
     * Returns a snapshot of all entries once (used by audit use cases).
     */
    suspend fun getAllOnce(): List<PasswordEntry>

    suspend fun getById(id: Long): PasswordEntry?

    fun search(query: String): Flow<List<PasswordEntry>>

    suspend fun save(entry: PasswordEntry): Long

    suspend fun delete(entry: PasswordEntry)

    suspend fun incrementUsage(id: Long)

    suspend fun updateCompromiseState(id: Long, isCompromised: Boolean, count: Int)

    suspend fun deleteAll()
}

