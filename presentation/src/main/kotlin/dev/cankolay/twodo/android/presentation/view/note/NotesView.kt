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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.CardStackList
import dev.cankolay.twodo.android.presentation.composable.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.Icon
import dev.cankolay.twodo.android.presentation.composable.PullToRefreshLazyColumn
import dev.cankolay.twodo.android.presentation.composable.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.layout.AppLazyColumn
import dev.cankolay.twodo.android.presentation.composable.layout.AppTopAppBar
import dev.cankolay.twodo.android.presentation.composition.LocalNavBackStack
import dev.cankolay.twodo.android.presentation.composition.LocalSnackbarHostState
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesView(noteViewModel: NoteViewModel = hiltViewModel()) {
    val navBackStack = LocalNavBackStack.current
    val snackbarHostState = LocalSnackbarHostState.current

    val state by noteViewModel.uiState.collectAsStateWithLifecycle()
    val notes = state.notes
    val error = state.error
    LaunchedEffect(key1 = Unit) {
        noteViewModel.fetchNotes()
    }

    LaunchedEffect(key1 = error) {
        error?.let { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }

    var showCreateNoteSheet by remember { mutableStateOf(value = false) }

    AppLayout(route = Route.Notes, topBar = { context ->
        AppTopAppBar(context = context, trailingContent = {
            IconButton(onClick = {
                showCreateNoteSheet = true
            }) {
                Icon(icon = Icons.Default.Add)
            }
        })
    }) {
        PullToRefreshLazyColumn(
            isLoading = state.isLoading,
            onRefresh = { noteViewModel.fetchNotes() },
        ) {
            when (true) {
                (error != null && notes == null) -> {
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
                        ) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                            )

                            Button(onClick = { noteViewModel.fetchNotes() }) {
                                Text(text = stringResource(id = R.string.try_again))
                            }
                        }
                    }
                }

                (notes != null) -> {
                    if (notes.isEmpty()) {
                        item {
                            CardStackList(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                items = listOf(
                                    CardStackListItem(
                                        title = stringResource(id = R.string.notes_empty_title),
                                        description = stringResource(id = R.string.notes_empty_desc),
                                        leadingContent = {
                                            Icon(
                                                icon = Icons.Default.Edit,
                                            )
                                        }
                                    )
                                ))
                        }
                    } else {
                        item {
                            CardStackList(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                items = notes.map { note ->
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
                }

                else -> {}
            }
        }

        if (showCreateNoteSheet) {
            CreateNoteSheet(
                isLoading = state.isLoading,
                onDismiss = {
                    showCreateNoteSheet = false
                },
                onCreate = { title ->
                    when (val result = noteViewModel.createNote(title = title)) {
                        is ApiResult.Success -> {
                            navBackStack.add(element = Route.Note(id = result.data.id))
                            true
                        }

                        else -> false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteSheet(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onCreate: suspend (title: String) -> Boolean
) {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        var title by remember { mutableStateOf(value = "") }

        AppLazyColumn(
            contentPadding = PaddingValues(all = 16.dp), fill = false
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.create_note),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

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
                    Button(
                        enabled = title.isNotBlank() && !isLoading,
                        onClick = {
                            scope.launch {
                                if (onCreate(title)) {
                                    sheetState.hide()
                                    onDismiss()
                                }
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.create))
                    }
                }
            }
        }
    }
}
