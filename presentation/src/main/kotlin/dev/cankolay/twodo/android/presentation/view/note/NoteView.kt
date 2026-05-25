package dev.cankolay.twodo.android.presentation.view.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import dev.cankolay.twodo.android.domain.model.api.ApiResult
import dev.cankolay.twodo.android.presentation.R
import dev.cankolay.twodo.android.presentation.composable.app.CardStackList
import dev.cankolay.twodo.android.presentation.composable.app.CardStackListItem
import dev.cankolay.twodo.android.presentation.composable.app.Icon
import dev.cankolay.twodo.android.presentation.composable.app.layout.AppBottomSheet
import dev.cankolay.twodo.android.presentation.composable.app.layout.AppLayout
import dev.cankolay.twodo.android.presentation.composable.app.layout.AppTopAppBar
import dev.cankolay.twodo.android.presentation.composable.app.layout.AppTopAppBarType
import dev.cankolay.twodo.android.presentation.composition.LocalNavBackStack
import dev.cankolay.twodo.android.presentation.composition.LocalSnackbarHostState
import dev.cankolay.twodo.android.presentation.navigation.route.Route
import dev.cankolay.twodo.android.presentation.viewmodel.NoteViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun NoteView(id: String, noteViewModel: NoteViewModel = hiltViewModel()) {
    val snackbarHostState = LocalSnackbarHostState.current

    val navBackStack = LocalNavBackStack.current

    val uiState by noteViewModel.uiState.collectAsStateWithLifecycle()
    val note = uiState.note
    LaunchedEffect(key1 = id) {
        noteViewModel.fetchNote(id = id)
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }

    val state = rememberRichTextState()
    state.config.linkColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(key1 = note) {
        state.setMarkdown(markdown = note?.content ?: "")
        state.selection = TextRange(index = 0)
    }

    note?.let { todo ->
        var todo by remember(key1 = todo.id) { mutableStateOf(value = todo.copy()) }

        LaunchedEffect(key1 = todo.id) {
            snapshotFlow {
                listOf(todo, state.annotatedString)
            }
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest {
                    todo = todo.copy(
                        updatedAt = OffsetDateTime.now().toString()
                    )

                    noteViewModel.updateNote(
                        id = id,
                        note = todo.copy(
                            content = state.toMarkdown(),
                        )
                    )
                }
        }

        val scope = rememberCoroutineScope()

        var showBottomSheet by remember { mutableStateOf(value = false) }
        var showDeleteNoteSheet by remember { mutableStateOf(value = false) }
        val bottomSheetState = rememberModalBottomSheetState()

        AppLayout(
            route = Route.Note(id = id),
            context = { context ->
                context.copy(
                    scrollBehavior = null
                )
            },
            topBar = { context ->
                AppTopAppBar(
                    type = AppTopAppBarType.Default,
                    context = context,
                    title = {
                        BasicTextField(
                            value = todo.title,
                            onValueChange = { title -> todo = todo.copy(title = title) },
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(value = MaterialTheme.colorScheme.primary),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                BasicRichTextEditor(
                    state = state,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(weight = 1f)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 64.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "hi")
                        }

                        FilledIconButton(onClick = {
                            showBottomSheet = true
                        }) {
                            Icon(icon = Icons.Default.MoreVert)
                        }
                    }
                }
            }

            if (showBottomSheet) {
                val sheetTitle =
                    if (todo.title.isBlank()) stringResource(id = R.string.notes) else todo.title

                AppBottomSheet(
                    title = sheetTitle,
                    onDismiss = {
                        showBottomSheet = false
                    },
                    sheetState = bottomSheetState
                ) {
                    item {
                        val onClick = { completed: Boolean ->
                            todo = todo.copy(completed = completed)
                        }

                        CardStackList(
                            items = listOf(
                                CardStackListItem(
                                    title = stringResource(id = R.string.completed),
                                    onClick = {
                                        onClick(!todo.completed)
                                    },
                                    trailingContent = {
                                        Switch(
                                            checked = todo.completed,
                                            onCheckedChange = onClick
                                        )
                                    }
                                )
                            )
                        )
                    }

                    item {
                        CardStackList(
                            items = listOf(
                                CardStackListItem(
                                    title = stringResource(
                                        id = R.string.edited_at,
                                        OffsetDateTime.parse(todo.updatedAt)
                                            .format(
                                                DateTimeFormatter.ofPattern("dd EEE yyyy HH:mm")
                                            )
                                    ),
                                    leadingContent = {
                                        Icon(icon = Icons.Default.Update)
                                    }
                                ),
                                CardStackListItem(
                                    title = stringResource(id = R.string.delete),
                                    onClick = {
                                        scope.launch {
                                            bottomSheetState.hide()
                                        }.invokeOnCompletion {
                                            showBottomSheet = false
                                            showDeleteNoteSheet = true
                                        }
                                    },
                                    leadingContent = {
                                        Icon(icon = Icons.Default.Delete)
                                    }
                                )
                            )
                        )
                    }
                }
            }

            if (showDeleteNoteSheet) {
                DeleteNoteSheet(
                    isLoading = uiState.isLoading,
                    onDismiss = {
                        showDeleteNoteSheet = false
                    },
                    onDelete = {
                        when (noteViewModel.deleteNote(id = id)) {
                            is ApiResult.Success -> {
                                navBackStack.add(element = Route.Notes)
                                while (navBackStack.size > 1) {
                                    navBackStack.removeAt(0)
                                }
                                true
                            }

                            else -> false
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteNoteSheet(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onDelete: suspend () -> Boolean
) {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()

    AppBottomSheet(
        title = stringResource(id = R.string.delete_note),
        description = stringResource(id = R.string.delete_note_desc),
        onDismiss = onDismiss,
        sheetState = sheetState,
        actions = {
            TextButton(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismiss()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }

            Button(
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                onClick = {
                    scope.launch {
                        if (onDelete()) {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    )
}
