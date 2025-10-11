package com.sample.starter.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

interface OpenAiApi {

    @POST("v1/chat/completions")
    @Streaming  // IMPORTANT: Tells Retrofit not to buffer the entire response
    suspend fun streamChatCompletion(
        @Header("Authorization") authorization: String,  // "Bearer YOUR_API_KEY"
        @Body request: ChatRequest
    ): ResponseBody  // Raw response body for streaming

    companion object {
        const val BASE_URL = "https://api.openai.com/"

        val api: OpenAiApi by lazy { createApi() }
        private fun createApi(): OpenAiApi {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .readTimeout(60, TimeUnit.SECONDS)  // Important for streaming!
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(OpenAiApi::class.java)
        }
    }
}
