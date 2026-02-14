package dev.cankolay.twodo.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.note.Note
import dev.cankolay.twodo.android.domain.usecase.api.note.CreateNoteUseCase
import dev.cankolay.twodo.android.domain.usecase.api.note.DeleteNoteUseCase
import dev.cankolay.twodo.android.domain.usecase.api.note.GetNoteUseCase
import dev.cankolay.twodo.android.domain.usecase.api.note.GetNotesUseCase
import dev.cankolay.twodo.android.domain.usecase.api.note.UpdateNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TodoEvent {
    data class CreateTodo(val title: String) : TodoEvent()

    object FetchTodos : TodoEvent()
    data class FetchTodo(val id: String) : TodoEvent()

    data class UpdateTodo(val id: String, val note: Note) : TodoEvent()

    data class DeleteTodo(val id: String) : TodoEvent()
}

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>?>(value = null)
    val notes = _notes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    private val _note = MutableStateFlow<Note?>(value = null)
    val todo = _note.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    private val _error = MutableStateFlow<String?>(value = null)
    val error = _error.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = null
    )

    fun onEvent(event: TodoEvent) {
        _error.value = null

        viewModelScope.launch {
            when (event) {
                is TodoEvent.CreateTodo -> {
                    when (val result = createNoteUseCase(title = event.title)) {
                        is ApiResult.Error -> _error.value = result.message
                        is ApiResult.Success -> {
                            _note.value = result.data
                        }

                        else -> {}
                    }

                    onEvent(event = TodoEvent.FetchTodos)
                }

                is TodoEvent.FetchTodos -> {
                    when (val result = getNotesUseCase()) {
                        is ApiResult.Error -> _error.value = result.message
                        is ApiResult.Success -> {
                            _notes.value = result.data
                        }

                        else -> {}
                    }
                }

                is TodoEvent.FetchTodo -> {
                    when (val result = getNoteUseCase(id = event.id)) {
                        is ApiResult.Error -> _error.value = result.message
                        is ApiResult.Success -> {
                            _note.value = result.data
                        }

                        else -> {}
                    }
                }

                is TodoEvent.UpdateTodo -> {
                    when (val result = updateNoteUseCase(id = event.id, note = event.note)) {
                        is ApiResult.Error -> _error.value = result.message
                        else -> {}
                    }
                }

                is TodoEvent.DeleteTodo -> {
                    when (val result = deleteNoteUseCase(id = event.id)) {
                        is ApiResult.Error -> _error.value = result.message
                        else -> {}
                    }
                }
            }
        }
    }
}
