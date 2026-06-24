package com.example.dipl.presentation.fragment.screen

import com.example.dipl.domain.responce.GeoAnalysisResponse
import com.example.dipl.domain.responce.ImageAnalysisResponse
import com.example.dipl.domain.responce.TextAnalysisResponse

data class CalculateFromAnalysisRequest(
    val area: Float,
    val rooms: Int,
    val textAnalysis: TextAnalysisResponse,
    val imageAnalysis: ImageAnalysisResponse,
    val geoAnalysis: GeoAnalysisResponse
)