package dev.cankolay.twodo.android.domain.usecase.application.auth

import dev.cankolay.twodo.android.domain.repository.application.AuthStateRepository
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val authStateRepository: AuthStateRepository
) {
    operator fun invoke() = authStateRepository.state
}