package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.PublicationCategoryModel
import retrofit2.Response
import retrofit2.http.*

interface PublicationCategoryService {
    /**
     * Obtener todas las categorías
     */
    @GET("PublicationCategory")
    suspend fun getAllCategories(): Response<List<PublicationCategoryModel>>

    /**
     * Obtener categoría por ID
     */
    @GET("PublicationCategory/{id}")
    suspend fun getCategoryById(@Path("id") categoryId: Int): Response<PublicationCategoryModel>

    /**
     * Crear nueva categoría (solo admin)
     */
    @POST("PublicationCategory")
    suspend fun createCategory(@Body category: Map<String, Any>): Response<PublicationCategoryModel>

    /**
     * Actualizar categoría
     */
    @PUT("PublicationCategory/{id}")
    suspend fun updateCategory(
        @Path("id") categoryId: Int,
        @Body category: Map<String, Any>
    ): Response<PublicationCategoryModel>

    /**
     * Eliminar categoría
     */
    @DELETE("PublicationCategory/{id}")
    suspend fun deleteCategory(@Path("id") categoryId: Int): Response<Map<String, String>>
}
