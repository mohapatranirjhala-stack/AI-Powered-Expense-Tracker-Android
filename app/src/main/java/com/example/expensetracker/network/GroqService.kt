package com.example.expensetracker.network

import com.example.expensetracker.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GroqService {

    private const val BASE_URL = "https://api.groq.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GroqApi::class.java)

    suspend fun generateReport(prompt: String): String? =
        withContext(Dispatchers.IO) {

            try {

                val request = GroqRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = listOf(
                        GroqMessage(
                            role = "user",
                            content = prompt
                        )
                    )
                )

                val response = api.chatCompletion(
                    "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request
                )

                if (response.isSuccessful) {
                    response.body()
                        ?.choices
                        ?.firstOrNull()
                        ?.message
                        ?.content
                } else {
                    null
                }

            } catch (e: Exception) {
                null
            }
        }
}