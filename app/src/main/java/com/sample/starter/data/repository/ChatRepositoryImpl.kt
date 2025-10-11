package com.sample.starter.data.repository

import android.content.Context
import android.util.Log
import com.sample.starter.BuildConfig
import com.sample.starter.data.local.ChatDatabase
import com.sample.starter.data.local.MessageDao
import com.sample.starter.data.remote.ChatMessage
import com.sample.starter.data.remote.ChatRequest
import com.sample.starter.data.remote.ChatStreamResponse
import com.sample.starter.data.remote.OpenAiApi
import com.sample.starter.domain.model.Message
import com.sample.starter.domain.repository.ChatRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID

class ChatRepositoryImpl(context: Context) : ChatRepository {

    companion object {
        private const val TAG = "ChatRepositoryImpl"
    }

    private val database = ChatDatabase.getInstance(context)
    private val messageDao: MessageDao = database.messageDao()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(ChatStreamResponse::class.java)

    /**
     * Observe all messages from database
     */
    override fun getAllMessages(): Flow<List<Message>> {
        return messageDao.getAllMessages()
    }

    /**
     * Send message - handles entire flow:
     * 1. Save user message to DB
     * 2. Create streaming AI message
     * 3. Call API and stream response
     * 4. Update DB with each chunk
     * 5. Mark as complete or failed
     */
    override suspend fun sendMessage(userMessage: String) = withContext(Dispatchers.IO) {
        try {
            // 1. Save user message to database
            val userMsg = Message(
                id = UUID.randomUUID().toString(),
                sender = Message.Sender.USER,
                content = userMessage,
                status = Message.Status.SENT,
                createdAt = System.currentTimeMillis()
            )
            messageDao.insertMessage(userMsg)
            Log.d(TAG, "User message saved to DB: ${userMsg.id}")

            // 2. Create initial streaming AI message in database
            val aiMessageId = UUID.randomUUID().toString()
            val aiMessage = Message(
                id = aiMessageId,
                sender = Message.Sender.SYSTEM,
                content = "",
                status = Message.Status.STREAMING,
                createdAt = System.currentTimeMillis()
            )
            messageDao.insertMessage(aiMessage)
            Log.d(TAG, "AI streaming message created in DB: $aiMessageId")

            // 3. Get conversation history from database
            val conversationHistory = messageDao.getAllMessages()
                .first() // Get current snapshot
                .filter { it.status != Message.Status.STREAMING } // Exclude current streaming message

            // 4. Make API call and stream response
            streamAiResponse(aiMessageId, userMessage, conversationHistory)

        } catch (e: Exception) {
            Log.e(TAG, "Error in sendMessage", e)
            throw e
        }
    }

    /**
     * Stream AI response from API and update database
     */
    private suspend fun streamAiResponse(
        aiMessageId: String,
        userMessage: String,
        conversationHistory: List<Message>
    ) = withContext(Dispatchers.IO) {
        try {
            // Check API key
            val apiKey = BuildConfig.OPENAI_API_KEY
            Log.d(TAG, "API Key configured: ${apiKey.isNotEmpty()}")

            if (apiKey.isEmpty()) {
                throw ChatException("OpenAI API key is not configured")
            }

            // Build request
            val messages = conversationHistory.map { it.toChatMessage() } +
                    ChatMessage(role = "user", content = userMessage)

            val request = ChatRequest(
                model = "gpt-4o",
                messages = messages,
                stream = true
            )

            Log.d(TAG, "Sending request with ${messages.size} messages")

            // Make API call
            val response = OpenAiApi.api.streamChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            // Stream response and update database
            var fullContent = ""
            response.byteStream().bufferedReader().use { reader ->
                reader.lineSequence().forEach { line ->
                    if (line.startsWith("data: ")) {
                        val data = line.removePrefix("data: ").trim()

                        if (data == "[DONE]") {
                            // Stream complete - mark as SENT
                            messageDao.updateMessageStatus(aiMessageId, Message.Status.SENT)
                            Log.d(TAG, "Stream completed for message: $aiMessageId")
                            return@forEach
                        }

                        // Parse chunk
                        val chunk = parseChunk(data)
                        val content = chunk.choices.firstOrNull()?.delta?.content

                        if (content != null) {
                            // Append to full content
                            fullContent += content

                            // Update database with new content
                            messageDao.updateMessageContent(aiMessageId, fullContent)
                            Log.d(TAG, "Updated message content: ${fullContent.take(50)}...")
                        }
                    }
                }
            }

        } catch (e: HttpException) {
            handleHttpError(aiMessageId, e)
        } catch (e: IOException) {
            handleNetworkError(aiMessageId, e)
        } catch (e: Exception) {
            handleGeneralError(aiMessageId, e)
        }
    }

