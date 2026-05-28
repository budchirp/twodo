package dev.cankolay.twodo.android.domain.usecase.api.calendar

import dev.cankolay.twodo.android.domain.repository.api.CalendarRepository
import javax.inject.Inject

class GetPeriodTrackerPredictionUseCase
@Inject
constructor(private val calendarRepository: CalendarRepository) {
    suspend operator fun invoke() = calendarRepository.getPeriodTrackerPrediction()
}
