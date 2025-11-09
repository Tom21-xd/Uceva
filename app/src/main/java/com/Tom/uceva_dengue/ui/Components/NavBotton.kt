package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.Tom.uceva_dengue.ui.Navigation.Rout

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Solo mostrar la barra en las rutas principales
    val mainRoutes = setOf(
        Rout.MapScreen.name,
        Rout.HomeScreen.name,
        Rout.NotificationScreen.name
    )

    // Determinar si estamos en una ruta principal
    val isMainRoute = currentRoute in mainRoutes

    NavigationBar(
        containerColor = Color(0xFF5E81F4),
        contentColor = Color.White,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Rout.MapScreen.name)
                        Icons.Filled.Map
                    else
                        Icons.Outlined.Map,
                    contentDescription = "Mapa"
                )
            },
            label = { Text("Mapa") },
            selected = currentRoute == Rout.MapScreen.name,
            onClick = {
                if (currentRoute != Rout.MapScreen.name) {
                    navController.navigate(Rout.MapScreen.name) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = Color(0xFF92C5FC)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Rout.HomeScreen.name)
                        Icons.Filled.LocalHospital
                    else
                        Icons.Outlined.LocalHospital,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio") },
            selected = currentRoute == Rout.HomeScreen.name,
            onClick = {
                if (currentRoute != Rout.HomeScreen.name) {
                    navController.navigate(Rout.HomeScreen.name) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = Color(0xFF92C5FC)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Rout.NotificationScreen.name)
                        Icons.Filled.Notifications
                    else
                        Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones"
                )
            },
            label = { Text("Notificaciones") },
            selected = currentRoute == Rout.NotificationScreen.name,
            onClick = {
                if (currentRoute != Rout.NotificationScreen.name) {
                    navController.navigate(Rout.NotificationScreen.name) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1E3A8A),
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = Color(0xFF92C5FC)
            )
        )
    }
}
