package dev.cankolay.twodo.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.invite.Invite
import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction
import dev.cankolay.twodo.android.domain.usecase.api.invite.CreateInviteUseCase
import dev.cankolay.twodo.android.domain.usecase.api.invite.GetInvitesUseCase
import dev.cankolay.twodo.android.domain.usecase.api.invite.HandleInviteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InviteUiState(
    val invites: List<Invite>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val createInviteUseCase: CreateInviteUseCase,
    private val getInvitesUseCase: GetInvitesUseCase,
    private val handleInviteUseCase: HandleInviteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(InviteUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchInvites() {
        viewModelScope.launch {
            refreshInvites()
        }
    }

    suspend fun createInvite(username: String): ApiResult<Nothing?> {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = createInviteUseCase(username = username)
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> refreshInvites(updateLoading = false)
            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }

    suspend fun handleInvite(id: String, action: InviteAction): ApiResult<Nothing?> {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = handleInviteUseCase(id = id, action = action)
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> refreshInvites(updateLoading = false)
            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }

    private suspend fun refreshInvites(updateLoading: Boolean = true): ApiResult<List<Invite>> {
        if (updateLoading) {
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        val result = getInvitesUseCase()
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> _uiState.update { it.copy(invites = result.data) }
            else -> Unit
        }

        if (updateLoading) {
            _uiState.update { it.copy(isLoading = false) }
        }

        return result
    }
}

private fun Throwable.messageOrDefault() =
    localizedMessage ?: message ?: "Unexpected error"
