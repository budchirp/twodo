package dev.cankolay.twodo.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.ErrorReason
import dev.cankolay.twodo.android.domain.model.api.invite.Invite
import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction
import dev.cankolay.twodo.android.domain.usecase.api.invite.CreateInviteUseCase
import dev.cankolay.twodo.android.domain.usecase.api.invite.GetInvitesUseCase
import dev.cankolay.twodo.android.domain.usecase.api.invite.HandleInviteUseCase
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.form.FormField
import dev.cankolay.twodo.android.presentation.form.update
import dev.cankolay.twodo.android.presentation.form.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvitePartnerFormState(
    val username: FormField<String> = FormField(value = "")
) {
    val canSubmit = username.value.isNotBlank()
}

data class InviteUiState(
    val invites: List<Invite>? = null,
    val inviteForm: InvitePartnerFormState? = null,
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

    fun openInvitePartnerSheet() {
        _uiState.update { it.copy(inviteForm = InvitePartnerFormState(), error = null) }
    }

    fun dismissInvitePartnerSheet() {
        _uiState.update { it.copy(inviteForm = null) }
    }

    fun updateInviteUsername(username: String) {
        _uiState.update { state ->
            state.inviteForm?.let { form ->
                state.copy(inviteForm = form.copy(username = form.username.update(username.trim())))
            } ?: state
        }
    }

    suspend fun submitInvite(): ApiResult<Nothing?> {
        val form = _uiState.value.inviteForm
            ?: return validationError(message = "Username is required.")
        val username = form.username.validateRequired(error = R.string.username_required)
        if (username.error != null) {
            _uiState.update { it.copy(inviteForm = form.copy(username = username)) }
            return validationError(message = "Username is required.")
        }

        _uiState.update { it.copy(inviteForm = form.copy(username = username)) }
        return createInvite(username = username.value.trim())
    }

    fun fetchInvites() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            refreshInvites()
        }
    }

    suspend fun createInvite(username: String): ApiResult<Nothing?> {
        if (_uiState.value.isLoading) return ApiResult.Loading

        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = createInviteUseCase(username = username.trim())
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
        if (_uiState.value.isLoading) return ApiResult.Loading

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

    private fun validationError(message: String) = ApiResult.Error(
        message = message,
        reason = ErrorReason.CLIENT,
        code = "validation_error"
    )
}

private fun Throwable.messageOrDefault() =
    localizedMessage ?: message ?: "Unexpected error"
