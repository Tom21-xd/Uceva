package com.Tom.uceva_dengue.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.Tom.uceva_dengue.ui.viewModel.MapViewModel
import com.Tom.uceva_dengue.utils.geocodeAddress
import com.Tom.uceva_dengue.utils.moveToUserLocation
import com.Tom.uceva_dengue.utils.parseLatLngFromString
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

@Composable
fun MapScreen(viewModel: MapViewModel) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoading by remember { mutableStateOf(true) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(1.61438, -75.60623), 12f)
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                    userLocation = it
                }
            }
        }
    )

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val cases by viewModel.cases.collectAsState()

    val heatmapPoints = remember(cases) {
        cases.mapNotNull {
            it.DIRECCION_CASOREPORTADO?.let { address ->
                parseLatLngFromString(address)
            }
        }
    }

    val heatmapProvider = remember(heatmapPoints) {
        if (heatmapPoints.isNotEmpty()) {
            HeatmapTileProvider.Builder()
                .data(heatmapPoints)
                .build()
        } else {
            null
        }
    }

    val tileOverlayState = rememberTileOverlayState()

    // Simular carga del mapa
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        isMapLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mostrar indicador de carga mientras se inicializa el mapa
        if (isMapLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = Color(0xFF0066CC)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cargando mapa...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }
        Column(Modifier.fillMaxSize()) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar dirección") },
                placeholder = { Text("Ej: Calle 10 #15-20") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    val location = geocodeAddress(context, searchText)
                    if (location != null) {
                        searchLocation = location
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp, bottom = 8.dp)
            ) {
                Text("Buscar ubicación")
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
                if (heatmapProvider != null) {
                    TileOverlay(
                        state = tileOverlayState,
                        tileProvider = heatmapProvider
                    )
                }
                searchLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Ubicación buscada"
                    )
                }

                // Círculo para ubicación actual del usuario
                userLocation?.let {
                    Circle(
                        center = it,
                        radius = 10.0,
                        strokeColor = Color.Blue,
                        strokeWidth = 2f,
                        fillColor = Color(0x440000FF) // Azul translúcido
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (hasLocationPermission) {
                    moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                        userLocation = it
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            containerColor = Color(0xFF0066CC),
            contentColor = Color.White,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.LocationSearching, contentDescription = "Mi ubicación")
        }
    }
}
