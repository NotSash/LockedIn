package com.lockedin.app.presentation.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.core.security.MasterKeyManager
import com.lockedin.app.core.security.PinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private object SettingsKeys {
    val THEME_MODE = intPreferencesKey("theme_mode") // 0=dark,1=light,2=system
    val ANIM_LEVEL = intPreferencesKey("anim_level") // 0=full,1=reduced,2=off
    val HAPTICS = booleanPreferencesKey("haptics_enabled")
    val AUTO_LOCK_SECONDS = intPreferencesKey("auto_lock_seconds")
    val CLIPBOARD_TIMEOUT = intPreferencesKey("clipboard_timeout_seconds")
    val BLOCK_SCREENSHOTS = booleanPreferencesKey("block_screenshots")
}

data class SettingsUiState(
    val isLoading: Boolean = true,
    val themeMode: Int = 0,
    val animationLevel: Int = 0,
    val hapticsEnabled: Boolean = true,
    val autoLockSeconds: Int = 60,
    val clipboardTimeoutSeconds: Int = 30,
    val blockScreenshots: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val masterKeyManager: MasterKeyManager,
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val prefs = dataStore.data.first()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        themeMode = prefs[SettingsKeys.THEME_MODE] ?: 0,
                        animationLevel = prefs[SettingsKeys.ANIM_LEVEL] ?: 0,
                        hapticsEnabled = prefs[SettingsKeys.HAPTICS] ?: true,
                        autoLockSeconds = prefs[SettingsKeys.AUTO_LOCK_SECONDS] ?: 60,
                        clipboardTimeoutSeconds = prefs[SettingsKeys.CLIPBOARD_TIMEOUT] ?: 30,
                        blockScreenshots = prefs[SettingsKeys.BLOCK_SCREENSHOTS] ?: true
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Failed to load settings"
                    )
                }
            }
        }
    }

    fun updateThemeMode(mode: Int) {
        viewModelScope.launch {
            dataStore.edit { it[SettingsKeys.THEME_MODE] = mode }
            _uiState.update { it.copy(themeMode = mode) }
        }
    }

    fun updateAnimationLevel(level: Int) {
        viewModelScope.launch {
            dataStore.edit { it[SettingsKeys.ANIM_LEVEL] = level }
            _uiState.update { it.copy(animationLevel = level) }
        }
    }

    fun updateHaptics(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[SettingsKeys.HAPTICS] = enabled }
            _uiState.update { it.copy(hapticsEnabled = enabled) }
        }
    }

    fun updateAutoLock(seconds: Int) {
        viewModelScope.launch {
            dataStore.edit { it[SettingsKeys.AUTO_LOCK_SECONDS] = seconds }
            _uiState.update { it.copy(autoLockSeconds = seconds) }
        }
    }

    fun updateClipboardTimeout(seconds: Int) {
        viewModelScope.launch {
            dataStore.edit { it[SettingsKeys.CLIPBOARD_TIMEOUT] = seconds }
            _uiState.update { it.copy(clipboardTimeoutSeconds = seconds) }
        }
    }

    fun updateBlockScreenshots(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[SettingsKeys.BLOCK_SCREENSHOTS] = enabled }
            _uiState.update { it.copy(blockScreenshots = enabled) }
        }
    }

    fun isMasterPasswordSet(): Boolean = masterKeyManager.isMasterPasswordSet()

    fun isPinSet(): Boolean = pinManager.isPinSet()
}

