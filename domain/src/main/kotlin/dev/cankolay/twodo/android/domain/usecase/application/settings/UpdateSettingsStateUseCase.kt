package dev.cankolay.twodo.android.domain.usecase.application.settings

import dev.cankolay.twodo.android.domain.model.application.SettingsState
import dev.cankolay.twodo.android.domain.repository.application.SettingsStateRepository
import javax.inject.Inject

class UpdateSettingsStateUseCase @Inject constructor(
    private val settingsStateRepository: SettingsStateRepository
) {
    suspend operator fun invoke(state: SettingsState) = settingsStateRepository.update(state)
}
