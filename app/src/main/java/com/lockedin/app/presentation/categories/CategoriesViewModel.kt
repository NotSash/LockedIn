package com.lockedin.app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.domain.model.Category
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.usecase.category.CreateCategoryUseCase
import com.lockedin.app.domain.usecase.category.DeleteCategoryUseCase
import com.lockedin.app.domain.usecase.category.GetCategoriesUseCase
import com.lockedin.app.domain.usecase.password.GetAllPasswordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val categories: List<Category> = emptyList(),
    val passwords: List<PasswordEntry> = emptyList(),
    val isCreateSheetVisible: Boolean = false,
    val createName: String = "",
    val createColorHex: String = "#7C4DFF",
    val createIconName: String = "folder",
    val isCreating: Boolean = false
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
    private val createCategory: CreateCategoryUseCase,
    private val deleteCategory: DeleteCategoryUseCase,
    private val getAllPasswords: GetAllPasswordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
        observePasswords()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            getCategories()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load categories"
                        )
                    }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, categories = list) }
                }
        }
    }

    private fun observePasswords() {
        viewModelScope.launch {
            getAllPasswords()
                .catch { /* ignore, dashboard will surface errors */ }
                .collect { list ->
                    _uiState.update { it.copy(passwords = list) }
                }
        }
    }

    fun toggleCreateSheet(show: Boolean) {
        _uiState.update { it.copy(isCreateSheetVisible = show, errorMessage = null) }
    }

    fun onCreateNameChange(value: String) {
        _uiState.update { it.copy(createName = value) }
    }

    fun onCreateColorChange(value: String) {
        _uiState.update { it.copy(createColorHex = value) }
    }

    fun onCreateIconChange(value: String) {
        _uiState.update { it.copy(createIconName = value) }
    }

    fun createNewCategory(onCreated: (Long) -> Unit) {
        val state = _uiState.value
        if (state.createName.isBlank() || state.isCreating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, errorMessage = null) }
            try {
                val id = createCategory(
                    Category(
                        id = 0L,
                        name = state.createName.trim(),
                        iconName = state.createIconName,
                        colorHex = state.createColorHex,
                        isDefault = false,
                        sortOrder = state.categories.size + 1
                    )
                )
                _uiState.update {
                    it.copy(
                        isCreating = false,
                        isCreateSheetVisible = false,
                        createName = ""
                    )
                }
                onCreated(id)
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isCreating = false,
                        errorMessage = t.message ?: "Failed to create category"
                    )
                }
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            try {
                deleteCategory(id)
            } catch (_: Throwable) {
                // errors surfaced elsewhere; deletion is gated in UI
            }
        }
    }

    fun countForCategory(name: String): Int =
        _uiState.value.passwords.count { it.category == name }
}

