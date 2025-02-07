package com.Tom.uceva_dengue.Domain.Interface

import com.Tom.uceva_dengue.Domain.Entities.Usuario

interface IUsuarioRepository {
    suspend fun crearUsuario(usuario: Usuario): Boolean
    suspend fun IniciarSesion (correo: String, contra: String) : Usuario?
}