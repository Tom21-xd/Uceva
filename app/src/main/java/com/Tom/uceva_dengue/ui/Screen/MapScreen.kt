package com.Tom.uceva_dengue.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.60971, -74.08175), 10f) // Ubicación inicial, Bogotá
    }

    // Configurar el cliente de ubicación
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                moveToUserLocation(context, fusedLocationClient, cameraPositionState)
            }
        }
    )

    // Verificar si el permiso de ubicación está otorgado
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // TextField para ingresar la dirección
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TextField para ingresar la dirección
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar dirección") },
                    modifier = Modifier
                        .weight(1f) // Ocupa el máximo ancho disponible
                        .padding(end = 8.dp) // Espacio entre el TextField y el botón
                )

                // Botón para buscar la dirección
                Button(
                    onClick = {
                        val location = geocodeAddress(context, searchText)
                        if (location != null) {
                            searchLocation = location // Almacena la ubicación buscada
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                        }
                    }
                ) {
                    Text("Buscar")
                }
            }
            // Solicitar permiso de ubicación si no está otorgado
            if (!hasLocationPermission) {
                Button(
                    onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                ) {
                    Text("Permitir acceso a la ubicación")
                }
            }

            // Mapa de Google con el marcador de la ubicación buscada
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,    // Habilita los controles de zoom (aparecen en la esquina inferior derecha)
                    compassEnabled = true,         // Habilita la brújula (aparece al rotar el mapa)
                    tiltGesturesEnabled = true     // Habilita el gesto de inclinación
                )

            ) {
                // Marcador temporal para la ubicación buscada
                searchLocation?.let {
                    Marker(
                        state = MarkerState(position = it), // Estado del marcador con la posición
                        title = "Ubicación buscada"
                    )
                }
            }
        }

        // Botón flotante para centrar en la ubicación actual del usuario
        FloatingActionButton(
            onClick = {
                if (hasLocationPermission) {
                    moveToUserLocation(context, fusedLocationClient, cameraPositionState)
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("📍") // Puedes usar un ícono aquí en lugar de texto
        }
    }
}

// Función para geocodificar la dirección ingresada
fun geocodeAddress(context: android.content.Context, address: String): LatLng? {
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

// Función para mover la cámara a la ubicación actual del usuario
fun moveToUserLocation(
    context: android.content.Context,
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState
) {
    // Verificar permisos de ubicación
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    // Obtener la última ubicación conocida del usuario
    fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
        location?.let {
            val userLatLng = LatLng(it.latitude, it.longitude)
            // Mover la cámara a la ubicación del usuario
            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
        }
    }
}

@Composable
@Preview
fun previewMapScreen() {
    MapScreen()
}
