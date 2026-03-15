package com.lockedin.app.domain.model

/**
 * Domain model for a vault password entry.
 *
 * SECURITY:
 * - Sensitive fields are CharArray or encrypted strings depending on context.
 * - Use cases that expose plaintext should wipe CharArray after use.
 */
data class PasswordEntry(
    val id: Long = 0L,
    val siteName: String,
    val siteUrl: String,
    val username: String,
    val password: CharArray,
    val faviconUrl: String?,
    val colorLabelHex: String,
    val category: String,
    val tags: List<String>,
    val notes: String?,
    val customFields: List<CustomField>,
    val totpSecret: String?,
    val strengthScore: Int,
    val strengthBucket: PasswordStrength,
    val isCompromised: Boolean,
    val compromiseCount: Int,
    val timesUsed: Int,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val lastUsedAtMillis: Long?
)

/**
 * Custom field for key/value pairs in a password entry.
 *
 * value is plaintext at the domain layer; repository encrypts it before persistence.
 */
data class CustomField(
    val label: String,
    val value: String
)

