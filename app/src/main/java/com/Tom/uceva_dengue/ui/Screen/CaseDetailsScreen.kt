package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .background(Color(0xFFF8F9FD))
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            error != null -> {
                Text(
                    text = error ?: "Error inesperado",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            caseModel != null -> {
                val c = caseModel!!

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = if (isEditing) "Editar Caso #${c.ID_CASOREPORTADO}" else "Detalle Caso #${c.ID_CASOREPORTADO}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Paciente (read-only)
                    item {
                        InfoCard(label = "Paciente", value = c.NOMBRE_PACIENTE)
                    }
                    // Fecha y reportado por
                    item {
                        InfoCard(label = "Fecha Reporte", value = c.FECHA_CASOREPORTADO)
                    }
                    item {
                        InfoCard(label = "Reportado por", value = c.NOMBRE_PERSONALMEDICO)
                    }

                    // Estado (editable or read-only)
                    item {
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
                            InfoCard(label = "Estado", value = c.NOMBRE_ESTADOCASO)
                        }
                    }

                    item {
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
                            InfoCard(label = "Tipo de Dengue", value = c.NOMBRE_TIPODENGUE)
                        }
                    }

                    // Descripción (editable or read-only)
                    item {
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
                                    .heightIn(min = 100.dp),
                                maxLines = 5
                            )
                        } else {
                            InfoCard(label = "Descripción", value = c.DESCRIPCION_CASOREPORTADO)
                        }
                    }

                    if (isEditing) {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // revert changes
                                        localDesc = c.DESCRIPCION_CASOREPORTADO ?: ""
                                        localStateName = c.NOMBRE_ESTADOCASO
                                        localDengueName = c.NOMBRE_TIPODENGUE
                                        viewModel.fetchData(caseId)
                                        isEditing = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        viewModel.updateCase(
                                            c.ID_CASOREPORTADO,
                                            onSuccess = {
                                                navController.popBackStack()
                                                Toast.makeText(context, "Caso actualizado", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { error ->

                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar", color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Botón flotante para entrar en modo edición
                if (!isEditing) {
                    FloatingActionButton(
                        onClick = { isEditing = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = Color(0xFF00796B),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String?) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value.takeIf { !it.isNullOrBlank() } ?: "Sin información",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B1B1F),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
