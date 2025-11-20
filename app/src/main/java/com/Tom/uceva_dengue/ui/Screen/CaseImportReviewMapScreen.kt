package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.ImportedCaseDto
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Colores
private val PrimaryBlue = Color(0xFF5E81F4)
private val SuccessGreen = Color(0xFF26DE81)
private val DangerRed = Color(0xFFFF4757)
private val WarningOrange = Color(0xFFFFA502)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseImportReviewMapScreen(
    navController: NavController,
    importedCases: List<ImportedCaseDto>,
    onDeleteCase: (Int) -> Unit,
    onFinish: () -> Unit
) {
    val dimensions = rememberAppDimensions()
    var casesToDelete by remember { mutableStateOf(setOf<Int>()) }
    var selectedCase by remember { mutableStateOf<ImportedCaseDto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeletingCases by remember { mutableStateOf(false) }

    // Calcular centro del mapa (promedio de todas las coordenadas válidas)
    val validCases = importedCases.filter {
        it.latitude != null && it.longitude != null &&
        it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
    }

    val centerLat = if (validCases.isNotEmpty()) {
        validCases.mapNotNull { it.latitude }.average()
    } else {
        4.5389 // Coordenadas por defecto de Tuluá, Valle del Cauca
    }

    val centerLng = if (validCases.isNotEmpty()) {
        validCases.mapNotNull { it.longitude }.average()
    } else {
        -76.1950
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(centerLat, centerLng),
            12f
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Revisar Casos Importados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${validCases.size} casos en el mapa",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium)
                ) {
                    // Información de casos a eliminar
                    if (casesToDelete.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dimensions.paddingSmall),
                            colors = CardDefaults.cardColors(
                                containerColor = WarningOrange.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(dimensions.paddingSmall)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensions.paddingMedium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = WarningOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                                Text(
                                    "${casesToDelete.size} caso(s) serán eliminados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningOrange
                                )
                            }
                        }
                    }

                    // Botón finalizar
                    Button(
                        onClick = {
                            isDeletingCases = true
                            // Eliminar casos marcados
                            casesToDelete.forEach { caseId ->
                                onDeleteCase(caseId)
                            }
                            // Esperar un momento para que se procesen las eliminaciones
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                onFinish()
                            }, 500)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDeletingCases,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        ),
                        shape = RoundedCornerShape(dimensions.paddingSmall)
                    ) {
                        if (isDeletingCases) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                            Text("Procesando...")
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                            Text(
                                if (casesToDelete.isEmpty()) "Confirmar Importación"
                                else "Confirmar y Eliminar ${casesToDelete.size} Caso(s)"
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (validCases.isEmpty()) {
                // Sin casos con coordenadas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensions.paddingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.LocationOff,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                    Text(
                        "No hay casos con coordenadas válidas",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }
            } else {
                // Mapa con markers
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = false
                    )
                ) {
                    // Filtrar los casos que NO están marcados para eliminar
                    val activeCases = validCases.filter { !casesToDelete.contains(it.caseId) }

                    activeCases.forEach { case ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    case.latitude ?: 0.0,
                                    case.longitude ?: 0.0
                                )
                            ),
                            title = case.temporaryName ?: "Caso ${case.caseId}",
                            snippet = "${case.neighborhood ?: "Sin barrio"} - ${case.dengueType ?: "Sin clasificar"}",
                            onClick = {
                                selectedCase = case
                                showDeleteDialog = true
                                true
                            }
                        )
                    }
                }

                // Card de instrucciones
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(dimensions.paddingMedium)
                        .fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(dimensions.paddingMedium),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                        Text(
                            "Toca un marcador para revisarlo o eliminarlo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Diálogo de confirmación de eliminación
        if (showDeleteDialog && selectedCase != null) {
            val isMarked = casesToDelete.contains(selectedCase?.caseId)

            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedCase = null
                },
                icon = {
                    Icon(
                        if (isMarked) Icons.Default.Undo else Icons.Default.Delete,
                        contentDescription = null,
                        tint = if (isMarked) WarningOrange else DangerRed,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        if (isMarked) "Caso marcado para eliminar" else "¿Marcar para eliminar?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            "Información del caso:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Nombre: ${selectedCase?.temporaryName ?: "Sin nombre"}")
                        Text("• Barrio: ${selectedCase?.neighborhood ?: "Sin barrio"}")
                        Text("• Tipo: ${selectedCase?.dengueType ?: "Sin clasificar"}")
                        Text("• Edad: ${selectedCase?.age ?: "N/A"} años")
                        Text("• Año: ${selectedCase?.year ?: "N/A"}")
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isMarked) {
                            androidx.compose.material3.Surface(
                                color = WarningOrange.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = WarningOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Este caso será eliminado al confirmar",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = WarningOrange
                                    )
                                }
                            }
                        } else {
                            Text(
                                "El caso desaparecerá del mapa y será eliminado al confirmar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedCase?.let { case ->
                                if (casesToDelete.contains(case.caseId)) {
                                    casesToDelete = casesToDelete - case.caseId
                                } else {
                                    casesToDelete = casesToDelete + case.caseId
                                }
                            }
                            showDeleteDialog = false
                            selectedCase = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMarked) SuccessGreen else DangerRed
                        )
                    ) {
                        Icon(
                            if (isMarked) Icons.Default.Undo else Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isMarked) "Desmarcar" else "Marcar para Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        selectedCase = null
                    }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}
