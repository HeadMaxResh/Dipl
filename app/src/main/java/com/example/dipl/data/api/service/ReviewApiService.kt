package com.example.dipl.data.api.service

import com.diplback.diplserver.model.Review
import com.example.dipl.domain.dto.ReviewDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @GET("/reviews/review/{apartmentInfoId}")
    fun getApartmentReviews(@Path("apartmentInfoId") apartmentInfoId: Int): Call<List<Review>>

    /*@GET("/reviews/review/{userId}")
    fun getReviewsForApartmentOwner(@Path("userId") userId: Int): Call<List<Review>>*/

    @POST("/reviews/review/{apartmentInfoId}/add")
    fun addReviewToApartmentInfo(
        //@Path("userId") userId: Int,
        @Path("apartmentInfoId") apartmentInfoId: Int,
        @Body reviewDto: ReviewDto
    ): Call<Review>

}