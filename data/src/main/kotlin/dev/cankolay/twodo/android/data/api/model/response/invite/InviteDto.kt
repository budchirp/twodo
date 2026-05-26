package dev.cankolay.twodo.android.data.api.model.response.invite

import dev.cankolay.twodo.android.data.api.model.response.user.UserSummaryDto
import dev.cankolay.twodo.android.data.api.model.response.user.toDomain
import dev.cankolay.twodo.android.domain.model.api.invite.Invite
import dev.cankolay.twodo.android.domain.model.api.invite.InviteStatus
import dev.cankolay.twodo.android.domain.model.api.invite.InviteType
import kotlinx.serialization.Serializable

@Serializable
data class InviteDto(
    val id: String,
    val user: UserSummaryDto,
    val type: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

fun InviteDto.toDomain() = Invite(
    id = id,
    user = user.toDomain(),
    type = InviteType.fromValue(value = type),
    status = InviteStatus.fromValue(value = status),
    createdAt = createdAt
)
