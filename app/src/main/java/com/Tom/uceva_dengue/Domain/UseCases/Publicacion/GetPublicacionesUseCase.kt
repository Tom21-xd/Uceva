package com.Tom.uceva_dengue.Domain.UseCases.Publicacion

import com.Tom.uceva_dengue.Data.Repositories.PublicacionRepository
import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import kotlinx.coroutines.flow.Flow

class GetPublicacionesUseCase(
    private val repository: PublicacionRepository = PublicacionRepository()
) {
    fun execute(): Flow<List<Publicacion>> = repository.getPublicaciones()
}