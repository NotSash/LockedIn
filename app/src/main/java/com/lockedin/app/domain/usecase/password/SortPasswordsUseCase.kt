package com.lockedin.app.domain.usecase.password

import com.lockedin.app.domain.model.PasswordEntry

/**
 * Pure in-memory sorting of password lists according to the configured mode.
 */
class SortPasswordsUseCase {

    operator fun invoke(
        passwords: List<PasswordEntry>,
        mode: SortMode
    ): List<PasswordEntry> {
        return when (mode) {
            SortMode.NAME_ASC -> passwords.sortedBy { it.siteName.lowercase() }
            SortMode.NAME_DESC -> passwords.sortedByDescending { it.siteName.lowercase() }
            SortMode.CREATED_NEWEST -> passwords.sortedByDescending { it.createdAtMillis }
            SortMode.CREATED_OLDEST -> passwords.sortedBy { it.createdAtMillis }
            SortMode.MODIFIED_NEWEST -> passwords.sortedByDescending { it.updatedAtMillis }
            SortMode.MODIFIED_OLDEST -> passwords.sortedBy { it.updatedAtMillis }
            SortMode.MOST_USED -> passwords.sortedByDescending { it.timesUsed }
            SortMode.CATEGORY -> passwords.sortedWith(
                compareBy<PasswordEntry> { it.category.lowercase() }
                    .thenBy { it.siteName.lowercase() }
            )
            SortMode.WEAKEST_FIRST -> passwords.sortedBy { it.strengthScore }
        }
    }

    enum class SortMode {
        NAME_ASC,
        NAME_DESC,
        CREATED_NEWEST,
        CREATED_OLDEST,
        MODIFIED_NEWEST,
        MODIFIED_OLDEST,
        MOST_USED,
        CATEGORY,
        WEAKEST_FIRST
    }
}

