package com.Tom.uceva_dengue.Domain.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import kotlinx.coroutines.flow.Flow

interface IPublicacionRepository {
    fun getPublicaciones(): Flow<List<Publicacion>>
    suspend fun getPublicacionById(id: String): Publicacion?
    suspend fun addPublicacion(publicacion: Publicacion): Boolean
    suspend fun updatePublicacion(publicacion: Publicacion): Boolean
    suspend fun deletePublicacion(id: String): Boolean
}