package com.lockedin.app.domain.usecase.data

import com.lockedin.app.domain.model.ExportFormat
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.model.PasswordStrength
import com.lockedin.app.domain.repository.PasswordRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

/**
 * Imports passwords from an encrypted LockedIn JSON backup or CSV.
 *
 * SECURITY:
 * - Encrypted JSON: expects [salt(32)][iv(12)][ciphertext] produced by ExportVaultUseCase.
 * - CSV: plaintext; callers must ensure they trust the file source.
 */
class ImportVaultUseCase(
    private val passwordRepository: PasswordRepository
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend operator fun invoke(
        masterPassword: CharArray?,
        format: ExportFormat,
        inputStream: InputStream
    ): Int {
        return when (format) {
            ExportFormat.ENCRYPTED_JSON -> {
                requireNotNull(masterPassword) { "Master password required for encrypted import" }
                importEncryptedJson(masterPassword, inputStream)
            }

            ExportFormat.CSV -> importCsv(inputStream)
        }
    }

    @Serializable
    private data class ImportPassword(
        val siteName: String,
        val siteUrl: String,
        val username: String,
        val password: String,
        val notes: String? = null,
        val category: String = "Other",
        val tags: List<String> = emptyList(),
        val colorLabelHex: String = "#7C4DFF"
    )

    private suspend fun importEncryptedJson(
        masterPassword: CharArray,
        inputStream: InputStream
    ): Int {
        val allBytes = inputStream.readBytes()
        require(allBytes.size > 44) { "Backup file too small" }

        val salt = allBytes.copyOfRange(0, 32)
        val iv = allBytes.copyOfRange(32, 44)
        val ciphertext = allBytes.copyOfRange(44, allBytes.size)

        val key = deriveKeyFromPassword(masterPassword, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val plaintext = cipher.doFinal(ciphertext)
        val jsonString = plaintext.toString(Charsets.UTF_8)

        val list = json.decodeFromString<List<ImportPassword>>(jsonString)
        val count = saveImported(list)

        // Wipe
        masterPassword.fill('\u0000')
        salt.fill(0)
        iv.fill(0)
        ciphertext.fill(0)
        plaintext.fill(0)
        allBytes.fill(0)

        return count
    }

    private suspend fun importCsv(inputStream: InputStream): Int {
        val lines = inputStream.bufferedReader().useLines { seq -> seq.toList() }
        if (lines.isEmpty()) return 0

        val headerSkipped = lines.drop(1)
        val imported = mutableListOf<ImportPassword>()

        headerSkipped.forEach { line ->
            if (line.isBlank()) return@forEach
            // Very simple CSV parsing assuming no embedded commas except inside quotes.
            val cols = parseCsvLine(line)
            if (cols.size < 4) return@forEach
            val siteName = cols.getOrNull(0)?.trim('"') ?: return@forEach
            val url = cols.getOrNull(1)?.trim('"') ?: ""
            val username = cols.getOrNull(2)?.trim('"') ?: ""
            val password = cols.getOrNull(3)?.trim('"') ?: ""
            val notes = cols.getOrNull(4)?.trim('"')
            val category = cols.getOrNull(5)?.trim('"') ?: "Other"
            val tags = cols.getOrNull(6)?.trim('"')?.split(";")?.filter { it.isNotBlank() } ?: emptyList()

            imported += ImportPassword(
                siteName = siteName,
                siteUrl = url,
                username = username,
                password = password,
                notes = notes,
                category = category,
                tags = tags
            )
        }

        return saveImported(imported)
    }

    private suspend fun saveImported(list: List<ImportPassword>): Int {
        var count = 0
        list.forEach { imp ->
            val chars = imp.password.toCharArray()
            val entry = PasswordEntry(
                id = 0L,
                siteName = imp.siteName,
                siteUrl = if (imp.siteUrl.startsWith("http")) imp.siteUrl else "https://${imp.siteUrl}",
                username = imp.username,
                password = chars,
                faviconUrl = null,
                colorLabelHex = imp.colorLabelHex,
                category = imp.category,
                tags = imp.tags,
                notes = imp.notes,
                customFields = emptyList(),
                totpSecret = null,
                strengthScore = 0,
                strengthBucket = PasswordStrength.WEAK,
                isCompromised = false,
                compromiseCount = 0,
                timesUsed = 0,
                createdAtMillis = System.currentTimeMillis(),
                updatedAtMillis = System.currentTimeMillis(),
                lastUsedAtMillis = null
            )
            passwordRepository.save(entry)
            chars.fill('\u0000')
            count++
        }
        return count
    }

    private fun deriveKeyFromPassword(password: CharArray, salt: ByteArray): java.security.Key {
        val iterations = 120_000
        val keyLength = 256
        val spec = PBEKeySpec(password, salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec)
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        line.forEach { ch ->
            when (ch) {
                '"' -> {
                    inQuotes = !inQuotes
                    sb.append(ch)
                }
                ',' -> {
                    if (inQuotes) {
                        sb.append(ch)
                    } else {
                        result += sb.toString()
                        sb.clear()
                    }
                }
                else -> sb.append(ch)
            }
        }
        if (sb.isNotEmpty()) result += sb.toString()
        return result
    }
}

