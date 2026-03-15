package com.lockedin.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lockedin.app.domain.model.PasswordEntry
import com.lockedin.app.domain.model.SecurityReport
import com.lockedin.app.domain.usecase.password.GetAllPasswordsUseCase
import com.lockedin.app.domain.usecase.password.SearchPasswordsUseCase
import com.lockedin.app.domain.usecase.password.SortPasswordsUseCase
import com.lockedin.app.domain.usecase.security.RunFullAuditUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class VaultViewMode {
    LIST,
    GRID,
    CATEGORY
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val passwords: List<PasswordEntry> = emptyList(),
    val filteredPasswords: List<PasswordEntry> = emptyList(),
    val viewMode: VaultViewMode = VaultViewMode.LIST,
    val sortMode: SortPasswordsUseCase.SortMode = SortPasswordsUseCase.SortMode.MODIFIED_NEWEST,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val errorMessage: String? = null,
    val securityReport: SecurityReport? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPasswords: GetAllPasswordsUseCase,
    private val searchPasswords: SearchPasswordsUseCase,
    private val sortPasswords: SortPasswordsUseCase,
    private val runFullAudit: RunFullAuditUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        observePasswords()
        refreshSecurityReport()
    }

    private fun observePasswords() {
        viewModelScope.launch {
            getAllPasswords()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load vault"
                        )
                    }
                }
                .collect { list ->
                    _uiState.update { state ->
                        val sorted = sortPasswords(list, state.sortMode)
                        state.copy(
                            isLoading = false,
                            passwords = sorted,
                            filteredPasswords = applySearch(sorted, state.searchQuery)
                        )
                    }
                }
        }
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            refreshSecurityReport()
            delay(600L)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun refreshSecurityReport() {
        viewModelScope.launch {
            try {
                val report = runFullAudit()
                _uiState.update { it.copy(securityReport = report) }
            } catch (_: Throwable) {
                // Keep previous report if available; avoid exposing details in UI.
            }
        }
    }

    fun onChangeViewMode(mode: VaultViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun onChangeSortMode(mode: SortPasswordsUseCase.SortMode) {
        _uiState.update { state ->
            val sorted = sortPasswords(state.passwords, mode)
            state.copy(
                sortMode = mode,
                passwords = sorted,
                filteredPasswords = applySearch(sorted, state.searchQuery)
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearchActive = query.isNotBlank()) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.flow.flowOf(query)
                .debounce(300L)
                .distinctUntilChanged()
                .collect { q ->
                    if (q.isBlank()) {
                        _uiState.update { state ->
                            state.copy(filteredPasswords = state.passwords, isSearchActive = false)
                        }
                    } else {
                        searchPasswords(q).collect { result ->
                            val sorted = sortPasswords(result, _uiState.value.sortMode)
                            _uiState.update { it.copy(filteredPasswords = sorted, isSearchActive = true) }
                        }
                    }
                }
        }
    }

    fun clearSearch() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                isSearchActive = false,
                filteredPasswords = state.passwords
            )
        }
        searchJob?.cancel()
    }

    private fun applySearch(
        passwords: List<PasswordEntry>,
        query: String
    ): List<PasswordEntry> {
        if (query.isBlank()) return passwords
        val lower = query.lowercase()
        return passwords.filter { entry ->
            entry.siteName.lowercase().contains(lower) ||
                entry.username.lowercase().contains(lower) ||
                entry.siteUrl.lowercase().contains(lower)
        }
    }
}

