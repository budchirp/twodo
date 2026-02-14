package dev.cankolay.twodo.android.presentation.view.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.Avatar
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.UserEvent
import dev.cankolay.twodo.android.presentation.viewmodel.UserViewModel
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoupleView(userViewModel: UserViewModel = hiltViewModel()) {
    val user by userViewModel.user.collectAsState()
    LaunchedEffect(key1 = Unit) {
        userViewModel.onEvent(event = UserEvent.FetchUser)
    }

    AppLayout(route = Route.Couple) {
        AnimatedVisibility(
            visible = user != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val user = user!!
            val couple = user.couple!!

            AppLazyColumn {
                item {
                    val first = couple.users.first()
                    val second = couple.users.last()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(width = 128.dp)
                        ) {
                            Avatar(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .zIndex(zIndex = 1f),
                                picture = first.picture,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                size = 80.dp
                            )

                            Avatar(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd),
                                picture = second.picture,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                size = 80.dp
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${first.name} & ${second.name}",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraLarge
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 4.dp
                                    ),
                                    style = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium),
                                    text = stringResource(
                                        id = R.string.days_together,
                                        ChronoUnit.DAYS.between(
                                            OffsetDateTime.parse(couple.createdAt),
                                            OffsetDateTime.now()
                                        ).toString(),
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)
                }

                item {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        onClick = {}) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon = Icons.Default.HeartBroken)

                            Text(text = "Disconnect partner")
                        }
                    }
                }
            }
        }
    }
}