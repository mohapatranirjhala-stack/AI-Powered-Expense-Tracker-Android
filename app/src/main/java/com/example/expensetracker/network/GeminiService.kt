package com.example.expensetracker.network

import com.example.expensetracker.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiService {

    private const val BASE_URL =
        "https://generativelanguage.googleapis.com/"

    private val logging =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val client =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    private val api =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(GeminiApi::class.java)

    suspend fun askGemini(
        prompt: String
    ): String {

        return try {

            val request =
                GeminiRequest(

                    contents = listOf(

                        Content(

                            parts = listOf(

                                Part(prompt)
                            )
                        )
                    )
                )

            val response =
                api.generateContent(
                    BuildConfig.GEMINI_API_KEY,
                    request
                )

            if (response.isSuccessful) {

                response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                    ?: "No response received."

            } else {

                "API Error : ${response.code()}"
            }

        } catch (e: Exception) {

            e.printStackTrace()

            "Error : ${e.localizedMessage}"
        }
    }
}
