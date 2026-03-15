package com.lockedin.app.domain.usecase.generator

import com.lockedin.app.domain.model.CharacterTypes
import com.lockedin.app.domain.model.GeneratedPassword
import kotlin.random.Random

/**
 * Generates a human-readable passphrase made from random words.
 *
 * You can replace the built-in word list with an injected larger list later.
 */
class GeneratePassphraseUseCase(
    private val random: Random = Random.Default
) {

    private val defaultWords: List<String> = listOf(
        "locked", "vault", "cipher", "neon", "shadow", "matrix", "quantum", "vector",
        "secure", "signal", "apex", "glyph", "crystal", "orbit", "nova", "pixel",
        "shield", "anchor", "aurora", "titan", "velvet", "prime", "onyx", "ember"
    )

    enum class SeparatorMode {
        HYPHEN,
        PERIOD,
        SPACE,
        UNDERSCORE,
        COMMA,
        NUMBER
    }

    operator fun invoke(
        wordCount: Int,
        separatorMode: SeparatorMode,
        capitalizeWords: Boolean,
        includeTrailingNumber: Boolean
    ): GeneratedPassword {
        require(wordCount in 3..10) { "Word count must be between 3 and 10" }

        val words = (0 until wordCount).map {
            val base = defaultWords.random(random)
            if (capitalizeWords) base.replaceFirstChar { ch -> ch.titlecase() } else base
        }.toMutableList()

        val separator: String = when (separatorMode) {
            SeparatorMode.HYPHEN -> "-"
            SeparatorMode.PERIOD -> "."
            SeparatorMode.SPACE -> " "
            SeparatorMode.UNDERSCORE -> "_"
            SeparatorMode.COMMA -> ","
            SeparatorMode.NUMBER -> random.nextInt(0, 10).toString()
        }

        var passphrase = words.joinToString(separator)

        if (includeTrailingNumber && separatorMode != SeparatorMode.NUMBER) {
            passphrase += random.nextInt(0, 10).toString()
        }

        val chars = passphrase.toCharArray()
        val score = estimatePassphraseStrength(chars, wordCount)

        val types = CharacterTypes(
            upper = chars.any { it.isUpperCase() },
            lower = chars.any { it.isLowerCase() },
            numbers = chars.any { it.isDigit() },
            symbols = chars.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        )

        return GeneratedPassword(
            value = chars,
            strengthScore = score,
            length = chars.size,
            characterTypes = types
        )
    }

    private fun estimatePassphraseStrength(password: CharArray, wordCount: Int): Int {
        if (password.isEmpty()) return 0
        // Rough heuristic: approx 11 bits/word for this small list
        val baseEntropy = wordCount * 11
        val extra = if (password.any { it.isDigit() }) 6 else 0
        val score = baseEntropy + extra
        return score.coerceIn(0, 100)
    }
}

