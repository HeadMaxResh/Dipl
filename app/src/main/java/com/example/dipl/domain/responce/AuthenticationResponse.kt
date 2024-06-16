package com.example.dipl.domain.responce

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationResponse(
    val status: String,
    val message: String?,
    val data: TokenResponse
)