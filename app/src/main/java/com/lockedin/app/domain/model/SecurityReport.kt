package com.lockedin.app.domain.model

/**
 * Aggregated security report for the vault.
 */
data class SecurityReport(
    val overallScore: Int,
    val totalPasswords: Int,
    val weakCount: Int,
    val reusedCount: Int,
    val oldCount: Int,
    val breachedCount: Int,
    val weakIds: List<Long>,
    val reusedGroups: List<List<Long>>,
    val oldIds: List<Long>,
    val breachedIds: List<Long>
)

