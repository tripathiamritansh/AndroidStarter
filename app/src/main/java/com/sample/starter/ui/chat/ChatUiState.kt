package com.sample.starter.ui.chat

import com.sample.starter.domain.model.Message

data class ChatUiState(
    val messages: List<Message>,
    val inputText: String,
    val screenState: ScreenState
) {
    // Derived state - computed from messages, not stored
    val isAiTyping: Boolean
        get() = messages.any { it.status == Message.Status.STREAMING }
}

sealed class ScreenState {
    object Idle : ScreenState()
    object Loading : ScreenState()
    data class Error(val message: String) : ScreenState()
}