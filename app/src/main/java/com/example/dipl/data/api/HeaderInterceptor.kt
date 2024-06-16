package com.example.dipl.data.api

import com.example.dipl.data.api.Token
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

/*
class HeaderInterceptor(private val token: String) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request().newBuilder()
            .build()

        return chain.proceed(request)
    }
}*/
class HeaderInterceptor(private val username: String, private val password: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(username, password)

        val request = chain.request().newBuilder()
            .header("Authorization", credentials)
            .build()

        return chain.proceed(request)
    }
}