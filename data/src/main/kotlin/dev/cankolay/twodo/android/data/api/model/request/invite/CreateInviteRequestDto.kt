package dev.cankolay.twodo.android.data.api.model.request.invite

import kotlinx.serialization.Serializable

@Serializable
data class CreateInviteRequestDto(
    val username: String
)