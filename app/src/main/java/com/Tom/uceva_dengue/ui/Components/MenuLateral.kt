package com.Tom.uceva_dengue.ui.Components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.ui.Menus.Items_Menu_lateral
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.Navigation.currentRoute
import kotlinx.coroutines.launch

@Composable
fun MenuLateral(
    navController: NavHostController,
    drawerState: DrawerState,
    authRepository: AuthRepository,
) {
    fun getMenuItemsByRole(role: Int): List<Items_Menu_lateral> {
        return when (role) {
            1 -> listOf(
                Items_Menu_lateral.Item_Menu_Lateral4, // Home
                Items_Menu_lateral.Item_Menu_Lateral1, // Perfil
                Items_Menu_lateral.Item_Menu_Lateral9, // Mis Guardados
                Items_Menu_lateral.Item_Menu_Lateral5, // Casos de dengue
                Items_Menu_lateral.Item_Menu_Lateral6,  // Hospitales
                Items_Menu_lateral.Item_Menu_Lateral8, // Guía de Prevención
                Items_Menu_lateral.Item_Menu_Lateral3, // Información
                Items_Menu_lateral.Item_Menu_Lateral2 // Opciones
            )
            2 -> listOf(
                Items_Menu_lateral.Item_Menu_Lateral4, // Home
                Items_Menu_lateral.Item_Menu_Lateral1, // Perfil
                Items_Menu_lateral.Item_Menu_Lateral9, // Mis Guardados
                Items_Menu_lateral.Item_Menu_Lateral5, // Casos de dengue
                Items_Menu_lateral.Item_Menu_Lateral6,  // Hospitales
                Items_Menu_lateral.Item_Menu_Lateral7, // Gestión de Usuarios (Solo Admin)
                Items_Menu_lateral.Item_Menu_Lateral8, // Guía de Prevención
                Items_Menu_lateral.Item_Menu_Lateral3, // Información
                Items_Menu_lateral.Item_Menu_Lateral2 // Opciones
            )
            3 -> listOf(
                Items_Menu_lateral.Item_Menu_Lateral4, // Home
                Items_Menu_lateral.Item_Menu_Lateral1, // Perfil
                Items_Menu_lateral.Item_Menu_Lateral9, // Mis Guardados
                Items_Menu_lateral.Item_Menu_Lateral5, // Casos de dengue
                Items_Menu_lateral.Item_Menu_Lateral6,  // Hospitales
                Items_Menu_lateral.Item_Menu_Lateral7, // Gestión de Usuarios (Personal Médico)
                Items_Menu_lateral.Item_Menu_Lateral8, // Guía de Prevención
                Items_Menu_lateral.Item_Menu_Lateral3, // Información
                Items_Menu_lateral.Item_Menu_Lateral2 // Opciones
            )
            else -> listOf(
                Items_Menu_lateral.Item_Menu_Lateral1,
                Items_Menu_lateral.Item_Menu_Lateral8, // Guía de Prevención
                Items_Menu_lateral.Item_Menu_Lateral3,
                Items_Menu_lateral.Item_Menu_Lateral4
            )
        }
    }
    val role = authRepository.getRole()
    val menuItems = getMenuItemsByRole(role)

    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }

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
                menuItems.forEach { item ->
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