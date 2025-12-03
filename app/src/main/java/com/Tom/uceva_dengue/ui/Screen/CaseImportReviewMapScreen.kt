package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.ImportedCaseDto
import com.Tom.uceva_dengue.ui.viewModel.CaseImportViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Colores
private val PrimaryBlue = Color(0xFF5E81F4)
private val SuccessGreen = Color(0xFF26DE81)
private val DangerRed = Color(0xFFFF4757)
private val WarningOrange = Color(0xFFFFA502)
private val EditBlue = Color(0xFF3498DB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseImportReviewMapScreen(
    navController: NavController,
    importedCases: List<ImportedCaseDto>,
    onDeleteCase: (Int) -> Unit,
    onFinish: () -> Unit,
    viewModel: CaseImportViewModel = viewModel()
) {
    val context = LocalContext.current
    val dimensions = rememberAppDimensions()

    // Estados
    var casesToDelete by remember { mutableStateOf(setOf<Int>()) }
    var selectedCase by remember { mutableStateOf<ImportedCaseDto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeletingCases by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var pendingCoordinateUpdates by remember { mutableStateOf(mapOf<Int, LatLng>()) }

    // Crear estados de markers para cada caso
    val markerStates = remember(importedCases) {
        importedCases.associate { case ->
            case.caseId to mutableStateOf(
                LatLng(case.latitude ?: 0.0, case.longitude ?: 0.0)
            )
        }
    }

    // Casos válidos
    val validCases = importedCases.filter {
        it.latitude != null && it.longitude != null &&
        it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
    }

    // Centro del mapa
    val centerLat = if (validCases.isNotEmpty()) {
        validCases.mapNotNull { it.latitude }.average()
    } else 4.5389

    val centerLng = if (validCases.isNotEmpty()) {
        validCases.mapNotNull { it.longitude }.average()
    } else -76.1950

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(centerLat, centerLng), 12f)
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
                            "${validCases.size} casos" + if (isEditMode) " - Modo Edición" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditMode) "Finalizar edición" else "Modo edición",
                            tint = if (isEditMode) SuccessGreen else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isEditMode) EditBlue else PrimaryBlue,
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
                    // Info modo edición
                    if (isEditMode) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = EditBlue.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TouchApp, null, tint = EditBlue)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Modo Edición", fontWeight = FontWeight.Bold, color = EditBlue)
                                    Text("Arrastra los marcadores para ajustar", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Info coordenadas modificadas
                    if (pendingCoordinateUpdates.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = SuccessGreen)
                                Spacer(Modifier.width(8.dp))
                                Text("${pendingCoordinateUpdates.size} ubicación(es) modificadas", fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                    }

                    // Info casos a eliminar
                    if (casesToDelete.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.1f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = WarningOrange)
                                Spacer(Modifier.width(8.dp))
                                Text("${casesToDelete.size} caso(s) serán eliminados", fontWeight = FontWeight.Bold, color = WarningOrange)
                            }
                        }
                    }

                    // Botón confirmar
                    Button(
                        onClick = {
                            isDeletingCases = true
                            val totalUpdates = pendingCoordinateUpdates.size

                            if (totalUpdates > 0) {
                                var completed = 0
                                pendingCoordinateUpdates.forEach { (caseId, coords) ->
                                    viewModel.updateCaseCoordinates(caseId, coords.latitude, coords.longitude,
                                        onSuccess = {
                                            completed++
                                            if (completed == totalUpdates) {
                                                processDeletions(casesToDelete, viewModel, onFinish)
                                            }
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                            completed++
                                            if (completed == totalUpdates) {
                                                processDeletions(casesToDelete, viewModel, onFinish)
                                            }
                                        }
                                    )
                                }
                            } else {
                                processDeletions(casesToDelete, viewModel, onFinish)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDeletingCases,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isDeletingCases) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Procesando...")
                        } else {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(8.dp))
                            val text = buildString {
                                append("Confirmar")
                                if (pendingCoordinateUpdates.isNotEmpty()) append(" (${pendingCoordinateUpdates.size} ediciones)")
                                if (casesToDelete.isNotEmpty()) append(" y Eliminar ${casesToDelete.size}")
                            }
                            Text(text)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (validCases.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.LocationOff, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No hay casos con coordenadas válidas", color = Color.Gray)
                }
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false)
                ) {
                    val activeCases = validCases.filter { !casesToDelete.contains(it.caseId) }

                    activeCases.forEach { case ->
                        val currentPosition = pendingCoordinateUpdates[case.caseId]
                            ?: LatLng(case.latitude ?: 0.0, case.longitude ?: 0.0)

                        val markerState = rememberMarkerState(
                            key = "marker_${case.caseId}_${isEditMode}",
                            position = currentPosition
                        )

                        // Detectar cuando el marker fue arrastrado
                        LaunchedEffect(markerState.position) {
                            val originalPos = LatLng(case.latitude ?: 0.0, case.longitude ?: 0.0)
                            val hasMoved = markerState.position.latitude != originalPos.latitude ||
                                           markerState.position.longitude != originalPos.longitude

                            // Solo actualizar si realmente se movió y estamos en modo edición
                            if (hasMoved && isEditMode) {
                                pendingCoordinateUpdates = pendingCoordinateUpdates + (case.caseId to markerState.position)
                            }
                        }

                        Marker(
                            state = markerState,
                            title = case.temporaryName ?: "Caso ${case.caseId}",
                            snippet = "${case.neighborhood ?: "Sin barrio"} - ${case.dengueType ?: "Sin tipo"}",
                            draggable = isEditMode,
                            onClick = {
                                if (!isEditMode) {
                                    selectedCase = case
                                    showDeleteDialog = true
                                }
                                true
                            }
                        )
                    }
                }

                // Card de instrucciones
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEditMode) EditBlue.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isEditMode) Icons.Default.TouchApp else Icons.Default.Info,
                            null,
                            tint = if (isEditMode) Color.White else PrimaryBlue
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isEditMode) "Mantén presionado y arrastra para mover"
                            else "Toca un marcador para revisarlo",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isEditMode) Color.White else Color.DarkGray
                        )
                    }
                }
            }
        }

        // Diálogo
        if (showDeleteDialog && selectedCase != null) {
            val isMarked = casesToDelete.contains(selectedCase?.caseId)
            val isModified = pendingCoordinateUpdates.containsKey(selectedCase?.caseId)

            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; selectedCase = null },
                icon = {
                    Icon(
                        when {
                            isMarked -> Icons.AutoMirrored.Filled.Undo
                            isModified -> Icons.Default.EditLocation
                            else -> Icons.Default.Delete
                        },
                        null,
                        tint = when {
                            isMarked -> WarningOrange
                            isModified -> SuccessGreen
                            else -> DangerRed
                        },
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        when {
                            isMarked -> "Caso marcado para eliminar"
                            isModified -> "Ubicación modificada"
                            else -> "Opciones del caso"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text("• Nombre: ${selectedCase?.temporaryName ?: "Sin nombre"}")
                        Text("• Barrio: ${selectedCase?.neighborhood ?: "Sin barrio"}")
                        Text("• Tipo: ${selectedCase?.dengueType ?: "Sin clasificar"}")
                        Text("• Edad: ${selectedCase?.age ?: "N/A"} años")
                        Text("• Año: ${selectedCase?.year ?: "N/A"}")

                        if (isModified) {
                            Spacer(Modifier.height(8.dp))
                            val coords = pendingCoordinateUpdates[selectedCase?.caseId]
                            Text(
                                "Nueva ubicación: ${String.format("%.6f", coords?.latitude)}, ${String.format("%.6f", coords?.longitude)}",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (isMarked) "Este caso será eliminado al confirmar"
                            else "Puedes marcar para eliminar o editar ubicación",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isMarked) WarningOrange else Color.DarkGray
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedCase?.let { case ->
                                casesToDelete = if (casesToDelete.contains(case.caseId))
                                    casesToDelete - case.caseId
                                else
                                    casesToDelete + case.caseId
                            }
                            showDeleteDialog = false
                            selectedCase = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMarked) SuccessGreen else DangerRed
                        )
                    ) {
                        Icon(if (isMarked) Icons.AutoMirrored.Filled.Undo else Icons.Default.Delete, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (isMarked) "Desmarcar" else "Marcar para Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false; selectedCase = null }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

private fun processDeletions(
    casesToDelete: Set<Int>,
    viewModel: CaseImportViewModel,
    onFinish: () -> Unit
) {
    if (casesToDelete.isEmpty()) {
        onFinish()
        return
    }

    var completed = 0
    casesToDelete.forEach { caseId ->
        viewModel.deleteCase(caseId,
            onSuccess = {
                completed++
                if (completed == casesToDelete.size) onFinish()
            },
            onError = {
                completed++
                if (completed == casesToDelete.size) onFinish()
            }
        )
    }
}
