package dev.cankolay.twodo.android.domain.repository.api

import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.user.User

interface UserRepository {
    suspend fun initialize(): ApiResult<Nothing?>
    suspend fun get(): ApiResult<User>
}