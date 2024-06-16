package com.example.dipl.domain.request

import com.squareup.moshi.JsonClass

class EnterRequest {

    @JsonClass(generateAdapter = true)
    class EnterRequest(
        val phone: String,
        val passwordHashed: String
    )
}