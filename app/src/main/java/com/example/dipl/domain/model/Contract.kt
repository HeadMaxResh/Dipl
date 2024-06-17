package com.example.dipl.domain.model

import com.diplback.diplserver.model.Passport

data class Contract(
    val id: Int = -1,
    val apartmentInfo: ApartmentInfo,
    val userOwner: User,
    val passportOwner: Passport,
    val userSender: User,
    val passportSender: Passport,
    val date: String,
    val ownerElectronicSignature: String,
    val senderElectronicSignature: String
): java.io.Serializable