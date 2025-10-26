package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.AuthResponse
import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RecoverPasswordRequest
import com.Tom.uceva_dengue.Data.Model.RecoverPasswordResponse
import com.Tom.uceva_dengue.Data.Model.RefreshTokenRequest
import com.Tom.uceva_dengue.Data.Model.RefreshTokenResponse
import com.Tom.uceva_dengue.Data.Model.RegisterResponse
import com.Tom.uceva_dengue.Data.Model.RegisterUserModel
import com.Tom.uceva_dengue.Data.Model.RethusResponse
import com.Tom.uceva_dengue.Data.Model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    /**
     * Login con email y contraseña
     * Retorna usuario + access token + refresh token
     */
    @POST("Auth/login")
    suspend fun login(@Body body: LoginModel): Response<AuthResponse>

    /**
     * Renueva el access token usando el refresh token
     */
    @POST("Auth/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("Auth/register")
    suspend fun register(@Body body: RegisterUserModel): Response<RegisterResponse>

    @POST("Auth/Rethus")
    suspend fun consultarRethus(@Body body: Map<String, String>): Response<RethusResponse>

    @POST("Auth/recoverPassword")
    suspend fun recoverPassword(@Body body: RecoverPasswordRequest): Response<RecoverPasswordResponse>

}
