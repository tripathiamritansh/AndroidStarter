package com.sample.starter.data.respository

import com.sample.starter.data.local.UserDao
import com.sample.starter.data.local.model.UserEntity
import com.sample.starter.data.remote.model.UserDetailsResponse
import com.sample.starter.data.remote.model.UserListResponse
import com.sample.starter.data.remote.api.ApiService
import com.sample.starter.data.remote.model.UserResponse
import com.sample.starter.domain.model.User
import com.sample.starter.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : UserRepository {

    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    override fun getUsersFlow(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override suspend fun loadNextPage(): Result<Unit> {
        // Prevent loading if already loading or past last page
        if (isLoading || currentPage > totalPages) {
            return Result.success(Unit)
        }

        isLoading = true
        return try {
            val response = apiService.getUsers(currentPage)
            totalPages = response.totalPages
            
            val userEntities = response.data.map { it.toEntity(currentPage) }
            userDao.insertUsers(userEntities)
            
            currentPage++
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            isLoading = false
        }
    }

    override suspend fun refresh(): Result<Unit> {
        currentPage = 1
        totalPages = 1
        userDao.clearAll()
        return loadNextPage()
    }

    override suspend fun getUserById(id: Int): Result<User> {
        return try {
            // Try cache first
            val cachedUser = userDao.getUserById(id)
            if (cachedUser != null) {
                return Result.success(cachedUser.toDomain())
            }
            
            // Not in cache, fetch from API
            val response = apiService.getUserById(id).data
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun UserResponse.toEntity(page: Int): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        year = this.year,
        color = this.color,
        pantoneValue = this.pantoneValue,
        page = page
    )
}

fun UserResponse.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        year = this.year,
        color = this.color,
        pantoneValue = this.pantoneValue
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        year = this.year,
        color = this.color,
        pantoneValue = this.pantoneValue
    )
}

