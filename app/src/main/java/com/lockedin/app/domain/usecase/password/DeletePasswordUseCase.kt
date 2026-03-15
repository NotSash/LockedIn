package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import javax.inject.Inject

/**
 * Deletes a password entry from the vault.
 */
class DeletePasswordUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(entry: PasswordEntry) {
        passwordRepository.delete(entry)
    }
}

