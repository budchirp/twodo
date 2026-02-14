package dev.cankolay.twodo.android.domain.usecase.api.note

import dev.cankolay.twodo.android.domain.repository.api.NoteRepository
import javax.inject.Inject

class GetNotesUseCase
@Inject
constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke() = noteRepository.getAll()
}