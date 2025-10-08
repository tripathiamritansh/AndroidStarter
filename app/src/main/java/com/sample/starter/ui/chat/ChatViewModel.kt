package com.sample.starter.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.starter.data.repository.ChatException
import com.sample.starter.data.repository.ChatRepositoryImpl
import com.sample.starter.domain.model.Message
import com.sample.starter.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {

    // Repository instance (no DI for now)
    private val repository: ChatRepository = ChatRepositoryImpl()

    // Mutable state for internal updates
    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = emptyList(),
            inputText = "",
            screenState = ScreenState.Idle
        )
    )

    // Immutable state for UI observation
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Track current streaming message ID
    private var currentStreamingMessageId: String? = null

    /**
     * Handle user input text changes
     */
    fun onInputTextChanged(text: String) {
        _uiState.update { currentState ->
            currentState.copy(inputText = text)
        }
    }

    /**
     * Handle send message action
     */
    fun onSendMessage() {
        val userText = _uiState.value.inputText.trim()

        // Validate input
        if (userText.isBlank()) return

        // Don't send if already streaming
        if (_uiState.value.isAiTyping) return

        // Create user message
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            sender = Message.Sender.USER,
            content = userText,
            status = Message.Status.SENT,
            createdAt = System.currentTimeMillis()
        )

        // Add user message and clear input (optimistic update)
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                inputText = "",
                screenState = ScreenState.Idle
            )
        }

        // Get AI response
        getAiResponse(userText)
    }

    /**
     * Get AI response with streaming
     */
    private fun getAiResponse(userMessage: String) {
        viewModelScope.launch {
            // Create initial streaming message
            val aiMessageId = UUID.randomUUID().toString()
            currentStreamingMessageId = aiMessageId

            val initialAiMessage = Message(
                id = aiMessageId,
                sender = Message.Sender.SYSTEM,
                content = "",
                status = Message.Status.STREAMING,
                createdAt = System.currentTimeMillis()
            )

            // Add streaming message to UI
            _uiState.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + initialAiMessage
                )
            }

            // Collect streaming response
            repository.sendMessage(
                message = userMessage,
                conversationHistory = _uiState.value.messages.filter {
                    it.status != Message.Status.STREAMING
                }
            )
                .onCompletion { value ->
                    if (value == null) {
                        // Mark as completed when stream finishes
                        markMessageAsCompleted(aiMessageId)
                    } else {
                        handleStreamError(aiMessageId, value)
                    }
                }
                .collect { chunk ->
                    // Append each chunk to the streaming message
                    appendChunkToMessage(aiMessageId, chunk)
                }


        }
    }

    /**
     * Append text chunk to streaming message
     */
    private fun appendChunkToMessage(messageId: String, chunk: String) {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.map { message ->
                    if (message.id == messageId) {
                        message.copy(content = message.content + chunk)
                    } else {
                        message
                    }
                }
            )
        }
    }

    /**
     * Mark message as completed
     */
    private fun markMessageAsCompleted(messageId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.map { message ->
                    if (message.id == messageId) {
                        message.copy(status = Message.Status.SENT)
                    } else {
                        message
                    }
                }
            )
        }
        currentStreamingMessageId = null
    }

    /**
     * Handle streaming errors
     */
    private fun handleStreamError(messageId: String, exception: Throwable) {
        val errorMessage = when (exception) {
            is ChatException -> exception.message ?: "Failed to get response"
            else -> "Network error. Please try again."
        }

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.map { message ->
                    if (message.id == messageId) {
                        message.copy(
                            status = Message.Status.FAILED,
                            content = message.content.ifEmpty { "Error: $errorMessage" }
                        )
                    } else {
                        message
                    }
                },
                screenState = ScreenState.Error(errorMessage)
            )
        }
        currentStreamingMessageId = null
    }

    /**
     * Retry failed message
     */
    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages
            .lastOrNull { it.sender == Message.Sender.USER }
            ?.content

        if (lastUserMessage != null) {
            // Remove failed AI message
            _uiState.update { currentState ->
                currentState.copy(
                    messages = currentState.messages.filterNot {
                        it.status == Message.Status.FAILED
                    },
                    screenState = ScreenState.Idle
                )
            }

            // Retry
            getAiResponse(lastUserMessage)
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(screenState = ScreenState.Idle)
        }
    }

    /**
     * Clear all messages (new chat)
     */
    fun clearChat() {
        _uiState.update {
            ChatUiState(
                messages = emptyList(),
                inputText = "",
                screenState = ScreenState.Idle
            )
        }
        currentStreamingMessageId = null
    }
}
