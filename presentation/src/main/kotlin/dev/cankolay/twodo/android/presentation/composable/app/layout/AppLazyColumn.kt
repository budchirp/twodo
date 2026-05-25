package dev.cankolay.twodo.android.presentation.composable.app.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppLazyColumn(
    modifier: Modifier = Modifier,
    fill: Boolean = true,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(space = 16.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = if (fill) modifier.fillMaxSize() else modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}