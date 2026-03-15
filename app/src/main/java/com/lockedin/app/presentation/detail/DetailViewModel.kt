package com.lockedin.app.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.usecase.password.DeletePasswordUseCase
import com.lockedin.app.domain.usecase.password.GetPasswordByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val entry: PasswordEntry? = null,
    val isPasswordVisible: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPasswordById: GetPasswordByIdUseCase,
    private val deletePassword: DeletePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val entry = getPasswordById(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entry = entry,
                        errorMessage = if (entry == null) "Entry not found" else null
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Failed to load entry"
                    )
                }
            }
        }
    }

    fun togglePasswordVisible() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun delete(onDeleted: () -> Unit) {
        val entry = _uiState.value.entry ?: return
        viewModelScope.launch {
            try {
                deletePassword(entry)
                onDeleted()
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = t.message ?: "Failed to delete entry")
                }
            }
        }
    }
}

