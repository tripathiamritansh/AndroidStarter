package com.sample.starter.data.remote.model

import com.squareup.moshi.Json

data class UserListResponse(
    val page: Int,
    @Json(name = "per_page") val perPage: Int,
    val total: Int,
    @Json(name = "total_pages") val totalPages: Int,
    val data: List<UserResponse>
)