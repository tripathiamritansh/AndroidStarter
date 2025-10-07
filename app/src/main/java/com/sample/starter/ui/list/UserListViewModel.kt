package com.sample.starter.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.starter.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserListState>(UserListState.Loading)
    val uiState: StateFlow<UserListState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        observeUsers()
        loadInitialPage()
    }

    private fun observeUsers() {
        repository
            .getUsersFlow()
            .onEach { users ->
                _uiState.value = if (users.isEmpty()) {
                    UserListState.Loading
                } else {
                    UserListState.Success(users)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadInitialPage() {
        viewModelScope.launch {
            repository.loadNextPage()
                .onFailure { exception ->
                    _uiState.value = UserListState.Error(
                        exception.message ?: "Unknown error"
                    )
                }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            repository.loadNextPage()
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        viewModelScope.launch {
            repository.refresh()
                .onSuccess {
                    _isRefreshing.value = false
                }
                .onFailure { exception ->
                    _uiState.value = UserListState.Error(
                        exception.message ?: "Unknown error"
                    )
                    _isRefreshing.value = false
                }
        }
    }
}