package com.example.dipl.domain.model

import com.diplback.diplserver.model.Review
import java.io.Serializable
import java.util.*

data class ApartmentInfo(
    val id: Int  = UNDEFINED_ID,
    val name: String,
    val city: String,
    val rent: Int,
    val listImages: List<String>,
    val area: Float,
    val countRooms: Int,
    val rate: Double,
    val userOwner: User,
    val description: String,
    val reviewList: MutableList<Review>? = Collections.emptyList(),
    var isFavorite: Boolean = false
) : Serializable {
    companion object {

        const val UNDEFINED_ID = -1
    }
}