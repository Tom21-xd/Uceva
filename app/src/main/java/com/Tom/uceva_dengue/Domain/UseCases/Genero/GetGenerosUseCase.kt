package com.Tom.uceva_dengue.Domain.UseCases.Genero

import com.Tom.uceva_dengue.Data.Repositories.FirestoreGeneroRepository
import com.Tom.uceva_dengue.Domain.Entities.Genero
import kotlinx.coroutines.flow.Flow

class GetGenerosUseCase(private val repository: FirestoreGeneroRepository = FirestoreGeneroRepository()) {
    fun execute() : Flow<List<Genero>> = repository.getGeneros()
}