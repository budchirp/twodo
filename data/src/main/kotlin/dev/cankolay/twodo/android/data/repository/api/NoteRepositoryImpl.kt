package dev.cankolay.twodo.android.data.repository.api

import dev.cankolay.twodo.android.data.api.model.request.note.CreateNoteRequestDto
import dev.cankolay.twodo.android.data.api.model.request.note.UpdateNoteRequestDto
import dev.cankolay.twodo.android.data.api.model.response.note.toDomain
import dev.cankolay.twodo.android.data.api.service.CoupleService
import dev.cankolay.twodo.android.data.api.service.NoteService
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.ErrorReason
import dev.cankolay.twodo.android.domain.model.api.note.Note
import dev.cankolay.twodo.android.domain.repository.api.NoteRepository
import javax.inject.Inject

class NoteRepositoryImpl
@Inject
constructor(
    private val noteService: NoteService,
    private val coupleService: CoupleService
) : NoteRepository {
    override suspend fun create(title: String) = withCouple {
        when (val result = noteService.create(dto = CreateNoteRequestDto(title = title))) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.toDomain(),
                code = result.code
            )

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }
    }

    override suspend fun getAll() = withCouple {
        when (val result = noteService.getAll()) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.map { it.toDomain() },
                code = result.code
            )

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }
    }

    override suspend fun get(id: String) = withCouple {
        when (val result = noteService.get(id = id)) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.toDomain(),
                code = result.code
            )

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }
    }

    override suspend fun update(
        id: String,
        note: Note
    ) = withCouple {
        when (val result = noteService.update(
            id = id,
            dto = UpdateNoteRequestDto(
                title = note.title,
                content = note.content
            )
        )) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.toDomain(),
                code = result.code
            )

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }
    }

    override suspend fun delete(id: String) = withCouple {
        noteService.delete(id = id)
    }

    private suspend fun <T> withCouple(block: suspend () -> ApiResult<T>): ApiResult<T> {
        return when (val result = coupleService.getMe()) {
            is ApiResult.Success -> {
                if (result.data == null) {
                    ApiResult.Error(
                        message = "Create a couple before using notes.",
                        reason = ErrorReason.CLIENT,
                        code = "couple_required"
                    )
                } else {
                    block()
                }
            }

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }
    }
}
