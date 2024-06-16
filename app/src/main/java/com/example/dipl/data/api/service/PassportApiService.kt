package com.example.dipl.data.api.service

import com.diplback.diplserver.model.Passport
import com.example.dipl.domain.dto.PassportDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PassportApiService {

    @GET("/passport/{userId}")
    fun getPassportByUserId(@Path("userId") userId: Int): Call<Passport>

    @POST("/passport/{userId}/add")
    fun addPassportToUser(
        @Path("userId") userId: Int,
        @Body passportDto: PassportDto
    ): Call<Passport>

}