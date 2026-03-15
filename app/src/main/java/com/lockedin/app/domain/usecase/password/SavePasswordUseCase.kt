package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import javax.inject.Inject

/**
 * Creates a new password entry in the vault.
 *
 * SECURITY:
 * - Caller must ensure PasswordEntry.password is wiped from memory
 *   once it is no longer needed.
 */
class SavePasswordUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(entry: PasswordEntry): Long {
        require(entry.id == 0L) { "SavePasswordUseCase is intended for new entries (id == 0)" }
        return passwordRepository.save(entry)
    }
}

