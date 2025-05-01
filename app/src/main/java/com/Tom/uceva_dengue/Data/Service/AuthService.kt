package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RegisterUserModel
import com.Tom.uceva_dengue.Data.Model.UserModel
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("Auth/login")
    suspend fun login(@Body body: LoginModel): Result<UserModel>

    @POST("Auth/register")
    suspend fun register(@Body body: RegisterUserModel): String

}
