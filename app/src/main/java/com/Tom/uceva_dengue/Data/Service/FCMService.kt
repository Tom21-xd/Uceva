package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.FCMTokenRequest
import com.Tom.uceva_dengue.Data.Model.FCMTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface FCMService {
    @POST("FCM/saveToken")
    suspend fun saveToken(@Body request: FCMTokenRequest): Response<FCMTokenResponse>

    @DELETE("FCM/deleteToken/{userId}")
    suspend fun deleteToken(@Path("userId") userId: Int): Response<FCMTokenResponse>
}
