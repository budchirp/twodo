package dev.cankolay.twodo.android

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.cankolay.twodo.android.presentation.AppUI
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.SettingsViewModel

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private var appIntent by mutableStateOf<android.content.Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appIntent = intent

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            settingsViewModel.uiState.value.settingsState == null ||
                authViewModel.uiState.value.authState == null
        }

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView,
        ) { view: View, insets: WindowInsetsCompat ->
            view.setPadding(0, 0, 0, 0)
            insets
        }

        setContent {
            appIntent?.let { intent ->
                AppUI(
                    intent = intent,
                    settingsViewModel = settingsViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        appIntent = intent
    }
}
