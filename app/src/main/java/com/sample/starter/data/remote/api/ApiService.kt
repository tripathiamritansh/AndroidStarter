package com.sample.starter.data.remote.api

import com.sample.starter.data.remote.model.UserDetailsResponse
import com.sample.starter.data.remote.model.UserListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(@Query("page") page: Int): UserListResponse

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDetailsResponse

}