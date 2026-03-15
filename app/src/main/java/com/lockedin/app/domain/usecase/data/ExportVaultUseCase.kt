package com.lockedin.app.domain.usecase.data

import android.content.Context
import com.lockedin.app.domain.model.ExportFormat
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random

/**
 * Exports the entire vault to either an encrypted JSON file or CSV.
 *
 * SECURITY:
 * - Encrypted JSON: AES-256-GCM with key derived from the master password
 *   via PBKDF2-HMAC-SHA256.
 * - CSV: plaintext; caller must confirm user understands the risk.
 */
class ExportVaultUseCase(
    private val context: Context,
    private val passwordRepository: PasswordRepository
) {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend operator fun invoke(
        masterPassword: CharArray,
        format: ExportFormat,
        outputStream: OutputStream
    ) {
        val entries = passwordRepository.getAllOnce()
        when (format) {
            ExportFormat.ENCRYPTED_JSON -> exportEncryptedJson(masterPassword, entries, outputStream)
            ExportFormat.CSV -> exportCsv(entries, outputStream)
        }
    }

    @Serializable
    private data class ExportPassword(
        val siteName: String,
        val siteUrl: String,
        val username: String,
        val password: String,
        val notes: String?,
        val category: String,
        val tags: List<String>,
        val colorLabelHex: String
    )

    private fun exportEncryptedJson(
        masterPassword: CharArray,
        entries: List<PasswordEntry>,
        outputStream: OutputStream
    ) {
        val exportList = entries.map { entry ->
            ExportPassword(
                siteName = entry.siteName,
                siteUrl = entry.siteUrl,
                username = entry.username,
                password = entry.password.concatToString(),
                notes = entry.notes,
                category = entry.category,
                tags = entry.tags,
                colorLabelHex = entry.colorLabelHex
            )
        }

        val plaintext = json.encodeToString(exportList).encodeToByteArray()

        val salt = ByteArray(32).also { Random.Default.nextBytes(it) }
        val key = deriveKeyFromPassword(masterPassword, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12).also { Random.Default.nextBytes(it) }
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val ciphertext = cipher.doFinal(plaintext)

        // File format: [salt(32)][iv(12)][ciphertext...]
        outputStream.use { out ->
            out.write(salt)
            out.write(iv)
            out.write(ciphertext)
            out.flush()
        }

        // Wipe sensitive data
        masterPassword.fill('\u0000')
        plaintext.fill(0)
        salt.fill(0)
        iv.fill(0)
        ciphertext.fill(0)
    }

    private fun exportCsv(
        entries: List<PasswordEntry>,
        outputStream: OutputStream
    ) {
        // WARNING: plaintext passwords; UI must show clear warning before calling.
        outputStream.bufferedWriter().use { writer ->
            writer.appendLine("Site Name,URL,Username,Password,Notes,Category,Tags")
            entries.forEach { entry ->
                val tagsJoined = entry.tags.joinToString(";")
                val notesEscaped = (entry.notes ?: "").replace("\"", "\"\"")
                val line = buildString {
                    append('"').append(entry.siteName.replace("\"", "\"\"")).append('"').append(',')
                    append('"').append(entry.siteUrl.replace("\"", "\"\"")).append('"').append(',')
                    append('"').append(entry.username.replace("\"", "\"\"")).append('"').append(',')
                    append('"').append(entry.password.concatToString().replace("\"", "\"\"")).append('"').append(',')
                    append('"').append(notesEscaped).append('"').append(',')
                    append('"').append(entry.category.replace("\"", "\"\"")).append('"').append(',')
                    append('"').append(tagsJoined.replace("\"", "\"\"")).append('"')
                }
                writer.appendLine(line)
            }
            writer.flush()
        }
    }

    private fun deriveKeyFromPassword(password: CharArray, salt: ByteArray): java.security.Key {
        val iterations = 120_000
        val keyLength = 256
        val spec = PBEKeySpec(password, salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec)
    }
}

