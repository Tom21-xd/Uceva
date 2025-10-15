package com.Tom.uceva_dengue.Data.Service

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object FCMTokenManager {
    private const val TAG = "FCMTokenManager"
    private const val PREFS_NAME = "user_preferences"
    private const val KEY_FCM_TOKEN = "fcm_token"

    /**
     * Obtiene el token FCM actual del dispositivo
     */
    suspend fun getFCMToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "Token FCM obtenido: $token")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener token FCM", e)
            null
        }
    }

    /**
     * Guarda el token FCM localmente en SharedPreferences
     */
    fun saveTokenLocally(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d(TAG, "Token guardado localmente")
    }

    /**
     * Obtiene el token FCM guardado localmente
     */
    fun getLocalToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_FCM_TOKEN, null)
    }

    /**
     * Borra el token FCM guardado localmente
     */
    fun clearLocalToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_FCM_TOKEN).apply()
        Log.d(TAG, "Token local eliminado")
    }
}
