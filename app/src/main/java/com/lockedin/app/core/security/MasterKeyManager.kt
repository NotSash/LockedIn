package com.lockedin.app.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Handles master password hashing and verification.
 *
 * PBKDF2-HMAC-SHA256, 120k iterations, 256-bit key, random 256-bit salt.
 * Stores only hash+salt in EncryptedSharedPreferences.
 */
class MasterKeyManager(
    private val context: Context
) {

    companion object {
        private const val PREF_NAME = "lockedin_master_prefs"
        private const val KEY_HASH = "master_password_hash"
        private const val KEY_SALT = "master_password_salt"
        private const val KEY_SETUP = "master_password_is_setup"

        private const val ITERATIONS = 120_000
        private const val KEY_LENGTH_BITS = 256
        private const val SALT_LEN = 32
    }

    private val secureRandom = SecureRandom()

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isMasterPasswordSet(): Boolean =
        prefs.getBoolean(KEY_SETUP, false)

    fun setMasterPassword(password: CharArray) {
        val salt = ByteArray(SALT_LEN).also { secureRandom.nextBytes(it) }
        val hash = hashPassword(password, salt)
        val hashB64 = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        val saltB64 = android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP)

        prefs.edit()
            .putString(KEY_HASH, hashB64)
            .putString(KEY_SALT, saltB64)
            .putBoolean(KEY_SETUP, true)
            .apply()

        password.fill('\u0000')
        hash.fill(0)
        salt.fill(0)
    }

    fun verifyMasterPassword(password: CharArray): Boolean {
        val hashB64 = prefs.getString(KEY_HASH, null)
        val saltB64 = prefs.getString(KEY_SALT, null)
        if (hashB64.isNullOrEmpty() || saltB64.isNullOrEmpty()) {
            password.fill('\u0000')
            return false
        }

        val storedHash = android.util.Base64.decode(hashB64, android.util.Base64.NO_WRAP)
        val salt = android.util.Base64.decode(saltB64, android.util.Base64.NO_WRAP)
        val candidate = hashPassword(password, salt)

        val ok = constantTimeEquals(storedHash, candidate)

        password.fill('\u0000')
        storedHash.fill(0)
        salt.fill(0)
        candidate.fill(0)

        return ok
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun hashPassword(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var r = 0
        for (i in a.indices) {
            r = r or (a[i].toInt() xor b[i].toInt())
        }
        return r == 0
    }
}

