package com.example.dipl.data.api.service

import com.example.dipl.domain.model.Chat
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatWebSocketApiService {

    @POST("/app/chat/{senderIDString}/{receiverIDString}")
    fun sendChatMessage(
        @Path("senderIDString") senderIDString: String,
        @Path("receiverIDString") receiverIDString: String,
        @Body content: String
    ): Call<List<Chat?>>

    @GET("/app/chat/{senderIDString}/{receiverIDString}")
    fun getChatMessages(
        @Path("senderIDString") senderIDString: String,
        @Path("receiverIDString") receiverIDString: String
    ): Call<List<Chat?>>

    @GET("/app/chat/{senderIDString}/{receiverIDString}/listen")
    fun subscribeToChatMessages(
        @Path("senderIDString") senderIDString: String,
        @Path("receiverIDString") receiverIDString: String
    ): Call<List<Chat?>>

    @GET("/app/hello/{receiverIDString}")
    fun getChatWithReceiver(
        @Path("receiverIDString") receiverIDString: String
    ): Call<List<Chat?>>
}