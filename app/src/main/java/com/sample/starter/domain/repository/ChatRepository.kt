package com.sample.starter.domain.repository

import com.sample.starter.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun sendMessage(message: String, conversationHistory: List<Message>): Flow<String>
}