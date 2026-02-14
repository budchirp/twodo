package dev.cankolay.twodo.android.presentation.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.cankolay.twodo.android.presentation.composable.Avatar
import dev.cankolay.twodo.android.presentation.composable.CardStackList
import dev.cankolay.twodo.android.presentation.composable.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.composition.LocalNavBackStack
import dev.cankolay.twodo.android.presentation.composition.LocalSnackbarHostState
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.navigation.route.getDetails
import dev.cankolay.twodo.android.presentation.viewmodel.UserEvent
import dev.cankolay.twodo.android.presentation.viewmodel.UserViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthEvent
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    userViewModel: UserViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val navBackStack = LocalNavBackStack.current

    val user by userViewModel.user.collectAsState()
    LaunchedEffect(key1 = Unit) {
        userViewModel.onEvent(event = UserEvent.FetchUser)
    }

    val error by userViewModel.error.collectAsState()
    LaunchedEffect(key1 = error) {
        error?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    AppLayout(route = Route.Settings) {
        AnimatedVisibility(
            visible = user != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val user = user!!

            AppLazyColumn {
                item {
                    CardStackList(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        items = listOf(
                            CardStackListItem(
                                title = user.name,
                                leadingContent = {
                                    Avatar(picture = user.picture)
                                },
                                trailingContent = {
                                    Button(onClick = {
                                        authViewModel.onEvent(event = AuthEvent.Logout)

                                        navBackStack.clear()
                                        navBackStack.add(element = Route.Welcome)
                                    }) {
                                        Text(text = "Logout")
                                    }
                                },
                                contentPadding = PaddingValues(vertical = 16.dp),
                                contentSize = null
                            ),
                        )
                    )
                }

                item {
                    val routes =
                        if (user.couple == null) listOf(Route.Invites) else listOf(Route.Couple)

                    CardStackList(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        items = routes.map { route ->
                            val details = route.getDetails()

                            CardStackListItem(
                                title = details.title,
                                leadingContent = {
                                    Icon(
                                        icon = details.icon.default,
                                    )
                                },
                                onClick = {
                                    navBackStack.add(element = route)
                                }
                            )
                        }
                    )
                }

                item {
                    val routes =
                        listOf(Route.Appearance, Route.Languages, Route.About)

                    CardStackList(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        items = routes.map { route ->
                            val details = route.getDetails()

                            CardStackListItem(
                                title = details.title,
                                description = details.description,
                                leadingContent = {
                                    Icon(
                                        icon = details.icon.default,
                                    )
                                },
                                onClick = {
                                    navBackStack.add(element = route)
                                },
                            )
                        }
                    )
                }
            }
        }
    }
}