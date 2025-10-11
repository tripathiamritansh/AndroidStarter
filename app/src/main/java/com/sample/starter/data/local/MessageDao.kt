package com.sample.starter.data.local

import androidx.room.*
import com.sample.starter.domain.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    
    /**
     * Get all messages as a Flow (reactive stream)
     * Automatically updates when database changes!
     */
    @Query("SELECT * FROM messages ORDER BY createdAt ASC")
    fun getAllMessages(): Flow<List<Message>>
    
    /**
     * Get recent N messages
     */
    @Query("SELECT * FROM messages ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMessages(limit: Int): Flow<List<Message>>
    
    /**
     * Get a specific message by ID
     */
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): Message?
    
    /**
     * Insert a message (replace if ID exists)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
    
    /**
     * Insert multiple messages
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    /**
     * Update a message
     */
    @Update
    suspend fun updateMessage(message: Message)
    
    /**
     * Update message status
     */
    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: Message.Status)
    
    /**
     * Update message content
     */
    @Query("UPDATE messages SET content = :content WHERE id = :messageId")
    suspend fun updateMessageContent(messageId: String, content: String)
    
    /**
     * Delete a specific message
     */
    @Delete
    suspend fun deleteMessage(message: Message)
    
    /**
     * Delete all messages
     */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
    
    /**
     * Get message count
     */
    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getMessageCount(): Int
}
