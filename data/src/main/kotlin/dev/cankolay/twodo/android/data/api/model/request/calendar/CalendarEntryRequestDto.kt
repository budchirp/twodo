package dev.cankolay.twodo.android.data.api.model.request.calendar

import dev.cankolay.twodo.android.domain.model.api.calendar.CalendarEntryInput
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEntryRequestDto(
    val date: String,
    val type: String,
    val notes: String? = null,
    val period: PeriodRequestDto? = null,
    val sexualActivity: SexualActivityRequestDto? = null
)

@Serializable
data class PeriodRequestDto(
    val event: String,
    val flowLevel: String,
    val symptoms: List<String>
)

@Serializable
data class SexualActivityRequestDto(
    val sexOccurred: Boolean,
    val protectionMethod: String,
    val contraceptionUsed: Boolean,
    val condomUsed: Boolean,
    val withdrawal: Boolean,
    val emergencyContraception: Boolean,
    val ejaculationLocation: String
)

fun CalendarEntryInput.toDto() = CalendarEntryRequestDto(
    date = date.toString(),
    type = type.value,
    notes = notes,
    period = period?.let { period ->
        PeriodRequestDto(
            event = period.event.value,
            flowLevel = period.flowLevel.value,
            symptoms = period.symptoms.map { it.value }
        )
    },
    sexualActivity = sexualActivity?.let { sexualActivity ->
        SexualActivityRequestDto(
            sexOccurred = sexualActivity.sexOccurred,
            protectionMethod = sexualActivity.protectionMethod.value,
            contraceptionUsed = sexualActivity.protectionMethod.value != "none",
            condomUsed = sexualActivity.protectionMethod.value == "condom",
            withdrawal = sexualActivity.protectionMethod.value == "withdrawal",
            emergencyContraception = sexualActivity.protectionMethod.value == "emergency_contraception",
            ejaculationLocation = sexualActivity.ejaculationLocation.value
        )
    }
)
