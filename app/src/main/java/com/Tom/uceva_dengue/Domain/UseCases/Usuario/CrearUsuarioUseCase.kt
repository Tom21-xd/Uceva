package com.Tom.uceva_dengue.Domain.UseCases.Usuario

import com.Tom.uceva_dengue.Data.Repositories.FirestoreUsuarioRepository
import com.Tom.uceva_dengue.Domain.Entities.Usuario

class CrearUsuarioUseCase(private val repository: FirestoreUsuarioRepository = FirestoreUsuarioRepository()) {
    suspend fun execute(usuario: Usuario): Result<Boolean> {
        return try {
            val success = repository.crearUsuario(usuario)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al registrar usuario en Firestore"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
