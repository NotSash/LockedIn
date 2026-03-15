package com.lockedin.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lockedin.app.data.local.entity.PasswordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {

    @Query("SELECT * FROM password_entries ORDER BY updated_at DESC")
    fun getAll(): Flow<List<PasswordEntity>>

    @Query("SELECT * FROM password_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PasswordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PasswordEntity): Long

    @Update
    suspend fun update(entity: PasswordEntity)

    @Delete
    suspend fun delete(entity: PasswordEntity)

    @Query("DELETE FROM password_entries")
    suspend fun deleteAll()

    @Query(
        """
        SELECT * FROM password_entries
        WHERE site_name LIKE :query
           OR username LIKE :query
           OR site_url LIKE :query
        ORDER BY updated_at DESC
        """
    )
    fun searchBasic(query: String): Flow<List<PasswordEntity>>

    @Query("UPDATE password_entries SET times_used = times_used + 1, last_used_at = :usedAt WHERE id = :id")
    suspend fun incrementUsage(id: Long, usedAt: Long)

    @Query("UPDATE password_entries SET is_compromised = :isComp, compromise_count = :count WHERE id = :id")
    suspend fun updateCompromiseState(id: Long, isComp: Int, count: Int)

    @Query("SELECT * FROM password_entries")
    suspend fun getAllOnce(): List<PasswordEntity>
}

