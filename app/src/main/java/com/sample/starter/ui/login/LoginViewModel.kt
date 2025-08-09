package com.sample.starter.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<UiState>(UiState())
    val uiState = mutableState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState()
        )

    val mutableSideEffects = MutableSharedFlow<SideEffects>()

    val sideEffects = mutableSideEffects.asSharedFlow()

    val events = Channel<Events>(CONFLATED)

    init {
        fetchData()
        processEvents()
    }

    fun fetchData() {
        viewModelScope.launch {
            delay(5000L)
            mutableState.value = mutableState.value.copy(isLoading = false)
        }
    }

    fun processEvents() {
        viewModelScope.launch {
            events.receiveAsFlow().collect {
                when (it) {
                    Events.Submit -> login()
                    is Events.FirstNameUpdate -> {
                        mutableState.value = mutableState.value.copy(firstName = it.name)

                    }

                    is Events.LastNameUpdated -> mutableState.value =
                        mutableState.value.copy(lastName = it.name)

                }
            }
        }
    }


    suspend fun login() {
        val value = mutableState.value
        if (value.lastName.isNotEmpty() && value.firstName.isNotEmpty()) {
            mutableState.value = mutableState.value.copy(isLoading = true)
            delay(5000L)
            mutableState.value = mutableState.value.copy(isLoading = false)
            mutableSideEffects.emit(SideEffects.ShowToast("Success"))
        } else {
            mutableSideEffects.emit(SideEffects.ShowToast("Failure"))
        }
    }

}

sealed class SideEffects {
    data class ShowToast(val message: String) : SideEffects()
}

sealed class Events {

    data object Submit : Events()
    data class FirstNameUpdate(val name: String) : Events()
    data class LastNameUpdated(val name: String) : Events()

}

data class UiState(
    val isLoading: Boolean = true, val firstName: String = "", val lastName: String = ""
)