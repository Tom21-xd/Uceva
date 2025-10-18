package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.PublicationTagModel
import retrofit2.Response
import retrofit2.http.*

interface PublicationTagService {
    /**
     * Obtener todas las etiquetas/tags disponibles
     */
    @GET("PublicationTag")
    suspend fun getAllTags(): Response<List<PublicationTagModel>>

    /**
     * Obtener etiqueta por ID
     */
    @GET("PublicationTag/{id}")
    suspend fun getTagById(@Path("id") tagId: Int): Response<PublicationTagModel>

    /**
     * Crear nueva etiqueta (solo admin)
     */
    @POST("PublicationTag")
    suspend fun createTag(@Body tag: Map<String, String>): Response<PublicationTagModel>

    /**
     * Agregar etiqueta a una publicación
     */
    @POST("Publication/{publicationId}/tag/{tagId}")
    suspend fun addTagToPublication(
        @Path("publicationId") publicationId: Int,
        @Path("tagId") tagId: Int
    ): Response<Map<String, String>>

    /**
     * Quitar etiqueta de una publicación
     */
    @DELETE("Publication/{publicationId}/tag/{tagId}")
    suspend fun removeTagFromPublication(
        @Path("publicationId") publicationId: Int,
        @Path("tagId") tagId: Int
    ): Response<Map<String, String>>

    /**
     * Obtener todas las etiquetas de una publicación
     */
    @GET("Publication/{id}/tags")
    suspend fun getPublicationTags(@Path("id") publicationId: Int): Response<List<PublicationTagModel>>

    /**
     * Buscar etiquetas por nombre
     */
    @GET("PublicationTag/search")
    suspend fun searchTags(@Query("query") query: String): Response<List<PublicationTagModel>>
}
