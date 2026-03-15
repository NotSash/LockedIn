package com.lockedin.app.data.mapper

import com.lockedin.app.data.local.entity.CategoryEntity

data class CategoryData(
    val id: Long,
    val name: String,
    val iconName: String,
    val color: String,
    val isDefault: Boolean,
    val sortOrder: Int
)

object CategoryMapper {

    fun toData(entity: CategoryEntity): CategoryData =
        CategoryData(
            id = entity.id,
            name = entity.name,
            iconName = entity.iconName,
            color = entity.color,
            isDefault = entity.isDefault == 1,
            sortOrder = entity.sortOrder
        )

    fun fromData(data: CategoryData): CategoryEntity =
        CategoryEntity(
            id = data.id,
            name = data.name,
            iconName = data.iconName,
            color = data.color,
            isDefault = if (data.isDefault) 1 else 0,
            sortOrder = data.sortOrder
        )
}

