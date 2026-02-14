package dev.cankolay.twodo.android.domain.repository.api

import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.note.Note

interface NoteRepository {
    suspend fun create(title: String): ApiResult<Note>

    suspend fun getAll(): ApiResult<List<Note>>
    suspend fun get(id: String): ApiResult<Note>

    suspend fun update(
        id: String,
        note: Note
    ): ApiResult<Note>

    suspend fun delete(id: String): ApiResult<Nothing?>
}