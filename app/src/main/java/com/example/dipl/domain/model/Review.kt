package com.diplback.diplserver.model

import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import java.time.LocalDateTime
import java.time.Month

data class Review(
    val id: Int = -1,
    //val date: LocalDateTime,
    val rate: Int,
    val user: User,
    //val apartmentInfo: ApartmentInfo,
    val dignityText: String,
    val flawsText: String,
    val commentText: String
)