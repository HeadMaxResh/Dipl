package com.example.dipl.data.api.service

import com.example.dipl.domain.request.CalculateFromAnalysisRequest
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApartmentAnalysisApiService {

    @POST("api/apartments/calculate-from-analysis")
    fun calculateFromAnalysis(
        @Body request: RequestBody
    ): Call<ResponseBody>

    @Multipart
    @POST("api/apartments/analyze-photos")
    fun analyzePhotos(
        @Part photos: List<MultipartBody.Part>
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/apartments/analyze-location")
    fun analyzeLocation(
        @Field("address") address: String,
        @Field("rooms") rooms: Int,
        @Field("area") area: Double
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/apartments/analyze-text")
    fun analyzeText(
        @Field("description") description: String
    ): Call<ResponseBody>

    @Multipart
    @POST("api/apartments/evaluate")
    fun evaluateApartment(
        @Part("description") description: RequestBody,
        @Part("address") address: RequestBody,
        @Part("rooms") rooms: RequestBody,
        @Part("area") area: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): Call<ResponseBody>

}