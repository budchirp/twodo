package dev.cankolay.twodo.android.domain.model.api.note

data class Note(
    val id: String,

    val title: String,
    val content: String,

    val completed: Boolean,

    val createdAt: String,
    val updatedAt: String
)
