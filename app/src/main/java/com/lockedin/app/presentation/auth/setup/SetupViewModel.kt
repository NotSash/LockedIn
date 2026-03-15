package com.lockedin.app.presentation.auth.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.core.security.MasterKeyManager
import com.lockedin.app.core.security.PinManager
import com.lockedin.app.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MasterPasswordRequirements(
    val minLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSymbol: Boolean = false
) {
    val allMet: Boolean
        get() = minLength && hasUppercase && hasLowercase && hasDigit && hasSymbol
}

data class SetupUiState(
    val masterPassword: String = "",
    val confirmPassword: String = "",
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val requirements: MasterPasswordRequirements = MasterPasswordRequirements(),
    val passwordsMatch: Boolean = false,
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val authState: AuthState = AuthState.SetupRequired,
    val pinFirstEntry: String = "",
    val pinConfirmEntry: String = "",
    val isOnPinConfirmStep: Boolean = false,
    val pinError: String? = null,
    val isPinSubmitting: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val masterKeyManager: MasterKeyManager,
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState

    fun onMasterPasswordChanged(value: String) {
        _uiState.update {
            val reqs = evaluateRequirements(value)
            val match = value.isNotEmpty() && value == it.confirmPassword
            it.copy(
                masterPassword = value,
                requirements = reqs,
                passwordsMatch = match,
                canSubmit = reqs.allMet && match,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { state ->
            val match = state.masterPassword.isNotEmpty() && state.masterPassword == value
            state.copy(
                confirmPassword = value,
                passwordsMatch = match,
                canSubmit = state.requirements.allMet && match,
                errorMessage = null
            )
        }
    }

    fun toggleShowPassword() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun toggleShowConfirmPassword() {
        _uiState.update { it.copy(showConfirmPassword = !it.showConfirmPassword) }
    }

    fun submitMasterPassword() {
        val snapshot = _uiState.value
        if (!snapshot.canSubmit || snapshot.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val chars = snapshot.masterPassword.toCharArray()
            try {
                masterKeyManager.setMasterPassword(chars)
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        authState = AuthState.Locked
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "Failed to save master password. Please try again."
                    )
                }
            } finally {
                chars.fill('\u0000')
            }
        }
    }

    private fun evaluateRequirements(password: String): MasterPasswordRequirements {
        val minLength = password.length >= 12
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        return MasterPasswordRequirements(
            minLength = minLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            hasSymbol = hasSymbol
        )
    }

    fun onPinDigitEntered(digit: Int) {
        _uiState.update { state ->
            val maxLength = 8
            return@update if (!state.isOnPinConfirmStep) {
                if (state.pinFirstEntry.length >= maxLength) state
                else state.copy(pinFirstEntry = state.pinFirstEntry + digit.toString(), pinError = null)
            } else {
                if (state.pinConfirmEntry.length >= maxLength) state
                else state.copy(pinConfirmEntry = state.pinConfirmEntry + digit.toString(), pinError = null)
            }
        }
    }

    fun onPinBackspace() {
        _uiState.update { state ->
            if (!state.isOnPinConfirmStep) {
                if (state.pinFirstEntry.isEmpty()) state
                else state.copy(pinFirstEntry = state.pinFirstEntry.dropLast(1))
            } else {
                if (state.pinConfirmEntry.isEmpty()) state
                else state.copy(pinConfirmEntry = state.pinConfirmEntry.dropLast(1))
            }
        }
    }

    fun onPinStageComplete() {
        val state = _uiState.value
        if (!state.isOnPinConfirmStep) {
            if (state.pinFirstEntry.length == 8) {
                _uiState.update { it.copy(isOnPinConfirmStep = true, pinError = null) }
            }
        } else {
            if (state.pinConfirmEntry.length == 8) {
                if (state.pinFirstEntry == state.pinConfirmEntry) {
                    savePin(state.pinFirstEntry)
                } else {
                    _uiState.update {
                        it.copy(
                            pinError = "PINs do not match. Please try again.",
                            pinFirstEntry = "",
                            pinConfirmEntry = "",
                            isOnPinConfirmStep = false
                        )
                    }
                }
            }
        }
    }

    fun skipPinSetup() {
        _uiState.update { it.copy(authState = AuthState.Unlocked) }
    }

    private fun savePin(pin: String) {
        if (_uiState.value.isPinSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isPinSubmitting = true, pinError = null) }
            val chars = pin.toCharArray()
            try {
                pinManager.setPin(chars)
                _uiState.update {
                    it.copy(
                        isPinSubmitting = false,
                        authState = AuthState.Unlocked
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isPinSubmitting = false,
                        pinError = "Failed to save PIN. You can configure it later in Settings.",
                        authState = AuthState.Unlocked
                    )
                }
            } finally {
                chars.fill('\u0000')
            }
        }
    }
}

