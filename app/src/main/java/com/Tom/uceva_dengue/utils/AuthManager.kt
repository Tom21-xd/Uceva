package com.Tom.uceva_dengue.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class  AuthManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun SingInAnonymously() : AuthRes<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously().await()
            AuthRes.Success(result.user?: throw Exception("User is null"))
        } catch (e: Exception) {
            AuthRes.Error(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthRes<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            AuthRes.Success(result.user ?: throw Exception("User is null"))
        } catch (e: Exception) {
            AuthRes.Error(e)
        }
    }

}

sealed class AuthRes<out T> {
    data class Success<out T>(val data: T) : AuthRes<T>()
    data class Error(val exception: Exception) : AuthRes<Nothing>()
}
