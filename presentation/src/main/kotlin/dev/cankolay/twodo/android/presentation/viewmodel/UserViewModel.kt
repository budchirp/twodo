package dev.cankolay.twodo.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.user.User
import dev.cankolay.twodo.android.domain.usecase.api.couple.LeaveCoupleUseCase
import dev.cankolay.twodo.android.domain.usecase.api.user.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val leaveCoupleUseCase: LeaveCoupleUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getUserUseCase()) {
                is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
                is ApiResult.Fatal -> _uiState.update {
                    it.copy(error = result.exception.messageOrDefault())
                }

                is ApiResult.Success -> _uiState.update { it.copy(user = result.data) }
                else -> Unit
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    suspend fun leaveCouple(): ApiResult<Nothing?> {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = leaveCoupleUseCase()
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> _uiState.update { state ->
                state.copy(user = state.user?.copy(couple = null))
            }

            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }

    fun clearUser() {
        _uiState.value = UserUiState()
    }
}

private fun Throwable.messageOrDefault() =
    localizedMessage ?: message ?: "Unexpected error"
