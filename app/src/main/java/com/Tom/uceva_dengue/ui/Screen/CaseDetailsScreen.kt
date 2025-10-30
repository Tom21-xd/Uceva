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
import com.Tom.uceva_dengue.ui.theme.*
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsScreen(
    caseId: String,
    viewModel: CaseDetailsViewModel,
    navController: NavHostController
) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()
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
                    verticalArrangement = Arrangement.spacedBy(paddingMedium),
                    contentPadding = PaddingValues(paddingMedium),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Contenido principal
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingMedium),
                            verticalArrangement = Arrangement.spacedBy(paddingMedium)
                        ) {
                            // Card de información del paciente
                            ModernInfoCard(
                                icon = Icons.Default.Person,
                                title = "Información del Paciente",
                                iconBackgroundColor = Color(0xFFB2DFDB),
                                iconTintColor = Color(0xFF00796B),
                                items = listOf(
                                    "Paciente" to c.NOMBRE_PACIENTE,
                                    "Fecha Reporte" to c.FECHA_CASOREPORTADO,
                                    "Reportado por" to c.NOMBRE_PERSONALMEDICO
                                )
                            )

                            // Card de información médica
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(borderRadiusMedium),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(paddingLarge),
                                    verticalArrangement = Arrangement.spacedBy(paddingMedium)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(iconBackgroundRadius),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocalHospital,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(iconPadding).size(iconSizeMedium)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(spacerMedium))
                                        Text(
                                            text = "Información Médica",
                                            fontSize = fontSizeLarge,
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

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

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
                                                .heightIn(min = textFieldHeightLarge),
                                            maxLines = 5,
                                            shape = RoundedCornerShape(iconBackgroundRadius),
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
                                        .padding(vertical = paddingSmall),
                                    horizontalArrangement = Arrangement.spacedBy(spacerMedium)
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
                                            .height(buttonHeight),
                                        shape = RoundedCornerShape(iconBackgroundRadius),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFF00796B)
                                        )
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(iconSizeSmall))
                                        Spacer(modifier = Modifier.width(paddingSmall))
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
                                            .height(buttonHeight),
                                        shape = RoundedCornerShape(iconBackgroundRadius),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00796B)
                                        )
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(iconSizeSmall))
                                        Spacer(modifier = Modifier.width(paddingSmall))
                                        Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            // Botones de evolución clínica
                            if (!isEditing) {
                                Text(
                                    text = "Evolución Clínica",
                                    fontSize = fontSizeLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = paddingSmall, bottom = iconBackgroundRadius)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacerMedium)
                                ) {
                                    // ELIMINADO: Botones de evolución clínica (módulo eliminado)
                                    /* Botón para ver historial
                                    OutlinedButton(
                                        onClick = {
                                            navController.navigate("${com.Tom.uceva_dengue.ui.Navigation.Rout.CaseEvolutionHistoryScreen.name}/${c.ID_CASOREPORTADO}")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(buttonHeight),
                                        shape = RoundedCornerShape(iconBackgroundRadius)
                                    ) {
                                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(iconSizeSmall))
                                        Spacer(modifier = Modifier.width(paddingSmall))
                                        Text("Ver Historial", fontWeight = FontWeight.SemiBold)
                                    }

                                    // Botón para crear nueva evolución
                                    Button(
                                        onClick = {
                                            val dengueTypeId = c.FK_ID_TIPODENGUE ?: 1
                                            val doctorId = c.FK_ID_PERSONALMEDICO ?: 1
                                            navController.navigate(
                                                "${com.Tom.uceva_dengue.ui.Navigation.Rout.CreateCaseEvolutionScreen.name}/${c.ID_CASOREPORTADO}/$dengueTypeId/$doctorId"
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(buttonHeight),
                                        shape = RoundedCornerShape(iconBackgroundRadius),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(iconSizeSmall))
                                        Spacer(modifier = Modifier.width(paddingSmall))
                                        Text("Nueva Evolución", fontWeight = FontWeight.SemiBold)
                                    }
                                    */
                                }
                            }

                            Spacer(modifier = Modifier.height(bottomPadding))
                        }
                    }
                }

                // Botón flotante para entrar en modo edición
                if (!isEditing) {
                    FloatingActionButton(
                        onClick = { isEditing = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(paddingExtraLarge)
                            .size(fabSize),
                        containerColor = Color(0xFF00796B),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(borderRadiusMedium),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = paddingSmall,
                            pressedElevation = iconBackgroundRadius
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(iconSizeLarge))
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
    items: List<Pair<String, String>>,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTintColor: Color = MaterialTheme.colorScheme.primary
) {
    val dimensions = rememberAppDimensions()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(borderRadiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
            verticalArrangement = Arrangement.spacedBy(iconBackgroundRadius)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(iconBackgroundRadius),
                    color = iconBackgroundColor
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.padding(iconPadding).size(iconSizeMedium)
                    )
                }
                Spacer(modifier = Modifier.width(spacerMedium))
                Text(
                    text = title,
                    fontSize = fontSizeLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
    val dimensions = rememberAppDimensions()

    Column {
        Text(
            text = label,
            fontSize = fontSizeSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(paddingExtraSmall))
        Text(
            text = value.takeIf { !it.isNullOrBlank() } ?: "Sin información",
            fontSize = fontSizeMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
