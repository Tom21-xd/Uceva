package com.Tom.uceva_dengue.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenModern(viewModel: MapViewModel) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()
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
    val selectedAgeGroup by viewModel.selectedAgeGroup.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val availableYears by viewModel.availableYears.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val viewModelSearchLocation by viewModel.searchLocation.collectAsState()
    val epidemicStats by viewModel.epidemicStats.collectAsState()

    var showFiltersPanel by remember { mutableStateOf(false) }
    var showDashboardPanel by remember { mutableStateOf(false) }

    // Estado local para el slider (con debounce)
    var localRadiusValue by remember { mutableStateOf(filterRadiusKm) }

    // Debounce para el cambio de radio (solo actualiza el ViewModel después de 300ms sin cambios)
    LaunchedEffect(localRadiusValue) {
        kotlinx.coroutines.delay(300)
        viewModel.updateFilterRadius(localRadiusValue)
    }

    // Usar casos filtrados para el heatmap - Optimizado con derivedStateOf
    val heatmapPoints by remember {
        derivedStateOf {
            filteredCases.mapNotNull { case ->
                // Priorizar campos LATITUD y LONGITUD separados (nuevo formato)
                val lat = case.LATITUD
                val lng = case.LONGITUD

                if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else {
                    // Fallback: parsear DIRECCION_CASOREPORTADO para compatibilidad con datos antiguos
                    case.DIRECCION_CASOREPORTADO?.let { address ->
                        parseLatLngFromString(address)
                    }
                }
            }
        }
    }

    // Contar casos por hospital - Optimizado con derivedStateOf y cálculo más eficiente
    val casesPerHospital by remember {
        derivedStateOf {
            val casesMap = mutableMapOf<Int, Int>()

            // Pre-procesar casos para evitar parsear múltiples veces
            val casesWithLocations = filteredCases.mapNotNull { caso ->
                val casoLat = caso.LATITUD
                val casoLng = caso.LONGITUD

                val location = if (casoLat != null && casoLng != null) {
                    LatLng(casoLat, casoLng)
                } else {
                    caso.DIRECCION_CASOREPORTADO?.let { parseLatLngFromString(it) }
                }
                location?.let { caso to it }
            }

            // Ahora iterar hospitales con casos pre-procesados
            hospitals.forEach { hospital ->
                val hospitalLat = hospital.LATITUD_HOSPITAL?.toDoubleOrNull()
                val hospitalLng = hospital.LONGITUD_HOSPITAL?.toDoubleOrNull()

                if (hospitalLat != null && hospitalLng != null) {
                    // Conteo optimizado con casos pre-procesados
                    val casosEnHospital = casesWithLocations.count { (_, casoLatLng) ->
                        // Calcular distancia aproximada más rápido usando diferencias cuadradas
                        val latDiff = hospitalLat - casoLatLng.latitude
                        val lngDiff = hospitalLng - casoLatLng.longitude
                        val distanceSquared = (latDiff * latDiff) + (lngDiff * lngDiff)
                        distanceSquared < 0.0025 // ~5km al cuadrado (evita sqrt)
                    }
                    casesMap[hospital.ID_HOSPITAL] = casosEnHospital
                }
            }
            casesMap
        }
    }

    // Optimizar marcadores de hospitales con derivedStateOf
    val hospitalMarkers by remember {
        derivedStateOf {
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

    // Optimizar heatmap provider con derivedStateOf
    val heatmapProvider by remember {
        derivedStateOf {
            if (heatmapPoints.isNotEmpty()) {
                HeatmapTileProvider.Builder()
                    .data(heatmapPoints)
                    .build()
            } else {
                null
            }
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

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
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
                            modifier = Modifier.size(dimensions.iconExtraLarge),
                            color = PrimaryBlue,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(dimensions.paddingLarge))
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
                heatmapProvider?.let { provider ->
                    TileOverlay(
                        state = tileOverlayState,
                        tileProvider = provider
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
                        snippet = searchText,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
                }

                // Círculo de radio alrededor de la ubicación activa (búsqueda o usuario)
                val activeLocation = viewModelSearchLocation ?: userLocation
                activeLocation?.let { center ->
                    // Círculo con opacidad fija para mejor rendimiento
                    Circle(
                        center = center,
                        radius = (filterRadiusKm * 1000).toDouble(), // Convertir km a metros
                        strokeColor = if (viewModelSearchLocation != null) Color(0xFFFF6B35) else WarningOrange,
                        fillColor = if (viewModelSearchLocation != null)
                            Color(0xFFFF6B35).copy(alpha = 0.10f)
                        else
                            WarningOrange.copy(alpha = 0.10f),
                        strokeWidth = 2.5f
                    )

                    // Marcador central de la ubicación activa
                    Marker(
                        state = MarkerState(position = center),
                        title = if (viewModelSearchLocation != null) "🔍 Punto de búsqueda" else "📍 Mi ubicación",
                        snippet = "${filteredCases.size} casos en ${filterRadiusKm.toInt()}km",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (viewModelSearchLocation != null) BitmapDescriptorFactory.HUE_ORANGE
                            else BitmapDescriptorFactory.HUE_CYAN
                        )
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
                .padding(dimensions.paddingMedium)
        ) {
            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(dimensions.cardCornerRadius)),
                    shape = RoundedCornerShape(dimensions.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.paddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Buscar dirección...", fontSize = dimensions.textSizeMedium, color = textSecondaryColor) },
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
                                                viewModel.updateSearchLocation(location) // Actualizar ViewModel para filtro de radio
                                                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 16f)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Buscar", tint = PrimaryBlue)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(dimensions.paddingSmall),
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
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
        ) {
            // Botón de dashboard epidemiológico
            FloatingActionButton(
                onClick = { showDashboardPanel = !showDashboardPanel },
                containerColor = if (showDashboardPanel) Color(0xFF8E44AD) else Color.White,
                contentColor = if (showDashboardPanel) Color.White else Color(0xFF8E44AD),
                shape = CircleShape,
                modifier = Modifier
                    .size(dimensions.iconExtraLarge)
                    .shadow(8.dp, CircleShape)
            ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = "Dashboard Epidemiológico"
                )
            }

            // Botón de filtros
            FloatingActionButton(
                onClick = { showFiltersPanel = !showFiltersPanel },
                containerColor = if (showFiltersPanel) PrimaryBlue else Color.White,
                contentColor = if (showFiltersPanel) Color.White else PrimaryBlue,
                shape = CircleShape,
                modifier = Modifier
                    .size(dimensions.iconExtraLarge)
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
                    .size(dimensions.iconExtraLarge)
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
                    .size(dimensions.iconExtraLarge)
                    .shadow(8.dp, CircleShape)
            ) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensions.iconLarge),
                        color = PrimaryBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }
            }

            // Botón de zoom a todos los casos
            if (heatmapPoints.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        // Calcular bounds de todos los casos visibles
                        val bounds = com.google.android.gms.maps.model.LatLngBounds.builder()
                        heatmapPoints.forEach { point ->
                            bounds.include(point)
                        }
                        try {
                            val latLngBounds = bounds.build()
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                latLngBounds.center,
                                12f
                            )
                        } catch (e: Exception) {
                            // Si solo hay un punto, hacer zoom simple
                            heatmapPoints.firstOrNull()?.let {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
                            }
                        }
                    },
                    containerColor = SuccessGreen.copy(alpha = 0.9f),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(dimensions.iconLarge)
                        .shadow(6.dp, CircleShape)
                ) {
                    Icon(
                        Icons.Default.ZoomOutMap,
                        contentDescription = "Ver todos los casos",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Info compacta en la parte inferior - scrollable horizontalmente
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingSmall, vertical = dimensions.paddingSmall)
                .shadow(4.dp, RoundedCornerShape(dimensions.paddingLarge)),
            shape = RoundedCornerShape(dimensions.paddingLarge),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
        ) {
            // Usar LazyRow para scroll horizontal en pantallas pequeñas
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.paddingSmall, vertical = dimensions.paddingSmall),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                item(key = "cases_count") {
                    // Contador animado de casos
                    val animatedCount by animateIntAsState(
                        targetValue = filteredCases.size,
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                        label = "casesCount"
                    )

                    Card(
                        shape = RoundedCornerShape(dimensions.paddingSmall),
                        colors = CardDefaults.cardColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.clickable {
                            showDashboardPanel = !showDashboardPanel
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = "Casos",
                                modifier = Modifier.size(16.dp),
                                tint = PrimaryBlue
                            )
                            Text(
                                "$animatedCount casos",
                                fontSize = dimensions.textSizeSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }

                item(key = "hospitals_count") {
                    Card(
                        shape = RoundedCornerShape(dimensions.paddingSmall),
                        colors = CardDefaults.cardColors(
                            containerColor = HospitalGreen.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = dimensions.paddingSmall, vertical = dimensions.paddingSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalHospital,
                                contentDescription = "Hospitales",
                                modifier = Modifier.size(dimensions.paddingSmall),
                                tint = HospitalGreen
                            )
                            Text(
                                "${hospitalMarkers.size}",
                                fontSize = dimensions.textSizeSmall,
                                fontWeight = FontWeight.Bold,
                                color = HospitalGreen
                            )
                        }
                    }
                }

                // Indicador de ubicación activa con radio
                if (viewModelSearchLocation != null) {
                    // Cuando hay una búsqueda activa, mostrar chip destacado para volver
                    item(key = "search_location_chip") {
                        Card(
                            shape = RoundedCornerShape(dimensions.paddingSmall),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.clickable {
                                viewModel.clearSearchLocation()
                                searchLocation = null
                                searchText = ""
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.TravelExplore,
                                    contentDescription = "Búsqueda",
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFFFF6B35)
                                )
                                Text(
                                    searchText.take(15).ifEmpty { "Búsqueda" },
                                    fontSize = dimensions.textSizeSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF6B35),
                                    maxLines = 1
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF6B35),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cerrar",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                } else if (userLocation != null) {
                    // Cuando solo hay ubicación del usuario, mostrar chip simple
                    item(key = "user_location_chip") {
                        Card(
                            shape = RoundedCornerShape(dimensions.paddingSmall),
                            colors = CardDefaults.cardColors(
                                containerColor = WarningOrange.copy(alpha = 0.15f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.MyLocation,
                                    contentDescription = "Mi ubicación",
                                    modifier = Modifier.size(16.dp),
                                    tint = WarningOrange
                                )
                                Text(
                                    "${filterRadiusKm.toInt()}km",
                                    fontSize = dimensions.textSizeSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningOrange
                                )
                            }
                        }
                    }
                }

                // Botón de acceso rápido para año actual
                item(key = "year_2025_chip") {
                    Card(
                        shape = RoundedCornerShape(dimensions.paddingSmall),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF00BCD4).copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.clickable {
                            viewModel.updateSelectedYear(2025)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Año actual",
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF00BCD4)
                            )
                            Text(
                                "2025",
                                fontSize = dimensions.textSizeSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF00BCD4)
                            )
                        }
                    }
                }
            }
        }

        // Panel de Dashboard Epidemiológico
        AnimatedVisibility(
            visible = showDashboardPanel,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn() + expandVertically(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut() + shrinkVertically(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            EpidemicDashboardPanel(
                stats = epidemicStats,
                onClose = { showDashboardPanel = false },
                isDarkTheme = isDarkTheme,
                dimensions = dimensions,
                cardBackgroundColor = cardBackgroundColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )
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
                    .heightIn(max = 400.dp) // Altura máxima para pantallas pequeñas
                    .padding(dimensions.paddingSmall)
                    .padding(bottom = dimensions.iconExtraLarge)
                    .shadow(12.dp, RoundedCornerShape(dimensions.paddingLarge)),
                shape = RoundedCornerShape(dimensions.paddingLarge),
                colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()) // Hacer scrollable
                        .padding(dimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                ) {
                    // Título
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filtros de búsqueda",
                            fontSize = dimensions.textSizeLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        IconButton(
                            onClick = { showFiltersPanel = false },
                            modifier = Modifier.size(dimensions.iconLarge)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(dimensions.iconMedium),
                                tint = textSecondaryColor
                            )
                        }
                    }

                    // Radio de búsqueda compacto
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Radio de distancia",
                                fontSize = dimensions.textSizeMedium,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                            Card(
                                shape = RoundedCornerShape(dimensions.paddingSmall),
                                colors = CardDefaults.cardColors(
                                    containerColor = PrimaryBlue.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    "${localRadiusValue.toInt()} km",
                                    modifier = Modifier.padding(horizontal = dimensions.paddingSmall, vertical = 4.dp),
                                    fontSize = dimensions.textSizeMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                        Slider(
                            value = localRadiusValue,
                            onValueChange = { localRadiusValue = it },
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
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                    ) {
                        Text(
                            "Tipo de dengue",
                            fontSize = dimensions.textSizeMedium,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )

                        // Chips en LazyRow horizontal scrollable
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                        ) {
                            // Chip "Todos" al inicio
                            item {
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
                                                    modifier = Modifier.size(dimensions.iconSmall)
                                                )
                                            }
                                            Text(
                                                "Todos",
                                                fontSize = dimensions.textSizeSmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            // Chips de tipos de dengue - Optimizado con key
                            items(
                                items = dengueTypes,
                                key = { it.ID_TIPODENGUE }
                            ) { dengueType ->
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
                                                    modifier = Modifier.size(dimensions.iconSmall)
                                                )
                                            }
                                            Text(
                                                dengueType.NOMBRE_TIPODENGUE ?: "Tipo ${dengueType.ID_TIPODENGUE}",
                                                fontSize = dimensions.textSizeSmall,
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

                    // Filtro de grupo etario
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                    ) {
                        Text(
                            "Grupo de edad",
                            fontSize = dimensions.textSizeMedium,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )

                        // Chips en LazyRow horizontal scrollable
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                        ) {
                            // Chip "Todas las edades" al inicio
                            item {
                                FilterChip(
                                    selected = selectedAgeGroup == null,
                                    onClick = { viewModel.updateSelectedAgeGroup(null) },
                                    label = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (selectedAgeGroup == null) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(dimensions.iconSmall)
                                                )
                                            }
                                            Text(
                                                "Todas",
                                                fontSize = dimensions.textSizeSmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            // Chips de grupos etarios según clasificación OMS
                            items(
                                items = listOf(
                                    1 to "0-4 años",
                                    2 to "5-14 años",
                                    3 to "15-49 años",
                                    4 to "50-64 años",
                                    5 to "65+ años"
                                )
                            ) { (groupId, groupLabel) ->
                                FilterChip(
                                    selected = selectedAgeGroup == groupId,
                                    onClick = { viewModel.updateSelectedAgeGroup(groupId) },
                                    label = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            if (selectedAgeGroup == groupId) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(dimensions.iconSmall)
                                                )
                                            }
                                            Text(
                                                groupLabel,
                                                fontSize = dimensions.textSizeSmall,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = getAgeGroupColor(groupId),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Filtro de año con Dropdown
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                    ) {
                        Text(
                            "Año de reporte",
                            fontSize = dimensions.textSizeMedium,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )

                        var yearDropdownExpanded by remember { mutableStateOf(false) }

                        // Botón del selector de año
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { yearDropdownExpanded = true },
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (selectedYear != null) Color(0xFF00BCD4).copy(alpha = 0.1f) else Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedYear != null) Color(0xFF00BCD4) else textSecondaryColor.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensions.paddingMedium),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = if (selectedYear != null) Color(0xFF00BCD4) else textSecondaryColor,
                                        modifier = Modifier.size(dimensions.iconSmall)
                                    )
                                    Text(
                                        text = selectedYear?.toString() ?: "Todos los años",
                                        fontSize = dimensions.textSizeMedium,
                                        fontWeight = if (selectedYear != null) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedYear != null) Color(0xFF00BCD4) else textColor
                                    )
                                }
                                Icon(
                                    if (yearDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = textSecondaryColor
                                )
                            }
                        }

                        // DropdownMenu para años
                        DropdownMenu(
                            expanded = yearDropdownExpanded,
                            onDismissRequest = { yearDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            // Opción "Todos los años"
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (selectedYear == null) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(dimensions.iconSmall)
                                            )
                                        }
                                        Text(
                                            "Todos los años",
                                            fontWeight = if (selectedYear == null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.updateSelectedYear(null)
                                    yearDropdownExpanded = false
                                }
                            )

                            Divider()

                            // Años disponibles
                            availableYears.forEach { year ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (selectedYear == year) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00BCD4),
                                                    modifier = Modifier.size(dimensions.iconSmall)
                                                )
                                            }
                                            Text(
                                                year.toString(),
                                                fontWeight = if (selectedYear == year) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateSelectedYear(year)
                                        yearDropdownExpanded = false
                                    }
                                )
                            }
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

