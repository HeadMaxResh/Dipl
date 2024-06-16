package com.example.dipl.domain.request

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RegistrationRequest(
    val name: String,
    val surname: String,
    val phone: String,
    val passwordHashed: String,
)
