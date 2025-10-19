package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.RoleModel
import retrofit2.Response
import retrofit2.http.*

interface RoleService {
    @GET("Role/getRoles")
    suspend fun getRoles(): Response<List<RoleModel>>

    @GET("Role/{id}")
    suspend fun getRoleById(@Path("id") id: Int): Response<RoleModel>

    @POST("Role")
    suspend fun createRole(@Body role: RoleModel): Response<RoleModel>

    @PUT("Role/{id}")
    suspend fun updateRole(@Path("id") id: Int, @Body role: RoleModel): Response<Any>

    @DELETE("Role/{id}")
    suspend fun deleteRole(@Path("id") id: Int): Response<Any>
}
