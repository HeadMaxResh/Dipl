package com.example.dipl.data.api.service

import com.diplback.diplserver.dto.ContractDto
import com.example.dipl.domain.model.Contract
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContractApiService {

    @GET("/contracts/userSender/{userId}")
    fun getContractsByUserSender(@Path("userId") userId: Int): Call<List<Contract>>

    @GET("/contracts/userOwner/{userId}")
    fun getContractsByUserOwner(@Path("userId") userId: Int): Call<List<Contract>>

    @GET("/contracts/apartmentInfo/{apartmentInfoId}")
    fun getContractsByApartmentInfoId(@Path("apartmentInfoId") apartmentInfoId: Int): Call<List<Contract>>

    @GET("/contracts/usersApartments/{userSenderId}/{userOwnerId}/{apartmentInfoId}")
    fun getContractByUsersAndApartmentInfo(
        @Path("userSenderId") userSenderId: Int,
        @Path("userOwnerId") userOwnerId: Int,
        @Path("apartmentInfoId") apartmentInfoId: Int,
    ): Call<Contract>

    @POST("/contracts/create")
    fun createContract(
        @Body contractDto: ContractDto
    ): Call<Contract>

    @GET("/contracts/user/{userId}/contracts")
    fun getContractsByUserSenderOrOwner(@Path("userId") userId: Int): Call<List<Contract>>

}