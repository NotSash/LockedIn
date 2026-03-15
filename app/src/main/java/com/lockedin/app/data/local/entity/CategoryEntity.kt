package com.lockedin.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_name")
    val iconName: String,
    @ColumnInfo(name = "color")
    val color: String,
    @ColumnInfo(name = "is_default")
    val isDefault: Int = 0,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)

