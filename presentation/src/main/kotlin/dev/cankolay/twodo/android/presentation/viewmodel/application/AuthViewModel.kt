package dev.cankolay.twodo.android.presentation.viewmodel.application

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.application.AuthState
import dev.cankolay.twodo.android.domain.usecase.api.user.InitializeUserUseCase
import dev.cankolay.twodo.android.domain.usecase.application.auth.GetAuthStateUseCase
import dev.cankolay.twodo.android.domain.usecase.application.auth.UpdateAuthStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthEvent {
    data class Authenticate(val token: String) : AuthEvent()
    object Logout : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val updateAuthStateUseCase: UpdateAuthStateUseCase,
    private val initializeUserUseCase: InitializeUserUseCase
) : ViewModel() {
    val state = getAuthStateUseCase()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = null
        )

    fun onEvent(event: AuthEvent) {
        viewModelScope.launch {
            when (event) {
                is AuthEvent.Authenticate -> {
                    updateAuthStateUseCase(
                        state = AuthState(
                            token = event.token
                        )
                    )

                    initializeUserUseCase()
                }

                is AuthEvent.Logout -> {
                    updateAuthStateUseCase(
                        state = AuthState(
                            token = ""
                        )
                    )
                }
            }
        }
    }

    fun authenticate(uri: Uri) {
        val token = uri.getQueryParameter("token")
        token?.let { token ->
            onEvent(event = AuthEvent.Authenticate(token = token))
        }
    }
}