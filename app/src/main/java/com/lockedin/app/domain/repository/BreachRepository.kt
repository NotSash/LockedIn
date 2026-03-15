package com.lockedin.app.domain.repository

/**
 * Exposes password breach checks to the domain layer.
 */
interface BreachRepository {

    /**
     * Checks all passwords and returns a map of id -> compromised.
     */
    suspend fun checkAllPasswords(): Map<Long, Boolean>

    /**
     * Checks a single plaintext password for compromise.
     */
    suspend fun isPasswordCompromised(plainPassword: String): Boolean
}

