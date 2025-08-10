package com.sample.starter.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val mutableState = MutableStateFlow(UiState(chatMessages))
    val uiState = mutableState.asStateFlow()

    fun handUiEvents(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.SendMessage -> {
                //ideally send to repository
                mutableState.value = mutableState.value.copy(
                    messages = mutableState.value.messages.toMutableList().apply {
                        add(
                            Message(
                                message = uiEvent.message,
                                role = Message.Role.USER
                            )
                        )
                    }
                )
            }

            is UiEvent.OnTextInput -> {
                mutableState.value = mutableState.value.copy(
                    textInput = uiEvent.message
                )
            }
        }
    }
}

sealed class UiEvent {

    data class OnTextInput(val message: String) : UiEvent()
    data class SendMessage(val message: String) : UiEvent()
}

data class UiState(
    val messages: List<Message>,
    val textInput: String = "",
    val title : String = "GPT 5"
)

data class Message(
    val id: UUID = UUID.randomUUID(),
    val message: String,
    val role: Role
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