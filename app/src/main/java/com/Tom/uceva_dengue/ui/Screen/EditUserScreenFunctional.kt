package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.viewModel.EditUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreenFunctional(
    userId: String?,
    navController: NavController,
    viewModel: EditUserViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Local state to hold form values
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var selectedRol by remember { mutableStateOf(1) }
    var selectedGenero by remember { mutableStateOf(1) }
    var selectedMunicipio by remember { mutableStateOf(0) }
    var loadedUserId by remember { mutableStateOf<Int?>(null) }

    var expandedRol by remember { mutableStateOf(false) }
    var expandedGenero by remember { mutableStateOf(false) }

    // Load user once
    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            android.util.Log.d("EditUserScreen", "Loading user with ID: $userId")
            viewModel.loadUser(userId)
        } else {
            android.util.Log.e("EditUserScreen", "User ID is null or empty!")
        }
    }

    // Update local state when user loads
    LaunchedEffect(state.user) {
        state.user?.let { user ->
            nombre = user.NOMBRE_USUARIO ?: ""
            correo = user.CORREO_USUARIO ?: ""
            direccion = user.DIRECCION_USUARIO ?: ""
            selectedRol = user.FK_ID_ROL
            selectedGenero = user.FK_ID_GENERO
            selectedMunicipio = user.FK_ID_MUNICIPIO ?: 0
            loadedUserId = user.ID_USUARIO
            android.util.Log.d("EditUserScreen", "User data loaded: ID=${user.ID_USUARIO}, Name=${user.NOMBRE_USUARIO}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading && state.user == null) {
            // Only show loading on first load
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF00796B)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00796B)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (userId.isNullOrEmpty()) "Crear Usuario" else "Editar Usuario",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (!userId.isNullOrEmpty()) {
                                Text(
                                    text = "ID: ${loadedUserId ?: userId}",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Datos Personales Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF00796B),
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

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )

                        // Nombre
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00796B),
                                focusedLabelColor = Color(0xFF00796B),
                                focusedLeadingIconColor = Color(0xFF00796B)
                            )
                        )

                        // Correo
                        OutlinedTextField(
                            value = correo,
                            onValueChange = { correo = it },
                            label = { Text("Correo electrónico") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00796B),
                                focusedLabelColor = Color(0xFF00796B),
                                focusedLeadingIconColor = Color(0xFF00796B)
                            )
                        )

                        // Dirección
                        OutlinedTextField(
                            value = direccion,
                            onValueChange = { direccion = it },
                            label = { Text("Dirección") },
                            leadingIcon = {
                                Icon(Icons.Default.Home, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00796B),
                                focusedLabelColor = Color(0xFF00796B),
                                focusedLeadingIconColor = Color(0xFF00796B)
                            )
                        )
                    }
                }

                // Configuración Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF00796B),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Configuración",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )

                        // Dropdown de Rol
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Rol de Usuario",
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
                                    value = when (selectedRol) {
                                        1 -> "Usuario"
                                        2 -> "Administrador"
                                        3 -> "Personal Médico"
                                        else -> "Seleccionar"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00796B),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
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
                                            selectedRol = 1
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
                                            selectedRol = 2
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
                                            selectedRol = 3
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
                                    value = when (selectedGenero) {
                                        1 -> "Masculino"
                                        2 -> "Femenino"
                                        3 -> "Otro"
                                        else -> "Seleccionar"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00796B),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedGenero,
                                    onDismissRequest = { expandedGenero = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Masculino") },
                                        onClick = {
                                            selectedGenero = 1
                                            expandedGenero = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Femenino") },
                                        onClick = {
                                            selectedGenero = 2
                                            expandedGenero = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Otro") },
                                        onClick = {
                                            selectedGenero = 3
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
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF00796B)
                        ),
                        enabled = !state.isSaving
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            // Validate
                            if (nombre.isBlank()) {
                                Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (correo.isBlank()) {
                                Toast.makeText(context, "El correo es requerido", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (direccion.isBlank()) {
                                Toast.makeText(context, "La dirección es requerida", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Validate user ID
                            if (loadedUserId == null || loadedUserId == 0) {
                                Toast.makeText(context, "Error: Usuario no cargado. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                                android.util.Log.e("EditUserScreen", "loadedUserId is null or 0")
                                return@Button
                            }

                            android.util.Log.d("EditUserScreen", "Saving user with ID: $loadedUserId")

                            // Save
                            viewModel.saveUser(
                                userId = loadedUserId!!,
                                nombre = nombre,
                                correo = correo,
                                direccion = direccion,
                                idRol = selectedRol,
                                idGenero = selectedGenero,
                                idMunicipio = selectedMunicipio,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Usuario guardado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigateUp()
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00796B)
                        ),
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
