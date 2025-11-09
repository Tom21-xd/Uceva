package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.ui.viewModel.HospitalViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.location.LocationServices
import com.Tom.uceva_dengue.utils.moveToUserLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateHospitalScreen(
    hospitalId: Int,
    navController: NavHostController,
    viewModel: HospitalViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var selectedCityId by remember { mutableStateOf<Int?>(null) }
    var selectedCityName by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Estados de UI
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Para el dropdown de municipios
    var cities by remember { mutableStateOf<List<CityModel>>(emptyList()) }
    var expandedCityDropdown by remember { mutableStateOf(false) }

    // Para el mapa
    var showMapDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // Cargar municipios
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.cityService.getCities("")
            cities = response
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar municipios: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Cargar datos del hospital
    LaunchedEffect(hospitalId) {
        viewModel.getHospitalById(
            id = hospitalId,
            onSuccess = { hospital ->
                nombre = hospital.NOMBRE_HOSPITAL
                direccion = hospital.DIRECCION_HOSPITAL ?: ""
                latitud = hospital.LATITUD_HOSPITAL ?: ""
                longitud = hospital.LONGITUD_HOSPITAL ?: ""
                selectedCityId = hospital.FK_ID_MUNICIPIO
                selectedCityName = hospital.NOMBRE_MUNICIPIO
                imageUrl = if (!hospital.IMAGEN_HOSPITAL.isNullOrBlank()) {
                    "https://api.prometeondev.com/Image/getImage/${hospital.IMAGEN_HOSPITAL}"
                } else ""

                // Inicializar ubicación en el mapa
                if (latitud.isNotEmpty() && longitud.isNotEmpty()) {
                    try {
                        selectedLocation = LatLng(latitud.toDouble(), longitud.toDouble())
                    } catch (e: Exception) {
                        selectedLocation = LatLng(3.4516, -76.5320) // Tuluá por defecto
                    }
                } else {
                    selectedLocation = LatLng(3.4516, -76.5320)
                }

                isLoadingData = false
            },
            onError = { error ->
                errorMessage = error
                isLoadingData = false
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoadingData) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error al cargar el hospital", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigateUp() }) {
                        Text("Volver")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mostrar imagen actual
                if (imageUrl.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = "Imagen del hospital",
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Error al cargar imagen", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }

                    Text(
                        text = "Nota: La imagen no se puede modificar desde aquí",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Hospital *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Campo Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Dropdown de Municipios
                ExposedDropdownMenuBox(
                    expanded = expandedCityDropdown,
                    onExpandedChange = { expandedCityDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCityName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Municipio") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar municipio"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCityDropdown,
                        onDismissRequest = { expandedCityDropdown = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city.NOMBRE_MUNICIPIO) },
                                onClick = {
                                    selectedCityId = city.ID_MUNICIPIO
                                    selectedCityName = city.NOMBRE_MUNICIPIO
                                    expandedCityDropdown = false
                                }
                            )
                        }
                    }
                }

                // Sección de Ubicación con Mapa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ubicación del Hospital",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = { showMapDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOutMap,
                            contentDescription = "Expandir mapa",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Vista previa del mapa
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false)
                        .clickable { showMapDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                selectedLocation ?: LatLng(3.4516, -76.5320),
                                15f
                            )
                        }

                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false,
                                rotationGesturesEnabled = false,
                                tiltGesturesEnabled = false,
                                zoomControlsEnabled = false
                            )
                        ) {
                            selectedLocation?.let { location ->
                                Marker(
                                    state = MarkerState(position = location),
                                    title = "Ubicación del Hospital"
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Toca para seleccionar ubicación",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            )
                        }
                    }
                }

                // Campos de Coordenadas (editables y sincronizados con mapa)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitud,
                        onValueChange = {
                            latitud = it
                            if (it.isNotEmpty() && longitud.isNotEmpty()) {
                                try {
                                    selectedLocation = LatLng(it.toDouble(), longitud.toDouble())
                                } catch (e: Exception) {
                                    // Ignorar errores de conversión
                                }
                            }
                        },
                        label = { Text("Latitud") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    OutlinedTextField(
                        value = longitud,
                        onValueChange = {
                            longitud = it
                            if (latitud.isNotEmpty() && it.isNotEmpty()) {
                                try {
                                    selectedLocation = LatLng(latitud.toDouble(), it.toDouble())
                                } catch (e: Exception) {
                                    // Ignorar errores de conversión
                                }
                            }
                        },
                        label = { Text("Longitud") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                Text(
                    text = "Tip: Toca en el mapa para seleccionar la ubicación o ingresa las coordenadas manualmente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "* Campos obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Actualizar
                Button(
                    onClick = {
                        if (nombre.isEmpty()) {
                            Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        viewModel.updateHospital(
                            id = hospitalId,
                            nombre = nombre,
                            direccion = direccion.ifBlank { null },
                            latitud = latitud.ifBlank { null },
                            longitud = longitud.ifBlank { null },
                            idMunicipio = selectedCityId,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Hospital actualizado con éxito",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigateUp()
                            },
                            onError = { error ->
                                isLoading = false
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Actualizar Hospital",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Cancelar
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancelar", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // Diálogo con mapa para selección precisa
            if (showMapDialog) {
                Dialog(
                    onDismissRequest = { showMapDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    val configuration = LocalConfiguration.current
                    val screenHeight = configuration.screenHeightDp.dp
                    val dialogHeight = screenHeight * 0.75f

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dialogHeight)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val dialogCameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(
                                    selectedLocation ?: LatLng(3.4516, -76.5320),
                                    15f
                                )
                            }

                            var tempSelectedLocation by remember {
                                mutableStateOf(selectedLocation ?: LatLng(3.4516, -76.5320))
                            }

                            val fusedLocationClient = remember {
                                LocationServices.getFusedLocationProviderClient(context)
                            }

                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = dialogCameraPositionState,
                                onMapClick = { latLng ->
                                    tempSelectedLocation = latLng
                                },
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = true,
                                    myLocationButtonEnabled = false
                                )
                            ) {
                                Marker(
                                    state = MarkerState(position = tempSelectedLocation),
                                    title = "Ubicación del Hospital",
                                    snippet = "Lat: ${tempSelectedLocation.latitude}, Lng: ${tempSelectedLocation.longitude}"
                                )
                            }

                            // Botón cerrar
                            IconButton(
                                onClick = { showMapDialog = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Botón para obtener ubicación actual
                            IconButton(
                                onClick = {
                                    moveToUserLocation(
                                        context = context,
                                        fusedLocationClient = fusedLocationClient,
                                        cameraPositionState = dialogCameraPositionState,
                                        onLocationFound = { location ->
                                            tempSelectedLocation = location
                                            Toast.makeText(context, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Mi ubicación",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Panel inferior compacto
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(
                                        Color.White.copy(alpha = 0.95f),
                                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                    )
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Coordenadas
                                Text(
                                    text = "${String.format("%.4f", tempSelectedLocation.latitude)}, ${String.format("%.4f", tempSelectedLocation.longitude)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )

                                // Botón confirmar
                                Button(
                                    onClick = {
                                        latitud = tempSelectedLocation.latitude.toString()
                                        longitud = tempSelectedLocation.longitude.toString()
                                        selectedLocation = tempSelectedLocation
                                        showMapDialog = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Confirmar Ubicación",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
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
