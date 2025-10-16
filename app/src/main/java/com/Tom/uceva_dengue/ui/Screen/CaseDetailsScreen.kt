package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.viewModel.CaseDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsScreen(
    caseId: String,
    viewModel: CaseDetailsViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    // Observing all relevant state
    val caseModel by viewModel.case.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val description by viewModel.description.collectAsState()
    val states by viewModel.states.collectAsState()
    val dengueTypes by viewModel.typesOfDengue.collectAsState()
    val selectedStateId by viewModel.selectedStateId.collectAsState()
    val selectedDengueId by viewModel.selectedDengueTypeId.collectAsState()

    // Local UI state for edit mode and interim values
    var isEditing by remember { mutableStateOf(false) }
    var localDesc by remember { mutableStateOf("") }
    var localStateName by remember { mutableStateOf("") }
    var localDengueName by remember { mutableStateOf("") }

    // Load everything once
    LaunchedEffect(caseId) {
        viewModel.fetchData(caseId)
    }

    // Whenever the caseModel loads or changes, seed the local edit buffers
    LaunchedEffect(caseModel) {
        caseModel?.let { c ->
            localDesc = c.DESCRIPCION_CASOREPORTADO ?: ""
            localStateName = states.firstOrNull { it.ID_ESTADOCASO == c.FK_ID_ESTADOCASO }?.NOMBRE_ESTADOCASO
                ?: ""
            localDengueName = dengueTypes.firstOrNull { it.ID_TIPODENGUE == c.FK_ID_TIPODENGUE }
                ?.NOMBRE_TIPODENGUE ?: ""
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            error != null -> {
                Text(
                    text = error ?: "Error inesperado",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            caseModel != null -> {
                val c = caseModel!!

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Contenido principal
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Card de información del paciente
                            ModernInfoCard(
                                icon = Icons.Default.Person,
                                title = "Información del Paciente",
                                items = listOf(
                                    "Paciente" to c.NOMBRE_PACIENTE,
                                    "Fecha Reporte" to c.FECHA_CASOREPORTADO,
                                    "Reportado por" to c.NOMBRE_PERSONALMEDICO
                                )
                            )

                            // Card de información médica
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocalHospital,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(12.dp).size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Información Médica",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (isEditing) {
                                        ComboBox(
                                            selectedValue = localStateName,
                                            options = states.map { it.NOMBRE_ESTADOCASO },
                                            label = "Estado del Caso",
                                        ) { choice ->
                                            localStateName = choice
                                            viewModel.setSelectedState(choice)
                                        }
                                    } else {
                                        ModernInfoItem(label = "Estado", value = c.NOMBRE_ESTADOCASO)
                                    }

                                    if (isEditing) {
                                        ComboBox(
                                            selectedValue = localDengueName,
                                            options = dengueTypes.map { it.NOMBRE_TIPODENGUE },
                                            label = "Tipo de Dengue",
                                        ) { choice ->
                                            localDengueName = choice
                                            viewModel.setSelectedDengue(choice)
                                        }
                                    } else {
                                        ModernInfoItem(label = "Tipo de Dengue", value = c.NOMBRE_TIPODENGUE)
                                    }

                                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = localDesc,
                                            onValueChange = {
                                                localDesc = it
                                                viewModel.setDescription(it)
                                            },
                                            label = { Text("Descripción") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 120.dp),
                                            maxLines = 5,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    } else {
                                        ModernInfoItem(label = "Descripción", value = c.DESCRIPCION_CASOREPORTADO)
                                    }
                                }
                            }

                            if (isEditing) {
                                // Botones de acción
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            localDesc = c.DESCRIPCION_CASOREPORTADO ?: ""
                                            localStateName = c.NOMBRE_ESTADOCASO
                                            localDengueName = c.NOMBRE_TIPODENGUE
                                            viewModel.fetchData(caseId)
                                            isEditing = false
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFF5E81F4)
                                        )
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Cancelar", fontWeight = FontWeight.SemiBold)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.updateCase(
                                                c.ID_CASOREPORTADO,
                                                onSuccess = {
                                                    navController.popBackStack()
                                                    Toast.makeText(context, "Caso actualizado", Toast.LENGTH_SHORT).show()
                                                },
                                                onError = { error -> }
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF5E81F4)
                                        )
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                // Botón flotante para entrar en modo edición
                if (!isEditing) {
                    FloatingActionButton(
                        onClick = { isEditing = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp),
                        containerColor = Color(0xFF5E81F4),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernInfoCard(
    icon: ImageVector,
    title: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1B1F)
                )
            }

            items.forEach { (label, value) ->
                ModernInfoItem(label = label, value = value)
            }
        }
    }
}

@Composable
private fun ModernInfoItem(label: String, value: String?) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.takeIf { !it.isNullOrBlank() } ?: "Sin información",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B1B1F)
        )
    }
}
