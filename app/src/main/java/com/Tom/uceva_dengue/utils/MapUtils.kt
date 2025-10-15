package com.Tom.uceva_dengue.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.android.gms.maps.model.CameraPosition

/**
 * Parsea una cadena de coordenadas en formato "latitud:longitud" a LatLng
 */
fun parseLatLngFromString(address: String): LatLng? {
    val coordinates = address.split(":")
    return if (coordinates.size == 2) {
        try {
            val lat = coordinates[0].toDouble()
            val lng = coordinates[1].toDouble()
            LatLng(lat, lng)
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}

/**
 * Geocodifica una dirección de texto a coordenadas LatLng
 */
fun geocodeAddress(context: Context, address: String): LatLng? {
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

/**
 * Obtiene la ubicación actual del usuario y mueve la cámara a esa posición
 */
fun moveToUserLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    onLocationFound: (LatLng) -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val locationRequest = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        )

        locationRequest.addOnSuccessListener { location ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
                onLocationFound(userLatLng)
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }
}
