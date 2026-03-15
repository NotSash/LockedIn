package com.lockedin.app.data.repository

import com.lockedin.app.data.local.dao.PasswordDao
import com.lockedin.app.data.mapper.PasswordData
import com.lockedin.app.data.mapper.PasswordMapper
import com.lockedin.app.data.remote.HibpApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Locale

/**
 * Performs password breach checks using HaveIBeenPwned k-anonymity API.
 *
 * Only the first 5 characters of the SHA-1 hash are sent to HIBP; the full
 * hash never leaves the device.
 */
class BreachRepositoryImpl(
    private val passwordDao: PasswordDao,
    private val passwordMapper: PasswordMapper,
    private val hibpApi: HibpApi
) {

    /**
     * Returns map of password entry id -> isCompromised.
     */
    suspend fun checkAllPasswords(): Map<Long, Boolean> = withContext(Dispatchers.IO) {
        val result = mutableMapOf<Long, Boolean>()
        val entities = passwordDao.getAllOnce()

        for (entity in entities) {
            val data: PasswordData = passwordMapper.toData(entity)
            val plain = data.password.concatToString()
            data.password.fill('\u0000')

            val compromised = isPasswordCompromised(plain)
            plain.toCharArray().fill('\u0000')

            result[entity.id] = compromised
        }

        result
    }

    suspend fun isPasswordCompromised(plainPassword: String): Boolean {
        val sha1 = sha1Hex(plainPassword)
        val prefix = sha1.substring(0, 5)
        val suffix = sha1.substring(5)

        val response = hibpApi.getHashSuffixes(prefix)
        if (!response.isSuccessful) {
            // Treat network failures as "unknown" -> do not mark compromised.
            return false
        }
        val body = response.body() ?: return false

        body.lineSequence().forEach { line ->
            val parts = line.split(":")
            if (parts.size >= 2) {
                val returnedSuffix = parts[0].trim()
                if (returnedSuffix.equals(suffix, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    private fun sha1Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val bytes = input.toByteArray(Charsets.UTF_8)
        val hash = digest.digest(bytes)
        bytes.fill(0)
        return hash.joinToString("") { "%02x".format(it).uppercase(Locale.US) }
    }
}

