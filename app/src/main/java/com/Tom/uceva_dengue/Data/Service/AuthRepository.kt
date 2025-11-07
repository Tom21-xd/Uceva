package com.Tom.uceva_dengue.Data.Service

import android.content.Context
import android.util.Log
import com.Tom.uceva_dengue.utils.SecureTokenManager
import com.Tom.uceva_dengue.utils.UserPermissionsManager


class AuthRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val tokenManager = SecureTokenManager(context)
    private val permissionsManager = UserPermissionsManager.getInstance(context)

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

    suspend fun clearUserSession() {
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("user_role")
        editor.remove("user_display_name")
        editor.apply()

        // Limpiar tokens también
        tokenManager.clearTokens()

        // Limpiar permisos
        permissionsManager.clearPermissions()
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
        val token = tokenManager.getAccessToken()
        android.util.Log.d("AuthRepository", "Getting access token: ${token != null}")
        if (token != null) {
            android.util.Log.d("AuthRepository", "Token length: ${token.length}, Preview: ${token.take(30)}...")
        }
        return token
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

    suspend fun clearAllData() {
        clearUserSession()
        tokenManager.clearAll()
    }

    // ========== Métodos para permisos ==========

    /**
     * Guarda los permisos del usuario
     */
    suspend fun saveUserPermissions(
        userId: Int,
        roleId: Int,
        roleName: String,
        permissions: List<String>
    ) {
        Log.d("AuthRepository", "Guardando permisos - UserId: $userId, RoleId: $roleId, RoleName: $roleName")
        Log.d("AuthRepository", "Permisos a guardar: $permissions (Total: ${permissions.size})")
        permissionsManager.saveUserPermissions(userId, roleId, roleName, permissions)
        Log.d("AuthRepository", "Permisos guardados exitosamente en UserPermissionsManager")
    }

    /**
     * Obtiene los permisos del usuario
     */
    suspend fun getUserPermissions(): List<String> {
        return permissionsManager.getUserPermissions()
    }

    /**
     * Verifica si el usuario tiene un permiso específico
     */
    suspend fun hasPermission(permission: String): Boolean {
        return permissionsManager.hasPermission(permission)
    }

    /**
     * Verifica si hay permisos cargados
     */
    suspend fun hasPermissionsLoaded(): Boolean {
        return permissionsManager.hasPermissionsLoaded()
    }

    /**
     * Obtiene el permissionsManager para uso directo
     */
    fun getPermissionsManager(): UserPermissionsManager {
        return permissionsManager
    }
}
