package com.example.dipl.presentation.fragment.screen

import android.net.Uri
import com.example.dipl.domain.responce.GeoAnalysisResponse
import com.example.dipl.domain.responce.ImageAnalysisResponse
import com.example.dipl.domain.responce.PriceAnalysisResponse
import com.example.dipl.domain.responce.TextAnalysisResponse

data class AddApartmentState(
    val photoUris: List<Uri> = emptyList(),

    val city: String = "",
    val address: String = "",
    val apartmentNumber: String = "",
    val rooms: Int = 1,
    val area: Float = 0f,
    val cadastr: String = "",

    val description: String = "",

    val imageAnalysis: ImageAnalysisResponse? = null,
    val geoAnalysis: GeoAnalysisResponse? = null,
    val textAnalysis: TextAnalysisResponse? = null,
    val priceAnalysis: PriceAnalysisResponse? = null,

    val finalRent: Int = 0,

    val isLoading: Boolean = false
) {
    fun fullAddress(): String {
        return "$city, $address"
    }
}