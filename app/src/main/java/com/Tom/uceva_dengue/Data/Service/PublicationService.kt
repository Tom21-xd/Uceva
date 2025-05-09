package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.PublicationModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PublicationService{
    @GET("Publication/getPublications")
    suspend fun getPublications(): List<PublicationModel>

    @GET("Publication/getPublication")
    suspend fun getPublication(@Query("nombre") nombre: String): List<PublicationModel>

    @Multipart
    @POST("Publication/createPublication")
    suspend fun createPublication(
        @Part("Titulo") titulo: RequestBody,
        @Part("Descripcion") descripcion: RequestBody,
        @Part imagen: MultipartBody.Part,
        @Part("UsuarioId") usuarioId: RequestBody
    ): PublicationModel

}