package com.lockedin.app.domain.usecase.generator

import com.lockedin.app.domain.model.CharacterTypes
import com.lockedin.app.domain.model.GeneratedPassword
import com.lockedin.app.domain.model.PasswordHistory
import com.lockedin.app.domain.repository.HistoryRepository

/**
 * Saves a generated password into the history list.
 *
 * Repository is responsible for trimming history to max size (e.g., 100 entries).
 */
class SaveToHistoryUseCase(
    private val historyRepository: HistoryRepository
) {

    suspend operator fun invoke(
        generated: GeneratedPassword,
        wasSavedToVault: Boolean
    ) {
        val history = PasswordHistory(
            id = 0L,
            password = generated.value.copyOf(),
            strengthScore = generated.strengthScore,
            length = generated.length,
            characterTypes = CharacterTypes(
                upper = generated.characterTypes.upper,
                lower = generated.characterTypes.lower,
                numbers = generated.characterTypes.numbers,
                symbols = generated.characterTypes.symbols
            ),
            wasSavedToVault = wasSavedToVault,
            createdAtMillis = System.currentTimeMillis()
        )

        historyRepository.add(history)
    }
}

