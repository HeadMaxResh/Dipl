package com.example.dipl.presentation.fragment.screen

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun Uri.toMultipartPart(
    context: Context,
    partName: String,
    fileName: String
): MultipartBody.Part {
    val bytes = context.contentResolver.openInputStream(this)
        ?.use { it.readBytes() }
        ?: byteArrayOf()

    val requestBody = bytes.toRequestBody("image/*".toMediaType())

    return MultipartBody.Part.createFormData(
        partName,
        fileName,
        requestBody
    )
}