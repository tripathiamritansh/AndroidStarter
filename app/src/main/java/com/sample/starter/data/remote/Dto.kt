package com.sample.starter.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Request
@JsonClass(generateAdapter = true)
data class ChatRequest(
    @Json(name = "model")
    val model: String = "gpt-4o",  // GPT-4o model
    @Json(name = "messages")
    val messages: List<ChatMessage>,
    @Json(name = "stream")
    val stream: Boolean = true,  // KEY: Enable streaming!
    @Json(name = "verbosity")
    val verbosity: String? = null,  // "low", "medium", "high" - Controls response detail (optional)
    @Json(name = "reasoning_effort")
    val reasoning_effort: String? = null  // "minimal", "low", "medium", "high" - Depth of reasoning (optional)
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    @Json(name = "role")
    val role: String,  // "user" or "assistant" or "system"
    @Json(name = "content")
    val content: String
)

// Response (for streaming, you'll parse this from SSE)
@JsonClass(generateAdapter = true)
data class ChatStreamResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "choices")
    val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    @Json(name = "delta")
    val delta: Delta,
    @Json(name = "finish_reason")
    val finish_reason: String?
)

@JsonClass(generateAdapter = true)
data class Delta(
    @Json(name = "content")
    val content: String?  // This is the text chunk!
)