package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns a flow of all password entries in the vault, ordered by updated time (default DAO order).
 */
class GetAllPasswordsUseCase(
    private val passwordRepository: PasswordRepository
) {

    operator fun invoke(): Flow<List<PasswordEntry>> = passwordRepository.getAll()
}

