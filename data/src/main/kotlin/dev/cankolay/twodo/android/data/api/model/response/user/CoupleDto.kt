package dev.cankolay.twodo.android.data.api.model.response.user

import dev.cankolay.twodo.android.domain.model.api.user.Couple
import dev.cankolay.twodo.android.domain.model.api.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CoupleDto(
    val users: List<UserDto>,
    @SerialName(value = "created_at")
    val createdAt: String
)

fun CoupleDto.toDomain() = Couple(
    users = users.map {
        User(
            id = it.id,
            name = it.name,
            picture = it.picture,
            couple = null
        )
    },
    createdAt = createdAt
)