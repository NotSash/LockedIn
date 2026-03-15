package com.lockedin.app.domain.repository

import com.lockedin.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getAll(): Flow<List<Category>>

    suspend fun getAllOnce(): List<Category>

    suspend fun save(category: Category): Long

    suspend fun delete(id: Long)

    suspend fun getByName(name: String): Category?
}

