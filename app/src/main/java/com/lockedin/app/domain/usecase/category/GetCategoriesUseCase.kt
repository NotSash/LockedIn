package com.lockedin.app.domain.usecase.category

import com.lockedin.app.domain.model.Category
import com.lockedin.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observes all categories ordered by sortOrder and name.
 */
class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(): Flow<List<Category>> = categoryRepository.getAll()
}

