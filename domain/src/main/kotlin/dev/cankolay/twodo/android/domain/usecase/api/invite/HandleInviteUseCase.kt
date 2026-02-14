package dev.cankolay.twodo.android.domain.usecase.api.invite

import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction
import dev.cankolay.twodo.android.domain.repository.api.InviteRepository
import javax.inject.Inject

class HandleInviteUseCase
@Inject
constructor(private val inviteRepository: InviteRepository) {
    suspend operator fun invoke(id: String, action: InviteAction) =
        inviteRepository.handleInvite(action = action, id = id)
}