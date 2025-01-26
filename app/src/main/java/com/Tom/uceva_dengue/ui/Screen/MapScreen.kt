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
import com.google.android.gms.location.Priority
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
        position = CameraPosition.fromLatLngZoom(LatLng(1.61438, -75.60623), 12f)
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

    val heatmapPoints = listOf(
        LatLng(1.61438, -75.60623),
        LatLng(1.61000, -75.60500),
        LatLng(1.61250, -75.60400),
        LatLng(1.61500, -75.60750),
        LatLng(1.61700, -75.60800),
        LatLng(1.62000, -75.61000),
        LatLng(1.62200, -75.61150),
        LatLng(1.61800, -75.60900),
        LatLng(1.61300, -75.60300),
        LatLng(1.61900, -75.60850)
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
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    tiltGesturesEnabled = true,
                    zoomGesturesEnabled = true
                )
            ) {
                TileOverlay(
                    state = tileOverlayState,
                    tileProvider = heatmapProvider
                )

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
        val locationRequest = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        )

        locationRequest.addOnSuccessListener { location ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 15f)
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }
}

@Composable
@Preview
fun previewMapScreen() {
    MapScreen()
}
