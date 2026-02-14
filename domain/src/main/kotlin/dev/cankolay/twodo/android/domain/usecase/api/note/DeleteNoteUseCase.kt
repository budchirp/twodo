package dev.cankolay.twodo.android.domain.usecase.api.note

import dev.cankolay.twodo.android.domain.repository.api.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase
@Inject
constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(id: String) = noteRepository.delete(id = id)
}