package dev.cankolay.twodo.android.domain.usecase.application.auth

import dev.cankolay.twodo.android.domain.model.application.AuthState
import dev.cankolay.twodo.android.domain.repository.application.AuthStateRepository
import javax.inject.Inject

class UpdateAuthStateUseCase @Inject constructor(
    private val authStateRepository: AuthStateRepository
) {
    suspend operator fun invoke(state: AuthState) = authStateRepository.update(state)
}