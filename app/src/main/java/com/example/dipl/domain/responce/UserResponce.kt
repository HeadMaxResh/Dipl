package com.example.dipl.domain.responce

import com.example.dipl.domain.model.User

data class UserResponse(
    val success: Boolean,
    val message: String?,
    val user: User?
)