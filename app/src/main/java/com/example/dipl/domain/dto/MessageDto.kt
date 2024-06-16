package com.example.dipl.domain.dto

data class MessageDto(
    val senderId: Int,
    val receiverId: Int,
    val content: String
)