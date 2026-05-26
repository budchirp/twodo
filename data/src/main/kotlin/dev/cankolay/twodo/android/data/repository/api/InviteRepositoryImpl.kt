package dev.cankolay.twodo.android.data.repository.api

import dev.cankolay.twodo.android.data.api.model.request.invite.CreateInviteRequestDto
import dev.cankolay.twodo.android.data.api.model.response.invite.toDomain
import dev.cankolay.twodo.android.data.api.service.InviteService
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction
import dev.cankolay.twodo.android.domain.repository.api.InviteRepository
import javax.inject.Inject

class InviteRepositoryImpl
@Inject
constructor(val inviteService: InviteService) : InviteRepository {
    override suspend fun create(
        username: String
    ) = when (val result =
        inviteService.create(dto = CreateInviteRequestDto(username = username))) {
        is ApiResult.Success -> ApiResult.Success(
            message = result.message,
            data = null,
            code = result.code
        )

        is ApiResult.Loading -> result

        is ApiResult.Error -> result
        is ApiResult.Fatal -> result
    }

    override suspend fun getAll() =
        when (val result = inviteService.getAll()) {
            is ApiResult.Success -> ApiResult.Success(
                message = result.message,
                data = result.data.map { it.toDomain() },
                code = result.code
            )

            is ApiResult.Loading -> result

            is ApiResult.Error -> result
            is ApiResult.Fatal -> result
        }

    override suspend fun handleInvite(
        action: InviteAction,
        id: String
    ) = inviteService.handleInvite(action = action, id = id)
}
