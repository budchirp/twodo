package dev.cankolay.twodo.android.domain.model.api.user

enum class Gender(val value: String) {
    FEMALE(value = "female"),
    MALE(value = "male");

    companion object {
        fun fromValue(value: String) =
            entries.firstOrNull { it.value == value }
                ?: error("Unknown Gender: $value")
    }
}
