package dev.cankolay.twodo.android.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.cankolay.twodo.android.presentation.composable.layout.AppMainLayout
import dev.cankolay.twodo.android.presentation.composition.ProvideNavBackStack
import dev.cankolay.twodo.android.presentation.composition.ProvideSnackbarHostState
import dev.cankolay.twodo.android.presentation.navigation.AppNavigation
import dev.cankolay.twodo.android.presentation.theme.AppTheme
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.SettingsViewModel

@Composable
fun AppUI(
    intent: Intent,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()

    val uri = intent.data
    LaunchedEffect(key1 = uri) {
        uri?.let { uri ->
            authViewModel.authenticate(uri = uri)
        }
    }
    
    if (settingsState != null && authState != null) {
        AppTheme(settingsState = settingsState!!) {
            ProvideNavBackStack(authState = authState!!) {
                ProvideSnackbarHostState {
                    AppMainLayout {
                        AppNavigation()
                    }
                }
            }
        }
    }
}