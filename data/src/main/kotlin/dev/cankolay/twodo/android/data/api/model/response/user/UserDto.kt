package dev.cankolay.twodo.android.data.api.model.response.user

import dev.cankolay.twodo.android.domain.model.api.user.Couple
import dev.cankolay.twodo.android.domain.model.api.user.User
import kotlinx.serialization.Serializable


@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val picture: String?,
    val gender: String?,
    val couple: UserCoupleDto?
)

@Serializable
data class UserSummaryDto(
    val id: String,
    val username: String,
    val name: String,
    val picture: String?,
    val gender: String?
)

@Serializable
data class UserCoupleDto(
    val id: String,
    val users: List<UserSummaryDto>,
    val createdAt: String,
    val updatedAt: String
)

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    picture = picture,
    couple = couple?.toDomain()
)

fun UserSummaryDto.toDomain() = User(
    id = id,
    name = name,
    picture = picture,
    couple = null
)

fun UserCoupleDto.toDomain() = Couple(
    users = users.map { it.toDomain() },
    createdAt = createdAt
)
