package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository

/**
 * Deletes a password entry from the vault.
 */
class DeletePasswordUseCase(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(entry: PasswordEntry) {
        passwordRepository.delete(entry)
    }
}

