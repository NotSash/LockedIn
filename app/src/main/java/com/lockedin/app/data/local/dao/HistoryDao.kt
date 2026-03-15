package com.lockedin.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lockedin.app.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM password_history ORDER BY created_at DESC LIMIT :limit")
    fun getRecent(limit: Int = 100): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity): Long

    @Query("DELETE FROM password_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM password_history")
    suspend fun clearAll()

    @Query(
        """
        DELETE FROM password_history 
        WHERE id IN (
            SELECT id FROM password_history
            ORDER BY created_at ASC
            LIMIT (SELECT MAX(0, COUNT(*) - :maxCount) FROM password_history)
        )
        """
    )
    suspend fun trimToMax(maxCount: Int)
}

