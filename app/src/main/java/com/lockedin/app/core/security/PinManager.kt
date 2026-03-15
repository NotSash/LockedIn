package com.lockedin.app.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Manages quick-unlock PIN (8 digits) storage and verification.
 *
 * SECURITY:
 * - PBKDF2WithHmacSHA256, 100k iterations, 256-bit output.
 * - Stores only salted hash + salt in EncryptedSharedPreferences.
 * - Plaintext PIN is never persisted; CharArray is wiped after use.
 */
class PinManager(
    private val context: Context
) {

    companion object {
        private const val PREF_NAME = "lockedin_pin_prefs"
        private const val KEY_HASH = "pin_hash"
        private const val KEY_SALT = "pin_salt"
        private const val KEY_SET = "pin_is_set"

        private const val ITERATIONS = 100_000
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

    fun isPinSet(): Boolean = prefs.getBoolean(KEY_SET, false)

    fun setPin(pinDigits: CharArray) {
        val salt = ByteArray(SALT_LEN).also { secureRandom.nextBytes(it) }
        val hash = hashPin(pinDigits, salt)

        val hashB64 = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        val saltB64 = android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP)

        prefs.edit()
            .putString(KEY_HASH, hashB64)
            .putString(KEY_SALT, saltB64)
            .putBoolean(KEY_SET, true)
            .apply()

        pinDigits.fill('\u0000')
        hash.fill(0)
        salt.fill(0)
    }

    fun verifyPin(pinDigits: CharArray): Boolean {
        val hashB64 = prefs.getString(KEY_HASH, null)
        val saltB64 = prefs.getString(KEY_SALT, null)
        if (hashB64.isNullOrEmpty() || saltB64.isNullOrEmpty()) {
            pinDigits.fill('\u0000')
            return false
        }

        val storedHash = android.util.Base64.decode(hashB64, android.util.Base64.NO_WRAP)
        val salt = android.util.Base64.decode(saltB64, android.util.Base64.NO_WRAP)
        val candidate = hashPin(pinDigits, salt)

        val ok = constantTimeEquals(storedHash, candidate)

        pinDigits.fill('\u0000')
        storedHash.fill(0)
        salt.fill(0)
        candidate.fill(0)

        return ok
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun hashPin(pinDigits: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pinDigits, salt, ITERATIONS, KEY_LENGTH_BITS)
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

