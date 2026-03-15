package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository

/**
 * Creates a new password entry in the vault.
 *
 * SECURITY:
 * - Caller must ensure PasswordEntry.password is wiped from memory
 *   once it is no longer needed.
 */
class SavePasswordUseCase(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(entry: PasswordEntry): Long {
        require(entry.id == 0L) { "SavePasswordUseCase is intended for new entries (id == 0)" }
        return passwordRepository.save(entry)
    }
}

