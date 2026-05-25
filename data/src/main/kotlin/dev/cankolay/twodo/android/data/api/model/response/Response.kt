package dev.cankolay.twodo.android.data.api.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ErrorResponse(
    val error: Boolean,
    val message: String
)

@Serializable
data class SuccessResponse<T>(
    val error: Boolean,
    val message: String,
    val data: T
)

@Serializable
data class EmptySuccessResponse(
    val error: Boolean,
    val message: String,
    val data: JsonElement? = null
)
