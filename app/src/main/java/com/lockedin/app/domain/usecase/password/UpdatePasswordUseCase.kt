package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository

/**
 * Updates an existing password entry.
 */
class UpdatePasswordUseCase(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(entry: PasswordEntry) {
        require(entry.id != 0L) { "UpdatePasswordUseCase requires an existing entry (id != 0)" }
        passwordRepository.save(entry)
    }
}

