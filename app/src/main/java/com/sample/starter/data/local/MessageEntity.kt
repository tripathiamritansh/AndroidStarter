package com.sample.starter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.starter.domain.model.Message

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val sender: Message.Sender,
    val content: String,
    val status: Message.Status,
    val createdAt: Long
)