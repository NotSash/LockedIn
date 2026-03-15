package com.lockedin.app.presentation.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.domain.model.SecurityReport
import com.lockedin.app.domain.usecase.security.RunFullAuditUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SecurityIssueFilter {
    ALL,
    WEAK,
    REUSED,
    OLD,
    BREACHED
}

data class SecurityReportUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val report: SecurityReport? = null,
    val filter: SecurityIssueFilter = SecurityIssueFilter.ALL
)

@HiltViewModel
class SecurityReportViewModel @Inject constructor(
    private val runFullAudit: RunFullAuditUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityReportUiState())
    val uiState: StateFlow<SecurityReportUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val report = runFullAudit()
                _uiState.update { it.copy(isLoading = false, report = report) }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Failed to load security report"
                    )
                }
            }
        }
    }

    fun setFilter(filter: SecurityIssueFilter) {
        _uiState.update { it.copy(filter = filter) }
    }
}

