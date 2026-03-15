package com.lockedin.app.presentation.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private object OnboardingPrefs {
    val KEY_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val KEY_WANTS_TOUR = booleanPreferencesKey("onboarding_wants_tour")
}

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isCompleted: Boolean = false,
    val showTourSheet: Boolean = false,
    val wantsGuidedTour: Boolean? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val completed = prefs[OnboardingPrefs.KEY_COMPLETED] ?: false
            if (completed) {
                _uiState.update { it.copy(isCompleted = true) }
            }
        }
    }

    fun setPage(index: Int) {
        _uiState.update { it.copy(currentPage = index.coerceIn(0, 2)) }
    }

    fun onNext() {
        _uiState.update { state ->
            val next = (state.currentPage + 1).coerceAtMost(2)
            state.copy(currentPage = next).let { updated ->
                if (next == 2) updated else updated
            }
        }
    }

    fun onSkip() {
        // Jump to tour sheet decision directly
        _uiState.update { it.copy(currentPage = 2, showTourSheet = true) }
    }

    fun onReachedLastPage() {
        _uiState.update { it.copy(showTourSheet = true) }
    }

    fun onTourChoice(wantsTour: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[OnboardingPrefs.KEY_COMPLETED] = true
                prefs[OnboardingPrefs.KEY_WANTS_TOUR] = wantsTour
            }
            _uiState.update {
                it.copy(
                    isCompleted = true,
                    wantsGuidedTour = wantsTour,
                    showTourSheet = false
                )
            }
        }
    }
}

