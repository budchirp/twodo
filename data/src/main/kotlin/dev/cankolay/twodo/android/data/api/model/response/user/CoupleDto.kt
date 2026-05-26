package dev.cankolay.twodo.android.data.api.model.response.user

import dev.cankolay.twodo.android.domain.model.api.user.Couple
import kotlinx.serialization.Serializable


@Serializable
data class CoupleDto(
    val id: String,
    val users: List<UserSummaryDto>,
    val createdAt: String,
    val updatedAt: String
)

fun CoupleDto.toDomain() = Couple(
    users = users.map { it.toDomain() },
    createdAt = createdAt
)
