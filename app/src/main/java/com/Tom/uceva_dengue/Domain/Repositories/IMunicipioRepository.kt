package com.Tom.uceva_dengue.Domain.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Municipio
import kotlinx.coroutines.flow.Flow

interface IMunicipioRepository {
    fun getMunicipios(departamentoId: String): Flow<List<Municipio>>
}