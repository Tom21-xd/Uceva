package com.Tom.uceva_dengue.Data.Service

import android.content.Context
import com.Tom.uceva_dengue.utils.SecureTokenManager


class AuthRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val tokenManager = SecureTokenManager(context)

    // ========== Métodos existentes ==========

    fun saveUserAndRole(username: String, roleId: Int, displayName: String? = null) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putInt("user_role", roleId)
        if (displayName != null) {
            editor.putString("user_display_name", displayName)
        }
        editor.apply()
    }

    fun getUser(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getUserDisplayName(): String? {
        return sharedPreferences.getString("user_display_name", null)
    }

    fun getRole(): Int {
        return sharedPreferences.getInt("user_role", 0)
    }

    fun clearUserSession() {
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("user_role")
        editor.remove("user_display_name")
        editor.apply()

        // Limpiar tokens también
        tokenManager.clearTokens()
    }

    // ========== Nuevos métodos para tokens ==========

    /**
     * Guarda los tokens de autenticación de forma segura
     */
    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Int) {
        tokenManager.saveAccessToken(accessToken)
        tokenManager.saveRefreshToken(refreshToken)

        // Calcular tiempo de expiración (expiresIn está en segundos)
        val expirationTime = System.currentTimeMillis() + (expiresIn * 1000L)
        tokenManager.saveTokenExpiration(expirationTime)
    }

    /**
     * Obtiene el access token guardado
     */
    fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    /**
     * Obtiene el refresh token guardado
     */
    fun getRefreshToken(): String? {
        return tokenManager.getRefreshToken()
    }

    /**
     * Verifica si el access token ha expirado
     */
    fun isAccessTokenExpired(): Boolean {
        return tokenManager.isAccessTokenExpired()
    }

    /**
     * Actualiza el access token (después de usar refresh)
     */
    fun updateAccessToken(accessToken: String, expiresIn: Int) {
        tokenManager.saveAccessToken(accessToken)
        val expirationTime = System.currentTimeMillis() + (expiresIn * 1000L)
        tokenManager.saveTokenExpiration(expirationTime)
    }

    fun clearAllData() {
        clearUserSession()
        tokenManager.clearAll()
    }
}
