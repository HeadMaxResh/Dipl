package com.example.dipl.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject

class AddApartmentSharedViewModel : ViewModel() {

    val photos = mutableListOf<ByteArray>()

    var imageAnalysis: JsonObject? = null
    var geoAnalysis: JsonObject? = null
    var textAnalysis: JsonObject? = null
    var priceAnalysis: JsonObject? = null

    var city: String = ""
    var address: String = ""
    var apartmentNumber: String = ""

    var rooms: Int = 0
    var area: Float = 0f
    var cadastr: String = ""

    var description: String = ""

    var recommendedPrice: Int = 0
    var finalRent: Int = 0

    fun fullAddress(): String {
        return "$city, $address"
    }

    fun clear() {
        photos.clear()
        imageAnalysis = null
        geoAnalysis = null
        textAnalysis = null
        priceAnalysis = null

        city = ""
        address = ""
        apartmentNumber = ""
        rooms = 0
        area = 0f
        cadastr = ""
        description = ""

        recommendedPrice = 0
        finalRent = 0
    }
}