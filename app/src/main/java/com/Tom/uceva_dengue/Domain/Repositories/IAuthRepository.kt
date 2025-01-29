package com.Tom.uceva_dengue.Domain.Repositories

interface IAuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean
    fun signOut()
}
