package com.example.dipl.data.api.service

import com.example.dipl.domain.dto.MessageDto
import com.example.dipl.domain.model.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {

    @POST("/chat/send")
    fun sendMessage(@Body messageDto: MessageDto): Call<Message>

    @GET("/chat/history/{userId}")
    fun getChatHistoryUserIds(@Path("userId") userId: Int): Call<List<Int>>

    @GET("/chat/history/{userId}/chat/{otherUserId}")
    fun getChatHistory(@Path("userId") userId: Int, @Path("otherUserId")  otherUserId: Int): Call<List<Message>>
}