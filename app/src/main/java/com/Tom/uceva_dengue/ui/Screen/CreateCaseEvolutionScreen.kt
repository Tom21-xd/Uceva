package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.viewModel.CaseEvolutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCaseEvolutionScreen(
    caseId: Int,
    typeOfDengueId: Int,
    doctorId: Int,
    viewModel: CaseEvolutionViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    // UI State
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Patient States
    val patientStates by viewModel.patientStates.collectAsState()

    // Form fields
    val selectedPatientStateId by viewModel.selectedPatientStateId.collectAsState()
    val dayOfIllness by viewModel.dayOfIllness.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val systolicBP by viewModel.systolicBP.collectAsState()
    val diastolicBP by viewModel.diastolicBP.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val respiratoryRate by viewModel.respiratoryRate.collectAsState()
    val oxygenSaturation by viewModel.oxygenSaturation.collectAsState()
    val platelets by viewModel.platelets.collectAsState()
    val hematocrit by viewModel.hematocrit.collectAsState()
    val hemoglobin by viewModel.hemoglobin.collectAsState()
    val whiteBloodCells by viewModel.whiteBloodCells.collectAsState()
    val clinicalObservations by viewModel.clinicalObservations.collectAsState()
    val prescribedTreatment by viewModel.prescribedTreatment.collectAsState()

    // Warning signs
    val abdominalPain by viewModel.abdominalPain.collectAsState()
    val persistentVomiting by viewModel.persistentVomiting.collectAsState()
    val mucosalBleeding by viewModel.mucosalBleeding.collectAsState()
    val lethargy by viewModel.lethargy.collectAsState()

    // Expandable sections
    var isVitalSignsExpanded by remember { mutableStateOf(true) }
    var isLabsExpanded by remember { mutableStateOf(false) }
    var isWarningSignsExpanded by remember { mutableStateOf(false) }
    var isClinicalExpanded by remember { mutableStateOf(false) }

    // Load patient states on start
    LaunchedEffect(Unit) {
        viewModel.fetchPatientStates()
    }

    // Show success/error messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Evolución Clínica") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Registro de Evolución",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Caso #$caseId",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Patient State and Day of Illness
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Información General",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ComboBox(
                            label = "Estado del Paciente *",
                            options = patientStates.map { it.NOMBRE_ESTADO_PACIENTE },
                            selectedValue = patientStates.find { it.ID_ESTADO_PACIENTE == selectedPatientStateId }?.NOMBRE_ESTADO_PACIENTE ?: "",
                            onValueChangedEvent = { viewModel.setSelectedPatientState(it) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = dayOfIllness,
                            onValueChange = { viewModel.setDayOfIllness(it) },
                            label = { Text("Día de Enfermedad *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Vital Signs Section
            item {
                ExpandableSection(
                    title = "Signos Vitales",
                    icon = Icons.Default.Favorite,
                    isExpanded = isVitalSignsExpanded,
                    onToggle = { isVitalSignsExpanded = !isVitalSignsExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { viewModel.setTemperature(it) },
                            label = { Text("Temperatura (°C)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("36.5") }
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = systolicBP,
                                onValueChange = { viewModel.setSystolicBP(it) },
                                label = { Text("TA Sistólica") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("120") }
                            )
                            OutlinedTextField(
                                value = diastolicBP,
                                onValueChange = { viewModel.setDiastolicBP(it) },
                                label = { Text("TA Diastólica") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("80") }
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = heartRate,
                                onValueChange = { viewModel.setHeartRate(it) },
                                label = { Text("FC (lpm)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("70") }
                            )
                            OutlinedTextField(
                                value = respiratoryRate,
                                onValueChange = { viewModel.setRespiratoryRate(it) },
                                label = { Text("FR (rpm)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("18") }
                            )
                        }

                        OutlinedTextField(
                            value = oxygenSaturation,
                            onValueChange = { viewModel.setOxygenSaturation(it) },
                            label = { Text("Saturación O₂ (%)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("98") }
                        )
                    }
                }
            }

            // Laboratory Tests Section
            item {
                ExpandableSection(
                    title = "Exámenes de Laboratorio",
                    icon = Icons.Default.Science,
                    isExpanded = isLabsExpanded,
                    onToggle = { isLabsExpanded = !isLabsExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = platelets,
                            onValueChange = { viewModel.setPlatelets(it) },
                            label = { Text("Plaquetas (/μL)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("150000") }
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = hematocrit,
                                onValueChange = { viewModel.setHematocrit(it) },
                                label = { Text("Hematocrito (%)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("42") }
                            )
                            OutlinedTextField(
                                value = hemoglobin,
                                onValueChange = { viewModel.setHemoglobin(it) },
                                label = { Text("Hemoglobina (g/dL)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text("14") }
                            )
                        }

                        OutlinedTextField(
                            value = whiteBloodCells,
                            onValueChange = { viewModel.setWhiteBloodCells(it) },
                            label = { Text("Leucocitos (/μL)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("7000") }
                        )
                    }
                }
            }

            // Warning Signs Section
            item {
                ExpandableSection(
                    title = "Signos de Alarma",
                    icon = Icons.Default.Warning,
                    isExpanded = isWarningSignsExpanded,
                    onToggle = { isWarningSignsExpanded = !isWarningSignsExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dolor abdominal intenso")
                            Switch(checked = abdominalPain, onCheckedChange = { viewModel.setAbdominalPain(it) })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Vómito persistente")
                            Switch(checked = persistentVomiting, onCheckedChange = { viewModel.setPersistentVomiting(it) })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sangrado de mucosas")
                            Switch(checked = mucosalBleeding, onCheckedChange = { viewModel.setMucosalBleeding(it) })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Letargia / Irritabilidad")
                            Switch(checked = lethargy, onCheckedChange = { viewModel.setLethargy(it) })
                        }
                    }
                }
            }

            // Clinical Observations Section
            item {
                ExpandableSection(
                    title = "Observaciones Clínicas",
                    icon = Icons.Default.Notes,
                    isExpanded = isClinicalExpanded,
                    onToggle = { isClinicalExpanded = !isClinicalExpanded }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = clinicalObservations,
                            onValueChange = { viewModel.setClinicalObservations(it) },
                            label = { Text("Observaciones Clínicas") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            placeholder = { Text("Evolución del paciente, hallazgos...") }
                        )

                        OutlinedTextField(
                            value = prescribedTreatment,
                            onValueChange = { viewModel.setPrescribedTreatment(it) },
                            label = { Text("Tratamiento Indicado") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            placeholder = { Text("Medicamentos, indicaciones...") }
                        )
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        viewModel.createEvolution(
                            caseId = caseId,
                            typeOfDengueId = typeOfDengueId,
                            doctorId = doctorId,
                            onSuccess = { /* Handled by LaunchedEffect */ },
                            onError = { /* Handled by LaunchedEffect */ }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading && selectedPatientStateId > 0 && dayOfIllness.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Evolución", fontSize = 16.sp)
                    }
                }
            }

            // Spacer for bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}
