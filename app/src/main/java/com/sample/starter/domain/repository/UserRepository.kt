package com.sample.starter.domain.repository

import com.sample.starter.data.User

interface UserRepository {

    suspend fun getUsers(): Result<List<User>>

    suspend fun getUserById(id: Int): Result<User>
}