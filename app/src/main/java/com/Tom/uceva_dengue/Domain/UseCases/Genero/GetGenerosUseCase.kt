package com.Tom.uceva_dengue.Domain.UseCases.Genero

import com.Tom.uceva_dengue.Data.Repositories.GeneroRepository
import com.Tom.uceva_dengue.Domain.Entities.Genero
import kotlinx.coroutines.flow.Flow

class GetGenerosUseCase(private val repository: GeneroRepository = GeneroRepository()) {
    fun execute() : Flow<List<Genero>> = repository.getGeneros()
}