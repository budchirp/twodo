package dev.cankolay.twodo.android.data.api.model.response.note

import dev.cankolay.twodo.android.domain.model.api.note.Note
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)

fun NoteDto.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
