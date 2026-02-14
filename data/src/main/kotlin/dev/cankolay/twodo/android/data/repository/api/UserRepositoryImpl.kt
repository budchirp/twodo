package dev.cankolay.twodo.android.data.repository.api

import dev.cankolay.twodo.android.data.api.model.response.user.toDomain
import dev.cankolay.twodo.android.data.api.service.UserService
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.repository.api.UserRepository
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(private val userService: UserService) : UserRepository {
    override suspend fun initialize() =
        userService.initialize()

    override suspend fun get() = when (val result = userService.get()) {
        is ApiResult.Success -> ApiResult.Success(
            message = result.message,
            data = result.data.toDomain()
        )

        is ApiResult.Loading -> result

        is ApiResult.Error -> result
        is ApiResult.Fatal -> result
    }
}