package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Domain.Repositories.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository : IAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}