package com.sample.starter.ui.chat

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sample.starter.data.repository.ChatException
import com.sample.starter.data.repository.ChatRepositoryImpl
import com.sample.starter.domain.model.Message
import com.sample.starter.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    // Repository - contains all business logic
    private val chatRepository: ChatRepository = ChatRepositoryImpl(application)

    // Input text state (only UI state that doesn't come from DB)
    private val _inputText = MutableStateFlow("")

    // Error state
    private val _errorState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    // Combine messages from DB with UI state
    val uiState: StateFlow<ChatUiState> = combine(
        chatRepository.getAllMessages(),
        _inputText,
        _errorState
    ) { messages, inputText, errorState ->
        ChatUiState(
            messages = messages,
            inputText = inputText,
            screenState = errorState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatUiState(emptyList(), "", ScreenState.Idle)
    )

    /**
     * Handle user input text changes
     */
    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    /**
     * Send message - delegates to repository
     * Repository handles:
     * - Saving to DB
     * - API call
     * - Streaming updates to DB
     * - Error handling
     */
    fun onSendMessage() {
        val messageText = _inputText.value.trim()

        // Validate
        if (messageText.isBlank()) return

        // Check if already streaming
        val isStreaming = uiState.value.messages.any {
            it.status == Message.Status.STREAMING
        }
        if (isStreaming) return

        // Clear input immediately for better UX
        _inputText.value = ""

        // Send message via repository
        viewModelScope.launch {
            try {
                _errorState.value = ScreenState.Idle
                chatRepository.sendMessage(messageText)
            } catch (e: ChatException) {
                _errorState.value = ScreenState.Error(e.message ?: "Failed to send message")
            } catch (e: Exception) {
                _errorState.value = ScreenState.Error("An unexpected error occurred")
            }
        }
    }

    /**
     * Retry last failed message - delegates to repository
     */
    fun retryLastMessage() {
        viewModelScope.launch {
            try {
                _errorState.value = ScreenState.Idle
                chatRepository.retryLastMessage()
            } catch (e: ChatException) {
                _errorState.value = ScreenState.Error(e.message ?: "Failed to retry message")
            } catch (e: Exception) {
                _errorState.value = ScreenState.Error("An unexpected error occurred")
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _errorState.value = ScreenState.Idle
    }

    /**
     * Clear all messages - delegates to repository
     */
    fun clearChat() {
        viewModelScope.launch {
            try {
                chatRepository.clearAllMessages()
                _errorState.value = ScreenState.Idle
            } catch (e: Exception) {
                _errorState.value = ScreenState.Error("Failed to clear chat")
            }
        }
    }
}
