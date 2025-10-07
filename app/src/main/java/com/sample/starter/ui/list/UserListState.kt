package com.sample.starter.ui.list

import com.sample.starter.domain.model.User

sealed class UserListState {

    data object Loading : UserListState()

    data class Error(val message: String) : UserListState()

    data class Success(val users: List<User>) : UserListState()
}