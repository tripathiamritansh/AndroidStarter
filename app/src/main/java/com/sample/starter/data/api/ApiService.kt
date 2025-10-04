package com.sample.starter.data.api

import com.sample.starter.data.User
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/users")
    suspend fun getUsers(): List<User>

    @GET("/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): User

}