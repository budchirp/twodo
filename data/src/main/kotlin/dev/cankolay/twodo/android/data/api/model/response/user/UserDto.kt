package dev.cankolay.twodo.android.data.api.model.response.user

import dev.cankolay.twodo.android.domain.model.api.user.User
import kotlinx.serialization.Serializable


@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val picture: String?,
    val couple: CoupleDto?
)

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    picture = picture,
    couple = couple?.toDomain()
)