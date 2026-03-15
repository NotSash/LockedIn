package com.lockedin.app.domain.model

/**
 * Domain model for a generated password history entry.
 */
data class PasswordHistory(
    val id: Long = 0L,
    val password: CharArray,
    val strengthScore: Int,
    val length: Int,
    val characterTypes: CharacterTypes,
    val wasSavedToVault: Boolean,
    val createdAtMillis: Long
)

