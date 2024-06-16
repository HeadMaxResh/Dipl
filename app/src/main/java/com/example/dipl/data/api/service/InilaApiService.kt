package com.example.dipl.data.api.service

import com.diplback.diplserver.model.Inila
import com.example.dipl.domain.dto.InilaDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InilaApiService {

    @GET("/inila/{userId}")
    fun getInilaByUserId(@Path("userId") userId: Int): Call<Inila>

    @POST("/inila/{userId}/add")
    fun addInilaToUser(
        @Path("userId") userId: Int,
        @Body inilaDto: InilaDto
    ): Call<Inila>
}