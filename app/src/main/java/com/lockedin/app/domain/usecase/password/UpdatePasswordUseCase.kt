package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import javax.inject.Inject

/**
 * Updates an existing password entry.
 */
class UpdatePasswordUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(entry: PasswordEntry) {
        require(entry.id != 0L) { "UpdatePasswordUseCase requires an existing entry (id != 0)" }
        passwordRepository.save(entry)
    }
}

