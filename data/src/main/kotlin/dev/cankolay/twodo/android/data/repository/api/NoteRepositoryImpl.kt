package dev.cankolay.twodo.android.data.repository.api

import dev.cankolay.twodo.android.data.api.model.request.todo.CreateTodoRequestDto
import dev.cankolay.twodo.android.data.api.model.request.todo.UpdateTodoRequestDto
import dev.cankolay.twodo.android.data.api.model.response.todo.toDomain
import dev.cankolay.twodo.android.data.api.service.TodoService
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.note.Note
import dev.cankolay.twodo.android.domain.repository.api.NoteRepository
import javax.inject.Inject

class NoteRepositoryImpl
@Inject
constructor(val todoService: TodoService) : NoteRepository {
    override suspend fun create(title: String) =
        when (val result = todoService.create(dto = CreateTodoRequestDto(title = title))) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.toDomain()
            )

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }

    override suspend fun getAll() =
        when (val result = todoService.getAll()) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.map { it.toDomain() })

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }

    override suspend fun get(id: String) = when (val result = todoService.get(id = id)) {
        is ApiResult.Success -> ApiResult.Success(
            message = result.message,
            data = result.data.toDomain()
        )

        is ApiResult.Loading -> result

        is ApiResult.Error -> result
        is ApiResult.Fatal -> result
    }

    override suspend fun update(
        id: String,
        note: Note
    ) = when (val result = todoService.update(
        id = id,
        dto = UpdateTodoRequestDto(
            title = note.title,
            content = note.content,
            completed = note.completed
        )
    )) {
        is ApiResult.Success -> ApiResult.Success(
            message = result.message,
            data = result.data.toDomain()
        )

        is ApiResult.Loading -> result

        is ApiResult.Error -> result
        is ApiResult.Fatal -> result
    }

    override suspend fun delete(id: String) = todoService.delete(id = id)
}