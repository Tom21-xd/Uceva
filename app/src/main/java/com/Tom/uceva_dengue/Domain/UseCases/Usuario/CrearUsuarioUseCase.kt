package com.Tom.uceva_dengue.Domain.UseCases.Usuario

import com.Tom.uceva_dengue.Data.Repositories.UsuarioRepository
import com.Tom.uceva_dengue.Domain.Entities.Usuario

class CrearUsuarioUseCase(private val repository: UsuarioRepository = UsuarioRepository()) {
    suspend fun execute(usuario: Usuario): Result<Boolean> {
        return try {
            val success = repository.crearUsuario(usuario)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception(""))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
