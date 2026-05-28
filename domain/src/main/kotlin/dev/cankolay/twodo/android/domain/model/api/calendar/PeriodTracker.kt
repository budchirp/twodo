package dev.cankolay.twodo.android.domain.model.api.calendar

import java.time.LocalDate

enum class PredictionReliability(val value: String) {
    INSUFFICIENT_DATA(value = "insufficient_data"),
    LOW(value = "low"),
    MEDIUM(value = "medium"),
    HIGH(value = "high");

    companion object {
        fun fromValue(value: String) =
            entries.firstOrNull { it.value == value }
                ?: error("Unknown PredictionReliability: $value")
    }
}

data class CalendarDateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class PeriodRange(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val durationDays: Int,
    val isComplete: Boolean,
    val flowLevels: List<FlowLevel>,
    val symptoms: List<PeriodSymptom>
)

data class CycleHistory(
    val periodStartDate: LocalDate,
    val periodEndDate: LocalDate,
    val periodDurationDays: Int,
    val cycleLengthDays: Int?
)

data class PeriodPrediction(
    val hasEnoughData: Boolean,
    val reliability: PredictionReliability?,
    val nextPeriodWindow: CalendarDateRange?,
    val ovulationWindow: CalendarDateRange?,
    val expectedPeriodStartDate: LocalDate?,
    val expectedPeriodEndDate: LocalDate?,
    val cycleLengthDays: Int?,
    val periodDurationDays: Int?,
    val cycleLengthVariabilityDays: Int?,
    val recentIrregularity: Boolean?,
    val basis: String?,
    val disclaimer: String?
)

data class PeriodTrackerSummary(
    val ranges: List<PeriodRange>,
    val cycles: List<CycleHistory>,
    val averageCycleLengthDays: Int?,
    val averagePeriodDurationDays: Int?,
    val prediction: PeriodPrediction?
)
