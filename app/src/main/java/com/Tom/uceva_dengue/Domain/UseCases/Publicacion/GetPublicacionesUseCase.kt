package com.Tom.uceva_dengue.Domain.UseCases.Publicacion

import com.Tom.uceva_dengue.Data.Repositories.FirestorePublicacionRepository
import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import kotlinx.coroutines.flow.Flow

class GetPublicacionesUseCase(
    private val repository: FirestorePublicacionRepository = FirestorePublicacionRepository()
) {
    fun execute(): Flow<List<Publicacion>> = repository.getPublicaciones()
}