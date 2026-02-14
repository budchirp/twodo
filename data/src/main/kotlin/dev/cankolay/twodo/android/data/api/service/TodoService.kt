package dev.cankolay.twodo.android.data.api.service

import dev.cankolay.twodo.android.data.api.client.KtorClient
import dev.cankolay.twodo.android.data.api.client.request
import dev.cankolay.twodo.android.data.api.model.request.todo.CreateTodoRequestDto
import dev.cankolay.twodo.android.data.api.model.request.todo.UpdateTodoRequestDto
import dev.cankolay.twodo.android.data.api.model.response.todo.TodoDto
import dev.cankolay.twodo.android.domain.model.api.ApiConstants
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import javax.inject.Inject

class TodoService
@Inject
constructor(val client: KtorClient) {
    suspend fun create(dto: CreateTodoRequestDto) = request<TodoDto> {
        client().post {
            url {
                path(ApiConstants.Endpoints.TODO)
            }

            setBody(body = dto)
        }
    }

    suspend fun getAll() = request<List<TodoDto>> {
        client().get {
            url {
                path(ApiConstants.Endpoints.TODOS)
            }
        }
    }

    suspend fun get(id: String) = request<TodoDto> {
        client().get {
            url {
                path(ApiConstants.Endpoints.TODO, id)
            }
        }
    }

    suspend fun update(id: String, dto: UpdateTodoRequestDto) = request<TodoDto> {
        client().patch {
            url {
                path(ApiConstants.Endpoints.TODO, id)
            }

            setBody(body = dto)
        }
    }

    suspend fun delete(id: String) = request(no_return = true) {
        client().delete {
            url {
                path(ApiConstants.Endpoints.TODO, id)
            }
        }
    }
}