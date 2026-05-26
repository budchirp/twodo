package dev.cankolay.twodo.android.data.api.model.response.server

import kotlinx.serialization.Serializable

@Serializable
data class HealthDto(
    val status: String
)
