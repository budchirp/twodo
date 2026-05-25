package dev.cankolay.twodo.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.cankolay.twodo.android.presentation.composition.LocalNavBackStack
import dev.cankolay.twodo.android.presentation.motion.TransitionType
import dev.cankolay.twodo.android.presentation.motion.navigationTransition
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.view.CoupleSetupView
import dev.cankolay.twodo.android.presentation.view.WelcomeView
import dev.cankolay.twodo.android.presentation.view.note.NoteView
import dev.cankolay.twodo.android.presentation.view.note.NotesView
import dev.cankolay.twodo.android.presentation.view.settings.AboutView
import dev.cankolay.twodo.android.presentation.view.settings.CoupleView
import dev.cankolay.twodo.android.presentation.view.settings.LanguagesView
import dev.cankolay.twodo.android.presentation.view.settings.SettingsView
import dev.cankolay.twodo.android.presentation.view.settings.appearance.AppearanceView
import dev.cankolay.twodo.android.presentation.view.settings.appearance.MaterialYouView
import dev.cankolay.twodo.android.presentation.view.settings.appearance.ThemeView

@Composable
fun AppNavigation() {
    val navBackStack = LocalNavBackStack.current

    NavDisplay(
        backStack = navBackStack,
        onBack = {
            navBackStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Welcome>(metadata = navigationTransition()) {
                WelcomeView()
            }

            entry<Route.CoupleSetup>(metadata = navigationTransition()) {
                CoupleSetupView()
            }

            entry<Route.Notes>(metadata = navigationTransition(type = TransitionType.FADE)) {
                NotesView()
            }

            entry<Route.Note> {
                NoteView(id = it.id)
            }



            entry<Route.Settings>(metadata = navigationTransition(type = TransitionType.FADE)) {
                SettingsView()
            }

            entry<Route.Couple>(metadata = navigationTransition()) {
                CoupleView()
            }

            entry<Route.Languages>(metadata = navigationTransition()) {
                LanguagesView()
            }

            entry<Route.Appearance>(metadata = navigationTransition()) {
                AppearanceView()
            }

            entry<Route.Theme>(metadata = navigationTransition()) {
                ThemeView()
            }

            entry<Route.MaterialYou>(metadata = navigationTransition()) {
                MaterialYouView()
            }

            entry<Route.About>(metadata = navigationTransition()) {
                AboutView()
            }
        }
    )
}
