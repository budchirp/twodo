package dev.cankolay.twodo.android.domain.usecase.api.user

import dev.cankolay.twodo.android.domain.model.api.user.Gender
import dev.cankolay.twodo.android.domain.repository.api.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase
@Inject
constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(name: String, gender: Gender) =
        userRepository.updateProfile(name = name, gender = gender)
}
