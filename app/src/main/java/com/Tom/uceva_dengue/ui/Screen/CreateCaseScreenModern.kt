package com.Tom.uceva_dengue.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.Data.Model.UserModel
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.CreateCaseViewModel
import com.Tom.uceva_dengue.ui.viewModel.MapViewModel
import com.Tom.uceva_dengue.utils.geocodeAddress
import com.Tom.uceva_dengue.utils.moveToUserLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CreateCaseScreenModern(
    viewModel: CreateCaseViewModel,
    authViewModel: com.Tom.uceva_dengue.ui.viewModel.AuthViewModel,
    role: Int,
    user: String?,
    navController: NavHostController
) {
    // Colores del tema
    val PrimaryBlue = MaterialTheme.colorScheme.primary
    val SecondaryBlue = MaterialTheme.colorScheme.primaryContainer
    val AccentPurple = MaterialTheme.colorScheme.secondary
    val SuccessGreen = MaterialTheme.colorScheme.tertiary
    val BackgroundColor = MaterialTheme.colorScheme.background
    val CardColor = MaterialTheme.colorScheme.surface

    var isPatientSectionExpanded by remember { mutableStateOf(true) }
    var isDengueSectionExpanded by remember { mutableStateOf(false) }
    var isLocationSectionExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isExistingUser by viewModel.isExistingUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Reportar Caso",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "Nuevo caso de dengue",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                ModernSectionCard(
                    title = "Información del Paciente",
                    icon = Icons.Default.Person,
                    expanded = isPatientSectionExpanded,
                    onExpandChanged = { isPatientSectionExpanded = !isPatientSectionExpanded }
                ) {
                    PatientSectionModern(viewModel, authViewModel)
                }
            }

            item {
                ModernSectionCard(
                    title = "Tipo de Dengue y Síntomas",
                    icon = Icons.Default.Warning,
                    expanded = isDengueSectionExpanded,
                    onExpandChanged = { isDengueSectionExpanded = !isDengueSectionExpanded }
                ) {
                    DengueSectionModern(viewModel)
                }
            }

            item {
                ModernSectionCard(
                    title = "Ubicación",
                    icon = Icons.Default.LocationOn,
                    expanded = isLocationSectionExpanded,
                    onExpandChanged = { isLocationSectionExpanded = !isLocationSectionExpanded }
                ) {
                    LocationSectionModern(viewModel, MapViewModel())
                }
            }

            item {
                // Botón de submit
                Button(
                    onClick = {
                        isLoading = true
                        if (!isExistingUser) {
                            // Primero registrar el nuevo usuario usando AuthViewModel
                            viewModel.createCaseWithNewUser(
                                authViewModel = authViewModel,
                                idPersonalMedico = user?.toInt() ?: 0,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Caso creado exitosamente", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Rout.CaseScreen.name)
                                },
                                onError = { errorMsg ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            // Usuario existente, crear caso directamente
                            viewModel.createCase(
                                idPersonalMedico = user?.toInt() ?: 0,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "Caso creado exitosamente", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Rout.CaseScreen.name)
                                },
                                onError = { errorMsg ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reportar Caso",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ModernSectionCard(
    title: String,
    icon: ImageVector,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    content: @Composable () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header clickable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChanged() }
                    .background(primaryColor.copy(alpha = 0.05f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = primaryColor
                )
            }

            // Content
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun PatientSectionModern(viewModel: CreateCaseViewModel, authViewModel: com.Tom.uceva_dengue.ui.viewModel.AuthViewModel) {
    val isExistingUser by viewModel.isExistingUser.collectAsState()
    val users by viewModel.users.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    var searchQuery by remember { mutableStateOf("") }
    var filteredUsers by remember { mutableStateOf(users) }
    var showDropdown by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Toggle usuario existente
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isExistingUser,
                    onClick = {
                        viewModel.setExistingUser(true)
                        showDropdown = false
                        searchQuery = ""
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = primaryColor,
                        unselectedColor = onSurfaceColor.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "Usuario Existente",
                    fontSize = 14.sp,
                    color = onSurfaceColor
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isExistingUser,
                    onClick = {
                        viewModel.setExistingUser(false)
                        showDropdown = false
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = primaryColor,
                        unselectedColor = onSurfaceColor.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "Nuevo Usuario",
                    fontSize = 14.sp,
                    color = onSurfaceColor
                )
            }
        }

        if (isExistingUser) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        filteredUsers = if (query.isNotBlank()) {
                            users.filter {
                                it.NOMBRE_USUARIO.toString().contains(query, ignoreCase = true)
                            }
                        } else {
                            emptyList()
                        }
                        showDropdown = filteredUsers.isNotEmpty()
                    },
                    label = { Text("Buscar Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredUsers.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.NOMBRE_USUARIO.toString()) },
                            onClick = {
                                viewModel.selectUser(user)
                                searchQuery = user.NOMBRE_USUARIO.toString()
                                showDropdown = false
                            }
                        )
                    }
                }
            }

            selectedUser?.let { SelectedUserCardModern(it) }
        } else {
            // Usar el formulario de registro completo sin campo de contraseña
            com.Tom.uceva_dengue.ui.Components.RegistrationForm(
                viewModel = authViewModel,
                showMedicalPersonnelOption = false,
                showTitle = false,
                showPasswordField = false,
                medicalPersonnelData = null
            )
        }
    }
}

