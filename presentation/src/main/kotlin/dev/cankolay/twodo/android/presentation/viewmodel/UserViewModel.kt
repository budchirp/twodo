package dev.cankolay.twodo.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.user.User
import dev.cankolay.twodo.android.domain.usecase.api.user.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserEvent {
    object FetchUser : UserEvent()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(value = null)
    val user = _user.stateIn(
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

    fun onEvent(event: UserEvent) {
        viewModelScope.launch {
            when (event) {
                is UserEvent.FetchUser -> {
                    when (val result = getUserUseCase()) {
                        is ApiResult.Error -> _error.value = result.message
                        is ApiResult.Success -> {
                            _user.value = result.data
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}