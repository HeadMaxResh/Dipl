package com.diplback.diplserver.dto

import java.time.LocalDate

data class ContractDto(
    val apartmentInfoId: Int,
    val userOwnerId: Int,
    val userSenderId: Int,
    val date: String?
)