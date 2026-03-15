package com.lockedin.app.domain.model

/**
 * High-level strength buckets with associated score ranges.
 *
 * Score is an integer 0..100 produced by the PasswordStrengthCalculator
 * (implemented later in the util layer).
 */
enum class PasswordStrength(
    val minScore: Int,
    val maxScore: Int
) {
    WEAK(0, 24),
    FAIR(25, 49),
    STRONG(50, 74),
    VERY_STRONG(75, 100);

    companion object {
        fun fromScore(score: Int): PasswordStrength {
            val s = score.coerceIn(0, 100)
            return values().first { s in it.minScore..it.maxScore }
        }
    }
}

