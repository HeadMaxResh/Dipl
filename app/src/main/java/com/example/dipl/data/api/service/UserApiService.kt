package com.example.dipl.data.api.service

import com.example.dipl.domain.dto.UpdatedUserDto
import com.example.dipl.domain.dto.UserDto
import com.example.dipl.domain.model.ApartmentInfo
import com.example.dipl.domain.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @GET("/users")
    suspend fun getAllUsers(): List<User>

    @POST("/login")
    suspend fun loginUser(@Body userDto: UserDto): Response<User>

    @POST("/register")
    suspend fun registerUser(@Body userDto: UserDto): Response<Boolean>

    @GET("/user/{userId}")
    fun getUserById(@Path("userId") userId: Int): Call<User>

    @POST("/apartments/favorite/{apartmentId}/user/{userId}")
    fun addFavoriteApartment(
        @Path("userId") userId: Int,
        @Path("apartmentId") apartmentId: Int,
    ): Call<Boolean>

    @POST("/apartments/favorite/{apartmentId}/user/{userId}/remove")
    fun removeFavoriteApartment(
        @Path("userId") userId: Int,
        @Path("apartmentId") apartmentId: Int
    ): Call<Boolean>

    @POST("/user/{userId}/edit")
    fun updateUser(
        @Path("userId") userId: Int,
        @Body updatedUserDto: UpdatedUserDto
    ): Call<User>

    @GET("/apartments/favorite/user/{userId}")
    fun getFavoriteApartments(
        @Path("userId") userId: Int
    ): Call<List<ApartmentInfo>>

    @GET("/apartments/favorite/{apartmentId}/user/{userId}/check")
    fun checkIfApartmentIsFavorite(
        @Path("userId") userId: Int,
        @Path("apartmentId") apartmentId: Int
    ): Call<Boolean>
}