package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import javax.inject.Inject

/**
 * Loads a single password entry by id.
 */
class GetPasswordByIdUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(id: Long): PasswordEntry? =
        passwordRepository.getById(id)
}

