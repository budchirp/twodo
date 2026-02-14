package dev.cankolay.twodo.android.domain.usecase.api.note

import dev.cankolay.twodo.android.domain.model.api.note.Note
import dev.cankolay.twodo.android.domain.repository.api.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase
@Inject
constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(id: String, note: Note) =
        noteRepository.update(id = id, note = note)
}