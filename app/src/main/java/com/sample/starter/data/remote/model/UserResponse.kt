package com.sample.starter.data.remote.model

import com.squareup.moshi.Json

data class UserResponse(
    val id: Int,
    val name: String,
    val year: Int,
    val color: String,
    @Json(name = "pantone_value") val pantoneValue: String
)