package com.lockedin.app.presentation.addEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.domain.model.CustomField
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.model.PasswordStrength
import com.lockedin.app.domain.usecase.password.GetPasswordByIdUseCase
import com.lockedin.app.domain.usecase.password.SavePasswordUseCase
import com.lockedin.app.domain.usecase.password.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val siteName: String = "",
    val siteUrl: String = "",
    val username: String = "",
    val password: String = "",
    val notes: String = "",
    val category: String = "Other",
    val tags: List<String> = emptyList(),
    val tagInput: String = "",
    val customFields: List<CustomField> = emptyList(),
    val totpSecret: String = "",
    val strengthScore: Int = 0,
    val canSave: Boolean = false
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val getPasswordById: GetPasswordByIdUseCase,
    private val savePassword: SavePasswordUseCase,
    private val updatePassword: UpdatePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var editingId: Long = 0L
    private var originalCreatedAt: Long = 0L
    private var originalTimesUsed: Int = 0

    fun loadForEdit(id: Long) {
        if (editingId == id && _uiState.value.isEditMode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val entry = getPasswordById(id) ?: run {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Entry not found"
                        )
                    }
                    return@launch
                }
                editingId = entry.id
                originalCreatedAt = entry.createdAtMillis
                originalTimesUsed = entry.timesUsed
                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        isLoading = false,
                        siteName = entry.siteName,
                        siteUrl = entry.siteUrl,
                        username = entry.username,
                        password = entry.password.concatToString(),
                        notes = entry.notes.orEmpty(),
                        category = entry.category,
                        tags = entry.tags,
                        customFields = entry.customFields,
                        totpSecret = entry.totpSecret.orEmpty(),
                        strengthScore = entry.strengthScore
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

    fun onSiteNameChange(value: String) {
        _uiState.update { it.copy(siteName = value) }
        validate()
    }

    fun onSiteUrlChange(value: String) {
        _uiState.update { it.copy(siteUrl = normalizeUrl(value)) }
        validate()
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
        validate()
    }

    fun onPasswordChange(value: String, strengthScore: Int) {
        _uiState.update { it.copy(password = value, strengthScore = strengthScore) }
        validate()
    }

    fun onNotesChange(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value) }
    }

    fun onTagInputChange(value: String) {
        _uiState.update { it.copy(tagInput = value) }
    }

    fun addTagFromInput() {
        val state = _uiState.value
        val trimmed = state.tagInput.trim()
        if (trimmed.isEmpty()) return
        if (state.tags.contains(trimmed)) {
            _uiState.update { it.copy(tagInput = "") }
            return
        }
        _uiState.update {
            it.copy(
                tags = it.tags + trimmed,
                tagInput = ""
            )
        }
    }

    fun removeTag(tag: String) {
        _uiState.update { it.copy(tags = it.tags.filterNot { t -> t == tag }) }
    }

    fun addCustomField() {
        _uiState.update {
            it.copy(
                customFields = it.customFields + CustomField(label = "", value = "")
            )
        }
    }

    fun updateCustomFieldLabel(index: Int, label: String) {
        _uiState.update { state ->
            val list = state.customFields.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(label = label)
            }
            state.copy(customFields = list)
        }
    }

    fun updateCustomFieldValue(index: Int, value: String) {
        _uiState.update { state ->
            val list = state.customFields.toMutableList()
            if (index in list.indices) {
                list[index] = list[index].copy(value = value)
            }
            state.copy(customFields = list)
        }
    }

    fun removeCustomField(index: Int) {
        _uiState.update { state ->
            val list = state.customFields.toMutableList()
            if (index in list.indices) {
                list.removeAt(index)
            }
            state.copy(customFields = list)
        }
    }

    fun onTotpSecretChange(value: String) {
        _uiState.update { it.copy(totpSecret = value.trim()) }
    }

    fun save(onFinished: (Long) -> Unit) {
        val snapshot = _uiState.value
        if (!snapshot.canSave || snapshot.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val now = System.currentTimeMillis()
            val chars = snapshot.password.toCharArray()
            try {
                val entry = PasswordEntry(
                    id = editingId,
                    siteName = snapshot.siteName.trim(),
                    siteUrl = snapshot.siteUrl.trim(),
                    username = snapshot.username.trim(),
                    password = chars,
                    faviconUrl = null,
                    colorLabelHex = "#7C4DFF",
                    category = snapshot.category,
                    tags = snapshot.tags,
                    notes = snapshot.notes.ifBlank { null },
                    customFields = snapshot.customFields,
                    totpSecret = snapshot.totpSecret.ifBlank { null },
                    strengthScore = snapshot.strengthScore,
                    strengthBucket = strengthBucket(snapshot.strengthScore),
                    isCompromised = false,
                    compromiseCount = 0,
                    timesUsed = originalTimesUsed,
                    createdAtMillis = if (editingId == 0L) now else originalCreatedAt,
                    updatedAtMillis = now,
                    lastUsedAtMillis = null
                )

                val id = if (editingId == 0L) {
                    savePassword(entry)
                } else {
                    updatePassword(entry)
                    editingId
                }
                _uiState.update { it.copy(isSaving = false) }
                onFinished(id)
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = t.message ?: "Failed to save entry"
                    )
                }
            } finally {
                chars.fill('\u0000')
            }
        }
    }

    private fun validate() {
        val s = _uiState.value
        val ok = s.siteName.isNotBlank() &&
            s.siteUrl.isNotBlank() &&
            s.username.isNotBlank() &&
            s.password.isNotBlank()
        _uiState.update { it.copy(canSave = ok) }
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return trimmed
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    private fun strengthBucket(score: Int): PasswordStrength =
        when {
            score < 30 -> PasswordStrength.WEAK
            score < 60 -> PasswordStrength.FAIR
            score < 80 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
}

