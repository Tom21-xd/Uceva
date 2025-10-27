package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tom.uceva_dengue.Data.Model.Permission
import com.Tom.uceva_dengue.ui.viewModel.PermissionsUiState
import com.Tom.uceva_dengue.ui.viewModel.PermissionsViewModel

/**
 * Pantalla de gestión de permisos por rol
 * Permite al administrador asignar/revocar permisos de forma granular
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsManagementScreen(
    viewModel: PermissionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tabs para seleccionar rol
        RoleTabsSection(
            selectedRole = selectedRole,
            onRoleSelected = { viewModel.selectRole(it) }
        )

        // Barra de búsqueda
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) }
        )

        // Contenido según estado
        when (val state = uiState) {
            is PermissionsUiState.Loading -> {
                LoadingState()
            }
            is PermissionsUiState.Success -> {
                PermissionsContent(
                    permissionsByCategory = viewModel.getPermissionsByCategory(),
                    onPermissionToggle = { permissionId, enabled ->
                        viewModel.togglePermission(permissionId, enabled)
                    }
                )
            }
            is PermissionsUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadPermissions() }
                )
            }
        }
    }
}

@Composable
private fun RoleTabsSection(
    selectedRole: Int,
    onRoleSelected: (Int) -> Unit
) {
    val roles = listOf(
        1 to "Administrador",
        2 to "Usuario",
        3 to "Personal Médico"
    )

    ScrollableTabRow(
        selectedTabIndex = roles.indexOfFirst { it.first == selectedRole },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 16.dp
    ) {
        roles.forEach { (roleId, roleName) ->
            Tab(
                selected = selectedRole == roleId,
                onClick = { onRoleSelected(roleId) },
                text = {
                    Text(
                        text = roleName,
                        fontWeight = if (selectedRole == roleId) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar permiso...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun PermissionsContent(
    permissionsByCategory: Map<String, List<Permission>>,
    onPermissionToggle: (Int, Boolean) -> Unit
) {
    if (permissionsByCategory.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            permissionsByCategory.forEach { (category, permissions) ->
                item(key = category) {
                    PermissionCategoryCard(
                        category = category,
                        permissions = permissions,
                        onPermissionToggle = onPermissionToggle
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionCategoryCard(
    category: String,
    permissions: List<Permission>,
    onPermissionToggle: (Int, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header de categoría
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = category,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${permissions.size} permisos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Contraer" else "Expandir"
                    )
                }
            }

            // Lista de permisos (animada)
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    permissions.forEach { permission ->
                        PermissionItem(
                            permission = permission,
                            onToggle = { enabled ->
                                onPermissionToggle(permission.id, enabled)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(
    permission: Permission,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = permission.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (!permission.description.isNullOrEmpty()) {
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = permission.isActive,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Cargando permisos...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Error al cargar permisos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Sin resultados",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No se encontraron permisos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Intenta con otro término de búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Retorna el icono correspondiente a cada categoría
 */
private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Casos" -> Icons.Default.FolderOpen
        "Usuarios" -> Icons.Default.People
        "Hospitales" -> Icons.Default.LocalHospital
        "Publicaciones" -> Icons.Default.Article
        "Notificaciones" -> Icons.Default.Notifications
        "Estadísticas" -> Icons.Default.BarChart
        "Administración" -> Icons.Default.AdminPanelSettings
        "Educación" -> Icons.Default.School
        else -> Icons.Default.Lock
    }
}
