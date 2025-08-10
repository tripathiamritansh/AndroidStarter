package com.sample.starter.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sample.feed.ui.theme.StarterTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {

    val viewModel: ChatViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Content(
        uiState = uiState.value,
        onEvent = { viewModel.handUiEvents(it) })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit
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
        }
    ) { insetPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insetPadding)
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    when (message.role) {
                        Message.Role.USER -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            var isExpanded by remember { mutableStateOf(false) }
                            Text(
                                text = message.message,
                                modifier = Modifier
                                    .fillMaxSize(.7f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(8.dp)
                                    .clickable(
                                        onClick = {
                                            isExpanded = true
                                        }
                                    )
                                ,
                                textAlign = TextAlign.End
                            )
                            TextDropDownMenu(isExpanded) {
                                isExpanded = false
                            }
                        }

                        Message.Role.AI -> Text(message.message, Modifier.fillMaxWidth())
                    }
                }
            }
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
                        value = uiState.textInput,
                        onValueChange = {
                            onEvent(UiEvent.OnTextInput(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        placeholder = {
                            Text("Ask anything")
                        },
                        colors = TextFieldDefaults.colors().copy(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@PreviewLightDark()
@Composable
fun ChatScreenPreview() {
    StarterTheme {
        Content(UiState(messages = chatMessages, textInput = ""), {})
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