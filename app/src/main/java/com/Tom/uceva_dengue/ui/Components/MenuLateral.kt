package com.Tom.uceva_dengue.ui.Components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.ui.Menus.Items_Menu_lateral
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.Navigation.currentRoute
import com.Tom.uceva_dengue.utils.PermissionChecker
import com.Tom.uceva_dengue.utils.UserPermissionsManager
import kotlinx.coroutines.launch

/**
 * Maps menu items to their required permissions
 */
private fun getMenuItemPermissions(item: Items_Menu_lateral): List<String> {
    return when (item) {
        is Items_Menu_lateral.Item_Menu_Lateral1 -> PermissionChecker.MenuPermissions.PROFILE
        is Items_Menu_lateral.Item_Menu_Lateral2 -> PermissionChecker.MenuPermissions.OPTIONS
        is Items_Menu_lateral.Item_Menu_Lateral3 -> PermissionChecker.MenuPermissions.INFO
        is Items_Menu_lateral.Item_Menu_Lateral4 -> PermissionChecker.MenuPermissions.HOME
        is Items_Menu_lateral.Item_Menu_Lateral5 -> PermissionChecker.MenuPermissions.CASES
        is Items_Menu_lateral.Item_Menu_Lateral6 -> PermissionChecker.MenuPermissions.HOSPITALS
        is Items_Menu_lateral.Item_Menu_Lateral7 -> PermissionChecker.MenuPermissions.USER_MANAGEMENT
        is Items_Menu_lateral.Item_Menu_Lateral8 -> PermissionChecker.MenuPermissions.PREVENTION_GUIDE
        is Items_Menu_lateral.Item_Menu_Lateral9 -> PermissionChecker.MenuPermissions.SAVED_PUBLICATIONS
        is Items_Menu_lateral.Item_Menu_Lateral10 -> PermissionChecker.MenuPermissions.PERMISSIONS_MANAGEMENT
        is Items_Menu_lateral.Item_Menu_Lateral11 -> PermissionChecker.MenuPermissions.ROLE_MANAGEMENT
        is Items_Menu_lateral.Item_Menu_Lateral12 -> PermissionChecker.MenuPermissions.IMPORT_CASES
        else -> emptyList()
    }
}

@Composable
fun MenuLateral(
    navController: NavHostController,
    drawerState: DrawerState,
    authRepository: AuthRepository,
) {
    val context = LocalContext.current
    val permissionsManager = remember { UserPermissionsManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }

    // All possible menu items in desired order
    val allMenuItems = listOf(
        Items_Menu_lateral.Item_Menu_Lateral4, // Home
        Items_Menu_lateral.Item_Menu_Lateral1, // Perfil
        Items_Menu_lateral.Item_Menu_Lateral9, // Mis Guardados
        Items_Menu_lateral.Item_Menu_Lateral5, // Casos de dengue
        Items_Menu_lateral.Item_Menu_Lateral12, // Importar Casos
        Items_Menu_lateral.Item_Menu_Lateral6, // Hospitales
        Items_Menu_lateral.Item_Menu_Lateral7, // Gestión de Usuarios
        Items_Menu_lateral.Item_Menu_Lateral11, // Gestión de Roles
        Items_Menu_lateral.Item_Menu_Lateral8, // Guía de Prevención
        Items_Menu_lateral.Item_Menu_Lateral3, // Información
        Items_Menu_lateral.Item_Menu_Lateral2  // Opciones
    )

    // Filter menu items based on user permissions
    // Observe permissions changes in real-time
    val userPermissions by permissionsManager.getUserPermissionsFlow().collectAsState(initial = emptyList())

    // Recalculate visible items whenever permissions change
    val visibleMenuItems = remember(userPermissions) {
        Log.d("MenuLateral", "=== PERMISSION DEBUG ===")
        Log.d("MenuLateral", "User permissions count: ${userPermissions.size}")
        Log.d("MenuLateral", "User permissions: $userPermissions")

        val items = allMenuItems.filter { item ->
            val requiredPermissions = getMenuItemPermissions(item)
            // If no permissions required, show the item
            if (requiredPermissions.isEmpty()) {
                Log.d("MenuLateral", "Item ${item.title} - No permissions required, showing")
                return@filter true
            }
            // Otherwise, check if user has all required permissions
            val hasPermissions = requiredPermissions.all { it in userPermissions }
            Log.d("MenuLateral", "Item ${item.title} - Required: $requiredPermissions, Has: $hasPermissions")
            hasPermissions
        }

        Log.d("MenuLateral", "Visible menu items: ${items.size} of ${allMenuItems.size}")
        items
    }

    ModalDrawerSheet(
        modifier = Modifier.width(250.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                visibleMenuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = item.title)
                            }
                        },
                        selected = currentRoute(navController) == item.route,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

            NavigationDrawerItem(
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Cerrar Sesión")
                    }
                },
                selected = false,
                onClick = {
                    coroutineScope.launch {
                        isLoading.value = true
                        drawerState.close()
                        authRepository.clearUserSession()
                        navController.navigate(Rout.LoginScreen.name) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                        isLoading.value = false
                    }
                }
            )

            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}