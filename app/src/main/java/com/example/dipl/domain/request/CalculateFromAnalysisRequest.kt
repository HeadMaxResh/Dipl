package com.example.dipl.domain.request

import com.google.gson.JsonObject

data class CalculateFromAnalysisRequest(
    val area: Float,
    val rooms: Int,
    val textAnalysis: JsonObject,
    val imageAnalysis: JsonObject,
    val geoAnalysis: JsonObject
)