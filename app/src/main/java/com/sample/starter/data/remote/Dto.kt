package com.sample.starter.data.remote

// Request
data class ChatRequest(
    val model: String = "gpt-5",  // GPT-5 model
    val messages: List<ChatMessage>,
    val stream: Boolean = true,  // KEY: Enable streaming!
    val verbosity: String = "low",  // "low", "medium", "high" - Controls response detail
    val reasoning_effort: String = "low"  // "minimal", "low", "medium", "high" - Depth of reasoning
)

data class ChatMessage(
    val role: String,  // "user" or "assistant" or "system"
    val content: String
)

// Response (for streaming, you'll parse this from SSE)
data class ChatStreamResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val delta: Delta,
    val finish_reason: String?
)

data class Delta(
    val content: String?  // This is the text chunk!
)