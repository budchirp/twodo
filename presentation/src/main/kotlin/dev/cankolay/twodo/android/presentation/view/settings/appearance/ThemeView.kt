package dev.cankolay.twodo.android.presentation.view.settings.appearance

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.cankolay.twodo.android.domain.model.application.Theme
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.CardStackList
import dev.cankolay.twodo.android.presentation.composable.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.application.SettingsEvent
import dev.cankolay.twodo.android.presentation.viewmodel.application.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeView(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val state by settingsViewModel.state.collectAsState()

    AppLayout(route = Route.Theme) {
        state?.let { state ->
            AppLazyColumn {
                item {
                    CardStackList(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp),
                        items = Theme.entries.map { theme ->
                            val onClick = {
                                settingsViewModel.onEvent(
                                    event = SettingsEvent.UpdateSettings(
                                        settingsState = state.copy(
                                            theme = theme
                                        )
                                    )
                                )
                            }

                            CardStackListItem(
                                title =
                                    stringResource(
                                        id = when (theme) {
                                            Theme.SYSTEM -> R.string.system
                                            Theme.DARK -> R.string.dark
                                            Theme.LIGHT -> R.string.light
                                        }
                                    ),
                                onClick = onClick,
                                leadingContent = {
                                    RadioButton(
                                        selected = theme == state.theme,
                                        onClick = onClick,
                                    )
                                },
                            )
                        }
                    )
                }

                item {
                    val onClick = { isAmoled: Boolean ->
                        settingsViewModel.onEvent(
                            event = SettingsEvent.UpdateSettings(
                                settingsState = state.copy(
                                    isAmoled = isAmoled
                                )
                            )
                        )
                    }

                    CardStackList(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp),
                        items = listOf(
                            CardStackListItem(
                                title = stringResource(id = R.string.amoled),
                                enabled = state.theme != Theme.LIGHT,
                                onClick = { onClick(!state.isAmoled) },
                                trailingContent = {
                                    Switch(
                                        checked = state.theme != Theme.LIGHT && state.isAmoled,
                                        enabled = state.theme != Theme.LIGHT,
                                        onCheckedChange = onClick,
                                    )
                                },
                            )
                        )
                    )
                }
            }
        }
    }
}