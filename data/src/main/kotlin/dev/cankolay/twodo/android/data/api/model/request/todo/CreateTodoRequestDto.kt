package dev.cankolay.twodo.android.data.api.model.request.todo

import kotlinx.serialization.Serializable

@Serializable
data class CreateTodoRequestDto(
    val title: String
)