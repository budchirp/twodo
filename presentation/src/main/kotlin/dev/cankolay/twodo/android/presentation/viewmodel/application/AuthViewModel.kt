package dev.cankolay.twodo.android.presentation.viewmodel.application

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.application.AuthState
import dev.cankolay.twodo.android.domain.usecase.api.user.InitializeUserUseCase
import dev.cankolay.twodo.android.domain.usecase.application.auth.GetAuthStateUseCase
import dev.cankolay.twodo.android.domain.usecase.application.auth.UpdateAuthStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val authState: AuthState? = null,
    val isAuthenticating: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    getAuthStateUseCase: GetAuthStateUseCase,
    private val updateAuthStateUseCase: UpdateAuthStateUseCase,
    private val initializeUserUseCase: InitializeUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAuthStateUseCase().collect { authState ->
                _uiState.update { it.copy(authState = authState) }
            }
        }
    }

    fun authenticate(uri: Uri) {
        val token = uri.getQueryParameter("token") ?: return

        viewModelScope.launch {
            authenticate(token = token)
        }
    }

    suspend fun authenticate(token: String): Boolean {
        _uiState.update { it.copy(isAuthenticating = true) }

        return try {
            updateAuthStateUseCase(state = AuthState(token = token))
            initializeUserUseCase()
            true
        } finally {
            _uiState.update { it.copy(isAuthenticating = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthenticating = false) }
            updateAuthStateUseCase(state = AuthState(token = ""))
        }
    }
}
