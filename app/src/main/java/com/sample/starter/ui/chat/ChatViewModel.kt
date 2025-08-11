package com.sample.starter.ui.chat

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val mutableState = MutableStateFlow(UiState(chatMessages))
    val uiState = mutableState.asStateFlow()

    init {

    }

    fun handUiEvents(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.SendMessage -> {
                //ideally send to repository
                viewModelScope.launch {
                    mutableState.value = mutableState.value.copy(
                        messages = mutableState.value.messages.toMutableList().apply {
                            add(
                                Message(
                                    message = mutableState.value.textInput,
                                    role = Message.Role.USER
                                )
                            )
                        }
                    )
                    stream()
                }
            }

            is UiEvent.OnTextInput -> {
                mutableState.value = mutableState.value.copy(
                    textInput = uiEvent.message
                )
            }
        }
    }

    suspend fun stream() {
        val string = "Sure! Kotlin provides a variety of collections such as lists, sets, and maps."

        // Add a new AI message that will be streamed
        val messages = mutableState.value.messages.toMutableList()
        messages.add(Message(message = "", role = Message.Role.AI))

        mutableState.value = mutableState.value.copy(messages = messages, isStreaming = true)

        // Stream character by character with delays
        var streamedMessage = ""
        string.forEach { char ->
            streamedMessage += char
            val updatedMessages = mutableState.value.messages.toMutableList()
            updatedMessages[updatedMessages.size - 1] = updatedMessages.last().copy(
                message = streamedMessage
            )
            mutableState.value = mutableState.value.copy(messages = updatedMessages)

            // Add delay for streaming effect
            kotlinx.coroutines.delay(50) // 50ms delay between characters
        }

        // Mark streaming as complete
        val finalMessages = mutableState.value.messages.toMutableList()
        mutableState.value = mutableState.value.copy(messages = finalMessages, isStreaming = false)
    }
}


sealed class UiEvent {

    data class OnTextInput(val message: String) : UiEvent()
    data object SendMessage : UiEvent()
}

@Immutable
data class UiState(
    val messages: List<Message>,
    val textInput: String = "",
    val title: String = "GPT 5",
    val isStreaming: Boolean = false
)

data class Message(
    val id: UUID = UUID.randomUUID(),
    val message: String,
    val role: Role,
) {
    enum class Role {
        AI,
        USER
    }
}

val chatMessages = listOf(
    Message(message = "Hey, how can I help you today?", role = Message.Role.AI),
    Message(
        message = "I'm looking for information on Kotlin collections.",
        role = Message.Role.USER
    ),
    Message(
        message = "Sure! Kotlin provides a variety of collections such as lists, sets, and maps.",
        role = Message.Role.AI
    ),
    Message(
        message = "Can you explain how immutable lists work in Kotlin?",
        role = Message.Role.USER
    ),
    Message(
        message = "Absolutely! In Kotlin, an immutable list cannot be modified after it is created.",
        role = Message.Role.AI
    ),
    Message(
        message = "So, if I want to change the list, I would need to create a new one?",
        role = Message.Role.USER
    ),
    Message(
        message = "Yes, that's correct. You would create a new list with the changes you need.",
        role = Message.Role.AI
    ),
    Message(message = "Got it! How about sets in Kotlin?", role = Message.Role.USER),
    Message(
        message = "A set in Kotlin is a collection of unique elements, meaning no duplicates are allowed.",
        role = Message.Role.AI
    ),
    Message(message = "Can I use a set for a collection of IDs?", role = Message.Role.USER),
    Message(
        message = "Yes, a set would be perfect for ensuring that all the IDs are unique.",
        role = Message.Role.AI
    )

)