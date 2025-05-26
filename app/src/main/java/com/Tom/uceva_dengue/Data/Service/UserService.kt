package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.UserModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {

    @GET("User/getUsers")
    suspend fun getUsers(): Response<List<UserModel>>

    @GET("User/getUser")
    suspend fun getUser(@Query("id") id: String): Response<UserModel>

    @GET("User/getUserLive")
    suspend fun  getUserLives(): Response<List<UserModel>>
}