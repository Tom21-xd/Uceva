package com.Tom.uceva_dengue.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manager para almacenar tokens de forma segura usando EncryptedSharedPreferences
 * Los tokens se encriptan automáticamente antes de guardarlos
 */
class SecureTokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda el access token de forma segura
     */
    fun saveAccessToken(token: String) {
        encryptedPrefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    /**
     * Obtiene el access token guardado
     */
    fun getAccessToken(): String? {
        return encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Guarda el refresh token de forma segura
     */
    fun saveRefreshToken(token: String) {
        encryptedPrefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    /**
     * Obtiene el refresh token guardado
     */
    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Guarda la fecha de expiración del access token
     */
    fun saveTokenExpiration(expirationTime: Long) {
        encryptedPrefs.edit().putLong(KEY_TOKEN_EXPIRATION, expirationTime).apply()
    }

    /**
     * Obtiene la fecha de expiración del access token
     */
    fun getTokenExpiration(): Long {
        return encryptedPrefs.getLong(KEY_TOKEN_EXPIRATION, 0)
    }

    /**
     * Verifica si el access token ha expirado
     */
    fun isAccessTokenExpired(): Boolean {
        val expiration = getTokenExpiration()
        return expiration > 0 && System.currentTimeMillis() >= expiration
    }

    /**
     * Limpia todos los tokens (logout)
     */
    fun clearTokens() {
        encryptedPrefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRATION)
            .apply()
    }

    /**
     * Limpia todos los datos incluyendo preferencias de biometría
     */
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
    }
}
