package com.sample.starter.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val year: Int,
    val color: String,
    val pantoneValue: String,
    val page: Int  // Track which page this user belongs to
)