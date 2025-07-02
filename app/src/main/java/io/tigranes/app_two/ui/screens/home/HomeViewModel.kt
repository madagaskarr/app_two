package io.tigranes.app_two.ui.screens.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_two.data.preferences.PreferencesManager
import io.tigranes.app_two.data.preferences.RecentEdit
import io.tigranes.app_two.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadRecentEdits()
    }
    
    private fun loadRecentEdits() {
        viewModelScope.launch {
            preferencesManager.getRecentEdits().collect { recentEdits ->
                _uiState.value = _uiState.value.copy(recentEdits = recentEdits)
            }
        }
    }
}

data class HomeUiState(
    val recentEdits: List<RecentEdit> = emptyList()
)