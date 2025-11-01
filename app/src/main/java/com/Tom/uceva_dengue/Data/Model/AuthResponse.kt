package com.Tom.uceva_dengue.Data.Model

/**
 * Respuesta de autenticación con tokens
 * Corresponde a AuthResponseDto del backend
 */
data class AuthResponse(
    val user: UserModel,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int, // En segundos
    val permissions: List<String> = emptyList() // Permisos del usuario
)

/**
 * Request para renovar token
 */
data class RefreshTokenRequest(
    val refreshToken: String,
    val deviceInfo: String? = null
)

/**
 * Respuesta de renovación de token
 */
data class RefreshTokenResponse(
    val accessToken: String,
    val expiresIn: Int // En segundos
)
