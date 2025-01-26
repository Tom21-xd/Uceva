package com.Tom.uceva_dengue.Data.Repositories

import IDepartamentoRepository
import com.Tom.uceva_dengue.Domain.Entities.Departamento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreDepartmentRepository : IDepartamentoRepository {
    private val db = FirebaseFirestore.getInstance()

    override fun getDepartamentos(): Flow<List<Departamento>> = callbackFlow {
        val listener = db.collection("Departamento")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val departments = snapshot?.documents?.map { doc ->
                    Departamento(
                        Id = doc.id,
                        Nombre = doc.getString("Nombre") ?: "",
                        Estado = doc.getBoolean("Estado") ?: true
                    )
                } ?: emptyList()

                trySend(departments)
            }

        awaitClose { listener.remove() }
    }
}
