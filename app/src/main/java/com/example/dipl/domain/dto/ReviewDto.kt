package com.example.dipl.domain.dto

import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User

data class ReviewDto(
    val rate: Int,
    val user: User,
    val dignityText: String,
    val flawsText: String,
    val commentText: String
)