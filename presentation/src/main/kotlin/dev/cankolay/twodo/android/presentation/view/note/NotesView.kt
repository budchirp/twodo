package dev.cankolay.twodo.android.presentation.view.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.CardStackList
import dev.cankolay.twodo.android.presentation.composable.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.PullToRefreshLazyColumn
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.composable.layout.AppTopAppBar
import dev.cankolay.twodo.android.presentation.composition.LocalNavBackStack
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.NoteViewModel
import dev.cankolay.twodo.android.presentation.viewmodel.TodoEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesView(noteViewModel: NoteViewModel = hiltViewModel()) {
    val navBackStack = LocalNavBackStack.current

    val notes by noteViewModel.notes.collectAsState()
    LaunchedEffect(key1 = Unit) {
        noteViewModel.onEvent(event = TodoEvent.FetchTodos)
    }

    val error by noteViewModel.error.collectAsState()

    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(value = false) }
    val bottomSheetState = rememberModalBottomSheetState()

    AppLayout(route = Route.Notes, topBar = { context ->
        AppTopAppBar(context = context, trailingContent = {
            IconButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(icon = Icons.Default.Add)
            }
        })
    }) {
        PullToRefreshLazyColumn(
            isLoading = notes == null && error == null,
            onRefresh = { noteViewModel.onEvent(event = TodoEvent.FetchTodos) },
        ) {
            when (true) {
                (error != null) -> {
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                        ) {
                            Text(
                                text = error!!,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                            )

                            Button(onClick = { noteViewModel.onEvent(event = TodoEvent.FetchTodos) }) {
                                Text(text = stringResource(id = R.string.try_again))
                            }
                        }
                    }
                }

                (notes != null) -> {
                    item {
                        CardStackList(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            items = notes!!.map { note ->
                                CardStackListItem(
                                    title = note.title,
                                    trailingContent = {
                                        if (note.completed) Icon(
                                            icon = Icons.Default.Check,
                                        )
                                    },
                                    onClick = {
                                        navBackStack.add(element = Route.Note(id = note.id))
                                    }
                                )
                            }
                        )
                    }
                }

                else -> {}
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = {
                showBottomSheet = false
            }) {
                var title by remember { mutableStateOf(value = "") }

                val todo by noteViewModel.todo.collectAsState()

                AppLazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    fill = false,
                ) {
                    item {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(text = stringResource(id = R.string.title)) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = {
                                noteViewModel.onEvent(event = TodoEvent.CreateTodo(title = title))

                                scope.launch {
                                    bottomSheetState.hide()
                                }.invokeOnCompletion {
                                    showBottomSheet = false
                                }

                                todo?.let { navBackStack.add(element = Route.Note(id = it.id)) }
                            }) {
                                Text(text = stringResource(id = R.string.create))
                            }
                        }
                    }
                }
            }
        }
    }
}