    /**
     * Handle HTTP errors
     */
    private suspend fun handleHttpError(messageId: String, e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e(TAG, "HTTP ${e.code()} error: $errorBody", e)

        val errorMessage = when (e.code()) {
            401 -> "Authentication failed. Please check your API key."
            429 -> "Rate limit exceeded. Please try again later."
            500, 502, 503 -> "OpenAI service is currently unavailable."
            else -> "HTTP ${e.code()} error occurred."
        }

        // Update message status to FAILED in database
        messageDao.updateMessageStatus(messageId, Message.Status.FAILED)
        messageDao.updateMessageContent(messageId, "Error: $errorMessage")

        throw ChatException(errorMessage, e)
    }

    /**
     * Handle network errors
     */
    private suspend fun handleNetworkError(messageId: String, e: IOException) {
        Log.e(TAG, "Network error: ${e.message}", e)

        val errorMessage = "Network error. Please check your connection."
        messageDao.updateMessageStatus(messageId, Message.Status.FAILED)
        messageDao.updateMessageContent(messageId, "Error: $errorMessage")

        throw ChatException(errorMessage, e)
    }

    /**
     * Handle general errors
     */
    private suspend fun handleGeneralError(messageId: String, e: Exception) {
        Log.e(TAG, "Unexpected error: ${e.message}", e)

        val errorMessage = "Failed to get AI response: ${e.message}"
        messageDao.updateMessageStatus(messageId, Message.Status.FAILED)
        messageDao.updateMessageContent(messageId, "Error: $errorMessage")

        throw ChatException(errorMessage, e)
    }

    /**
     * Parse JSON chunk from stream
     */
    private fun parseChunk(json: String): ChatStreamResponse {
        return adapter.fromJson(json) ?: throw IllegalStateException("Failed to parse chunk")
    }

    /**
     * Convert Message to ChatMessage for API
     */
    private fun Message.toChatMessage() = ChatMessage(
        role = when (sender) {
            Message.Sender.USER -> "user"
            Message.Sender.SYSTEM -> "assistant"
        },
        content = content
    )

    /**
     * Clear all messages from database
     */
    override suspend fun clearAllMessages() {
        withContext(Dispatchers.IO) {
            messageDao.deleteAllMessages()
            Log.d(TAG, "All messages cleared from database")
        }
    }

    /**
     * Retry the last failed message
     */
    override suspend fun retryLastMessage() = withContext(Dispatchers.IO) {
        val allMessages = messageDao.getAllMessages().first()

        // Find the last failed message
        val failedMessage = allMessages.lastOrNull { it.status == Message.Status.FAILED }

        // Find the last user message
        val lastUserMessage = allMessages.lastOrNull { it.sender == Message.Sender.USER }

        if (failedMessage != null && lastUserMessage != null) {
            // Delete the failed message
            messageDao.deleteMessage(failedMessage)
            Log.d(TAG, "Deleted failed message: ${failedMessage.id}")

            // Resend the user message
            sendMessage(lastUserMessage.content)
        }
    }
}

/**
 * Custom exception for chat errors
 */
class ChatException(message: String, cause: Throwable? = null) : IOException(message, cause)
