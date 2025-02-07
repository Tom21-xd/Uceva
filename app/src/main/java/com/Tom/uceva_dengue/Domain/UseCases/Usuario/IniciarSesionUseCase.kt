package com.Tom.uceva_dengue.Domain.UseCases

import com.Tom.uceva_dengue.Data.Repositories.UsuarioRepository
import com.Tom.uceva_dengue.Domain.Entities.Usuario

class IniciarSesionUseCase(
    private val repository: UsuarioRepository = UsuarioRepository()
) {
    suspend fun execute(correo: String, contra: String): Result<Usuario> {
        return try {
            val usuario = repository.IniciarSesion(correo, contra)
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
