package dev.cankolay.twodo.android.domain.model.api.invite

import kotlinx.serialization.Serializable

@Serializable
enum class InviteStatus(val value: String) {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    companion object {
        fun fromValue(value: String) =
            entries.firstOrNull { it.value == value }
                ?: error("Unknown InviteStatus: $value")
    }
}