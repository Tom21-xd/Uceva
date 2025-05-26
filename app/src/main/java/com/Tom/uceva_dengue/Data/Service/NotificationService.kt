package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.NotificationModel
import retrofit2.Response
import retrofit2.http.GET

interface NotificationService {
    @GET("Notification/getNotifications")
    suspend fun getNotifications(): Response<List<NotificationModel>>

}