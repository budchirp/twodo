package dev.cankolay.twodo.android.domain.repository.api

import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.invite.Invite
import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction

interface InviteRepository {
    suspend fun create(username: String): ApiResult<Nothing?>

    suspend fun getAll(): ApiResult<List<Invite>>

    suspend fun handleInvite(action: InviteAction, id: String): ApiResult<Nothing?>
}