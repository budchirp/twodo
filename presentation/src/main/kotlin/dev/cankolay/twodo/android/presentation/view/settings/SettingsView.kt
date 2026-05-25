package dev.cankolay.twodo.android.presentation.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.Card
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
import dev.cankolay.twodo.android.presentation.viewmodel.UserViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    userViewModel: UserViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val navBackStack = LocalNavBackStack.current

    val userState by userViewModel.uiState.collectAsStateWithLifecycle()
    val user = userState.user
    LaunchedEffect(key1 = Unit) {
        userViewModel.fetchUser()
    }

    val isLoading = userState.isLoading
    val error = userState.error
    LaunchedEffect(key1 = error) {
        error?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    AppLayout(route = Route.Settings) {
        if (user == null) {
            AppLazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading && error == null) {
                    item {
                        CircularProgressIndicator()
                    }
                } else {
                    error?.let { message ->
                        item {
                            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )

                                    Button(onClick = {
                                        userViewModel.fetchUser()
                                    }) {
                                        Text(text = stringResource(id = R.string.try_again))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

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
                                        authViewModel.logout()

                                        navBackStack.clear()
                                        navBackStack.add(element = Route.Welcome)
                                    }) {
                                        Text(text = stringResource(id = R.string.sign_out))
                                    }
                                },
                                contentPadding = PaddingValues(vertical = 16.dp),
                                contentSize = null
                            ),
                        )
                    )
                }

                item {
                    val routes = listOf(Route.Couple)

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
