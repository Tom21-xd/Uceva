package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import com.Tom.uceva_dengue.Domain.Repositories.IPublicacionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestorePublicacionRepository : IPublicacionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Publicacion")
    private val usuariosRef = db.collection("Usuario") // Referencia a la colección de usuarios

    override fun getPublicaciones(): Flow<List<Publicacion>> = callbackFlow {
        val listener = collectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            launch {
                val publicaciones = snapshot?.documents?.mapNotNull { doc ->
                    val publicacion = doc.toObject(Publicacion::class.java)?.copy(Id = doc.id)

                    publicacion?.let {
                        val userId = it.Usuario

                        val usuarioSnapshot = usuariosRef.document(userId.toString()).get().await()
                        val nombreUsuario = usuarioSnapshot.getString("nombre") ?: "Desconocido"

                        it.copy(Usuario = nombreUsuario)
                    }
                } ?: emptyList()

                trySend(publicaciones)
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun getPublicacionById(id: String): Publicacion? {
        return try {
            val snapshot = collectionRef.document(id).get().await()
            snapshot.toObject(Publicacion::class.java)?.copy(Id = id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addPublicacion(publicacion: Publicacion): Boolean {
        return try {
            val documentRef = collectionRef.add(publicacion).await()
            documentRef.id.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updatePublicacion(publicacion: Publicacion): Boolean {
        return try {
            publicacion.Id?.let { collectionRef.document(it).set(publicacion).await() }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deletePublicacion(id: String): Boolean {
        return try {
            collectionRef.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
