package com.lockedin.app.domain.usecase.security

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Calculates an overall security score for the vault (0..100) based on:
 * - Average password strength
 * - Penalties for weak / reused / old / breached passwords
 *
 * This is a heuristic score used for UI, not a formal metric.
 */
class CalculateSecurityScoreUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(
        weakIds: Set<Long>,
        reusedGroups: List<List<Long>>,
        oldIds: Set<Long>,
        breachedIds: Set<Long>
    ): Int = withContext(Dispatchers.Default) {
        val all = passwordRepository.getAllOnce()
        if (all.isEmpty()) return@withContext 0

        val avgStrength = all.map { it.strengthScore }.average()

        val weakPenalty = weakIds.size * 2
        val reusedPenalty = reusedGroups.sumOf { group -> (group.size - 1) * 3 }
        val oldPenalty = oldIds.size
        val breachedPenalty = breachedIds.size * 10

        val raw = avgStrength - weakPenalty - reusedPenalty - oldPenalty - breachedPenalty
        raw.coerceIn(0.0, 100.0).toInt()
    }
}

