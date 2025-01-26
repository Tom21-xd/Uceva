package com.Tom.uceva_dengue.Domain.UseCases.Municipio

import com.Tom.uceva_dengue.Data.Repositories.FirestoreMunicipioRepository
import com.Tom.uceva_dengue.Domain.Entities.Municipio
import kotlinx.coroutines.flow.Flow

class GetMunicipiosUseCase(private val repository: FirestoreMunicipioRepository = FirestoreMunicipioRepository()) {
    fun execute(departamentoId: String): Flow<List<Municipio>> {
        return repository.getMunicipios(departamentoId)
    }
}