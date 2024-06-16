package com.diplback.diplserver.model

import com.example.dipl.domain.model.User

data class Ein(
    val id: Int = -1,
    val einNumber: Long,
    val user: User
)