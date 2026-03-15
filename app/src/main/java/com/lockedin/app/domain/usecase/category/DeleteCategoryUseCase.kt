package com.lockedin.app.domain.usecase.category

import com.lockedin.app.domain.repository.CategoryRepository

/**
 * Deletes a category by id.
 *
 * Higher layers must ensure entries are reassigned before deletion.
 */
class DeleteCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(id: Long) {
        require(id != 0L) { "Cannot delete category with id 0" }
        categoryRepository.delete(id)
    }
}

