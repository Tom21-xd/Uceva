package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Genero
import com.Tom.uceva_dengue.Domain.Repositories.IGeneroRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreGeneroRepository : IGeneroRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getGeneros(): Flow<List<Genero>> = callbackFlow {
        val listener = db.collection("Genero")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val generos = snapshot?.documents?.map { doc ->
                    println("Firestore doc: ${doc.data}") // DEPURACIÓN: Ver qué datos llegan
                    Genero(
                        Id = doc.id, // ID del documento
                        Nombre = doc.getString("Nombre") ?: "Sin Nombre",
                        Estado = doc.getBoolean("Estado") ?: false // Valor predeterminado si es null
                    )
                } ?: emptyList()

                if (!trySend(generos).isSuccess) {
                    println("Error al enviar los datos de Genero")
                }
            }

        awaitClose { listener.remove() } // Cierra correctamente el listener
    }
}
