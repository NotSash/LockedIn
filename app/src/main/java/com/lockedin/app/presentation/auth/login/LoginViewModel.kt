package com.lockedin.app.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.core.security.MasterKeyManager
import com.lockedin.app.core.security.PinManager
import com.lockedin.app.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LoginMethod {
    BIOMETRIC,
    PIN,
    MASTER_PASSWORD
}

data class LoginUiState(
    val method: LoginMethod = LoginMethod.BIOMETRIC,
    val pinDigits: String = "",
    val masterPassword: String = "",
    val masterPasswordVisible: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isPinAvailable: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val attemptsRemaining: Int = 10,
    val isCooldownActive: Boolean = false,
    val cooldownSecondsRemaining: Long = 0L,
    val authState: AuthState = AuthState.Locked
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val masterKeyManager: MasterKeyManager,
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            isBiometricAvailable = false,
            isPinAvailable = pinManager.isPinSet()
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState

    fun setMethod(method: LoginMethod) {
        _uiState.update { it.copy(method = method, errorMessage = null) }
    }

    fun onPinDigit(digit: Int) {
        _uiState.update { state ->
            if (state.pinDigits.length >= 8 || state.isSubmitting || state.isCooldownActive) {
                state
            } else {
                state.copy(pinDigits = state.pinDigits + digit.toString(), errorMessage = null)
            }
        }
        if (_uiState.value.pinDigits.length == 8) {
            submitPin()
        }
    }

    fun onPinBackspace() {
        _uiState.update { state ->
            if (state.pinDigits.isEmpty()) state
            else state.copy(pinDigits = state.pinDigits.dropLast(1))
        }
    }

    fun onMasterPasswordChanged(value: String) {
        _uiState.update { it.copy(masterPassword = value, errorMessage = null) }
    }

    fun toggleMasterPasswordVisible() {
        _uiState.update { it.copy(masterPasswordVisible = !it.masterPasswordVisible) }
    }

    fun onBiometricSuccess() {
        _uiState.update { it.copy(authState = AuthState.Unlocked) }
    }

    fun onBiometricError(message: String) {
        registerFailure(message)
    }

    fun submitMasterPassword() {
        val snapshot = _uiState.value
        if (snapshot.isSubmitting || snapshot.isCooldownActive || snapshot.masterPassword.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val chars = snapshot.masterPassword.toCharArray()
            val ok = try {
                masterKeyManager.verifyMasterPassword(chars)
            } catch (t: Throwable) {
                false
            } finally {
                chars.fill('\u0000')
            }

            if (ok) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        authState = AuthState.Unlocked,
                        masterPassword = ""
                    )
                }
            } else {
                _uiState.update { it.copy(isSubmitting = false, masterPassword = "") }
                registerFailure("Incorrect master password.")
            }
        }
    }

    private fun submitPin() {
        val snapshot = _uiState.value
        if (snapshot.isSubmitting || snapshot.isCooldownActive || snapshot.pinDigits.length != 8) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val chars = snapshot.pinDigits.toCharArray()
            val ok = try {
                pinManager.verifyPin(chars)
            } catch (t: Throwable) {
                false
            } finally {
                chars.fill('\u0000')
            }

            if (ok) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        authState = AuthState.Unlocked,
                        pinDigits = ""
                    )
                }
            } else {
                _uiState.update { it.copy(isSubmitting = false, pinDigits = "") }
                registerFailure("Incorrect PIN.")
            }
        }
    }

    private fun registerFailure(message: String) {
        val current = _uiState.value
        val remaining = (current.attemptsRemaining - 1).coerceAtLeast(0)
        _uiState.update { it.copy(errorMessage = message, attemptsRemaining = remaining) }

        if (remaining == 7) {
            startCooldown(30)
        } else if (remaining == 5) {
            startCooldown(5 * 60)
        } else if (remaining <= 0) {
            _uiState.update {
                it.copy(
                    errorMessage = "Too many failed attempts. Please use your master password.",
                    method = LoginMethod.MASTER_PASSWORD
                )
            }
        }
    }

    private fun startCooldown(seconds: Int) {
        if (seconds <= 0) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCooldownActive = true,
                    cooldownSecondsRemaining = seconds.toLong()
                )
            }
            var remaining = seconds
            while (remaining > 0) {
                delay(1000L)
                remaining--
                _uiState.update {
                    it.copy(cooldownSecondsRemaining = remaining.toLong())
                }
            }
            _uiState.update { it.copy(isCooldownActive = false, cooldownSecondsRemaining = 0L) }
        }
    }
}

