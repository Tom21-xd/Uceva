package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.Tom.uceva_dengue.ui.Components.AnimatedTextField
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
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Rout.EditUserScreen.name) },
                containerColor = Color(0xFF5E81F4),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear usuario")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header con título y estadísticas rápidas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Gestión de Usuarios",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Administra los usuarios del sistema",
                        fontSize = 14.sp,
                        color = Color(0xFF718096)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estadísticas rápidas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickStat(
                            icon = Icons.Default.People,
                            count = state.filteredUsers.size,
                            label = "Total",
                            color = Color(0xFF5E81F4)
                        )
                        QuickStat(
                            icon = Icons.Default.Person,
                            count = state.filteredUsers.count { it.FK_ID_ROL == 1 },
                            label = "Usuarios",
                            color = Color(0xFF2196F3)
                        )
                        QuickStat(
                            icon = Icons.Default.AdminPanelSettings,
                            count = state.filteredUsers.count { it.FK_ID_ROL == 2 },
                            label = "Admins",
                            color = Color(0xFFFF9800)
                        )
                        QuickStat(
                            icon = Icons.Default.LocalHospital,
                            count = state.filteredUsers.count { it.FK_ID_ROL == 3 },
                            label = "Médicos",
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de búsqueda
            AnimatedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.searchUsers(it) },
                label = "Buscar por nombre, correo o ID",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filtros por rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtrar:",
                    fontSize = 14.sp,
                    color = Color(0xFF718096),
                    fontWeight = FontWeight.Medium
                )
                FilterChip(
                    selected = state.selectedRoleFilter == null,
                    onClick = { viewModel.filterByRole(null) },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF5E81F4),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = state.selectedRoleFilter == 1,
                    onClick = { viewModel.filterByRole(1) },
                    label = { Text("Usuarios") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = state.selectedRoleFilter == 2,
                    onClick = { viewModel.filterByRole(2) },
                    label = { Text("Admins") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF9800),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = state.selectedRoleFilter == 3,
                    onClick = { viewModel.filterByRole(3) },
                    label = { Text("Médicos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        containerColor = Color(0xFFFFF3F3)
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
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.errorMessage ?: "Error desconocido",
                            color = Color(0xFFE53935)
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredUsers, key = { it.ID_USUARIO }) { user ->
                        UserCard(
                            user = user,
                            onEdit = {
                                navController.navigate("${Rout.EditUserScreen.name}/${user.ID_USUARIO}")
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
                        color = Color.Gray
                    )

                    if (deleteError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = deleteError ?: "",
                            color = Color(0xFFE53935),
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
private fun QuickStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF718096)
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
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Avatar con gradiente según el rol
            Box(
                modifier = Modifier
                    .size(56.dp)
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información del usuario
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.NOMBRE_USUARIO ?: "Sin nombre",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.CORREO_USUARIO ?: "Sin correo",
                    fontSize = 13.sp,
                    color = Color(0xFF718096)
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Rol badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (user.FK_ID_ROL) {
                        2 -> Color(0xFFFF9800).copy(alpha = 0.15f)
                        3 -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        else -> Color(0xFF2196F3).copy(alpha = 0.15f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (user.FK_ID_ROL) {
                                2 -> "Admin"
                                3 -> "Médico"
                                else -> "Usuario"
                            },
                            fontSize = 11.sp,
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

            // Botones de acción
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar usuario",
                        tint = Color(0xFF5E81F4),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar usuario",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
