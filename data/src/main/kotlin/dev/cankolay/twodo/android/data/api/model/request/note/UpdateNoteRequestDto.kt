package dev.cankolay.twodo.android.data.api.model.request.note

import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteRequestDto(
    val title: String? = null,
    val content: String? = null
)
