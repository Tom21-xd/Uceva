package com.Tom.uceva_dengue.Domain.UseCases.Auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegistrarUsuarioAuthUseCase(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    suspend fun execute(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("UID no generado")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
