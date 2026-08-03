package com.example.expensetracker.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApi {

    @POST("openai/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") token: String,
        @Body request: GroqRequest
    ): Response<GroqResponse>

}