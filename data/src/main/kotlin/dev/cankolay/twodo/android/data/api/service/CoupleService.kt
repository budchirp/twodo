package dev.cankolay.twodo.android.data.api.service

import android.R.attr.path
import dev.cankolay.twodo.android.data.api.client.KtorClient
import dev.cankolay.twodo.android.data.api.client.request
import dev.cankolay.twodo.android.data.api.model.response.invite.InviteDto
import dev.cankolay.twodo.android.domain.model.api.ApiConstants
import io.ktor.client.request.post
import io.ktor.http.path
import javax.inject.Inject

class CoupleService @Inject constructor(private val client: KtorClient) {
    suspend fun leave() = request<List<InviteDto>> {
        client().post {
            url {
                path(ApiConstants.Endpoints.COUPLE_LEAVE)
            }
        }
    }
}