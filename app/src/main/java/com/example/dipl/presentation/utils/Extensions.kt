package com.example.dipl.presentation.utils

import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String.toTextBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaType())
}

fun JsonObject.toJsonRequestBody(): RequestBody {
    return this.toString()
        .toRequestBody("application/json".toMediaType())
}