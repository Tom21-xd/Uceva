package com.Tom.uceva_dengue.utils

import android.content.Context
import android.location.Geocoder
import com.Tom.uceva_dengue.model.CasoReportado
import com.Tom.uceva_dengue.model.Usuario
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CasoReportadoRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchReportedCases(): List<LatLng> {
        val cases = mutableListOf<LatLng>()

        try {
            val snapshot = db.collection("CasoReportado").get().await()
            for (document in snapshot.documents) {
                val case = document.toObject(CasoReportado::class.java)?.copy(id = document.id)
                if (case != null) {
                    val userSnapshot = db.document(case.usuarioRef).get().await()
                    if (!userSnapshot.exists()) continue
                    val usuarioSnapshot = userSnapshot.toObject(Usuario::class.java)
                    if (usuarioSnapshot == null) continue
                    usuarioSnapshot.Id = userSnapshot.id

                    val ciudadRef = usuarioSnapshot.Ciudad
                    if (ciudadRef.isNullOrEmpty()) continue

                    val ciudadSnapshot = db.document(ciudadRef).get().await()
                    if (!ciudadSnapshot.exists()) continue

                    val ciudadNombre = ciudadSnapshot.getString("Nombre") ?: ""

                    val departamentoRef = ciudadSnapshot.reference.parent?.parent
                    if (departamentoRef == null) continue

                    val departamentoSnapshot = departamentoRef.get().await()
                    if (!departamentoSnapshot.exists()) continue

                    val departamentoNombre = departamentoSnapshot.getString("Nombre") ?: ""

                    val direccionCompleta = "${usuarioSnapshot.Direccion}, $ciudadNombre, $departamentoNombre"

                    val location = geocodeAddress(context, direccionCompleta)
                    if (location != null) {
                        cases.add(location)
                    } else {
                        println("Error al geocodificar: $direccionCompleta")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cases
    }

    private fun geocodeAddress(context: Context, address: String): LatLng? {
        val geocoder = Geocoder(context)
        return try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                LatLng(location.latitude, location.longitude)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
