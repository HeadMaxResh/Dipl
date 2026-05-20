package com.example.dipl.presentation.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody

fun ResponseBody.toJsonObject(): JsonObject {
    return JsonParser
        .parseString(this.string())
        .asJsonObject
}