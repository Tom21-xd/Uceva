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
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

@Composable
fun MapScreen() {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.60971, -74.08175), 10f) // Bogotá
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                moveToUserLocation(context, fusedLocationClient, cameraPositionState)
            }
        }
    )

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // **NUEVO: Datos estáticos para el mapa de calor**
    val heatmapPoints = listOf(
        LatLng(4.60971, -74.08175), // Bogotá centro
        LatLng(4.617, -74.090),     // Punto cercano 1
        LatLng(4.601, -74.072),     // Punto cercano 2
        LatLng(4.630, -74.087),     // Punto cercano 3
        LatLng(4.640, -74.120),     // Punto cercano 4
        LatLng(4.650, -74.070),     // Punto adicional 1
        LatLng(4.655, -74.100)      // Punto adicional 2
    )

    val heatmapProvider = remember {
        HeatmapTileProvider.Builder()
            .data(heatmapPoints)
            .build()
    }
    val tileOverlayState = rememberTileOverlayState()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar dirección") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = {
                        val location = geocodeAddress(context, searchText)
                        if (location != null) {
                            searchLocation = location
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                        }
                    }
                ) {
                    Text("Buscar")
                }
            }

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

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    tiltGesturesEnabled = true
                )
            ) {
                // **NUEVO: Mostrar el mapa de calor**
                TileOverlay(
                    state = tileOverlayState,
                    tileProvider = heatmapProvider
                )

                // **Ubicación buscada con marcador**
                searchLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Ubicación buscada"
                    )
                }
            }
        }

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
            Text("📍")
        }
    }
}

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

fun moveToUserLocation(
    context: android.content.Context,
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState
) {
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
            }
        }
    }
}

@Composable
@Preview
fun previewMapScreen() {
    MapScreen()
}
