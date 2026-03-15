package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

/**
 * Searches passwords by text query across site name, username, URL, etc.
 */
class SearchPasswordsUseCase(
    private val passwordRepository: PasswordRepository
) {

    operator fun invoke(query: String): Flow<List<PasswordEntry>> =
        passwordRepository.search(query.trim())
}

