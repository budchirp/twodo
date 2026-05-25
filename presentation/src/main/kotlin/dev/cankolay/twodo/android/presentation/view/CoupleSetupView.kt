package dev.cankolay.twodo.android.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.domain.model.api.invite.InviteAction
import dev.cankolay.twodo.android.domain.model.api.invite.InviteStatus
import dev.cankolay.twodo.android.domain.model.api.invite.InviteType
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.Card
import dev.cankolay.twodo.android.presentation.composable.CardStackList
import dev.cankolay.twodo.android.presentation.composable.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.PullToRefreshLazyColumn
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.composition.LocalSnackbarHostState
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.InviteViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.UserViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.application.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CoupleSetupView(
    userViewModel: UserViewModel = hiltViewModel(),
    inviteViewModel: InviteViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val snackbarHostState = LocalSnackbarHostState.current

    val scope = rememberCoroutineScope()

    val userState by userViewModel.uiState.collectAsStateWithLifecycle()
    val user = userState.user
    LaunchedEffect(key1 = Unit) {
        userViewModel.fetchUser()
    }

    val isLoading = userState.isLoading
    val error = userState.error

    val inviteState by inviteViewModel.uiState.collectAsStateWithLifecycle()
    val invites = inviteState.invites
    LaunchedEffect(key1 = Unit) {
        inviteViewModel.fetchInvites()
    }

    // TODO: SHOW THESE IN SHEET
    val isInviteLoading = inviteState.isLoading
    val inviteError = inviteState.error
    LaunchedEffect(key1 = inviteError) {
        inviteError?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    var showInviteSheet by remember { mutableStateOf(value = false) }

    AppLayout(
        route = Route.CoupleSetup,
        topBar = {}
    ) {
        PullToRefreshLazyColumn(
            isLoading = isLoading || isInviteLoading,
            onRefresh = {
                userViewModel.fetchUser()
                inviteViewModel.fetchInvites()
            },
            contentPadding = PaddingValues(top = 64.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 32.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 32.dp)
                ) {
                    AsyncImage(
                        model = context.packageManager.getApplicationIcon(context.packageName),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(size = 64.dp)
                                .clip(shape = CircleShape),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                        Text(
                            text = stringResource(id = R.string.couple_setup_title),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = stringResource(id = R.string.couple_setup_desc),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            if (user == null || error != null) {
                item {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
                            Icon(
                                icon = Icons.Default.ErrorOutline,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
                                Text(
                                    text = stringResource(id = R.string.couple_setup_error_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )

                                error?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            Button(onClick = {
                                userViewModel.fetchUser()
                            }) {
                                Text(text = stringResource(id = R.string.try_again))
                            }
                        }
                    }
                }
            } else {
                val pendingSent =
                    invites.orEmpty()
                        .filter { invite ->
                            invite.type == InviteType.Sent && invite.status == InviteStatus.PENDING
                        }

                val pendingReceived =
                    invites.orEmpty()
                        .filter { invite ->
                            invite.type == InviteType.Received && invite.status == InviteStatus.PENDING
                        }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.received_invites))

                            CardStackList(
                                items = if (pendingReceived.isEmpty()) listOf(
                                    CardStackListItem(
                                        title = stringResource(id = R.string.no_received_invites),
                                        description = stringResource(id = R.string.no_received_invites_desc),
                                        leadingContent = {
                                            Icon(icon = Icons.Default.Mail)
                                        },
                                    )
                                ) else pendingReceived.map { invite ->
                                    CardStackListItem(
                                        title = invite.user.name,
                                        contentSize = null,
                                        contentPadding = PaddingValues(
                                            horizontal = 12.dp,
                                            vertical = 12.dp
                                        ),
                                        trailingContent = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                                            ) {
                                                IconButton(onClick = {
                                                    scope.launch {
                                                        inviteViewModel.handleInvite(
                                                            action = InviteAction.Reject,
                                                            id = invite.id
                                                        )
                                                    }
                                                }) {
                                                    Icon(icon = Icons.Default.Close)
                                                }

                                                IconButton(
                                                    onClick = {
                                                        scope.launch {
                                                            val result =
                                                                inviteViewModel.handleInvite(
                                                                    action = InviteAction.Accept,
                                                                    id = invite.id
                                                                )

                                                            if (result is ApiResult.Success) {
                                                                userViewModel.fetchUser()
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(icon = Icons.Default.Check)
                                                }
                                            }
                                        }
                                    )
                                }
                            )
                        }

                        CardStackList(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            items = listOf(
                                if (pendingSent.isNotEmpty()) CardStackListItem(
                                    title = stringResource(
                                        id = R.string.waiting_for,
                                        pendingSent.lastOrNull()?.user?.name ?: ""
                                    ),
                                    description = stringResource(id = R.string.waiting_for_partner_desc),
                                    leadingContent = {
                                        Icon(icon = Icons.Default.HourglassEmpty)
                                    },
                                ) else
                                    CardStackListItem(
                                        title = stringResource(id = R.string.create_couple),
                                        description = stringResource(id = R.string.create_couple_desc),
                                        leadingContent = {
                                            Icon(icon = Icons.Default.PersonAdd)
                                        },
                                        onClick = {
                                            showInviteSheet = true
                                        }
                                    )
                            )
                        )
                    }
                }

                item {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = {
                            authViewModel.logout()
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon = Icons.Default.Logout)

                            Text(text = stringResource(id = R.string.sign_out))
                        }
                    }
                }
            }
        }

        if (showInviteSheet) {
            InvitePartnerSheet(
                isLoading = isInviteLoading,
                onDismiss = {
                    showInviteSheet = false
                },
                onInvite = { username ->
                    inviteViewModel.createInvite(username = username) is ApiResult.Success
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitePartnerSheet(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onInvite: suspend (String) -> Boolean
) {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        var username by remember { mutableStateOf(value = "") }

        AppLazyColumn(
            contentPadding = PaddingValues(all = 16.dp), fill = false
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.invite_partner),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            item {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = username,
                    onValueChange = { username = it.trim() },
                    singleLine = true,
                    label = { Text(text = stringResource(id = R.string.username)) },
                    supportingText = {
                        AnimatedVisibility(
                            visible = username.isEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(text = stringResource(id = R.string.username_required))
                        }
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        enabled = username.isNotEmpty() && !isLoading,
                        onClick = {
                            scope.launch {
                                if (onInvite(username)) {
                                    sheetState.hide()
                                    onDismiss()
                                }
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.invite))
                    }
                }
            }
        }
    }
}
