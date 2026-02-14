package dev.cankolay.twodo.android.presentation.view

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import dev.cankolay.twodo.android.domain.model.api.AuthApiConstants
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.CardStackList
import dev.cankolay.twodo.android.presentation.composable.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.navigation.route.Route

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeView() {
    val context = LocalContext.current

    AppLayout(
        route = Route.Welcome,
        topBar = {},
        bottomBar = {
            NavigationBar {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), onClick = {
                        val intent = CustomTabsIntent.Builder().setShowTitle(true).build()

                        intent.launchUrl(
                            context,
                            AuthApiConstants.AUTH_URL.toUri()
                        )
                    }) {
                    Text(text = stringResource(id = R.string.continue_with_trash))
                }
            }
        }
    ) {
        AppLazyColumn(
            contentPadding = PaddingValues(top = 64.dp),
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
                            text = stringResource(id = R.string.welcome),
                            style = MaterialTheme.typography.headlineLargeEmphasized.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = stringResource(id = R.string.welcome_desc),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            item {
                data class FeatureItem(
                    val title: String,
                    val description: String? = null,
                    val icon: ImageVector
                )

                val features = listOf(
                    FeatureItem(
                        title = stringResource(id = R.string.feature_1),
                        description = stringResource(id = R.string.feature_1_desc),
                        icon = Icons.Default.Check
                    ),
                    FeatureItem(
                        title = stringResource(id = R.string.feature_2),
                        description = stringResource(id = R.string.feature_2_desc),
                        icon = ImageVector.vectorResource(id = R.drawable.wand_stars_24px)
                    ),
                    FeatureItem(
                        title = stringResource(id = R.string.feature_3),
                        description = stringResource(id = R.string.feature_3_desc),
                        icon = ImageVector.vectorResource(id = R.drawable.partner_heart_24px)
                    )
                )

                CardStackList(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    items = features.map { feature ->
                        CardStackListItem(
                            title = feature.title,
                            description = feature.description,
                            leadingContent = {
                                Icon(icon = feature.icon)
                            }
                        )
                    })
            }
        }
    }
}