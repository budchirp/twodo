package dev.cankolay.twodo.android.presentation.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import dev.cankolay.twodo.android.domain.model.application.AuthState
import dev.cankolay.twodo.android.presentation.navigation.route.Route

val LocalNavBackStack =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("Not provided") }

@Composable
fun ProvideNavBackStack(authState: AuthState, content: @Composable () -> Unit) {
    val navBackStack = rememberNavBackStack(
        if (authState.token.isNotEmpty()) {
            Route.Notes
        } else {
            Route.Welcome
        }
    )

    LaunchedEffect(key1 = authState.token) {
        navBackStack.clear()
        navBackStack.add(element = if (authState.token.isNotEmpty()) Route.Notes else Route.Welcome)
    }

    CompositionLocalProvider(
        value = LocalNavBackStack provides navBackStack,
    ) {
        content()
    }
}