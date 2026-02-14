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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class InviteEvent {
    data class CreateInvite(val username: String) : InviteEvent()

    object FetchInvites : InviteEvent()

    data class HandleInvite(val action: InviteAction, val id: String) : InviteEvent()
}

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val createInviteUseCase: CreateInviteUseCase,
    private val getInvitesUseCase: GetInvitesUseCase,
    private val handleInviteUseCase: HandleInviteUseCase
) : ViewModel() {
    private val _invites = MutableStateFlow<List<Invite>?>(value = null)
    val invites = _invites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    private val _error = MutableStateFlow<String?>(value = null)
    val error = _error.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    fun onEvent(event: InviteEvent) {
        _error.value = null

        viewModelScope.launch {
            when (event) {
                is InviteEvent.CreateInvite -> {
                    when (val result = createInviteUseCase(username = event.username)) {
                        is ApiResult.Error -> _error.value = result.message
                        else -> {}
                    }

                    onEvent(event = InviteEvent.FetchInvites)
                }


                is InviteEvent.FetchInvites -> {
                    when (val result = getInvitesUseCase()) {
                        is ApiResult.Error -> _error.value = result.message
                        is ApiResult.Success -> {
                            _invites.value = result.data
                        }

                        else -> {}
                    }
                }

                is InviteEvent.HandleInvite -> {
                    when (val result = handleInviteUseCase(id = event.id, action = event.action)) {
                        is ApiResult.Error -> _error.value = result.message
                        else -> {}
                    }

                    onEvent(event = InviteEvent.FetchInvites)
                }
            }
        }
    }
}
