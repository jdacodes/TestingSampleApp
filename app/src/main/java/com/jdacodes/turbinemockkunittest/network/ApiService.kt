package com.jdacodes.turbinemockkunittest.network

import android.accounts.Account
import com.jdacodes.turbinemockkunittest.network.response.AuthLoginResponse
import com.jdacodes.turbinemockkunittest.network.response.Posts
import com.jdacodes.turbinemockkunittest.network.response.Users
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("/auth/login")
    suspend fun login(): Response<AuthLoginResponse>

    @GET("login")
    suspend fun login(@Header("Authorization") credentials: String?): Account

    @GET("posts")
    suspend fun posts(): List<Posts>

    @GET("users")
    suspend fun users(): List<Users>
}