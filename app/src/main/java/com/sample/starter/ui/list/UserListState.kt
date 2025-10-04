package com.sample.starter.ui.list

import com.sample.starter.data.User

sealed class UserListState {

    data object Loading : UserListState()

    data object Error : UserListState()

    data class Success(val users: List<User>) : UserListState()
}