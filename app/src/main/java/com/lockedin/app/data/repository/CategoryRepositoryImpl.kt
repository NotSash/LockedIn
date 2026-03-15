package com.lockedin.app.data.repository

import com.lockedin.app.data.local.dao.CategoryDao
import com.lockedin.app.data.local.entity.CategoryEntity
import com.lockedin.app.data.mapper.CategoryData
import com.lockedin.app.data.mapper.CategoryMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) {

    fun getAll(): Flow<List<CategoryData>> =
        dao.getAll().map { list -> list.map { CategoryMapper.toData(it) } }

    suspend fun getAllOnce(): List<CategoryData> = withContext(Dispatchers.IO) {
        dao.getAllOnce().map { CategoryMapper.toData(it) }
    }

    suspend fun save(category: CategoryData): Long = withContext(Dispatchers.IO) {
        val entity: CategoryEntity = CategoryMapper.fromData(category)
        if (entity.id == 0L) {
            dao.insert(entity)
        } else {
            dao.update(entity)
            entity.id
        }
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun getByName(name: String): CategoryData? = withContext(Dispatchers.IO) {
        dao.getByName(name)?.let { CategoryMapper.toData(it) }
    }
}

