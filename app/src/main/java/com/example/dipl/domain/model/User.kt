package com.example.dipl.domain.model

import java.io.Serializable

data class User(
    val id: Int,
    val name: String,
    val surname: String,
    val photoUser: String?,
    val email: String = "",
    val phone: String,
    var password: String,
    val rate: Int = 5,
    var electronicSignature: String?
) : Serializable
