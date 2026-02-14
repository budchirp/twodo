package dev.cankolay.twodo.android.presentation.composition

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarHostState =
    staticCompositionLocalOf<SnackbarHostState> { error("Not provided") }

@Composable
fun ProvideSnackbarHostState(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalSnackbarHostState provides remember { SnackbarHostState() },
    ) {
        content()
    }
}