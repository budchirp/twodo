package dev.cankolay.twodo.android.data.api.model.request.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequestDto(
    val name: String,
    val gender: String
)
