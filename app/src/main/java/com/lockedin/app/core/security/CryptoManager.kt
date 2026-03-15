package com.lockedin.app.core.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random

/**
 * AES-256-GCM encrypt/decrypt using keys from Android Keystore.
 *
 * SECURITY:
 * - 96-bit random IV per encryption, prepended to ciphertext.
 * - Output is Base64(IV || ciphertext) for storage.
 * - Input byte arrays are wiped where feasible.
 */
class CryptoManager(
    private val keystoreManager: KeystoreManager
) {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE_BYTES = 12
        private const val TAG_LENGTH_BITS = 128
    }

    fun encrypt(plaintext: ByteArray): String {
        val key: SecretKey = keystoreManager.getOrCreateMasterKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = Random.nextBytes(IV_SIZE_BYTES)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val cipherBytes = cipher.doFinal(plaintext)
        val combined = ByteArray(IV_SIZE_BYTES + cipherBytes.size)
        System.arraycopy(iv, 0, combined, 0, IV_SIZE_BYTES)
        System.arraycopy(cipherBytes, 0, combined, IV_SIZE_BYTES, cipherBytes.size)

        try {
            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } finally {
            plaintext.fill(0)
            cipherBytes.fill(0)
        }
    }

    fun decrypt(encoded: String): ByteArray {
        val key: SecretKey = keystoreManager.getOrCreateMasterKey()
        val combined = Base64.decode(encoded, Base64.NO_WRAP)
        require(combined.size > IV_SIZE_BYTES)

        val iv = combined.copyOfRange(0, IV_SIZE_BYTES)
        val cipherBytes = combined.copyOfRange(IV_SIZE_BYTES, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        try {
            return cipher.doFinal(cipherBytes)
        } finally {
            iv.fill(0)
            cipherBytes.fill(0)
            combined.fill(0)
        }
    }
}

