package dev.cankolay.twodo.android.data.api.service

import dev.cankolay.twodo.android.data.api.client.KtorClient
import dev.cankolay.twodo.android.data.api.client.request
import dev.cankolay.twodo.android.data.api.model.response.user.UserDto
import dev.cankolay.twodo.android.domain.model.api.ApiConstants
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.path
import javax.inject.Inject

class UserService @Inject constructor(private val client: KtorClient) {
    suspend fun initialize() = request(no_return = true) {
        client().post {
            url {
                path(ApiConstants.Endpoints.INITIALIZE)
            }
        }
    }

    suspend fun get() = request<UserDto> {
        client().get {
            url {
                path(ApiConstants.Endpoints.USER)
            }
        }
    }
}