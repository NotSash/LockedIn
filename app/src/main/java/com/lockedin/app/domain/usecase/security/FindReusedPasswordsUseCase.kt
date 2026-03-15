package com.lockedin.app.domain.usecase.security

import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Identifies groups of entries that reuse the same password (based on in-memory hash).
 *
 * SECURITY:
 * - Hashes are kept in memory only and not persisted.
 */
class FindReusedPasswordsUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {

    suspend operator fun invoke(): List<List<PasswordEntry>> =
        withContext(Dispatchers.Default) {
            val all = passwordRepository.getAllOnce()
            if (all.isEmpty()) return@withContext emptyList()

            val digest = MessageDigest.getInstance("SHA-256")
            val groups = mutableMapOf<String, MutableList<PasswordEntry>>()

            all.forEach { entry ->
                val bytes = entry.password.concatToString().toByteArray(Charsets.UTF_8)
                val hash = digest.digest(bytes)
                bytes.fill(0)
                val key = hash.joinToString("") { "%02x".format(it) }
                hash.fill(0)

                groups.getOrPut(key) { mutableListOf() } += entry
            }

            groups.values.filter { it.size > 1 }
        }
}

