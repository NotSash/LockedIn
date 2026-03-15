package com.lockedin.app.domain.usecase.security

import com.lockedin.app.domain.model.SecurityReport
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Runs the full security audit pipeline:
 * - weak passwords
 * - reused passwords
 * - old passwords
 * - breached passwords (via HIBP)
 * - overall score
 */
class RunFullAuditUseCase(
    private val passwordRepository: PasswordRepository,
    private val findWeakPasswords: FindWeakPasswordsUseCase,
    private val findReusedPasswords: FindReusedPasswordsUseCase,
    private val findOldPasswords: FindOldPasswordsUseCase,
    private val checkBreachedPasswords: CheckBreachedPasswordsUseCase,
    private val calculateSecurityScore: CalculateSecurityScoreUseCase
) {

    suspend operator fun invoke(
        weakThreshold: Int = 50,
        oldThresholdDays: Int = 180
    ): SecurityReport = withContext(Dispatchers.Default) {
        val all = passwordRepository.getAllOnce()
        if (all.isEmpty()) {
            return@withContext SecurityReport(
                overallScore = 0,
                totalPasswords = 0,
                weakCount = 0,
                reusedCount = 0,
                oldCount = 0,
                breachedCount = 0,
                weakIds = emptyList(),
                reusedGroups = emptyList(),
                oldIds = emptyList(),
                breachedIds = emptyList()
            )
        }

        val weak = findWeakPasswords(weakThreshold)
        val reusedGroups = findReusedPasswords()
        val old = findOldPasswords(oldThresholdDays)
        val breachedIds = checkBreachedPasswords()

        val weakIds = weak.map { it.id }.toSet()
        val oldIds = old.map { it.id }.toSet()

        val score = calculateSecurityScore(
            weakIds = weakIds,
            reusedGroups = reusedGroups.map { it.map { e -> e.id } },
            oldIds = oldIds,
            breachedIds = breachedIds
        )

        SecurityReport(
            overallScore = score,
            totalPasswords = all.size,
            weakCount = weak.size,
            reusedCount = reusedGroups.sumOf { it.size },
            oldCount = old.size,
            breachedCount = breachedIds.size,
            weakIds = weakIds.toList(),
            reusedGroups = reusedGroups.map { group -> group.map { it.id } },
            oldIds = oldIds.toList(),
            breachedIds = breachedIds.toList()
        )
    }
}

