package com.lockedin.app.domain.usecase.generator

import com.lockedin.app.domain.model.CharacterTypes
import com.lockedin.app.domain.model.GeneratedPassword
import kotlin.math.log
import kotlin.math.log2
import kotlin.random.Random

/**
 * Generates a random password according to the specified rules.
 *
 * SECURITY:
 * - Uses kotlin.random.Random; you can inject a SecureRandom-backed
 *   implementation if you want stronger guarantees.
 * - Returned CharArray should be wiped by callers when no longer needed.
 */
class GenerateRandomPasswordUseCase(
    private val random: Random = Random.Default
) {

    operator fun invoke(
        length: Int,
        includeUpper: Boolean,
        includeLower: Boolean,
        includeNumbers: Boolean,
        includeSymbols: Boolean,
        excludeAmbiguous: Boolean,
        minNumbers: Int,
        minSymbols: Int,
        customExcludedChars: Set<Char> = emptySet()
    ): GeneratedPassword {
        require(length in 8..128) { "Password length must be between 8 and 128" }

        val upper = ('A'..'Z').toMutableList()
        val lower = ('a'..'z').toMutableList()
        val numbers = ('0'..'9').toMutableList()
        val symbols = listOf('!', '@', '#', '$', '%', '^', '&', '*', '-', '_', '+', '=', '?').toMutableList()

        val ambiguous = setOf('0', 'O', 'o', 'l', '1', 'I')
        if (excludeAmbiguous) {
            upper.removeAll(ambiguous)
            lower.removeAll(ambiguous)
            numbers.removeAll(ambiguous)
        }

        if (customExcludedChars.isNotEmpty()) {
            upper.removeAll(customExcludedChars)
            lower.removeAll(customExcludedChars)
            numbers.removeAll(customExcludedChars)
            symbols.removeAll(customExcludedChars)
        }

        val pools = mutableListOf<List<Char>>()
        if (includeUpper && upper.isNotEmpty()) pools += upper
        if (includeLower && lower.isNotEmpty()) pools += lower
        if (includeNumbers && numbers.isNotEmpty()) pools += numbers
        if (includeSymbols && symbols.isNotEmpty()) pools += symbols

        require(pools.isNotEmpty()) { "At least one character type must remain enabled" }

        val result = mutableListOf<Char>()

        // Enforce minimum numbers
        repeat(minNumbers.coerceAtLeast(0)) {
            require(numbers.isNotEmpty()) { "Numbers must be enabled to enforce minimum numbers" }
            result += numbers.random(random)
        }

        // Enforce minimum symbols
        repeat(minSymbols.coerceAtLeast(0)) {
            require(symbols.isNotEmpty()) { "Symbols must be enabled to enforce minimum symbols" }
            result += symbols.random(random)
        }

        // Fill remaining length
        while (result.size < length) {
            val pool = pools.random(random)
            result += pool.random(random)
        }

        // Shuffle so required chars are not front-loaded
        result.shuffle(random)

        val chars = result.toCharArray()
        val score = estimateStrengthScore(chars)

        val types = CharacterTypes(
            upper = includeUpper,
            lower = includeLower,
            numbers = includeNumbers,
            symbols = includeSymbols
        )

        return GeneratedPassword(
            value = chars,
            strengthScore = score,
            length = chars.size,
            characterTypes = types
        )
    }

    /**
     * Very rough entropy-based strength score mapped to 0..100.
     */
    private fun estimateStrengthScore(password: CharArray): Int {
        if (password.isEmpty()) return 0

        var charsetSize = 0
        if (password.any { it.isLowerCase() }) charsetSize += 26
        if (password.any { it.isUpperCase() }) charsetSize += 26
        if (password.any { it.isDigit() }) charsetSize += 10
        if (password.any { !it.isLetterOrDigit() }) charsetSize += 32

        if (charsetSize == 0) return 0

        val length = password.size
        val entropyBits = length * (log2(charsetSize.toDouble()))
        val maxBits = 128.0
        val normalized = (entropyBits / maxBits) * 100.0
        return normalized.coerceIn(0.0, 100.0).toInt()
    }
}

