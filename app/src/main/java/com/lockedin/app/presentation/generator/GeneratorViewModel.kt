package com.lockedin.app.presentation.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.domain.model.GeneratedPassword
import com.lockedin.app.domain.model.PasswordHistory
import com.lockedin.app.domain.repository.HistoryRepository
import com.lockedin.app.domain.usecase.generator.GeneratePassphraseUseCase
import com.lockedin.app.domain.usecase.generator.GenerateRandomPasswordUseCase
import com.lockedin.app.domain.usecase.generator.SaveToHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GeneratorMode {
    RANDOM,
    PASSPHRASE
}

data class RandomGeneratorConfig(
    val length: Int = 16,
    val includeUpper: Boolean = true,
    val includeLower: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = true,
    val excludeAmbiguous: Boolean = false,
    val customExcludedChars: String = "",
    val minNumbers: Int = 0,
    val minSymbols: Int = 0
)

data class PassphraseConfig(
    val wordCount: Int = 4,
    val separatorMode: GeneratePassphraseUseCase.SeparatorMode =
        GeneratePassphraseUseCase.SeparatorMode.HYPHEN,
    val capitalizeWords: Boolean = false,
    val includeTrailingNumber: Boolean = false
)

data class GeneratorUiState(
    val mode: GeneratorMode = GeneratorMode.RANDOM,
    val randomConfig: RandomGeneratorConfig = RandomGeneratorConfig(),
    val passphraseConfig: PassphraseConfig = PassphraseConfig(),
    val currentPassword: GeneratedPassword? = null,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val history: List<PasswordHistory> = emptyList(),
    val isHistorySheetVisible: Boolean = false
)

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val generateRandomPassword: GenerateRandomPasswordUseCase,
    private val generatePassphrase: GeneratePassphraseUseCase,
    private val saveToHistory: SaveToHistoryUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
        generate()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            historyRepository.getRecent()
                .collectLatest { list ->
                    _uiState.update { it.copy(history = list) }
                }
        }
    }

    fun setMode(mode: GeneratorMode) {
        if (mode == _uiState.value.mode) return
        _uiState.update { it.copy(mode = mode, errorMessage = null) }
        generate()
    }

    fun updateRandomLength(length: Int) {
        _uiState.update {
            it.copy(randomConfig = it.randomConfig.copy(length = length.coerceIn(8, 128)))
        }
        generate()
    }

    fun toggleRandomFlag(flag: (RandomGeneratorConfig) -> RandomGeneratorConfig) {
        _uiState.update { it.copy(randomConfig = flag(it.randomConfig)) }
        generate()
    }

    fun updateCustomExcludedChars(chars: String) {
        _uiState.update {
            it.copy(randomConfig = it.randomConfig.copy(customExcludedChars = chars))
        }
        generate()
    }

    fun updateMinNumbers(count: Int) {
        _uiState.update {
            it.copy(randomConfig = it.randomConfig.copy(minNumbers = count.coerceIn(0, 9)))
        }
        generate()
    }

    fun updateMinSymbols(count: Int) {
        _uiState.update {
            it.copy(randomConfig = it.randomConfig.copy(minSymbols = count.coerceIn(0, 9)))
        }
        generate()
    }

    fun updatePassphraseConfig(transform: (PassphraseConfig) -> PassphraseConfig) {
        _uiState.update { it.copy(passphraseConfig = transform(it.passphraseConfig)) }
        generate()
    }

    fun generate() {
        val snapshot = _uiState.value
        if (snapshot.isGenerating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            try {
                val generated = when (snapshot.mode) {
                    GeneratorMode.RANDOM -> {
                        val cfg = snapshot.randomConfig
                        val excluded = cfg.customExcludedChars.toSet()
                        generateRandomPassword(
                            length = cfg.length,
                            includeUpper = cfg.includeUpper,
                            includeLower = cfg.includeLower,
                            includeNumbers = cfg.includeNumbers,
                            includeSymbols = cfg.includeSymbols,
                            excludeAmbiguous = cfg.excludeAmbiguous,
                            minNumbers = cfg.minNumbers,
                            minSymbols = cfg.minSymbols,
                            customExcludedChars = excluded
                        )
                    }

                    GeneratorMode.PASSPHRASE -> {
                        val cfg = snapshot.passphraseConfig
                        generatePassphrase(
                            wordCount = cfg.wordCount,
                            separatorMode = cfg.separatorMode,
                            capitalizeWords = cfg.capitalizeWords,
                            includeTrailingNumber = cfg.includeTrailingNumber
                        )
                    }
                }
                _uiState.update { it.copy(currentPassword = generated, isGenerating = false) }
                saveToHistory(generated, wasSavedToVault = false)
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = t.message ?: "Failed to generate password"
                    )
                }
            }
        }
    }

    fun markCurrentSavedToVault() {
        val current = _uiState.value.currentPassword ?: return
        viewModelScope.launch {
            saveToHistory(current, wasSavedToVault = true)
        }
    }

    fun showHistorySheet(show: Boolean) {
        _uiState.update { it.copy(isHistorySheetVisible = show) }
    }
}

