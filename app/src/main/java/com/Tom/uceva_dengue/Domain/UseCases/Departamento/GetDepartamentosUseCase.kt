package com.Tom.uceva_dengue.Domain.UseCases.Departamento

import com.Tom.uceva_dengue.Data.Repositories.DepartmentRepository
import com.Tom.uceva_dengue.Domain.Entities.Departamento
import kotlinx.coroutines.flow.Flow

class GetDepartamentosUseCase(private val repository: DepartmentRepository = DepartmentRepository()) {
    fun execute(): Flow<List<Departamento>> = repository.getDepartamentos()
}
