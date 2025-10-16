package com.Tom.uceva_dengue.Data.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.FCMTokenRequest
import com.Tom.uceva_dengue.MainActivity
import com.Tom.uceva_dengue.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM_Service"
        private const val CHANNEL_ID = "dengue_notifications"
        private const val CHANNEL_NAME = "Notificaciones Dengue"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo FCM Token: $token")

        // Guardar el token localmente
        saveFCMToken(token)

        // Enviar el token al servidor (si el usuario está logueado)
        sendTokenToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Mensaje recibido desde: ${message.from}")

        // Verificar si el mensaje contiene notificación
        message.notification?.let {
            Log.d(TAG, "Título: ${it.title}")
            Log.d(TAG, "Cuerpo: ${it.body}")
            showNotification(it.title ?: "Notificación", it.body ?: "")
        }

        // Verificar si el mensaje contiene datos
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Datos del mensaje: ${message.data}")
            handleDataPayload(message.data)
        }
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de casos de dengue"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Crear la notificación
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Manejar datos personalizados del mensaje
        val title = data["title"] ?: "Nueva Notificación"
        val body = data["body"] ?: ""
        val type = data["type"] // Ej: "new_case", "case_updated", etc.

        showNotification(title, body)

        // Aquí puedes agregar lógica específica según el tipo de notificación
        when (type) {
            "new_case" -> {
                // Lógica para nuevo caso
                Log.d(TAG, "Nuevo caso reportado")
            }
            "case_updated" -> {
                // Lógica para caso actualizado
                Log.d(TAG, "Caso actualizado")
            }
        }
    }

    private fun saveFCMToken(token: String) {
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("fcm_token", token).apply()
        Log.d(TAG, "Token guardado localmente")
    }

    private fun sendTokenToServer(token: String) {
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("username", null)

        if (userIdString != null) {
            try {
                val userId = userIdString.toIntOrNull()
                if (userId != null) {
                    Log.d(TAG, "Enviando token al servidor para usuario: $userId")

                    // Use coroutine to make async call
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val request = FCMTokenRequest(userId = userId, fcmToken = token)
                            val response = RetrofitClient.fcmService.saveToken(request)

                            if (response.isSuccessful) {
                                Log.d(TAG, "Token FCM enviado exitosamente al servidor")
                            } else {
                                Log.e(TAG, "Error al enviar token al servidor: ${response.errorBody()?.string()}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Excepción al enviar token al servidor: ${e.message}", e)
                        }
                    }
                } else {
                    Log.e(TAG, "ID de usuario no válido: $userIdString")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al procesar ID de usuario: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "Usuario no logueado, token no enviado al servidor")
        }
    }
}
