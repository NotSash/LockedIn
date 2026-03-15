package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Searches passwords by text query across site name, username, URL, etc.
 */
class SearchPasswordsUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    operator fun invoke(query: String): Flow<List<PasswordEntry>> =
        passwordRepository.search(query.trim())
}

