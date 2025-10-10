package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.PublicationModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PublicationService{
    @GET("Publication/getPublications")
    suspend fun getPublications(): List<PublicationModel>

    @GET("Publication/getPublication")
    suspend fun getPublication(@Query("nombre") nombre: String): List<PublicationModel>

    @Multipart
    @POST("/Publication/createPublication")
    suspend fun createPublication(
        @Part("Titulo") titulo: RequestBody,
        @Part("Descripcion") descripcion: RequestBody,
        @Part imagen: MultipartBody.Part,
        @Part("UsuarioId") usuarioId: RequestBody
    ): Response<String>

    // HU-007: Obtener publicación por ID
    @GET("Publication/getPublicationById/{id}")
    suspend fun getPublicationById(@Path("id") id: Int): Response<PublicationModel>

    // HU-007: Actualizar publicación
    @PUT("Publication/updatePublication/{id}")
    suspend fun updatePublication(
        @Path("id") id: Int,
        @Body publicationData: Map<String, String?>
    ): Response<Map<String, String>>

    // HU-007: Eliminar publicación
    @DELETE("Publication/deletePublication/{id}")
    suspend fun deletePublication(@Path("id") id: Int): Response<Map<String, String>>
}