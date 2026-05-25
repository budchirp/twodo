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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteUiState(
    val notes: List<Note>? = null,
    val note: Note? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getNotesUseCase()) {
                is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
                is ApiResult.Fatal -> _uiState.update {
                    it.copy(error = result.exception.messageOrDefault())
                }

                is ApiResult.Success -> _uiState.update { it.copy(notes = result.data) }
                else -> Unit
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun fetchNote(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getNoteUseCase(id = id)) {
                is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
                is ApiResult.Fatal -> _uiState.update {
                    it.copy(error = result.exception.messageOrDefault())
                }

                is ApiResult.Success -> _uiState.update { it.copy(note = result.data) }
                else -> Unit
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    suspend fun createNote(title: String): ApiResult<Note> {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = createNoteUseCase(title = title)
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> _uiState.update { state ->
                state.copy(
                    notes = state.notes.orEmpty() + result.data,
                    note = result.data
                )
            }

            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }

    suspend fun updateNote(id: String, note: Note): ApiResult<Note> {
        _uiState.update { it.copy(isSaving = true, error = null) }

        val result = updateNoteUseCase(id = id, note = note)
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> _uiState.update { state ->
                state.copy(
                    notes = state.notes?.map { item ->
                        if (item.id == id) result.data else item
                    }
                )
            }

            else -> Unit
        }

        _uiState.update { it.copy(isSaving = false) }
        return result
    }

    suspend fun deleteNote(id: String): ApiResult<Nothing?> {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = deleteNoteUseCase(id = id)
        when (result) {
            is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            is ApiResult.Fatal -> _uiState.update {
                it.copy(error = result.exception.messageOrDefault())
            }

            is ApiResult.Success -> _uiState.update { state ->
                state.copy(
                    notes = state.notes?.filterNot { it.id == id },
                    note = null
                )
            }

            else -> Unit
        }

        _uiState.update { it.copy(isLoading = false) }
        return result
    }
}

private fun Throwable.messageOrDefault() =
    localizedMessage ?: message ?: "Unexpected error"
