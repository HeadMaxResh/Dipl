package com.example.dipl.domain.model

import java.time.LocalDateTime

data class Message(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    //val timestamp: LocalDateTime = LocalDateTime.now()
)