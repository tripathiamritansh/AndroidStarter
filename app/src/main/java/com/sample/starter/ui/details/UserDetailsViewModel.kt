package com.sample.starter.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sample.starter.MainActivity
import com.sample.starter.data.User
import com.sample.starter.domain.repository.UserRepository
import com.sample.starter.ui.details.UserDetailUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mutableUiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)

    val uiState: StateFlow<UserDetailUiState> = mutableUiState.asStateFlow()

    init {
        fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch {
            repository.getUserById(savedStateHandle.toRoute<MainActivity.UserDetailRoute>().userId)
                .onSuccess {
                    mutableUiState.value = Success(it)
                }
                .onFailure {
                    mutableUiState.value = UserDetailUiState.Error(it.message.orEmpty())
                }
        }
    }


}

sealed class UserDetailUiState {

    data object Loading : UserDetailUiState()

    data class Error(val message: String) : UserDetailUiState()

    data class Success(val user: User) : UserDetailUiState()
}