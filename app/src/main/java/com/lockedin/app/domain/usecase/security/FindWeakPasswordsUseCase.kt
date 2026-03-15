package com.lockedin.app.domain.usecase.security

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Identifies weak passwords (strength score < threshold).
 */
class FindWeakPasswordsUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(threshold: Int = 50): List<PasswordEntry> =
        withContext(Dispatchers.Default) {
            passwordRepository.getAllOnce().filter { it.strengthScore < threshold }
        }
}

