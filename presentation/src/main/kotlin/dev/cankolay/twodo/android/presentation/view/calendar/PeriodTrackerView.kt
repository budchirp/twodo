package dev.cankolay.twodo.android.presentation.view.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.cankolay.twodo.android.domain.model.api.calendar.PeriodPrediction
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.ErrorCard
import dev.cankolay.twodo.android.presentation.composable.app.CardStackList
import dev.cankolay.twodo.android.presentation.composable.app.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.app.Icon
import dev.cankolay.twodo.android.presentation.composable.app.PullToRefreshLazyColumn
import dev.cankolay.twodo.android.presentation.composable.app.layout.AppLayout
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodTrackerView(
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        calendarViewModel.fetchPeriodTracker()
    }

    AppLayout(route = Route.PeriodTracker) {
        PullToRefreshLazyColumn(
            isLoading = uiState.isPeriodTrackerLoading,
            onRefresh = { calendarViewModel.fetchPeriodTracker() }
        ) {
            if (uiState.periodTrackerError != null &&
                uiState.periodTrackerSummary == null &&
                uiState.periodPrediction == null
            ) {
                item {
                    ErrorCard(
                        title = stringResource(id = R.string.period_tracker_error),
                        error = uiState.periodTrackerError,
                        onRefresh = { calendarViewModel.fetchPeriodTracker() }
                    )
                }
            } else {
                item {
                    PredictionCard(prediction = uiState.periodPrediction)
                }

                item {
                    TipsCard()
                }
            }
        }
    }
}

@Composable
private fun PredictionCard(prediction: PeriodPrediction?) {
    CardStackList(
        modifier = Modifier.padding(horizontal = 16.dp),
        items = listOf(
            CardStackListItem(
                title = stringResource(id = R.string.expected_period),
                description = prediction?.nextPeriodWindow?.let { formatDateRange(range = it) }
                    ?: stringResource(id = R.string.prediction_unavailable_desc),
                leadingContent = { Icon(icon = Icons.Default.CalendarMonth) }
            ),
            CardStackListItem(
                title = stringResource(id = R.string.ovulation_window),
                description = prediction?.ovulationWindow?.let { formatDateRange(range = it) }
                    ?: stringResource(id = R.string.prediction_unavailable_desc),
                leadingContent = { Icon(icon = Icons.Default.Info) }
            )
        )
    )
}

@Composable
private fun TipsCard() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.period_tips_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        CardStackList(
            items = listOf(
                CardStackListItem(
                    title = stringResource(id = R.string.period_tip_log_consistently),
                    description = stringResource(id = R.string.period_tip_log_consistently_desc)
                ),
                CardStackListItem(
                    title = stringResource(id = R.string.period_tip_symptoms),
                    description = stringResource(id = R.string.period_tip_symptoms_desc)
                ),
                CardStackListItem(
                    title = stringResource(id = R.string.period_tip_not_medical),
                    description = stringResource(id = R.string.period_tip_not_medical_desc)
                )
            )
        )
    }
}