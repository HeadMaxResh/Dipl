package com.example.dipl.domain.responce

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TokenResponse(
    val token: String
)