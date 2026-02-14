package dev.cankolay.twodo.android.domain.usecase.api.user

import dev.cankolay.twodo.android.domain.repository.api.UserRepository
import javax.inject.Inject

class GetUserUseCase
@Inject
constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.get()
}