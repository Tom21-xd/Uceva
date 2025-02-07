package com.Tom.uceva_dengue.Domain.Interface

import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import com.Tom.uceva_dengue.Domain.Entities.TipoDengue
import kotlinx.coroutines.flow.Flow

interface ITipoDengueRepository {
    fun getTiposDengue(): Flow<List<TipoDengue>>
    suspend fun getTipoDengueById(id: String): Publicacion?
    suspend fun addTipoDengue(TipoDengue: Publicacion): Boolean
    suspend fun updateTipoDengue(TipoDengue: Publicacion): Boolean
    suspend fun deleteTipoDengue(id: String): Boolean
}