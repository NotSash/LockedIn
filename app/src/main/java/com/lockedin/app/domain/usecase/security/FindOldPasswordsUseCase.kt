package com.lockedin.app.domain.usecase.security

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Identifies passwords that have not been updated for longer than thresholdDays.
 */
class FindOldPasswordsUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(thresholdDays: Int = 180): List<PasswordEntry> =
        withContext(Dispatchers.Default) {
            val all = passwordRepository.getAllOnce()
            if (all.isEmpty()) return@withContext emptyList()

            val now = System.currentTimeMillis()
            val thresholdMillis = thresholdDays.toLong() * 24L * 60L * 60L * 1000L

            all.filter { entry ->
                val age = now - entry.updatedAtMillis
                age >= thresholdMillis
            }
        }
}

