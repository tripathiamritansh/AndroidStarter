package com.sample.starter.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.feed.ui.theme.StarterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = remember { LazyListState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(uiState.value.messages.lastOrNull()?.message) {
        if (uiState.value.isStreaming) {
            val messages = uiState.value.messages
            if (messages.isNotEmpty()) {
                lazyListState.animateScrollToItem(messages.size)
            }
        }
    }
    ChatScaffold(
        uiState = uiState.value,
        lazyListState = lazyListState,
        onEvent = { viewModel.handUiEvents(it) },
        scope = scope
    ) { padding ->
        Content(
            uiState = uiState.value,
            lazyListState = lazyListState,
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScaffold(
    uiState: UiState,
    lazyListState: LazyListState,
    scope: CoroutineScope,
    onEvent: (UiEvent) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.title) },
                navigationIcon = {
                    Icon(Icons.Default.List, contentDescription = null)
                },
                actions = {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
            )
        },
        bottomBar = {
            InputText(
                textInput = uiState.textInput,
                onEvent = onEvent
            )
        },
        floatingActionButton = {
            val layoutInfo by remember { derivedStateOf { lazyListState.layoutInfo } }
            if ((layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    ?: 0) < uiState.messages.size - 4
            ) {
                FloatingActionButton(
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(uiState.messages.size)
                        }
                    }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }
        }
    ) { insetPadding ->
        content(insetPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    uiState: UiState,
    lazyListState: LazyListState,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        ChatList(
            messages = uiState.messages,
            lazyListState = lazyListState,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InputText(
    textInput: String,
    onEvent: (UiEvent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .minimumInteractiveComponentSize()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.wrapContentWidth()
                )
                Text("Agent Mode")
            }
            TextField(
                value = textInput,
                onValueChange = {
                    onEvent(UiEvent.OnTextInput(it))
                },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.clickable(
                            onClick = {
                                onEvent(UiEvent.SendMessage)
                            }
                        )
                    )
                },
                placeholder = {
                    Text("Ask anything")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun ChatList(messages: List<Message>, lazyListState: LazyListState, modifier: Modifier) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxWidth()
    ) {
        items(messages, key = { it.id }) { message ->
            when (message.role) {
                Message.Role.USER -> UserMessage(message.message)
                Message.Role.AI -> AssistantMessage(message.message)
            }
        }
    }
}

@Composable
private fun AssistantMessage(message: String) {
    Text(message, Modifier.fillMaxWidth())
}

@Composable
private fun UserMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        Text(
            text = message,
            modifier = Modifier
                .fillMaxSize(.7f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
                .clickable(
                    onClick = {
                        isExpanded = true
                    }
                ),
            textAlign = TextAlign.End
        )
        TextDropDownMenu(isExpanded) {
            isExpanded = false
        }
    }
}

@PreviewLightDark()
@Composable
fun ChatScreenPreview() {
    StarterTheme {
        val scope = rememberCoroutineScope()
        val lazyListState = rememberLazyListState()
        val uiState = UiState(messages = chatMessages, textInput = "")
        ChatScaffold(
            uiState = uiState,
            lazyListState = lazyListState,
            onEvent = {},
            scope = scope
        ) {
            Content(
                uiState = uiState,
                lazyListState = lazyListState,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun TextDropDownMenu(isExpanded: Boolean, onDismiss: () -> Unit) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Option 1") },
            onClick = { /* Do something... */ }
        )
        DropdownMenuItem(
            text = { Text("Option 2") },
            onClick = { /* Do something... */ }
        )
    }
}