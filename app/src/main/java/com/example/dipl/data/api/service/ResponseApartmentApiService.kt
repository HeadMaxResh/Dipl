package com.example.dipl.data.api.service

import com.example.dipl.domain.model.ResponseApartment
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT

interface ResponseApartmentApiService {

    @POST("/responses/add/{apartmentId}/user/{userId}")
    fun addResponseToApartment(
        @Path("apartmentId") apartmentId: Int,
        @Path("userId") userId: Int
    ) : Call<ResponseApartment>

    @GET("/responses/owner/{userId}")
    fun getResponsesForApartmentOwner(@Path("userId") userId: Int): Call<List<ResponseApartment>>

    @GET("/responses/user/{userId}")
    fun getSentResponsesForUser(@Path("userId") userId: Int): Call<List<ResponseApartment>>

    @PUT("/responses/{responseId}/status/{status}")
    fun updateResponseStatus(
        @Path("responseId") responseId: Int,
        @Path("status") status: String
    ): Call<ResponseApartment>

    @GET("/responses/{responseId}/status")
    fun getResponseStatus(@Path("responseId") responseId: Int): Call<String>

    @GET("/responses/status/{status}")
    fun getResponsesByStatus(@Path("status") status: String): Call<List<ResponseApartment>>

}