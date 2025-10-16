package com.sample.starter.ui.chat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sample.starter.domain.model.Message
import com.sample.starter.domain.model.Message.Sender.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        ChatScreenContent(
            state = state,
            onTextChange = viewModel::onInputTextChanged,
            onSendClick = viewModel::onSendMessage,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ChatScreenContent(
    state: ChatUiState,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.isAiTyping) {
        lazyListState.animateScrollToItem(state.messages.size)
    }

    Column(modifier = modifier.fillMaxSize()) {

        if (state.screenState is ScreenState.Error) {
            // Show error screen
        }

        MessageList(
            messages = state.messages,
            lazyListState = lazyListState,
            modifier = Modifier.weight(1f)
        )

        InputSection(
            value = state.inputText,
            onTextChange = onTextChange,
            onSendClick = onSendClick
        )
    }


}

@Composable
fun MessageList(
    messages: List<Message>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            currentTime = System.currentTimeMillis()
        }
    }
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(items = messages, key = { it.id }) {
            MessageItem(
                message = it,
                relativeTimeString = getRelativeTimeString(it.createdAt, currentTime)
            )
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    relativeTimeString: String
) {
    when (message.sender) {
        USER -> UserMessage(message.content, relativeTimeString, message.createdAt)
        SYSTEM -> SystemMessage(
            message.content,
            message.status == Message.Status.STREAMING,
            relativeTimeString,
            message.createdAt
        )
    }
}


fun formatAbsoluteTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


@Composable
fun UserMessage(content: String, relativeTimeString: String, createdAt: Long) {
    var showAbsoluteTime by remember { mutableStateOf(false) }

    val displayTime = if (showAbsoluteTime) {
        // Format the absolute time nicely
        formatAbsoluteTime(createdAt)
    } else {
        relativeTimeString  // This updates automatically from parent!
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(.6f)
                .clickable(
                    onClick = {
                        showAbsoluteTime = !showAbsoluteTime
                    }
                ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 0.dp
            )
        ) {
            Column {
                Text(text = content, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = displayTime, modifier = Modifier.padding(8.dp))
            }
        }
    }
}


@Composable
fun SystemMessage(
    content: String,
    isTyping: Boolean = false,
    relativeTimeString: String,
    createdAt: Long
) {
    var showAbsoluteTime by remember { mutableStateOf(false) }

    val displayTime = if (showAbsoluteTime) {
        // Format the absolute time nicely
        formatAbsoluteTime(createdAt)
    } else {
        relativeTimeString  // This updates automatically from parent!
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(.6f).clickable(
                onClick = {
                    showAbsoluteTime = !showAbsoluteTime
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 0.dp,
                bottomEnd = 16.dp
            )
        ) {
            if (isTyping) {
                TypingIndicator()
            } else {
                Column {
                    Text(text = content, modifier = Modifier.padding(8.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = displayTime, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

// Add this anywhere in ChatScreen.kt (outside composables)
fun getRelativeTimeString(createdAt: Long, currentTime: Long): String {
    val diffInSeconds = (currentTime - createdAt) / 1000

    return when {
        diffInSeconds < 10 -> "Just now"
        diffInSeconds < 60 -> "${diffInSeconds}s ago"
        diffInSeconds < 3600 -> "${diffInSeconds / 60}m ago"
        else -> "${diffInSeconds / 3600}h ago"
    }
}

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    // This looks exactly like SystemMessage, but with animated dots instead of text

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Thinking",
            // Optional: add style
            // style = MaterialTheme.typography.bodyMedium
        )

        // The animated dots!
        AnimatedDot()
    }

}

@Composable
fun AnimatedDot() {
    val inifiniteTransition = rememberInfiniteTransition(label = "bouncing_dots")
    val offset1 by inifiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
    )
    val offset2 by inifiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 500,
                delayMillis = 200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val offset3 by inifiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer {
                    translationY = offset1
                }
                .background(
                    color = Color.Gray,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer {
                    translationY = offset2
                }
                .background(
                    color = Color.Gray,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer {
                    translationY = offset3
                }
                .background(
                    color = Color.Gray,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun InputSection(
    value: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val wordCount = value.trim().length
    val textColor by remember(wordCount) {
        derivedStateOf {
            when {
                wordCount < 400 -> Color.Green
                wordCount < 450 -> Color.Yellow
                else -> Color.Red
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSendClick) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$wordCount/500", color = textColor)
        }
    }
}


@Preview
@Composable
fun PreviewContent() {
    ChatScreenContent(
        state = ChatUiState(
            inputText = "",
            messages = listOf(
                Message(
                    content = "Hello",
                    sender = Message.Sender.USER,
                    createdAt = 12123213L,
                    id = "123123f",
                    status = Message.Status.SENT
                ),
                Message(
                    content = "Hello",
                    sender = Message.Sender.SYSTEM,
                    createdAt = 123213L,
                    id = "123123c",
                    status = Message.Status.SENT
                ),
                Message(
                    content = "Hello",
                    sender = Message.Sender.USER,
                    createdAt = 123213L,
                    id = "123123a",
                    status = Message.Status.SENT
                ),
                Message(
                    content = "Hello",
                    sender = Message.Sender.SYSTEM,
                    createdAt = 123213L,
                    id = "1231e23",
                    status = Message.Status.SENT
                ),

                ),
            screenState = ScreenState.Idle
        ),
        onTextChange = {},
        onSendClick = {}
    )
}