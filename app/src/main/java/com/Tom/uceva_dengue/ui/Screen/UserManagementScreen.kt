package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.UserModel
import com.Tom.uceva_dengue.ui.Components.LoadingIndicator
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    viewModel: UserManagementViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserModel?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Rout.EditUserScreen.name) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear usuario")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header compacto con búsqueda y filtros integrados
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Barra de búsqueda compacta
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.searchUsers(it) },
                        placeholder = {
                            Text(
                                "Buscar usuarios...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF5E81F4),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.searchUsers("") },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Limpiar",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5E81F4),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filtros compactos en fila
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == null,
                                onClick = { viewModel.filterByRole(null) },
                                label = {
                                    Text(
                                        "Todos (${state.users.size})",
                                        fontSize = 12.sp,
                                        fontWeight = if (state.selectedRoleFilter == null) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (state.selectedRoleFilter == null) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF5E81F4),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == 1,
                                onClick = { viewModel.filterByRole(1) },
                                label = { Text("Usuarios", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        Modifier.size(14.dp),
                                        tint = if (state.selectedRoleFilter == 1) Color.White else Color(0xFF2196F3)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2196F3),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == 2,
                                onClick = { viewModel.filterByRole(2) },
                                label = { Text("Admins", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        null,
                                        Modifier.size(14.dp),
                                        tint = if (state.selectedRoleFilter == 2) Color.White else Color(0xFFFF9800)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFF9800),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.selectedRoleFilter == 3,
                                onClick = { viewModel.filterByRole(3) },
                                label = { Text("Médicos", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.LocalHospital,
                                        null,
                                        Modifier.size(14.dp),
                                        tint = if (state.selectedRoleFilter == 3) Color.White else Color(0xFF4CAF50)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4CAF50),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Loading indicator
            if (state.isLoading) {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    message = "Cargando usuarios..."
                )
            } else if (state.errorMessage != null) {
                // Error message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
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
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.errorMessage ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadUsers() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5E81F4)
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            } else if (state.filteredUsers.isEmpty()) {
                // Empty state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = Color(0xFF718096),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No se encontraron usuarios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D3748)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Intenta con otros filtros o términos de búsqueda",
                            fontSize = 14.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }
            } else {
                // Lista de usuarios
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredUsers, key = { it.ID_USUARIO }) { user ->
                        UserCard(
                            user = user,
                            onEdit = {
                                navController.navigate("${Rout.EditUserScreen.name}?userId=${user.ID_USUARIO}")
                            },
                            onDelete = {
                                userToDelete = user
                                showDeleteDialog = true
                            }
                        )
                    }
                    // Espacio al final para el FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                userToDelete = null
                deleteError = null
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Confirmar eliminación",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de que deseas eliminar al usuario '${userToDelete?.NOMBRE_USUARIO}'?"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Esta acción no se puede deshacer.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (deleteError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = deleteError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userToDelete?.let { user ->
                            viewModel.deleteUser(
                                userId = user.ID_USUARIO,
                                onSuccess = {
                                    showDeleteDialog = false
                                    userToDelete = null
                                    deleteError = null
                                },
                                onError = { error ->
                                    deleteError = error
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        userToDelete = null
                        deleteError = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UserCard(
    user: UserModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Avatar más pequeño
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = when (user.FK_ID_ROL) {
                                2 -> listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
                                3 -> listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                                else -> listOf(Color(0xFF5E81F4), Color(0xFF92C5FC))
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user.NOMBRE_USUARIO?.take(1) ?: "U").uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Información compacta del usuario
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.NOMBRE_USUARIO ?: "Sin nombre",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    maxLines = 1
                )
                Text(
                    text = user.CORREO_USUARIO ?: "Sin correo",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Rol badge más pequeño
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (user.FK_ID_ROL) {
                        2 -> Color(0xFFFF9800).copy(alpha = 0.15f)
                        3 -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        else -> Color(0xFF2196F3).copy(alpha = 0.15f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (user.FK_ID_ROL) {
                                2 -> Icons.Default.AdminPanelSettings
                                3 -> Icons.Default.LocalHospital
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (user.FK_ID_ROL) {
                                2 -> Color(0xFFFF9800)
                                3 -> Color(0xFF4CAF50)
                                else -> Color(0xFF2196F3)
                            },
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = when (user.FK_ID_ROL) {
                                2 -> "Admin"
                                3 -> "Médico"
                                else -> "Usuario"
                            },
                            fontSize = 10.sp,
                            color = when (user.FK_ID_ROL) {
                                2 -> Color(0xFFFF9800)
                                3 -> Color(0xFF4CAF50)
                                else -> Color(0xFF2196F3)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Botones de acción horizontales
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF5E81F4),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
