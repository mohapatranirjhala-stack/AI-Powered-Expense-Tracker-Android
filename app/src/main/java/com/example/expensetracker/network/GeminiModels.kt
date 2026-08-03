package com.example.expensetracker.network

// ---------------------------
// Request Models
// ---------------------------

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

// ---------------------------
// Response Models
// ---------------------------

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ResponseContent?
)

data class ResponseContent(
    val parts: List<ResponsePart>?
)

data class ResponsePart(
    val text: String?
)