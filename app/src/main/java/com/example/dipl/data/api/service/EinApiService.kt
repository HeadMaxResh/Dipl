package com.example.dipl.data.api.service

import com.diplback.diplserver.model.Ein
import com.example.dipl.domain.dto.EinDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EinApiService {

    @GET("/ein/{userId}")
    fun getEinByUserId(@Path("userId") userId: Int): Call<Ein>

    @POST("/ein/{userId}/add")
    fun addEinToUser(
        @Path("userId") userId: Int,
        @Body einDto: EinDto
    ): Call<Ein>
}