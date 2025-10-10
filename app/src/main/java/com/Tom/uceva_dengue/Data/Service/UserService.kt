package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.UserModel
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @GET("User/getUsers")
    suspend fun getUsers(): Response<List<UserModel>>

    @GET("User/getUser")
    suspend fun getUser(@Query("id") id: String): Response<UserModel>

    @GET("User/getUserLive")
    suspend fun getUserLives(): Response<List<UserModel>>

    // HU-004: Actualizar perfil propio
    @PUT("User/updateProfile/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Body userData: Map<String, Any?>
    ): Response<Map<String, String>>

    // HU-005: Actualizar usuario (Admin)
    @PUT("User/updateUser/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body userData: Map<String, Any?>
    ): Response<Map<String, String>>

    // HU-005: Eliminar usuario (Admin)
    @DELETE("User/deleteUser/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Map<String, String>>

    // HU-005: Buscar usuarios
    @GET("User/searchUsers")
    suspend fun searchUsers(
        @Query("filter") filter: String?,
        @Query("roleId") roleId: Int?
    ): Response<List<UserModel>>
}