package com.Tom.uceva_dengue.Domain.UseCases.Auth

import com.Tom.uceva_dengue.Data.Repositories.AuthRepository

class IniciarSesion (private val repository: AuthRepository = AuthRepository()) {
    suspend fun execute(email: String, password: String): Boolean {
        return repository.signInWithEmailAndPassword(email, password)
    }
}