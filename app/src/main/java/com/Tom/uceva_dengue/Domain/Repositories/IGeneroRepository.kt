package com.Tom.uceva_dengue.Domain.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Genero
import kotlinx.coroutines.flow.Flow

interface IGeneroRepository {
    fun getGeneros(): Flow<List<Genero>>
}