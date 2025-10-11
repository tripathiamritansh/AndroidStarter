package com.sample.starter.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val sender: Sender,
    val content: String,
    val status: Status,
    val createdAt: Long
) {
    enum class Status {
        SENT,
        SENDING,
        FAILED,
        STREAMING
    }

    enum class Sender {
        USER,
        SYSTEM
    }
}
