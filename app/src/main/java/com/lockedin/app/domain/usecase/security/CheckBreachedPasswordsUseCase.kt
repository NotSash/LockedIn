package com.lockedin.app.domain.usecase.security

import com.lockedin.app.domain.repository.BreachRepository
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Runs HaveIBeenPwned checks for all passwords and returns ids that are compromised.
 *
 * SECURITY:
 * - Uses BreachRepository which implements the k-anonymity protocol.
 */
class CheckBreachedPasswordsUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val breachRepository: BreachRepository
) {

    suspend operator fun invoke(): Set<Long> = withContext(Dispatchers.Default) {
        val results = breachRepository.checkAllPasswords()
        // Also update compromise state flags in repository
        results.forEach { (id, isCompromised) ->
            passwordRepository.updateCompromiseState(id, isCompromised, if (isCompromised) 1 else 0)
        }
        results.filterValues { it }.keys
    }
}

