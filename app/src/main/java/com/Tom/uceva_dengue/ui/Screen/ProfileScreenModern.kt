package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tom.uceva_dengue.ui.viewModel.ProfileViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize

@Composable
fun ProfileScreenModern(viewModel: ProfileViewModel, userId: String?) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    // Load user profile once
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile(userId)
    }

    // Update local state when user data loads
    LaunchedEffect(user) {
        user?.let {
            nombre = it.NOMBRE_USUARIO ?: ""
            correo = it.CORREO_USUARIO ?: ""
            direccion = it.DIRECCION_USUARIO ?: ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && user == null) {
            // Only show loading on first load
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF00796B)
            )
        } else if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensions.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF00796B), Color(0xFF004D40))
                                )
                            )
                            .padding(dimensions.paddingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dimensions.logoSize)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nombre.take(1).uppercase(),
                                fontSize = dimensions.textSizeHeader,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                        Text(
                            text = nombre,
                            fontSize = dimensions.textSizeTitle,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        Surface(
                            shape = RoundedCornerShape(dimensions.cardCornerRadius),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = user!!.NOMBRE_ROL ?: "Sin rol",
                                modifier = Modifier.padding(horizontal = dimensions.paddingSmall, vertical = dimensions.spacingSmall / 2),
                                fontSize = dimensions.textSizeMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensions.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
                ) {
                    Column(modifier = Modifier.padding(dimensions.paddingLarge)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Información Personal",
                                fontSize = dimensions.textSizeExtraLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (!isEditing && !isSaving) {
                                IconButton(onClick = { isEditing = true }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color(0xFF00796B),
                                        modifier = Modifier.size(dimensions.iconLarge)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                        // Nombre
                        if (isEditing) {
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre", fontSize = dimensions.textSizeMedium) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensions.iconMedium)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00796B),
                                    focusedLabelColor = Color(0xFF00796B),
                                    focusedLeadingIconColor = Color(0xFF00796B)
                                )
                            )
                        } else {
                            ProfileInfoRow(
                                dimensions = dimensions,
                                icon = Icons.Default.Person,
                                label = "Nombre",
                                value = nombre
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        // Correo
                        if (isEditing) {
                            OutlinedTextField(
                                value = correo,
                                onValueChange = { correo = it },
                                label = { Text("Correo Electrónico", fontSize = dimensions.textSizeMedium) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensions.iconMedium)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00796B),
                                    focusedLabelColor = Color(0xFF00796B),
                                    focusedLeadingIconColor = Color(0xFF00796B)
                                )
                            )
                        } else {
                            ProfileInfoRow(
                                dimensions = dimensions,
                                icon = Icons.Default.Email,
                                label = "Correo Electrónico",
                                value = correo
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        // Dirección
                        if (isEditing) {
                            OutlinedTextField(
                                value = direccion,
                                onValueChange = { direccion = it },
                                label = { Text("Dirección", fontSize = dimensions.textSizeMedium) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensions.iconMedium)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00796B),
                                    focusedLabelColor = Color(0xFF00796B),
                                    focusedLeadingIconColor = Color(0xFF00796B)
                                )
                            )
                        } else {
                            ProfileInfoRow(
                                dimensions = dimensions,
                                icon = Icons.Default.LocationOn,
                                label = "Dirección",
                                value = direccion
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = dimensions.spacingSmall),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Read-only fields
                        ProfileInfoRow(
                            dimensions = dimensions,
                            icon = Icons.Default.Transgender,
                            label = "Género",
                            value = user!!.NOMBRE_GENERO ?: "No especificado"
                        )

                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        ProfileInfoRow(
                            dimensions = dimensions,
                            icon = Icons.Default.Favorite,
                            label = "Tipo de Sangre",
                            value = user!!.NOMBRE_TIPOSANGRE ?: "No especificado"
                        )

                        if (user!!.NOMBRE_MUNICIPIO != null) {
                            Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                            ProfileInfoRow(
                                dimensions = dimensions,
                                icon = Icons.Default.Place,
                                label = "Municipio",
                                value = user!!.NOMBRE_MUNICIPIO ?: "No especificado"
                            )
                        }

                        // Action buttons in edit mode
                        if (isEditing) {
                            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // Reset values
                                        user?.let {
                                            nombre = it.NOMBRE_USUARIO ?: ""
                                            correo = it.CORREO_USUARIO ?: ""
                                            direccion = it.DIRECCION_USUARIO ?: ""
                                        }
                                        isEditing = false
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(dimensions.buttonHeight),
                                    shape = RoundedCornerShape(dimensions.cardCornerRadius),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF00796B)
                                    ),
                                    enabled = !isSaving
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensions.iconMedium)
                                    )
                                    Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                                    Text("Cancelar", fontWeight = FontWeight.SemiBold, fontSize = dimensions.textSizeMedium)
                                }

                                Button(
                                    onClick = {
                                        viewModel.saveProfile(
                                            nombre = nombre,
                                            correo = correo,
                                            direccion = direccion,
                                            onSuccess = {
                                                isEditing = false
                                                Toast.makeText(
                                                    context,
                                                    "Perfil actualizado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onError = { error ->
                                                Toast.makeText(
                                                    context,
                                                    error,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(dimensions.buttonHeight),
                                    shape = RoundedCornerShape(dimensions.cardCornerRadius),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00796B)
                                    ),
                                    enabled = !isSaving
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(dimensions.iconMedium),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(dimensions.iconMedium)
                                        )
                                        Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                                        Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = dimensions.textSizeMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                // ID Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensions.cardCornerRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFB2DFDB)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.paddingLarge),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF00796B),
                            modifier = Modifier.size(dimensions.iconLarge)
                        )
                        Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                        Text(
                            text = "ID: ${user!!.ID_USUARIO}",
                            fontSize = dimensions.textSizeLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF00796B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.paddingExtraLarge * 2))
            }
        } else {
            // No data state
            val dimensions = rememberAppDimensions()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(dimensions.iconExtraLarge)
                )
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                Text(
                    text = "No se pudo cargar el perfil",
                    fontSize = dimensions.textSizeExtraLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.iconButtonSize * 0.8f)
                .clip(CircleShape)
                .background(Color(0xFFB2DFDB)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00796B),
                modifier = Modifier.size(dimensions.iconMedium)
            )
        }

        Spacer(modifier = Modifier.width(dimensions.spacingMedium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = dimensions.textSizeSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall / 2))
            Text(
                text = value.ifBlank { "No especificado" },
                fontSize = dimensions.textSizeMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
