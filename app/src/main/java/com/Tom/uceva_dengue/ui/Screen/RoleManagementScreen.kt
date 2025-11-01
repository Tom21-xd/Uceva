package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.RoleModel
import com.Tom.uceva_dengue.Data.Model.Permission
import com.Tom.uceva_dengue.ui.viewModel.RoleManagementViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions

// Colores
private val PrimaryBlue = Color(0xFF5E81F4)
private val DarkBlue = Color(0xFF4A5FCD)
private val LightBlue = Color(0xFF85A5FF)
private val DangerRed = Color(0xFFFF4757)
private val SuccessGreen = Color(0xFF26DE81)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleManagementScreen(
    navController: NavController,
    viewModel: RoleManagementViewModel = viewModel()
) {
    val dimensions = rememberAppDimensions()
    val roles by viewModel.roles.collectAsState()
    val allPermissions by viewModel.allPermissions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<RoleModel?>(null) }
    var expandedRoleId by remember { mutableStateOf<Int?>(null) }

    // Cargar permisos al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadAllPermissions()
    }

    // Cargar permisos del rol cuando se expande
    LaunchedEffect(expandedRoleId) {
        expandedRoleId?.let {
            viewModel.loadRolePermissions(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.paddingMedium)
    ) {
        when {
            isLoading && roles.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            roles.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                    ) {
                        Icon(
                            Icons.Default.GroupWork,
                            contentDescription = null,
                            modifier = Modifier.size(dimensions.iconExtraLarge),
                            tint = Color.Gray
                        )
                        Text(
                            "No hay roles disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                ) {
                    items(roles) { role ->
                        RoleCardWithPermissions(
                            role = role,
                            isExpanded = expandedRoleId == role.ID_ROL,
                            allPermissions = allPermissions.flatMap { it.Permissions },
                            rolePermissions = if (expandedRoleId == role.ID_ROL) {
                                viewModel.rolePermissions.value?.permissions ?: emptyList()
                            } else {
                                emptyList()
                            },
                            onExpandClick = {
                                expandedRoleId = if (expandedRoleId == role.ID_ROL) null else role.ID_ROL
                            },
                            onSavePermissions = { selectedIds ->
                                viewModel.updateRolePermissions(role.ID_ROL, selectedIds) {
                                    // Opcional: cerrar expansion después de guardar
                                }
                            },
                            onDeleteClick = {
                                showDeleteDialog = role
                            },
                            dimensions = dimensions,
                            isLoadingPermissions = isLoading && expandedRoleId == role.ID_ROL
                        )
                    }

                    // Espacio al final para el FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // FAB para crear rol
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .shadow(8.dp, CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Crear rol")
        }

        // Mensajes de error
        errorMessage?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(dimensions.paddingSmall)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        it,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearMessages() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Mensajes de éxito
        successMessage?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(dimensions.paddingSmall)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        it,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearMessages() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Diálogo de crear rol
    if (showCreateDialog) {
        CreateRoleDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { nombre ->
                viewModel.createRole(
                    RoleModel(
                        ID_ROL = 0,
                        NOMBRE_ROL = nombre,
                        ESTADO_ROL = true
                    )
                ) {
                    showCreateDialog = false
                }
            },
            dimensions = dimensions
        )
    }

    // Diálogo de eliminar rol
    showDeleteDialog?.let { role ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Rol") },
            text = { Text("¿Está seguro de que desea eliminar el rol '${role.NOMBRE_ROL}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRole(role.ID_ROL) {
                            showDeleteDialog = null
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RoleCardWithPermissions(
    role: RoleModel,
    isExpanded: Boolean,
    allPermissions: List<Permission>,
    rolePermissions: List<Permission>,
    onExpandClick: () -> Unit,
    onSavePermissions: (List<Int>) -> Unit,
    onDeleteClick: () -> Unit,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    isLoadingPermissions: Boolean
) {
    var selectedPermissionIds by remember(rolePermissions) {
        mutableStateOf(rolePermissions.map { it.id }.toSet())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(dimensions.paddingMedium)),
        shape = RoundedCornerShape(dimensions.paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium)
        ) {
            // Encabezado del rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(dimensions.iconLarge)
                            .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }

                    Column {
                        Text(
                            role.NOMBRE_ROL,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (role.ESTADO_ROL) SuccessGreen else Color.Gray,
                                        CircleShape
                                    )
                            )
                            Text(
                                if (role.ESTADO_ROL) "Activo" else "Inactivo",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)) {
                    IconButton(
                        onClick = onExpandClick,
                        modifier = Modifier
                            .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape)
                            .size(dimensions.iconLarge)
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Contraer" else "Expandir",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(dimensions.iconSmall)
                        )
                    }

                    // Solo permitir eliminar roles personalizados (ID > 3)
                    if (role.ID_ROL > 3) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .background(DangerRed.copy(alpha = 0.1f), CircleShape)
                                .size(dimensions.iconLarge)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = DangerRed,
                                modifier = Modifier.size(dimensions.iconSmall)
                            )
                        }
                    }
                }
            }

            // Sección expandible de permisos
            if (isExpanded) {
                Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                Divider()
                Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                if (isLoadingPermissions) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(32.dp))
                    }
                } else {
                    Text(
                        "Permisos del Rol",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = dimensions.paddingSmall)
                    )

                    // Agrupar permisos por categoría
                    val groupedPermissions = allPermissions.groupBy { it.category ?: "Sin categoría" }

                    groupedPermissions.forEach { (category, permissions) ->
                        Text(
                            category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(vertical = dimensions.paddingSmall)
                        )

                        permissions.forEach { permission ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPermissionIds = if (permission.id in selectedPermissionIds) {
                                            selectedPermissionIds - permission.id
                                        } else {
                                            selectedPermissionIds + permission.id
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = permission.id in selectedPermissionIds,
                                    onCheckedChange = {
                                        selectedPermissionIds = if (it) {
                                            selectedPermissionIds + permission.id
                                        } else {
                                            selectedPermissionIds - permission.id
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        permission.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    permission.description?.let { desc ->
                                        Text(
                                            desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Botón de guardar
                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                    Button(
                        onClick = { onSavePermissions(selectedPermissionIds.toList()) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(dimensions.paddingSmall)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                        Text("Guardar Permisos")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRoleDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions
) {
    var roleName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nuevo Rol") },
        text = {
            OutlinedTextField(
                value = roleName,
                onValueChange = { roleName = it },
                label = { Text("Nombre del rol") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(roleName) },
                enabled = roleName.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
