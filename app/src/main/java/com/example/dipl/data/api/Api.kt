package com.example.dipl.data.api

import com.example.dipl.data.api.service.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Api {

    private const val BASE_URL = "http://192.168.0.2:8080"
    //private const val BASE_URL = "http://192.168.154.243:8080"
    private const val USERNAME = "your_username"
    private const val PASSWORD = "your_password"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT))
        .addInterceptor(HeaderInterceptor(USERNAME, PASSWORD))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    /*private val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", Credentials.basic("username", "password"))
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()*/

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()).withNullSerialization()
        )
        .build()

    val apartmentInfoApiService: ApartmentInfoApiService =
        retrofit.create(ApartmentInfoApiService::class.java)
    val chatWebSocketApiService: ChatWebSocketApiService =
        retrofit.create(ChatWebSocketApiService::class.java)
    val einApiService: EinApiService =
        retrofit.create(EinApiService::class.java)
    val inilaApiService: InilaApiService =
        retrofit.create(InilaApiService::class.java)
    val passportApiService: PassportApiService =
        retrofit.create(PassportApiService::class.java)
    val reviewApiService: ReviewApiService =
        retrofit.create(ReviewApiService::class.java)
    val userApiService: UserApiService =
        retrofit.create(UserApiService::class.java)
    val chatApiService: ChatApiService =
        retrofit.create(ChatApiService::class.java )
    val responseApartmentApiService: ResponseApartmentApiService =
        retrofit.create(ResponseApartmentApiService::class.java)
    val contractApiService: ContractApiService =
        retrofit.create(ContractApiService::class.java)
}
