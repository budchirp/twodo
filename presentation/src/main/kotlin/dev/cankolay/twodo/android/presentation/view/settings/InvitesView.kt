package dev.cankolay.twodo.android.presentation.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.cankolay.twodo.android.domain.model.api.invite.Invite
import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction
import dev.cankolay.twodo.android.domain.model.api.invite.InviteStatus
import dev.cankolay.twodo.android.domain.model.api.invite.InviteType
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.Card
import dev.cankolay.twodo.android.presentation.composable.CardStack
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.composable.layout.AppTopAppBar
import dev.cankolay.twodo.android.presentation.composition.LocalSnackbarHostState
import dev.cankolay.twodo.android.presentation.motion.TransitionType
import dev.cankolay.twodo.android.presentation.motion.navigationTransition
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.InviteEvent
import dev.cankolay.twodo.android.presentation.viewmodel.InviteViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed interface InviteRoute : NavKey {
    @Serializable
    data object Received : InviteRoute

    @Serializable
    data object Sent : InviteRoute
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitesView(inviteViewModel: InviteViewModel = hiltViewModel()) {
    val snackbarHostState = LocalSnackbarHostState.current
    val navBackStack = rememberNavBackStack(InviteRoute.Received)

    val invites by inviteViewModel.invites.collectAsState()
    LaunchedEffect(key1 = Unit) {
        inviteViewModel.onEvent(event = InviteEvent.FetchInvites)
    }

    val error by inviteViewModel.error.collectAsState()
    LaunchedEffect(key1 = error) {
        error?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(value = false) }
    val bottomSheetState = rememberModalBottomSheetState()

    var selectedTabIndex by remember { mutableIntStateOf(value = InviteType.Received.ordinal) }

    AppLayout(route = Route.Invites, topBar = { context ->
        AppTopAppBar(context = context, trailingContent = {
            IconButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(icon = Icons.Default.Add)
            }
        })
    }) {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            InviteType.entries.forEachIndexed { index, type ->
                Tab(selected = selectedTabIndex == index, onClick = {
                    selectedTabIndex = index

                    navBackStack.clear()
                    navBackStack.add(
                        element = when (index) {
                            0 -> InviteRoute.Received
                            else -> InviteRoute.Sent
                        }
                    )
                }, text = {
                    Text(
                        text = when (type) {
                            InviteType.Received -> stringResource(id = R.string.received)
                            InviteType.Sent -> stringResource(id = R.string.sent)
                        }
                    )
                })
            }
        }

        AnimatedVisibility(
            visible = invites != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val invites = invites!!

            NavDisplay(backStack = navBackStack, entryProvider = entryProvider {
                entry<InviteRoute.Received>(metadata = navigationTransition(type = TransitionType.FADE)) {
                    AppLazyColumn {
                        items(items = invites.filter { it.type == InviteType.Received }) { invite ->
                            InviteCard(
                                inviteViewModel = inviteViewModel,
                                invite = invite
                            )
                        }
                    }
                }

                entry<InviteRoute.Sent>(metadata = navigationTransition(type = TransitionType.FADE)) {
                    AppLazyColumn {
                        items(items = invites.filter { it.type == InviteType.Sent }) { invite ->
                            InviteCard(
                                inviteViewModel = inviteViewModel,
                                invite = invite
                            )
                        }
                    }
                }
            })
        }

        if (showBottomSheet) {
            ModalBottomSheet(sheetState = bottomSheetState, onDismissRequest = {
                showBottomSheet = false
            }) {
                var username by remember { mutableStateOf(value = "") }

                AppLazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    fill = false,
                ) {
                    error?.let { error ->
                        item {
                            Card {
                                Text(text = error)
                            }
                        }
                    }

                    item {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = username,
                            onValueChange = { username = it },
                            label = { Text(text = stringResource(id = R.string.username)) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = {
                                inviteViewModel.onEvent(event = InviteEvent.CreateInvite(username = username))

                                scope.launch {
                                    bottomSheetState.hide()
                                }.invokeOnCompletion {
                                    showBottomSheet = false
                                }
                            }) {
                                Text(text = stringResource(id = R.string.invite))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InviteCard(inviteViewModel: InviteViewModel, invite: Invite) {
    CardStack(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(all = 16.dp),
        items = listOf(
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = invite.user.name
                    )

                    when (invite.status) {
                        InviteStatus.ACCEPTED -> Text(
                            text = stringResource(id = R.string.accepted),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        InviteStatus.REJECTED -> Text(
                            text = stringResource(id = R.string.rejected),
                            color = MaterialTheme.colorScheme.error
                        )

                        InviteStatus.PENDING ->
                            if (invite.type == InviteType.Sent) {
                                Text(
                                    text = stringResource(id = R.string.pending),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                    }
                }
            }, if (invite.type == InviteType.Received && invite.status == InviteStatus.PENDING) {
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1f),
                            onClick = {
                                inviteViewModel.onEvent(
                                    event = InviteEvent.HandleInvite(
                                        action = InviteAction.Reject,
                                        id = invite.id
                                    )
                                )
                            }) {
                            Text(text = stringResource(id = R.string.reject))
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1f),
                            onClick = {
                                inviteViewModel.onEvent(
                                    event = InviteEvent.HandleInvite(
                                        action = InviteAction.Accept,
                                        id = invite.id
                                    )
                                )
                            }) {
                            Text(text = stringResource(id = R.string.accept))
                        }
                    }
                }
            } else {
                null
            }
        )
    )
}