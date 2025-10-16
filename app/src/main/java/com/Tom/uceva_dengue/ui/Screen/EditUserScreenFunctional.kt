package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.Components.AnimatedButton
import com.Tom.uceva_dengue.ui.Components.AnimatedTextField
import com.Tom.uceva_dengue.ui.Components.LoadingIndicator
import com.Tom.uceva_dengue.ui.theme.fondo
import com.Tom.uceva_dengue.ui.viewModel.EditUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreenFunctional(
    userId: String?,
    navController: NavController,
    viewModel: EditUserViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var expandedRol by remember { mutableStateOf(false) }
    var expandedGenero by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            viewModel.loadUser(userId)
        }
    }

    // Show success dialog
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            showSuccessDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
            if (state.isLoading) {
                LoadingIndicator(
                    modifier = Modifier.fillMaxSize(),
                    message = "Cargando datos del usuario..."
                )
            } else if (state.errorMessage != null && state.user == null && userId != null) {
            // Error loading user
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.errorMessage ?: "Error desconocido",
                    fontSize = 16.sp,
                    color = Color(0xFFE53935)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        userId?.let { viewModel.loadUser(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5E81F4)
                    )
                ) {
                    Text("Reintentar")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Información del usuario (solo si está editando)
                if (!userId.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ID de Usuario",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${state.user?.ID_USUARIO ?: userId}",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                // Sección de datos personales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Encabezado de sección
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Datos Personales",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )

                // Campo de nombre
                AnimatedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.updateNombre(it) },
                    label = "Nombre completo",
                    leadingIcon = Icons.Default.Person,
                    isError = state.nombreError != null,
                    errorMessage = state.nombreError,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de correo
                AnimatedTextField(
                    value = state.correo,
                    onValueChange = { viewModel.updateCorreo(it) },
                    label = "Correo electrónico",
                    leadingIcon = Icons.Default.Email,
                    isError = state.correoError != null,
                    errorMessage = state.correoError,
                    modifier = Modifier.fillMaxWidth()
                )

                        // Campo de dirección
                        AnimatedTextField(
                            value = state.direccion,
                            onValueChange = { viewModel.updateDireccion(it) },
                            label = "Dirección",
                            leadingIcon = Icons.Default.Home,
                            isError = state.direccionError != null,
                            errorMessage = state.direccionError,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Sección de configuración
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Encabezado de sección
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Configuración de Acceso",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )

                        // Dropdown de Rol
                        Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Rol",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedRol,
                        onExpandedChange = { expandedRol = !expandedRol }
                    ) {
                        OutlinedTextField(
                            value = when (state.selectedRol) {
                                1 -> "Usuario"
                                2 -> "Administrador"
                                3 -> "Personal Médico"
                                else -> "Desconocido"
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRol,
                            onDismissRequest = { expandedRol = false }
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Person, 
                                            contentDescription = null,
                                            tint = Color(0xFF2196F3),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text("Usuario")
                                    }
                                },
                                onClick = {
                                    viewModel.updateRol(1)
                                    expandedRol = false
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AdminPanelSettings, 
                                            contentDescription = null,
                                            tint = Color(0xFFFF9800),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text("Administrador")
                                    }
                                },
                                onClick = {
                                    viewModel.updateRol(2)
                                    expandedRol = false
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.LocalHospital, 
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text("Personal Médico")
                                    }
                                },
                                onClick = {
                                    viewModel.updateRol(3)
                                    expandedRol = false
                                }
                            )
                        }
                    }
                }

                // Dropdown de Género
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Género",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedGenero,
                        onExpandedChange = { expandedGenero = !expandedGenero }
                    ) {
                        OutlinedTextField(
                            value = when (state.selectedGenero) {
                                1 -> "Masculino"
                                2 -> "Femenino"
                                3 -> "Otro"
                                else -> "No especificado"
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGenero,
                            onDismissRequest = { expandedGenero = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Masculino") },
                                onClick = {
                                    viewModel.updateGenero(1)
                                    expandedGenero = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Femenino") },
                                onClick = {
                                    viewModel.updateGenero(2)
                                    expandedGenero = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Otro") },
                                onClick = {
                                    viewModel.updateGenero(3)
                                    expandedGenero = false
                                }
                            )
                        }
                    }
                }
                    }
                }

                // Error message
                if (state.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Guardar
                AnimatedButton(
                    text = if (userId.isNullOrEmpty()) "Crear Usuario" else "Guardar Cambios",
                    onClick = {
                        if (userId.isNullOrEmpty()) {
                            // Crear nuevo usuario
                            viewModel.validateAndSave(0) {
                                // Success handled by dialog
                            }
                        } else {
                            // Actualizar usuario existente
                            state.user?.let { user ->
                                viewModel.validateAndSave(user.ID_USUARIO) {
                                    // Success handled by dialog
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    loading = state.isSaving,
                    enabled = !state.isSaving
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.clearMessages()
                navController.navigateUp()
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¡Éxito!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    state.successMessage ?: if (userId.isNullOrEmpty()) "Usuario creado correctamente" else "Usuario actualizado correctamente"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.clearMessages()
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5E81F4)
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}
