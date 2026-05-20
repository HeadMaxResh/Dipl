package com.example.dipl.presentation

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun ByteArray.toPhotoPart(index: Int): MultipartBody.Part {
    val body = this.toRequestBody("image/jpeg".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "photos",
        "photo_$index.jpg",
        body
    )
}