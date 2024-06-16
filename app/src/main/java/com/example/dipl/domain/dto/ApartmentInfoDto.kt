package com.example.dipl.domain.dto

import com.example.dipl.domain.model.User

data class ApartmentInfoDto(
    val name: String,
    val city: String,
    val rent: Int,
    val rate: Double,
    val area: Float,
    val listImages: List<String>,
    val countRooms: Int,
    val userOwner: User,
    val description: String,
)