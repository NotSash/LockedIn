package com.lockedin.app.domain.model

/**
 * Domain representation of a category.
 */
data class Category(
    val id: Long = 0L,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isDefault: Boolean,
    val sortOrder: Int
)

