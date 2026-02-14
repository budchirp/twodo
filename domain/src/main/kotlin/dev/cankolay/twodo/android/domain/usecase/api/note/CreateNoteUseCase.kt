package dev.cankolay.twodo.android.domain.usecase.api.note

import dev.cankolay.twodo.android.domain.repository.api.NoteRepository
import javax.inject.Inject

class CreateNoteUseCase
@Inject
constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(title: String) = noteRepository.create(title = title)
}