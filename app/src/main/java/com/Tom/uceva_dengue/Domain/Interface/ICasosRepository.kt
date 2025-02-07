package com.Tom.uceva_dengue.Domain.Interface

import com.Tom.uceva_dengue.Domain.Entities.CasoReportado
import kotlinx.coroutines.flow.Flow

interface ICasosRepository {
    suspend fun getCasos(): Flow<List<CasoReportado>>

}