package com.sample.starter.domain.repository

import com.sample.starter.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    
    /**
     * Observe all messages from database
     * Automatically updates when database changes
     */
    fun getAllMessages(): Flow<List<Message>>
    
    /**
     * Send a message to the AI
     * Handles:
     * 1. Saving user message to DB
     * 2. Creating streaming AI message in DB
     * 3. Making API call
     * 4. Updating DB with streamed chunks
     * 5. Marking message as complete/failed
     */
    suspend fun sendMessage(userMessage: String)
    
    /**
     * Clear all messages from database
     */
    suspend fun clearAllMessages()
    
    /**
     * Retry the last failed message
     */
    suspend fun retryLastMessage()
}
