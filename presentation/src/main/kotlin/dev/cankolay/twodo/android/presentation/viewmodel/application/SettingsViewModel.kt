package dev.cankolay.twodo.android.presentation.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.application.SettingsState
import dev.cankolay.twodo.android.domain.usecase.application.settings.GetSettingsStateUseCase
import dev.cankolay.twodo.android.domain.usecase.application.settings.UpdateSettingsStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settingsState: SettingsState? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsStateUseCase: GetSettingsStateUseCase,
    private val updateSettingsStateUseCase: UpdateSettingsStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsStateUseCase().collect { settingsState ->
                _uiState.update { it.copy(settingsState = settingsState) }
            }
        }
    }

    fun updateSettings(settingsState: SettingsState) {
        viewModelScope.launch {
            updateSettingsStateUseCase(settingsState)
        }
    }
}
