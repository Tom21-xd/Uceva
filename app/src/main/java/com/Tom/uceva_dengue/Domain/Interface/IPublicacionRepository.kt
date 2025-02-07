package com.Tom.uceva_dengue.Domain.Interface

import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import kotlinx.coroutines.flow.Flow

interface IPublicacionRepository {
    fun getPublicaciones(): Flow<List<Publicacion>>

}