package com.sample.starter.domain.model

data class Message(
    val id: String,
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