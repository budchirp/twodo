package dev.cankolay.twodo.android.data.api.model.request.note

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequestDto(
    val title: String,
    val content: String? = null
)
