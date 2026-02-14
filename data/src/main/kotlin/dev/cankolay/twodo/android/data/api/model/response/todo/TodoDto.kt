package dev.cankolay.twodo.android.data.api.model.response.todo

import dev.cankolay.twodo.android.domain.model.api.note.Note
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoDto(
    val id: String,
    val title: String,
    val content: String,
    val completed: Boolean,
    @SerialName(value = "created_at")
    val createdAt: String,
    @SerialName(value = "updated_at")
    val updatedAt: String
)

fun TodoDto.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        completed = completed,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}