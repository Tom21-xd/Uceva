package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RecoverPasswordRequest
import com.Tom.uceva_dengue.Data.Model.RecoverPasswordResponse
import com.Tom.uceva_dengue.Data.Model.RegisterResponse
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
    suspend fun register(@Body body: RegisterUserModel): Response<RegisterResponse>

    @POST("Auth/Rethus")
    suspend fun consultarRethus(@Body body: Map<String, String>): Response<RethusResponse>

    @POST("Auth/recoverPassword")
    suspend fun recoverPassword(@Body body: RecoverPasswordRequest): Response<RecoverPasswordResponse>

}
