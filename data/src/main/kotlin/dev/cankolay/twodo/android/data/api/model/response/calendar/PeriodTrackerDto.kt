package dev.cankolay.twodo.android.data.api.model.response.calendar

import dev.cankolay.twodo.android.domain.model.api.calendar.CalendarDateRange
import dev.cankolay.twodo.android.domain.model.api.calendar.CycleHistory
import dev.cankolay.twodo.android.domain.model.api.calendar.FlowLevel
import dev.cankolay.twodo.android.domain.model.api.calendar.PeriodPrediction
import dev.cankolay.twodo.android.domain.model.api.calendar.PeriodRange
import dev.cankolay.twodo.android.domain.model.api.calendar.PeriodSymptom
import dev.cankolay.twodo.android.domain.model.api.calendar.PeriodTrackerSummary
import dev.cankolay.twodo.android.domain.model.api.calendar.PredictionReliability
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class PeriodTrackerSummaryDto(
    val ranges: List<PeriodRangeDto> = emptyList(),
    val cycles: List<CycleHistoryDto> = emptyList(),
    val averageCycleLengthDays: Int? = null,
    val averagePeriodDurationDays: Int? = null,
    val prediction: PeriodPredictionDto? = null
)

@Serializable
data class PeriodRangeDto(
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val isComplete: Boolean,
    val flowLevels: List<String> = emptyList(),
    val symptoms: List<String> = emptyList()
)

@Serializable
data class CycleHistoryDto(
    val periodStartDate: String,
    val periodEndDate: String,
    val periodDurationDays: Int,
    val cycleLengthDays: Int? = null
)

@Serializable
data class CalendarDateRangeDto(
    val startDate: String,
    val endDate: String
)

@Serializable
data class PeriodPredictionDto(
    val hasEnoughData: Boolean = false,
    val reliability: String? = null,
    val nextPeriodWindow: CalendarDateRangeDto? = null,
    val ovulationWindow: CalendarDateRangeDto? = null,
    val expectedPeriodStartDate: String? = null,
    val expectedPeriodEndDate: String? = null,
    val cycleLengthDays: Int? = null,
    val periodDurationDays: Int? = null,
    val cycleLengthVariabilityDays: Int? = null,
    val recentIrregularity: Boolean? = null,
    val basis: String? = null,
    val disclaimer: String? = null
)

fun PeriodTrackerSummaryDto.toDomain() = PeriodTrackerSummary(
    ranges = ranges.map { it.toDomain() },
    cycles = cycles.map { it.toDomain() },
    averageCycleLengthDays = averageCycleLengthDays,
    averagePeriodDurationDays = averagePeriodDurationDays,
    prediction = prediction?.toDomain()
)

fun PeriodRangeDto.toDomain() = PeriodRange(
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    durationDays = durationDays,
    isComplete = isComplete,
    flowLevels = flowLevels.map { FlowLevel.fromValue(value = it) },
    symptoms = symptoms.map { PeriodSymptom.fromValue(value = it) }
)

fun CycleHistoryDto.toDomain() = CycleHistory(
    periodStartDate = LocalDate.parse(periodStartDate),
    periodEndDate = LocalDate.parse(periodEndDate),
    periodDurationDays = periodDurationDays,
    cycleLengthDays = cycleLengthDays
)

fun CalendarDateRangeDto.toDomain() = CalendarDateRange(
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate)
)

fun PeriodPredictionDto.toDomain() = PeriodPrediction(
    hasEnoughData = hasEnoughData,
    reliability = reliability?.let { PredictionReliability.fromValue(value = it) },
    nextPeriodWindow = nextPeriodWindow?.toDomain(),
    ovulationWindow = ovulationWindow?.toDomain(),
    expectedPeriodStartDate = expectedPeriodStartDate?.let { LocalDate.parse(it) },
    expectedPeriodEndDate = expectedPeriodEndDate?.let { LocalDate.parse(it) },
    cycleLengthDays = cycleLengthDays,
    periodDurationDays = periodDurationDays,
    cycleLengthVariabilityDays = cycleLengthVariabilityDays,
    recentIrregularity = recentIrregularity,
    basis = basis,
    disclaimer = disclaimer
)
