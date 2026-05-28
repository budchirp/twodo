package dev.cankolay.twodo.android.domain.model.api.calendar

import java.time.LocalDate

data class CalendarEntryInput(
    val date: LocalDate,
    val type: CalendarEntryType,
    val notes: String?,
    val period: PeriodDetails?,
    val sexualActivity: SexualActivityDetails?
)
