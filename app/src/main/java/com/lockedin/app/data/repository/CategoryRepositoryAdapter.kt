package com.lockedin.app.data.repository

import com.lockedin.app.data.mapper.CategoryData
import com.lockedin.app.data.mapper.CategoryMapper
import com.lockedin.app.domain.model.Category
import com.lockedin.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Bridges CategoryRepositoryImpl to the domain CategoryRepository interface.
 */
class CategoryRepositoryAdapter(
    private val impl: CategoryRepositoryImpl
) : CategoryRepository {

    override fun getAll(): Flow<List<Category>> =
        impl.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllOnce(): List<Category> =
        impl.getAllOnce().map { it.toDomain() }

    override suspend fun save(category: Category): Long =
        impl.save(category.toData())

    override suspend fun delete(id: Long) {
        impl.delete(id)
    }

    override suspend fun getByName(name: String): Category? =
        impl.getByName(name)?.toDomain()
}

private fun CategoryData.toDomain(): Category =
    Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = color,
        isDefault = isDefault,
        sortOrder = sortOrder
    )

private fun Category.toData(): CategoryData =
    CategoryData(
        id = id,
        name = name,
        iconName = iconName,
        color = colorHex,
        isDefault = isDefault,
        sortOrder = sortOrder
    )

