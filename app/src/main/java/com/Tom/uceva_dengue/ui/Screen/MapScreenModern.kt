package com.Tom.uceva_dengue.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.MapStyleOptions
import com.Tom.uceva_dengue.ui.viewModel.MapViewModel
import com.Tom.uceva_dengue.utils.geocodeAddress
import com.Tom.uceva_dengue.utils.moveToUserLocation
import com.Tom.uceva_dengue.utils.parseLatLngFromString
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

// Colores modernos
private val PrimaryBlue = Color(0xFF5E81F4)
private val DarkBlue = Color(0xFF4A5FCD)
private val LightBlue = Color(0xFF85A5FF)
private val DangerRed = Color(0xFFFF4757)
private val WarningOrange = Color(0xFFFFB946)
private val SuccessGreen = Color(0xFF26DE81)
private val HospitalGreen = Color(0xFF20BF55)

// Clase simple para items de hospital
data class HospitalMarkerItem(
    val latLng: LatLng,
    val nombre: String,
    val direccion: String,
    val hospitalId: Int,
    val casosCount: Int = 0
)

@Composable
fun MapScreenModern(viewModel: MapViewModel) {
    val context = LocalContext.current
    // Detectar si estamos en tema oscuro comparando el color de fondo del tema
    // Un fondo oscuro tendrá valores RGB bajos (cercanos a 0)
    val themeBackgroundColor = MaterialTheme.colorScheme.background
    val isDarkTheme = remember(themeBackgroundColor) {
        // Calcular luminosidad manualmente
        val red = themeBackgroundColor.red
        val green = themeBackgroundColor.green
        val blue = themeBackgroundColor.blue
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
        luminance < 0.5
    }
    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoading by remember { mutableStateOf(true) }
    var showSearchBar by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var hasInitialLocationSet by remember { mutableStateOf(false) }

    // Colores adaptativos según el tema
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF2D2D2D) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF2D3748)
    val textSecondaryColor = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(1.61438, -75.60623), 12f)
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                isLoadingLocation = true
                moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                    userLocation = it
                    viewModel.updateUserLocation(it)
                    isLoadingLocation = false
                    if (!hasInitialLocationSet) {
                        hasInitialLocationSet = true
                    }
                }
            }
        }
    )

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Usar casos filtrados en lugar de todos los casos
    val filteredCases by viewModel.filteredCases.collectAsState()
    val filterRadiusKm by viewModel.filterRadiusKm.collectAsState()
    val hospitals by viewModel.hospitals.collectAsState()
    val dengueTypes by viewModel.dengueTypes.collectAsState()
    val selectedDengueTypeId by viewModel.selectedDengueTypeId.collectAsState()

    var showFiltersPanel by remember { mutableStateOf(false) }

    // Usar casos filtrados para el heatmap
    val heatmapPoints = remember(filteredCases) {
        filteredCases.mapNotNull {
            it.DIRECCION_CASOREPORTADO?.let { address ->
                parseLatLngFromString(address)
            }
        }
    }

    // Contar casos por hospital (basado en proximidad de coordenadas) - usar casos filtrados
    val casesPerHospital = remember(filteredCases, hospitals) {
        val casesMap = mutableMapOf<Int, Int>()
        hospitals.forEach { hospital ->
            val hospitalLat = hospital.LATITUD_HOSPITAL?.toDoubleOrNull()
            val hospitalLng = hospital.LONGITUD_HOSPITAL?.toDoubleOrNull()

            if (hospitalLat != null && hospitalLng != null) {
                val casosEnHospital = filteredCases.count { caso ->
                    val casoLatLng = caso.DIRECCION_CASOREPORTADO?.let { parseLatLngFromString(it) }
                    if (casoLatLng != null) {
                        // Calcular distancia aproximada (0.01 grados ≈ 1 km)
                        val distance = Math.sqrt(
                            Math.pow(hospitalLat - casoLatLng.latitude, 2.0) +
                            Math.pow(hospitalLng - casoLatLng.longitude, 2.0)
                        )
                        distance < 0.05 // Casos dentro de ~5km del hospital
                    } else false
                }
                casesMap[hospital.ID_HOSPITAL] = casosEnHospital
            }
        }
        casesMap
    }

    val hospitalMarkers = remember(hospitals, casesPerHospital) {
        hospitals.mapNotNull { hospital ->
            val latitud = hospital.LATITUD_HOSPITAL?.toDoubleOrNull()
            val longitud = hospital.LONGITUD_HOSPITAL?.toDoubleOrNull()

            if (latitud != null && longitud != null) {
                HospitalMarkerItem(
                    latLng = LatLng(latitud, longitud),
                    nombre = hospital.NOMBRE_HOSPITAL ?: "Hospital",
                    direccion = hospital.DIRECCION_HOSPITAL ?: "",
                    hospitalId = hospital.ID_HOSPITAL,
                    casosCount = casesPerHospital[hospital.ID_HOSPITAL] ?: 0
                )
            } else null
        }
    }

    // Crear icono de hospital personalizado
    val hospitalIcon = remember {
        try {
            val drawable = ContextCompat.getDrawable(context, com.Tom.uceva_dengue.R.drawable.ic_hospital)
            drawable?.let {
                val bitmap = it.toBitmap(width = 120, height = 120)
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        } catch (e: Exception) {
            null
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

    // Auto-obtener ubicación al cargar
    LaunchedEffect(hasLocationPermission, hasInitialLocationSet) {
        kotlinx.coroutines.delay(500)
        isMapLoading = false

        if (hasLocationPermission && !hasInitialLocationSet) {
            isLoadingLocation = true
            moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                userLocation = it
                viewModel.updateUserLocation(it)
                isLoadingLocation = false
                hasInitialLocationSet = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mostrar indicador de carga mientras se inicializa el mapa
        AnimatedVisibility(
            visible = isMapLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = PrimaryBlue,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Cargando mapa de calor...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textSecondaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Mapa
        AnimatedVisibility(
            visible = !isMapLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapType = MapType.NORMAL,
                    mapStyleOptions = if (isDarkTheme) {
                        try {
                            MapStyleOptions.loadRawResourceStyle(context, com.Tom.uceva_dengue.R.raw.map_style_dark)
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    tiltGesturesEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabled = true
                )
            ) {
                // Heatmap
                if (heatmapProvider != null) {
                    TileOverlay(
                        state = tileOverlayState,
                        tileProvider = heatmapProvider
                    )
                }

                // Marcadores de hospitales
                hospitalMarkers.forEach { hospital ->
                    Marker(
                        state = MarkerState(position = hospital.latLng),
                        title = "🏥 ${hospital.nombre}",
                        snippet = "${hospital.direccion}\n📊 Casos cercanos: ${hospital.casosCount}",
                        icon = hospitalIcon ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                }

                // Marcador de búsqueda
                searchLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Ubicación buscada",
                        snippet = searchText
                    )
                }

                // Marcador de usuario (si no está usando el nativo)
                if (!hasLocationPermission) {
                    userLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Mi ubicación",
                            icon = com.google.android.gms.maps.model.BitmapDescriptorFactory
                                .defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }
                }
            }
        }

        // Barra de búsqueda superior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Buscar dirección...", fontSize = 14.sp, color = textSecondaryColor) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue)
                            },
                            trailingIcon = {
                                Row {
                                    if (searchText.isNotEmpty()) {
                                        IconButton(onClick = { searchText = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = textColor)
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            val location = geocodeAddress(context, searchText)
                                            if (location != null) {
                                                searchLocation = location
                                                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 16f)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Buscar", tint = PrimaryBlue)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = if (isDarkTheme) Color(0xFF505050) else Color.LightGray,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                cursorColor = PrimaryBlue
                            )
                        )
                    }
                }
            }
        }

        // Botones de acción flotantes (derecha)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón de filtros
            FloatingActionButton(
                onClick = { showFiltersPanel = !showFiltersPanel },
                containerColor = if (showFiltersPanel) PrimaryBlue else Color.White,
                contentColor = if (showFiltersPanel) Color.White else PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filtros"
                )
            }

            // Botón de búsqueda
            FloatingActionButton(
                onClick = { showSearchBar = !showSearchBar },
                containerColor = if (showSearchBar) PrimaryBlue else Color.White,
                contentColor = if (showSearchBar) Color.White else PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Icon(
                    if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            }

            // Botón de mi ubicación
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        isLoadingLocation = true
                        moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                            userLocation = it
                            viewModel.updateUserLocation(it)
                            isLoadingLocation = false
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                containerColor = Color.White,
                contentColor = PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }
            }
        }

        // Info compacta en la parte inferior
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                modifier = Modifier.size(8.dp),
                                tint = PrimaryBlue
                            )
                            Text(
                                "${heatmapPoints.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HospitalGreen.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalHospital,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = HospitalGreen
                            )
                            Text(
                                "${hospitalMarkers.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HospitalGreen
                            )
                        }
                    }

                    if (userLocation != null) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = WarningOrange.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = WarningOrange
                                )
                                Text(
                                    "${filterRadiusKm.toInt()}km",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningOrange
                                )
                            }
                        }
                    }
                }
            }
        }

        // Panel de filtros desplegable
        AnimatedVisibility(
            visible = showFiltersPanel,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .padding(bottom = 56.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filtros de búsqueda",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        IconButton(
                            onClick = { showFiltersPanel = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = textSecondaryColor
                            )
                        }
                    }

                    // Radio de búsqueda compacto
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Radio de distancia",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = PrimaryBlue.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    "${filterRadiusKm.toInt()} km",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                        Slider(
                            value = filterRadiusKm,
                            onValueChange = { viewModel.updateFilterRadius(it) },
                            valueRange = 1f..50f,
                            steps = 48,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryBlue,
                                activeTrackColor = PrimaryBlue,
                                inactiveTrackColor = if (isDarkTheme) Color(0xFF505050) else Color.LightGray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Filtro de tipo de dengue
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Tipo de dengue",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )

                        // Chip "Todos"
                        FilterChip(
                            selected = selectedDengueTypeId == null,
                            onClick = { viewModel.updateSelectedDengueType(null) },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (selectedDengueTypeId == null) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        "Todos",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )

                        // Chips de tipos de dengue
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dengueTypes.forEach { dengueType ->
                                FilterChip(
                                    selected = selectedDengueTypeId == dengueType.ID_TIPODENGUE,
                                    onClick = { viewModel.updateSelectedDengueType(dengueType.ID_TIPODENGUE) },
                                    label = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            if (selectedDengueTypeId == dengueType.ID_TIPODENGUE) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Text(
                                                dengueType.NOMBRE_TIPODENGUE ?: "Tipo ${dengueType.ID_TIPODENGUE}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = getDengueTypeColor(dengueType.ID_TIPODENGUE),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Función auxiliar para colores de tipo de dengue
private fun getDengueTypeColor(typeId: Int): Color {
    return when (typeId) {
        1 -> Color(0xFFFF6B6B)  // Dengue Clásico - Rojo
        2 -> Color(0xFFFF8C42)  // Dengue Hemorrágico - Naranja
        3 -> Color(0xFFFFB74D)  // Dengue Grave - Naranja oscuro
        4 -> Color(0xFFE53935)  // Otro tipo - Rojo oscuro
        else -> Color(0xFF5E81F4) // Default - Azul
    }
}

@Composable
fun LegendItem(color: Color, label: String, modifier: Modifier = Modifier) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            label,
            fontSize = 10.sp,
            color = textColor
        )
    }
}
