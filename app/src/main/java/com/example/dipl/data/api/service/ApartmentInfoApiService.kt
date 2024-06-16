package com.example.dipl.data.api.service

import com.diplback.diplserver.model.Review
import com.example.dipl.domain.dto.ApartmentInfoDto
import com.example.dipl.domain.dto.ReviewDto
import com.example.dipl.domain.model.ApartmentInfo
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApartmentInfoApiService {

    @GET("/apartments/all")
    fun getAllApartments(): Call<List<ApartmentInfo>>

    @GET("/apartments/search/{name}")
    fun getApartmentsByName(@Path("name") name: String): Call<List<ApartmentInfo>>

    @GET("/apartments/filter/{city}/{minRent}/{maxRent}/{minArea}/{maxArea}/{countRooms}")
    fun getApartmentsByFilter(
        @Path("city") city: String,
        @Path("minRent") minRent: Int,
        @Path("maxRent") maxRent: Int,
        @Path("minArea") minArea: Float,
        @Path("maxArea") maxArea: Float,
        @Path("countRooms") countRooms: Int
    ): Call<List<ApartmentInfo>>

    @GET("/apartments/{id}")
    fun getApartmentById(@Path("id") id: Int): Call<ApartmentInfo>

    @GET("/apartments/userapartments/{userOwnerId}")
    fun getApartmentsByUser(@Path("userOwnerId") userOwnerId: Int): Call<List<ApartmentInfo>>

    @POST("/apartments/add")
    fun addApartment(
        //@Path("userId") userId: Int,
        @Body apartmentInfoDto: ApartmentInfoDto
    ): Call<ApartmentInfo>


    @POST("/apartments/{apartmentId}/review/add")
    fun addReviewToApartment(
        @Path("apartmentId") apartmentId: Int,
        @Body reviewDto: ReviewDto
    ): Call<Review>

    @GET("/apartments/{apartmentId}/reviews")
    fun getApartmentReviews(@Path("apartmentInfoId") apartmentInfoId: Int): Call<List<Review>>


}