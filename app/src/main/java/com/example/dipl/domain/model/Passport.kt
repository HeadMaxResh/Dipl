package com.diplback.diplserver.model

import com.example.dipl.domain.model.User

data class Passport(
    val id: Int = -1,
    val name: String,
    val lastname: String,
    val surname: String,
    val series: String,
    val number: String,
    val registration: String,
    val user: User
): java.io.Serializable {
    override fun toString(): String {
        return "Пасспорт серии $series № $number, зарегистрирован по адресу $registration"
    }
}