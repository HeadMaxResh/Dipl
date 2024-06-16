package com.diplback.diplserver.model

import com.example.dipl.domain.model.User

data class Inila(
    val id: Int = -1,
    val inilaNumber: Int,
    val user: User
)