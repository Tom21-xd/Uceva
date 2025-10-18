package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PublicationService{
    // ===== ENDPOINTS BÁSICOS =====

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
    ): Response<String>

    @GET("Publication/getPublicationById/{id}")
    suspend fun getPublicationById(@Path("id") id: Int): Response<PublicationModel>

    @PUT("Publication/updatePublication/{id}")
    suspend fun updatePublication(
        @Path("id") id: Int,
        @Body publicationData: Map<String, String?>
    ): Response<Map<String, String>>

    @DELETE("Publication/deletePublication/{id}")
    suspend fun deletePublication(@Path("id") id: Int): Response<Map<String, String>>

    // ===== ENDPOINTS NUEVOS - FEED Y FILTROS =====

    /**
     * Obtener feed ordenado por prioridad (Urgente > Alta > Normal > Baja)
     * Publicaciones fijadas aparecen primero
     */
    @GET("Publication/feed")
    suspend fun getFeed(
        @Query("ciudadId") ciudadId: Int? = null,
        @Query("categoriaId") categoriaId: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0
    ): Response<List<PublicationModel>>

    /**
     * Obtener publicaciones por categoría
     */
    @GET("Publication/category/{categoryId}")
    suspend fun getPublicationsByCategory(@Path("categoryId") categoryId: Int): Response<List<PublicationModel>>

    /**
     * Obtener solo publicaciones urgentes/alertas
     */
    @GET("Publication/urgent")
    suspend fun getUrgentPublications(): Response<List<PublicationModel>>

    /**
     * Obtener publicaciones fijadas
     */
    @GET("Publication/pinned")
    suspend fun getPinnedPublications(): Response<List<PublicationModel>>

    /**
     * Obtener publicaciones cercanas a una ubicación
     */
    @GET("Publication/nearby")
    suspend fun getNearbyPublications(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radiusKm: Double = 10.0
    ): Response<List<PublicationModel>>

    /**
     * Obtener publicaciones por etiqueta/tag
     */
    @GET("Publication/tag/{tagId}")
    suspend fun getPublicationsByTag(@Path("tagId") tagId: Int): Response<List<PublicationModel>>

    /**
     * Buscar publicaciones
     */
    @GET("Publication/search")
    suspend fun searchPublications(
        @Query("query") query: String,
        @Query("categoriaId") categoriaId: Int? = null
    ): Response<List<PublicationModel>>

    // ===== REACCIONES =====

    /**
     * Dar reacción a una publicación (like, importante, útil)
     */
    @POST("Publication/{id}/react")
    suspend fun reactToPublication(
        @Path("id") publicationId: Int,
        @Body reaction: CreateReactionRequest
    ): Response<Map<String, Any>>

    /**
     * Quitar reacción de una publicación
     */
    @DELETE("Publication/{id}/react")
    suspend fun removeReaction(@Path("id") publicationId: Int): Response<Map<String, String>>

    /**
     * Obtener todas las reacciones de una publicación
     */
    @GET("Publication/{id}/reactions")
    suspend fun getPublicationReactions(@Path("id") publicationId: Int): Response<List<PublicationReactionModel>>

    /**
     * Obtener contador de reacciones
     */
    @GET("Publication/{id}/reactions/count")
    suspend fun getReactionsCount(@Path("id") publicationId: Int): Response<Map<String, Int>>

    // ===== COMENTARIOS =====

    /**
     * Agregar comentario a una publicación
     */
    @POST("Publication/{id}/comment")
    suspend fun addComment(
        @Path("id") publicationId: Int,
        @Body comment: CreateCommentRequest
    ): Response<PublicationCommentModel>

    /**
     * Responder a un comentario
     */
    @POST("Comment/{id}/reply")
    suspend fun replyToComment(
        @Path("id") commentId: Int,
        @Body reply: CreateCommentRequest
    ): Response<PublicationCommentModel>

    /**
     * Obtener comentarios de una publicación
     */
    @GET("Publication/{id}/comments")
    suspend fun getPublicationComments(@Path("id") publicationId: Int): Response<List<PublicationCommentModel>>

    /**
     * Eliminar comentario
     */
    @DELETE("Comment/{id}")
    suspend fun deleteComment(@Path("id") commentId: Int): Response<Map<String, String>>

    // ===== GUARDADOS =====

    /**
     * Guardar/marcar como favorita una publicación
     */
    @POST("Publication/{id}/save")
    suspend fun savePublication(@Path("id") publicationId: Int): Response<Map<String, String>>

    /**
     * Quitar de guardados
     */
    @DELETE("Publication/{id}/save")
    suspend fun unsavePublication(@Path("id") publicationId: Int): Response<Map<String, String>>

    /**
     * Obtener publicaciones guardadas del usuario
     */
    @GET("User/{userId}/saved")
    suspend fun getSavedPublications(@Path("userId") userId: Int): Response<List<SavedPublicationModel>>

    /**
     * Contador de cuántos usuarios guardaron la publicación
     */
    @GET("Publication/{id}/saves/count")
    suspend fun getSavesCount(@Path("id") publicationId: Int): Response<Map<String, Int>>

    // ===== ESTADÍSTICAS =====

    /**
     * Registrar vista/lectura de publicación
     */
    @POST("Publication/{id}/view")
    suspend fun registerView(
        @Path("id") publicationId: Int,
        @Body viewData: RegisterViewRequest
    ): Response<Map<String, String>>

    /**
     * Obtener estadísticas completas de una publicación
     */
    @GET("Publication/{id}/stats")
    suspend fun getPublicationStats(@Path("id") publicationId: Int): Response<PublicationStatsModel>

    /**
     * Obtener publicaciones más populares (trending)
     */
    @GET("Publication/trending")
    suspend fun getTrendingPublications(
        @Query("limit") limit: Int = 10,
        @Query("days") days: Int = 7
    ): Response<List<PublicationModel>>
}