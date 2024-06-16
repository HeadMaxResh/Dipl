package com.example.dipl.domain.model

import com.example.dipl.domain.model.ApartmentInfo.Companion.UNDEFINED_ID

data class Chat(
    val id: Int = UNDEFINED_ID,
    val senderId: String,
    val receiverId: String,
    val content: String?
) {
}