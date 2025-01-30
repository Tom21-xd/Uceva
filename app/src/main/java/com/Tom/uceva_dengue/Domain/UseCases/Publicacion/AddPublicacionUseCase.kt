package com.Tom.uceva_dengue.Domain.UseCases.Publicacion

import com.Tom.uceva_dengue.Data.Repositories.FirestorePublicacionRepository
import com.Tom.uceva_dengue.Domain.Entities.Publicacion

class AddPublicacionUseCase(
    private val repository: FirestorePublicacionRepository = FirestorePublicacionRepository()
) {
    suspend fun execute(publicacion: Publicacion): Boolean = repository.addPublicacion(publicacion)
}