package dev.cankolay.twodo.android.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.cankolay.twodo.android.presentation.composable.app.layout.AppMainLayout
import dev.cankolay.twodo.android.presentation.composition.ProvideNavBackStack
import dev.cankolay.twodo.android.presentation.composition.ProvideSnackbarHostState
import dev.cankolay.twodo.android.presentation.navigation.AppNavigation
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.theme.AppTheme
import dev.cankolay.twodo.android.presentation.viewmodel.UserViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.SettingsViewModel

@Composable
fun AppUI(
    intent: Intent,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    onAuthIntentConsumed: () -> Unit = {}
) {
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val userUiState by userViewModel.uiState.collectAsStateWithLifecycle()

    val settingsState = settingsUiState.settingsState
    val authState = authUiState.authState
    val user = userUiState.user

    val uri = intent.data
    LaunchedEffect(key1 = uri) {
        uri?.let { uri ->
            authViewModel.authenticate(uri = uri)
            onAuthIntentConsumed()
        }
    }

    LaunchedEffect(
        authState?.token,
        authUiState.isAuthenticating,
        user?.id,
        user?.profileCompleted,
        userUiState.error
    ) {
        val token = authState?.token
        when {
            token == null -> Unit
            token.isEmpty() -> userViewModel.clearUser()
            !authUiState.isAuthenticating && user == null && !userUiState.isLoading && !userUiState.isInitialized ->
                userViewModel.fetchUser()
        }
    }

    if (settingsState != null && authState != null) {
        val startRoute =
            when {
                authState.token.isEmpty() -> Route.Welcome
                authUiState.isAuthenticating -> null
                userUiState.errorCode == "error-profile-required" -> Route.ProfileSetup
                user == null && userUiState.error == null -> null
                user == null && userUiState.error != null -> Route.StartupError
                user?.profileCompleted == false -> Route.ProfileSetup
                user?.couple != null -> Route.Notes
                else -> Route.CoupleSetup
            }

        AppTheme(settingsState = settingsState) {
            startRoute?.let { route ->
                ProvideNavBackStack(startRoute = route) {
                    ProvideSnackbarHostState {
                        AppMainLayout {
                            AppNavigation(
                                authViewModel = authViewModel,
                                userViewModel = userViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
