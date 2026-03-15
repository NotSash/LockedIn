package com.lockedin.app.domain.usecase.category

import com.lockedin.app.domain.model.Category
import com.lockedin.app.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Creates or updates a category.
 *
 * For new categories, id should be 0; for updates, id must be non-zero.
 */
class CreateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: Category): Long {
        require(category.name.isNotBlank()) { "Category name cannot be blank" }
        return categoryRepository.save(category)
    }
}

