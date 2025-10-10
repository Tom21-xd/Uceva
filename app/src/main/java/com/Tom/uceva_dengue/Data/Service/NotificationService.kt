package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.NotificationModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationService {
    @GET("Notification/getNotifications")
    suspend fun getNotifications(): Response<List<NotificationModel>>

    // Obtener solo notificaciones no leídas
    @GET("Notification/getUnread")
    suspend fun getUnreadNotifications(): Response<List<NotificationModel>>

    // Marcar una notificación como leída
    @PUT("Notification/markAsRead/{id}")
    suspend fun markAsRead(@Path("id") id: Int): Response<Map<String, String>>

    // Marcar todas las notificaciones como leídas
    @PUT("Notification/markAllAsRead")
    suspend fun markAllAsRead(): Response<Map<String, String>>
}