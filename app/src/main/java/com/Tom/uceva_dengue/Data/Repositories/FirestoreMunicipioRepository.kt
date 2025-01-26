package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Domain.Entities.Departamento
import com.Tom.uceva_dengue.Domain.Entities.Municipio
import com.Tom.uceva_dengue.Domain.Repositories.IMunicipioRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreMunicipioRepository : IMunicipioRepository {
    private val db = FirebaseFirestore.getInstance()
    override fun getMunicipios(departamentoId: String): Flow<List<Municipio>> = callbackFlow {
        val listener = db.collection("Departamento")
            .document(departamentoId)
            .collection("Municipios")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val municipios = snapshot?.documents?.map { doc ->
                    Municipio(
                        Id = doc.id,
                        Nombre = doc.getString("Nombre") ?: "",
                        Estado = doc.getBoolean("Estado") ?: true
                    )
                } ?: emptyList()

                trySend(municipios)
            }

        awaitClose { listener.remove() }
    }

}