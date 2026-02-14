package dev.cankolay.twodo.android.data.api.model.request.todo

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTodoRequestDto(
    val title: String,
    val content: String,
    val completed: Boolean
)