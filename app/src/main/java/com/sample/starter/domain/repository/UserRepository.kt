package com.sample.starter.domain.repository

import com.sample.starter.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // Observe all users from cache (Flow emits whenever cache changes)
    fun getUsersFlow(): Flow<List<User>>

    // Load next page from API and save to cache
    suspend fun loadNextPage(): Result<Unit>

    // Clear cache and reload first page
    suspend fun refresh(): Result<Unit>

    // Get specific user (from cache or API)
    suspend fun getUserById(id: Int): Result<User>
}