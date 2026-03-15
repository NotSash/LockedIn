package com.lockedin.app.domain.model

/**
 * Result of generating a password or passphrase.
 */
data class GeneratedPassword(
    val value: CharArray,
    val strengthScore: Int,
    val length: Int,
    val characterTypes: CharacterTypes
)

data class CharacterTypes(
    val upper: Boolean,
    val lower: Boolean,
    val numbers: Boolean,
    val symbols: Boolean
)