// Función auxiliar para colores de grupos etarios
private fun getAgeGroupColor(groupId: Int): Color {
    return when (groupId) {
        1 -> Color(0xFF9C27B0)  // 0-4 años - Púrpura
        2 -> Color(0xFF2196F3)  // 5-14 años - Azul
        3 -> Color(0xFF4CAF50)  // 15-49 años - Verde
        4 -> Color(0xFFFF9800)  // 50-64 años - Naranja
        5 -> Color(0xFFF44336)  // 65+ años - Rojo
        else -> Color(0xFF5E81F4) // Default - Azul
    }
}

@Composable
fun EpidemicDashboardPanel(
    stats: MapViewModel.EpidemicStats,
    onClose: () -> Unit,
    isDarkTheme: Boolean,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    cardBackgroundColor: Color,
    textColor: Color,
    textSecondaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .padding(dimensions.paddingSmall)
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "📊 Dashboard Epidemiológico",
                        fontSize = dimensions.textSizeExtraLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E44AD)
                    )
                    Text(
                        "Actualizado: ${stats.lastUpdate}",
                        fontSize = dimensions.textSizeSmall,
                        color = textSecondaryColor
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = textSecondaryColor
                    )
                }
            }

            HorizontalDivider(color = textSecondaryColor.copy(alpha = 0.2f))

            // Indicador de nivel de riesgo
            val riskColor = when (stats.riskLevel) {
                "CRÍTICO" -> Color(0xFFE74C3C)
                "ALTO" -> Color(0xFFE67E22)
                "MODERADO" -> WarningOrange
                else -> SuccessGreen
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = riskColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(dimensions.paddingMedium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = riskColor,
                            modifier = Modifier.size(dimensions.iconLarge)
                        )
                        Column {
                            Text(
                                "Nivel de Riesgo",
                                fontSize = dimensions.textSizeSmall,
                                color = textSecondaryColor
                            )
                            Text(
                                stats.riskLevel,
                                fontSize = dimensions.textSizeLarge,
                                fontWeight = FontWeight.Bold,
                                color = riskColor
                            )
                        }
                    }
                }
            }

            // Tarjetas de resumen
            Text(
                "Resumen General",
                fontSize = dimensions.textSizeLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Assessment,
                    label = "Total Casos",
                    value = stats.totalCases.toString(),
                    color = PrimaryBlue,
                    dimensions = dimensions,
                    textSecondaryColor = textSecondaryColor
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalHospital,
                    label = "Activos",
                    value = stats.activeCases.toString(),
                    color = DangerRed,
                    dimensions = dimensions,
                    textSecondaryColor = textSecondaryColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    label = "Recuperados",
                    value = stats.recoveredCases.toString(),
                    color = SuccessGreen,
                    dimensions = dimensions,
                    textSecondaryColor = textSecondaryColor
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Dangerous,
                    label = "Fallecidos",
                    value = stats.deceasedCases.toString(),
                    color = Color(0xFF5D4157),
                    dimensions = dimensions,
                    textSecondaryColor = textSecondaryColor
                )
            }

            // Distribución por tipo de dengue
            if (stats.casesByType.isNotEmpty()) {
                HorizontalDivider(color = textSecondaryColor.copy(alpha = 0.2f))

                Text(
                    "Casos por Tipo de Dengue",
                    fontSize = dimensions.textSizeLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                stats.casesByType.forEach { (type, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        when {
                                            type.contains("Clásico", ignoreCase = true) -> Color(0xFFFF6B6B)
                                            type.contains("Hemorrágico", ignoreCase = true) -> Color(0xFFFF8C42)
                                            type.contains("Grave", ignoreCase = true) -> Color(0xFFE53935)
                                            else -> PrimaryBlue
                                        },
                                        CircleShape
                                    )
                            )
                            Text(
                                type,
                                fontSize = dimensions.textSizeMedium,
                                color = textColor
                            )
                        }
                        Text(
                            count.toString(),
                            fontSize = dimensions.textSizeMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }

            // Distribución por grupo etario
            if (stats.casesByAgeGroup.isNotEmpty()) {
                HorizontalDivider(color = textSecondaryColor.copy(alpha = 0.2f))

                Text(
                    "Casos por Grupo de Edad",
                    fontSize = dimensions.textSizeLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                stats.casesByAgeGroup.toList().sortedBy {
                    when {
                        it.first.startsWith("0-4") -> 1
                        it.first.startsWith("5-14") -> 2
                        it.first.startsWith("15-49") -> 3
                        it.first.startsWith("50-64") -> 4
                        else -> 5
                    }
                }.forEach { (ageGroup, count) ->
                    val percentage = (count.toFloat() / stats.totalCases * 100).toInt()

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                ageGroup,
                                fontSize = dimensions.textSizeMedium,
                                color = textColor
                            )
                            Text(
                                "$count ($percentage%)",
                                fontSize = dimensions.textSizeMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        // Barra de progreso
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(textSecondaryColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(percentage / 100f)
                                    .fillMaxHeight()
                                    .background(
                                        when {
                                            ageGroup.startsWith("0-4") -> Color(0xFF9C27B0)
                                            ageGroup.startsWith("5-14") -> Color(0xFF2196F3)
                                            ageGroup.startsWith("15-49") -> Color(0xFF4CAF50)
                                            ageGroup.startsWith("50-64") -> Color(0xFFFF9800)
                                            else -> Color(0xFFF44336)
                                        },
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }
            }

            // Áreas más afectadas
            if (stats.mostAffectedAreas.isNotEmpty()) {
                HorizontalDivider(color = textSecondaryColor.copy(alpha = 0.2f))

                Text(
                    "🔥 Zonas Más Afectadas",
                    fontSize = dimensions.textSizeLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                stats.mostAffectedAreas.forEachIndexed { index, (area, count) ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (index) {
                                0 -> DangerRed.copy(alpha = 0.15f)
                                1 -> WarningOrange.copy(alpha = 0.15f)
                                else -> PrimaryBlue.copy(alpha = 0.1f)
                            }
                        ),
                        shape = RoundedCornerShape(dimensions.paddingSmall)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensions.paddingMedium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                            ) {
                                Text(
                                    "#${index + 1}",
                                    fontSize = dimensions.textSizeMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when (index) {
                                        0 -> DangerRed
                                        1 -> WarningOrange
                                        else -> PrimaryBlue
                                    }
                                )
                                Text(
                                    area,
                                    fontSize = dimensions.textSizeMedium,
                                    color = textColor
                                )
                            }
                            Text(
                                "$count casos",
                                fontSize = dimensions.textSizeMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    textSecondaryColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(dimensions.paddingMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(dimensions.iconLarge)
            )
            Text(
                value,
                fontSize = dimensions.textSizeExtraLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                label,
                fontSize = dimensions.textSizeSmall,
                color = textSecondaryColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, modifier: Modifier = Modifier) {
    val dimensions = rememberAppDimensions()
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color(0xFFB0B0B0) else Color.Gray

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.paddingSmall)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            label,
            fontSize = dimensions.textSizeSmall,
            color = textColor
        )
    }
}
