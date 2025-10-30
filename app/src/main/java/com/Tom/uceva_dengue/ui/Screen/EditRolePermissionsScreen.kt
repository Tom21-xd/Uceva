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
import com.Tom.uceva_dengue.Data.Model.Permission
import com.Tom.uceva_dengue.Data.Model.PermissionCategory
import com.Tom.uceva_dengue.ui.viewModel.RoleManagementViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions

// Colores
private val PrimaryBlue = Color(0xFF5E81F4)
private val DarkBlue = Color(0xFF4A5FCD)
private val SuccessGreen = Color(0xFF26DE81)
private val DangerRed = Color(0xFFFF4757)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRolePermissionsScreen(
    roleId: Int,
    navController: NavController,
    viewModel: RoleManagementViewModel = viewModel()
) {
    val dimensions = rememberAppDimensions()
    val allPermissions by viewModel.allPermissions.collectAsState()
    val rolePermissions by viewModel.rolePermissions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Estado local para permisos seleccionados
    var selectedPermissionIds by remember { mutableStateOf(setOf<Int>()) }

    // Cargar permisos del rol cuando cambie rolePermissions
    LaunchedEffect(rolePermissions) {
        rolePermissions?.let { response ->
            selectedPermissionIds = response.permissions.map { it.id }.toSet()
        }
    }

    // Cargar datos al inicio
    LaunchedEffect(roleId) {
        viewModel.loadRolePermissions(roleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Permisos del Rol",
                            fontWeight = FontWeight.Bold
                        )
                        rolePermissions?.let {
                            Text(
                                it.roleName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de seleccionar todos
                    IconButton(
                        onClick = {
                            val allPermissionIds = allPermissions.flatMap { category ->
                                category.Permissions.map { it.id }
                            }.toSet()

                            selectedPermissionIds = if (selectedPermissionIds.size == allPermissionIds.size) {
                                emptySet()
                            } else {
                                allPermissionIds
                            }
                        }
                    ) {
                        Icon(
                            if (selectedPermissionIds.size == allPermissions.flatMap { it.Permissions }.size)
                                Icons.Default.CheckBoxOutlineBlank
                            else
                                Icons.Default.CheckBox,
                            contentDescription = "Seleccionar todos"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.updateRolePermissions(
                        roleId = roleId,
                        permissionIds = selectedPermissionIds.toList()
                    ) {
                        navController.navigateUp()
                    }
                },
                containerColor = SuccessGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Save, contentDescription = null) },
                text = { Text("Guardar Cambios") },
                modifier = Modifier.shadow(8.dp, CircleShape)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                allPermissions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(dimensions.iconExtraLarge),
                                tint = Color.Gray
                            )
                            Text(
                                "No hay permisos disponibles",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Indicador de permisos seleccionados
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensions.paddingMedium),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryBlue.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(dimensions.paddingMedium)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensions.paddingMedium),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Permisos seleccionados:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${selectedPermissionIds.size} / ${allPermissions.flatMap { it.Permissions }.size}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }

                        // Lista de permisos por categoría
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = dimensions.paddingMedium,
                                end = dimensions.paddingMedium,
                                bottom = 80.dp // Espacio para el FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                        ) {
                            items(allPermissions) { category ->
                                PermissionCategoryCard(
                                    category = category,
                                    selectedPermissionIds = selectedPermissionIds,
                                    onPermissionToggle = { permissionId ->
                                        selectedPermissionIds = if (permissionId in selectedPermissionIds) {
                                            selectedPermissionIds - permissionId
                                        } else {
                                            selectedPermissionIds + permissionId
                                        }
                                    },
                                    onCategoryToggle = { categoryPermissions ->
                                        val categoryIds = categoryPermissions.map { it.id }.toSet()
                                        val allSelected = categoryIds.all { it in selectedPermissionIds }

                                        selectedPermissionIds = if (allSelected) {
                                            selectedPermissionIds - categoryIds
                                        } else {
                                            selectedPermissionIds + categoryIds
                                        }
                                    },
                                    dimensions = dimensions
                                )
                            }
                        }
                    }
                }
            }

            // Mensajes de error
            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium)
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
        }
    }
}

@Composable
fun PermissionCategoryCard(
    category: PermissionCategory,
    selectedPermissionIds: Set<Int>,
    onPermissionToggle: (Int) -> Unit,
    onCategoryToggle: (List<Permission>) -> Unit,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions
) {
    var expanded by remember { mutableStateOf(true) }

    val categoryPermissionIds = category.Permissions.map { it.id }.toSet()
    val selectedCount = categoryPermissionIds.count { it in selectedPermissionIds }
    val allSelected = selectedCount == category.Permissions.size
    val partiallySelected = selectedCount > 0 && !allSelected

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(dimensions.paddingMedium)),
        shape = RoundedCornerShape(dimensions.paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cabecera de categoría
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .background(
                        if (allSelected) PrimaryBlue.copy(alpha = 0.1f)
                        else if (partiallySelected) PrimaryBlue.copy(alpha = 0.05f)
                        else Color.Transparent
                    )
                    .padding(dimensions.paddingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = { onCategoryToggle(category.Permissions) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue
                        )
                    )

                    Column {
                        Text(
                            category.Category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$selectedCount / ${category.Permissions.size} permisos",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = PrimaryBlue
                )
            }

            // Lista de permisos
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimensions.paddingMedium)
                ) {
                    category.Permissions.forEach { permission ->
                        PermissionItem(
                            permission = permission,
                            isSelected = permission.id in selectedPermissionIds,
                            onToggle = { onPermissionToggle(permission.id) },
                            dimensions = dimensions
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionItem(
    permission: Permission,
    isSelected: Boolean,
    onToggle: () -> Unit,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(
                vertical = dimensions.paddingSmall,
                horizontal = dimensions.paddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryBlue
            )
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                permission.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            permission.description?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                permission.code,
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryBlue.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = (dimensions.paddingMedium.value * 2 + dimensions.iconMedium.value).dp),
        color = Color.LightGray.copy(alpha = 0.3f)
    )
}
