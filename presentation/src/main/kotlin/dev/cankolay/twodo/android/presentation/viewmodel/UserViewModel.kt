package dev.cankolay.twodo.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.ErrorReason
import dev.cankolay.twodo.android.domain.model.api.user.Gender
import dev.cankolay.twodo.android.domain.model.api.user.User
import dev.cankolay.twodo.android.domain.usecase.api.couple.LeaveCoupleUseCase
import dev.cankolay.twodo.android.domain.usecase.api.user.GetUserUseCase
import dev.cankolay.twodo.android.domain.usecase.api.user.UpdateProfileUseCase
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.form.FormField
import dev.cankolay.twodo.android.presentation.form.update
import dev.cankolay.twodo.android.presentation.form.validatePresent
import dev.cankolay.twodo.android.presentation.form.validateRequired
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileFormState(
    val name: FormField<String> = FormField(value = ""),
    val gender: FormField<Gender?> = FormField(value = null)
) {
    val canSubmit = name.value.isNotBlank() && gender.value != null
}

data class UserUiState(
    val user: User? = null,
    val profileForm: ProfileFormState = ProfileFormState(),
    val isLeaveCoupleSheetVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorCode: String? = null,
    val isFatalError: Boolean = false,
    val isInitialized: Boolean = false
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val leaveCoupleUseCase: LeaveCoupleUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()
    private var fetchUserJob: Job? = null

    fun updateProfileName(name: String) {
        _uiState.update { state ->
            state.copy(
                profileForm = state.profileForm.copy(
                    name = state.profileForm.name.update(
                        name
                    )
                )
            )
        }
    }

    fun updateProfileGender(gender: Gender) {
        _uiState.update { state ->
            state.copy(
                profileForm = state.profileForm.copy(
                    gender = state.profileForm.gender.update(value = gender)
                )
            )
        }
    }

    suspend fun submitProfile(): ApiResult<User> {
        val form = _uiState.value.profileForm
        val name = form.name.validateRequired(error = R.string.name_required)
        val gender = form.gender.validatePresent(error = R.string.gender_required)
        val selectedGender = gender.value
        if (name.error != null || gender.error != null || selectedGender == null) {
            _uiState.update { it.copy(profileForm = form.copy(name = name, gender = gender)) }
            return validationError(message = "Profile name and gender are required.")
        }

        _uiState.update { it.copy(profileForm = form.copy(name = name, gender = gender)) }
        return updateProfile(name = name.value.trim(), gender = selectedGender)
    }

    fun openLeaveCoupleSheet() {
        _uiState.update { it.copy(isLeaveCoupleSheetVisible = true) }
    }

    fun dismissLeaveCoupleSheet() {
        _uiState.update { it.copy(isLeaveCoupleSheetVisible = false) }
    }

    fun fetchUser() {
        if (fetchUserJob?.isActive == true) return

        fetchUserJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getUserUseCase()) {
                is ApiResult.Error -> _uiState.update {
                    it.copy(
                        error = result.message,
                        errorCode = result.code,
                        isFatalError = false,
                        user = null
                    )
                }

                is ApiResult.Fatal -> _uiState.update {
                    it.copy(
                        error = result.exception.messageOrDefault(),
                        errorCode = null,
                        isFatalError = true,
                        user = null
                    )
                }

                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        user = result.data,
                        profileForm = result.data.toProfileFormState(),
                        error = null,
                        errorCode = null,
                        isFatalError = false
                    )
                }

                else -> Unit
            }

            _uiState.update { it.copy(isLoading = false, isInitialized = true) }
        }
    }

    suspend fun leaveCouple(): ApiResult<Nothing?> {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = leaveCoupleUseCase()
        when (result) {
            is ApiResult.Error -> _uiState.update {
                it.copy(error = result.message, errorCode = result.code, isFatalError = false)
            }

            is ApiResult.Fatal -> _uiState.update {
                it.copy(
                    error = result.exception.messageOrDefault(),
                    errorCode = null,
                    isFatalError = true
                )
            }

            is ApiResult.Success -> _uiState.update { state ->
                state.copy(
                    user = state.user?.copy(couple = null),
                    isLeaveCoupleSheetVisible = false,
                    error = null,
                    errorCode = null
                )
            }

            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }

    suspend fun updateProfile(name: String, gender: Gender): ApiResult<User> {
        _uiState.update { it.copy(isLoading = true, error = null, errorCode = null) }

        val result = updateProfileUseCase(name = name, gender = gender)
        when (result) {
            is ApiResult.Error -> _uiState.update {
                it.copy(error = result.message, errorCode = result.code, isFatalError = false)
            }

            is ApiResult.Fatal -> _uiState.update {
                it.copy(
                    error = result.exception.messageOrDefault(),
                    errorCode = null,
                    isFatalError = true
                )
            }

            is ApiResult.Success -> {
                val refreshedUser = when (val refreshed = getUserUseCase()) {
                    is ApiResult.Success -> refreshed.data
                    else -> result.data
                }

                _uiState.update {
                    it.copy(
                        user = refreshedUser,
                        profileForm = refreshedUser.toProfileFormState(),
                        error = null,
                        errorCode = null,
                        isFatalError = false,
                        isInitialized = true
                    )
                }
            }

            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }

    fun clearUser() {
        fetchUserJob?.cancel()
        _uiState.value = UserUiState()
    }

    private fun validationError(message: String) = ApiResult.Error(
        message = message,
        reason = ErrorReason.CLIENT,
        code = "validation_error"
    )
}

private fun User.toProfileFormState() = ProfileFormState(
    name = FormField(value = name),
    gender = FormField(value = gender)
)

private fun Throwable.messageOrDefault() =
    localizedMessage ?: message ?: "Unexpected error"
