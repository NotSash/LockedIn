package com.lockedin.app.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Manages SQLCipher passphrase used by Room.
 *
 * SECURITY:
 * - Random 256-bit key generated once and stored via EncryptedSharedPreferences.
 * - Returned as raw bytes to SupportFactory; never logged.
 */
class DatabasePassphrase(
    private val context: Context
) {

    companion object {
        private const val PREF_NAME = "lockedin_db_passphrase"
        private const val KEY_PASSPHRASE = "db_passphrase"
        private const val KEY_SET = "db_passphrase_set"
        private const val LEN = 32
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

    @Synchronized
    fun getOrCreatePassphrase(): ByteArray {
        val isSet = prefs.getBoolean(KEY_SET, false)
        if (!isSet) {
            val pass = ByteArray(LEN).also { secureRandom.nextBytes(it) }
            val encoded = android.util.Base64.encodeToString(pass, android.util.Base64.NO_WRAP)
            prefs.edit()
                .putString(KEY_PASSPHRASE, encoded)
                .putBoolean(KEY_SET, true)
                .apply()
            return pass
        }

        val encoded = prefs.getString(KEY_PASSPHRASE, null)
            ?: throw IllegalStateException("DB passphrase flag set but value missing")
        return android.util.Base64.decode(encoded, android.util.Base64.NO_WRAP)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}

