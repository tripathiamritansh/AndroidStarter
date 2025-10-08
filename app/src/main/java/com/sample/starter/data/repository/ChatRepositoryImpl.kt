package com.sample.starter.data.repository

import com.sample.starter.BuildConfig
import java.io.IOException
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChatRepositoryImpl : ChatRepository {


    override fun sendMessage(
        userMessage: String,
        conversationHistory: List<Message>
    ): Flow<String> = flow {
        try {
            // 1. Build the request
            val messages = conversationHistory.map { it.toChatMessage() } +
                    ChatMessage(role = "user", content = userMessage)

            val request = ChatRequest(
                model = "gpt-5",
                messages = messages,
                stream = true,
                verbosity = "medium",  // Adjust response detail level
                reasoning_effort = "medium"  // Adjust reasoning depth
            )

            // 2. Make the API call
            val response = OpenAiApi.api.streamChatCompletion(
                authorization = "Bearer ${BuildConfig.OPENAI_API_KEY}",
                request = request
            )

            // 3. Read the streaming response
            response.byteStream().bufferedReader().use { reader ->
                reader.lineSequence().forEach { line ->
                    if (line.startsWith("data: ")) {
                        val data = line.removePrefix("data: ").trim()

                        // OpenAI sends "[DONE]" when stream ends
                        if (data == "[DONE]") return@forEach

                        // 4. Parse JSON and extract content
                        val chunk = parseChunk(data)  // Parse JSON to ChatStreamResponse
                        val content = chunk.choices.firstOrNull()?.delta?.content

                        // 5. Emit the text chunk
                        if (content != null) {
                            emit(content)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Handle errors gracefully
            throw ChatException("Failed to get AI response: ${e.message}", e)
        }
    }.flowOn(Dispatchers.IO)  // Run on IO dispatcher

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(ChatStreamResponse::class.java)

    private fun parseChunk(json: String): ChatStreamResponse {
        return adapter.fromJson(json) ?: throw IllegalStateException("Failed to parse")
    }

    private fun Message.toChatMessage() = ChatMessage(
        role = when (sender) {
            Message.Sender.USER -> "user"
            Message.Sender.SYSTEM -> "assistant"
        },
        content = content
    )

}

// Custom exception for chat errors
class ChatException(message: String, cause: Throwable? = null) : IOException(message, cause)