package com.lockedin.app.data.repository

import com.lockedin.app.domain.repository.BreachRepository

/**
 * Simple adapter that exposes BreachRepositoryImpl via the domain BreachRepository interface.
 */
class BreachRepositoryAdapter(
    private val impl: BreachRepositoryImpl
) : BreachRepository {

    override suspend fun checkAllPasswords(): Map<Long, Boolean> =
        impl.checkAllPasswords()

    override suspend fun isPasswordCompromised(plainPassword: String): Boolean =
        impl.isPasswordCompromised(plainPassword)
}

