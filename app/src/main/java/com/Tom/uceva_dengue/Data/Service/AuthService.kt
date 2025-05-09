package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RegisterUserModel
import com.Tom.uceva_dengue.Data.Model.RethusResponse
import com.Tom.uceva_dengue.Data.Model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("Auth/login")
    suspend fun login(@Body body: LoginModel): Response<UserModel>

    @POST("Auth/register")
    suspend fun register(@Body body: RegisterUserModel): Response<String>

    @POST("Auth/Rethus")
    suspend fun consultarRethus(
        @Query("primerNombre") primerNombre: String,
        @Query("primerApellido") primerApellido: String,
        @Query("tipoIdentificacion") tipoIdentificacion: String,
        @Query("cedula") cedula: String
    ): Response<RethusResponse>

}
