package dev.cankolay.twodo.android.domain.model.api.invite

import kotlinx.serialization.Serializable

@Serializable
enum class InviteType(val value: String) {
    Received(value = "received"),
    Sent(value = "sent");

    companion object {
        fun fromValue(value: String) =
            entries.firstOrNull { it.value == value }
                ?: error("Unknown InviteType: $value")
    }
}