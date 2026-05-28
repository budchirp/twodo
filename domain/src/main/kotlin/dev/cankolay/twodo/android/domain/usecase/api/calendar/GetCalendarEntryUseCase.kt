package dev.cankolay.twodo.android.domain.usecase.api.calendar

import dev.cankolay.twodo.android.domain.repository.api.CalendarRepository
import javax.inject.Inject

class GetCalendarEntryUseCase
@Inject
constructor(private val calendarRepository: CalendarRepository) {
    suspend operator fun invoke(id: String) = calendarRepository.get(id = id)
}
