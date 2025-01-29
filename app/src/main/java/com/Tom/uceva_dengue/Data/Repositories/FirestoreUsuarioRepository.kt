package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Usuario
import com.Tom.uceva_dengue.Domain.Repositories.IUsuarioRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreUsuarioRepository : IUsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
    override suspend fun crearUsuario(usuario: Usuario): Boolean {
        return try {
            val snapshot = db.collection("Usuario").get().await()
            val nuevoNumero = snapshot.documents.size + 1
            val nuevoId = usuario.Id

            val usuarioFirestore = usuario.copy(Id = nuevoId)

            db.collection("Usuario").document(nuevoId).set(usuarioFirestore).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}