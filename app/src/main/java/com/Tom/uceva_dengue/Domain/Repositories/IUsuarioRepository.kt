package com.Tom.uceva_dengue.Domain.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Usuario

interface IUsuarioRepository {
    suspend fun crearUsuario(usuario: Usuario): Boolean
}