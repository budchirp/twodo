package dev.cankolay.twodo.android.presentation.form

import androidx.annotation.StringRes
import java.time.LocalDate

data class FormField<T>(
    val value: T,
    @param:StringRes val error: Int? = null
)

fun <T> FormField<T>.update(value: T) = copy(value = value, error = null)

fun FormField<String>.validateRequired(@StringRes error: Int) =
    if (value.isBlank()) copy(error = error) else copy(error = null)

fun <T> FormField<T?>.validatePresent(@StringRes error: Int) =
    if (value == null) copy(error = error) else copy(error = null)

fun FormField<String>.parseLocalDate(@StringRes error: Int): Pair<FormField<String>, LocalDate?> {
    val date = runCatching { LocalDate.parse(value.trim()) }.getOrNull()
    return if (date == null) copy(error = error) to null else copy(error = null) to date
}
