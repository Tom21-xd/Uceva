package com.Tom.uceva_dengue.ui.Screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.viewModel.CreateHospitalViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHospitalScreen(
    navController: NavHostController,
    viewModel: CreateHospitalViewModel = viewModel()
) {
    val context = LocalContext.current
    val departamentos by viewModel.departamentos.collectAsState()
    val municipios by viewModel.municipios.collectAsState()
    val department by viewModel.department.observeAsState(initial = "")
    val cityName by viewModel.cityName.observeAsState(initial = "")
    val nombre by viewModel.nombre.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val latitud by viewModel.latitud.collectAsState()
    val longitud by viewModel.longitud.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showMapDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de imagen
            Text(
                text = "Imagen del Hospital *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp) // Reducido de 200dp y responsivo
                    .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false)
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .then(
                        if (selectedImageUri == null) {
                            Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Seleccionar Imagen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Toca para seleccionar imagen",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Requerida",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.setNombre(it) },
                label = { Text("Nombre del Hospital *") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Campo Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { viewModel.setDireccion(it) },
                label = { Text("Dirección *") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // ComboBox Departamento
            ComboBox(
                selectedValue = department,
                options = departamentos.map { it.NOMBRE_DEPARTAMENTO },
                label = "Departamento *"
            ) { nuevoDepartamento ->
                val departamentoSeleccionado = departamentos.firstOrNull {
                    it.NOMBRE_DEPARTAMENTO == nuevoDepartamento
                }
                departamentoSeleccionado?.ID_DEPARTAMENTO?.let {
                    viewModel.fetchMunicipios(it.toString())
                    viewModel.setDepartment(nuevoDepartamento)
                }
            }

            // ComboBox Municipio
            ComboBox(
                selectedValue = cityName,
                options = municipios.map { it.NOMBRE_MUNICIPIO ?: "" },
                label = "Municipio *"
            ) { nuevaCiudad ->
                val ciudadSeleccionada = municipios.firstOrNull {
                    it.NOMBRE_MUNICIPIO == nuevaCiudad
                }
                val idCiudad = ciudadSeleccionada?.ID_MUNICIPIO ?: 0
                viewModel.setCityId(idCiudad)
                viewModel.setCityName(nuevaCiudad)
            }

            // Sección de Ubicación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ubicación del Hospital *",
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

            // Mapa interactivo
            var selectedLocation by remember {
                mutableStateOf<LatLng?>(
                    if (latitud.isNotEmpty() && longitud.isNotEmpty()) {
                        try {
                            LatLng(latitud.toDouble(), longitud.toDouble())
                        } catch (e: Exception) {
                            LatLng(3.4516, -76.5320) // Tulua, Valle del Cauca por defecto
                        }
                    } else {
                        LatLng(3.4516, -76.5320)
                    }
                )
            }

            // Vista previa del mapa (solo visualización)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Reducido de 250dp y responsivo
                    .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false)
                    .clickable { showMapDialog = true },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(selectedLocation ?: LatLng(3.4516, -76.5320), 15f)
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

                    // Overlay para indicar que se puede tocar
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
                                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }

            // Campos de Coordenadas (solo lectura/información)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitud,
                    onValueChange = {
                        viewModel.setLatitud(it)
                        // Actualizar mapa si las coordenadas son válidas
                        if (it.isNotEmpty() && longitud.isNotEmpty()) {
                            try {
                                selectedLocation = LatLng(it.toDouble(), longitud.toDouble())
                            } catch (e: Exception) {
                                // Ignorar errores de conversión
                            }
                        }
                    },
                    label = { Text("Latitud *") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E81F4),
                        focusedLabelColor = Color(0xFF5E81F4)
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
                        viewModel.setLongitud(it)
                        // Actualizar mapa si las coordenadas son válidas
                        if (latitud.isNotEmpty() && it.isNotEmpty()) {
                            try {
                                selectedLocation = LatLng(latitud.toDouble(), it.toDouble())
                            } catch (e: Exception) {
                                // Ignorar errores de conversión
                            }
                        }
                    },
                    label = { Text("Longitud *") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E81F4),
                        focusedLabelColor = Color(0xFF5E81F4)
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
                text = "Tip: Toca en el mapa para seleccionar la ubicación del hospital o ingresa las coordenadas manualmente",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Crear
            Button(
                onClick = {
                    isLoading = true
                    viewModel.createHospital(
                        context = context,
                        imageUri = selectedImageUri,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Hospital creado con éxito",
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
                        "Crear Hospital",
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

        // Diálogo con mapa completo para selección precisa
        if (showMapDialog) {
            Dialog(
                onDismissRequest = { showMapDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    val dialogCameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            if (latitud.isNotEmpty() && longitud.isNotEmpty()) {
                                try {
                                    LatLng(latitud.toDouble(), longitud.toDouble())
                                } catch (e: Exception) {
                                    LatLng(3.4516, -76.5320)
                                }
                            } else {
                                LatLng(3.4516, -76.5320)
                            },
                            15f
                        )
                    }

                    var tempSelectedLocation by remember {
                        mutableStateOf(
                            if (latitud.isNotEmpty() && longitud.isNotEmpty()) {
                                try {
                                    LatLng(latitud.toDouble(), longitud.toDouble())
                                } catch (e: Exception) {
                                    LatLng(3.4516, -76.5320)
                                }
                            } else {
                                LatLng(3.4516, -76.5320)
                            }
                        )
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = dialogCameraPositionState,
                        onMapClick = { latLng ->
                            tempSelectedLocation = latLng
                        },
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = true
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
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Información de coordenadas y botón confirmar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lat: ${String.format("%.6f", tempSelectedLocation.latitude)}, Lng: ${String.format("%.6f", tempSelectedLocation.longitude)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.setLatitud(tempSelectedLocation.latitude.toString())
                                viewModel.setLongitud(tempSelectedLocation.longitude.toString())
                                showMapDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Confirmar Ubicación", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