@Composable
fun SelectedUserCardModern(user: UserModel) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = user.NOMBRE_USUARIO.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = onSurfaceColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "Género: ", 
                    fontWeight = FontWeight.Medium, 
                    fontSize = 14.sp,
                    color = onSurfaceColor
                )
                Text(
                    text = user.NOMBRE_GENERO ?: "Sin género", 
                    fontSize = 14.sp, 
                    color = onSurfaceVariantColor
                )
            }
            Row {
                Text(
                    text = "Dirección: ", 
                    fontWeight = FontWeight.Medium, 
                    fontSize = 14.sp,
                    color = onSurfaceColor
                )
                Text(
                    text = user.DIRECCION_USUARIO ?: "Sin dirección", 
                    fontSize = 14.sp, 
                    color = onSurfaceVariantColor
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DengueSectionModern(viewModel: CreateCaseViewModel) {
    val symptoms by viewModel.symptoms.collectAsState()
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsState()
    val typesOfDengue by viewModel.typesOfDengue.collectAsState()
    val selectedDengueType by viewModel.selectedDengueType.collectAsState()
    val diagnosisResult by viewModel.diagnosisResult.collectAsState()
    val isLoadingDiagnosis by viewModel.isLoadingDiagnosis.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSymptomsExpanded by remember { mutableStateOf(false) }
    var isAutoDiagnosisEnabled by remember { mutableStateOf(true) }

    val filteredSymptoms = remember(symptoms, searchQuery) {
        if (searchQuery.isBlank()) {
            symptoms
        } else {
            symptoms.filter {
                it.NOMBRE_SINTOMA?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    val errorColor = MaterialTheme.colorScheme.error
    val successColor = MaterialTheme.colorScheme.tertiary

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header with counter and expand button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isSymptomsExpanded = !isSymptomsExpanded }
                .background(primaryColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isSymptomsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = primaryColor
                )
                Text(
                    text = "Síntomas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = onSurfaceColor
                )
            }

            if (selectedSymptoms.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor
                    )
                ) {
                    Text(
                        text = "${selectedSymptoms.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        AnimatedVisibility(visible = isSymptomsExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Auto-diagnosis toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isAutoDiagnosisEnabled) successColor.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isAutoDiagnosisEnabled) successColor else onSurfaceColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Asistencia de Diagnóstico",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = onSurfaceColor
                            )
                            Text(
                                text = if (isAutoDiagnosisEnabled) "Recomendación automática activada" else "Selección manual",
                                fontSize = 11.sp,
                                color = onSurfaceColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Switch(
                        checked = isAutoDiagnosisEnabled,
                        onCheckedChange = { isAutoDiagnosisEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = successColor
                        )
                    )
                }

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar síntoma...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = primaryColor
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = onSurfaceColor.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineColor
                    ),
                    singleLine = true
                )

        // Symptom chips with FlowRow
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filteredSymptoms.forEach { symptom ->
                    val isSelected = selectedSymptoms.contains(symptom.ID_SINTOMA)

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleSymptom(symptom.ID_SINTOMA, isAutoDiagnosisEnabled) },
                        label = {
                            Text(
                                text = symptom.NOMBRE_SINTOMA ?: "Sin nombre",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = outlineColor,
                            selectedBorderColor = primaryColor,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.5.dp
                        ),
                        modifier = Modifier
                            .height(28.dp)
                            .padding(0.dp)
                    )
                }
            }
        }

        if (filteredSymptoms.isEmpty() && searchQuery.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = onSurfaceColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "No se encontraron síntomas",
                    color = onSurfaceColor.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

                // Diagnosis recommendation card (only show if auto-diagnosis is enabled)
                if (isAutoDiagnosisEnabled && isLoadingDiagnosis) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Analizando síntomas...",
                        fontSize = 14.sp,
                        color = onSurfaceColor
                    )
                }
            }
        }

                if (isAutoDiagnosisEnabled) {
                    diagnosisResult?.let { diagnosis ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (diagnosis.confidence) {
                        "Alta" -> successColor.copy(alpha = 0.15f)
                        "Media" -> Color(0xFFFFA726).copy(alpha = 0.15f)
                        else -> errorColor.copy(alpha = 0.15f)
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    when (diagnosis.confidence) {
                        "Alta" -> successColor
                        "Media" -> Color(0xFFFFA726)
                        else -> errorColor
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = when (diagnosis.confidence) {
                                "Alta" -> successColor
                                "Media" -> Color(0xFFFFA726)
                                else -> errorColor
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Diagnóstico Recomendado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = diagnosis.typeOfDengueName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Coincidencia:",
                                fontSize = 12.sp,
                                color = onSurfaceColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${diagnosis.matchPercentage.toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurfaceColor
                            )
                        }
                        Column {
                            Text(
                                text = "Confianza:",
                                fontSize = 12.sp,
                                color = onSurfaceColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = diagnosis.confidence,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (diagnosis.confidence) {
                                    "Alta" -> successColor
                                    "Media" -> Color(0xFFFFA726)
                                    else -> errorColor
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

        Divider(
            color = outlineColor.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Confirme o ajuste el tipo de dengue",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = onSurfaceColor.copy(alpha = 0.8f)
        )

        ComboBox(
            selectedValue = selectedDengueType,
            options = typesOfDengue.map { it.NOMBRE_TIPODENGUE },
            label = "Tipo de Dengue"
        ) { seleccion ->
            viewModel.setDengueType(seleccion)
        }
    }
}

@Composable
fun LocationSectionModern(viewModel: CreateCaseViewModel, mapViewModel: MapViewModel) {
    val hospitals by viewModel.hospitals.collectAsState()
    val description by viewModel.description.collectAsState()
    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()

    val cityName: String by viewModel.cityName.observeAsState(initial = "")
    val selectedHospital by viewModel.selectedHospital.observeAsState(initial = "")
    val department: String by viewModel.department.observeAsState(initial = "")
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val outlineColor = MaterialTheme.colorScheme.outline

    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(1.61438, -75.60623), 12f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                    userLocation = it
                }
            }
        }
    )

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ComboBox(
            selectedValue = department,
            options = departamentos.map { it.NOMBRE_DEPARTAMENTO },
            label = "Departamento",
            enabled = true
        ) { nuevoDepartamento ->
            val departamentoSeleccionado = departamentos.firstOrNull { it.NOMBRE_DEPARTAMENTO == nuevoDepartamento }
            departamentoSeleccionado?.ID_DEPARTAMENTO?.let {
                viewModel.fetchMunicipios(it.toString())
                viewModel.setDepartment(nuevoDepartamento)
                viewModel.setCityName("")
            }
        }

        ComboBox(
            selectedValue = cityName,
            options = ciudades.map { it.NOMBRE_MUNICIPIO ?: "" },
            label = "Municipio",
            enabled = department.isNotBlank()
        ) { nuevaCiudad ->
            val ciudadSeleccionada = ciudades.firstOrNull { it.NOMBRE_MUNICIPIO == nuevaCiudad }
            ciudadSeleccionada?.let {
                viewModel.setCityName(nuevaCiudad)
                viewModel.setCityId(it.ID_MUNICIPIO)
                viewModel.setSelectedHospital("", 0)
                viewModel.fetchHospitals(it.ID_MUNICIPIO)
            }
        }

        ComboBox(
            selectedValue = selectedHospital,
            options = hospitals.map { it.NOMBRE_HOSPITAL ?: "" },
            label = "Hospital",
            enabled = cityName.isNotBlank()
        ) { hospital ->
            val hospitalSeleccionado = hospitals.firstOrNull { it.NOMBRE_HOSPITAL == hospital }
            hospitalSeleccionado?.let {
                viewModel.setSelectedHospital(it.NOMBRE_HOSPITAL, it.ID_HOSPITAL)
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = viewModel::setDescription,
            label = { Text("Descripción del Caso") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = outlineColor
            )
        )

        // Búsqueda de dirección
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar dirección") },
                placeholder = { Text("Ej: Calle 10 #15-20") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = outlineColor
                )
            )

            IconButton(
                onClick = {
                    val location = geocodeAddress(context, searchText)
                    if (location != null) {
                        searchLocation = location
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                        viewModel.setLocationCoordinates(location)
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(primaryColor, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = onPrimaryColor)
            }
        }

        // Mapa
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        compassEnabled = true
                    )
                ) {
                    searchLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Ubicación buscada"
                        )
                    }
                    userLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Mi ubicación"
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                                userLocation = it
                            }
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    containerColor = primaryColor,
                    contentColor = onPrimaryColor,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                }
            }
        }
    }
}
