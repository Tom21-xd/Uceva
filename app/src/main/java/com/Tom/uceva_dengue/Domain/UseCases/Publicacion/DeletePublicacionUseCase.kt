package com.Tom.uceva_dengue.Domain.UseCases.Publicacion

import com.Tom.uceva_dengue.Data.Repositories.FirestorePublicacionRepository

class DeletePublicacionUseCase(
    private val repository: FirestorePublicacionRepository = FirestorePublicacionRepository()
) {
    suspend fun execute(id: String): Boolean = repository.deletePublicacion(id)
}