package com.example.dipl.domain.model

data class ResponseApartment(
    val id: Int = -1,
    val apartmentInfo: ApartmentInfo,
    val user: User,
    var status: String = PENDING
) {
    companion object {
        const val PENDING = "В ожидании"
        const val REJECTED = "Одобрено"
        const val APPROVED = "Отклонено"

    }
}