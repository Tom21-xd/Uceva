package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.theme.*
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // Definir las rutas válidas para el menú inferior
    val items = remember { NavigationBarItems.values() }
    val selectedIndex = remember(currentRoute) {
        items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: -1
    }

    // Siempre mostrar el menú, pero solo mostrar el indicador si selectedIndex >= 0
    Column {
        AnimatedNavigationBar(
            modifier = Modifier.height(64.dp),
            selectedIndex = if (selectedIndex >= 0) selectedIndex else 0, // Default a 0 cuando no está en una ruta principal
            cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
            ballAnimation = Parabolic(tween(300)),
            indentAnimation = Height(tween(300)),
            barColor = Color(0xFF5E81F4), // Color azul del TopAppBar
            ballColor = if (selectedIndex >= 0) Color(0xFF92C5FC) else Color.Transparent, // Color azul claro para la bolita
        ) {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // Siempre permitir navegación
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        imageVector = item.icon,
                        contentDescription = "Bottom Bar Icon",
                        tint = if (selectedIndex == index) Color(0xFF1E3A8A) else Color.White
                    )
                }
            }
        }
        // Agregar espaciador para la barra de navegación del sistema
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

enum class NavigationBarItems(val icon: ImageVector, val route: String) {
    Map(icon = Icons.Default.Map, Rout.MapScreen.name),
    Hospital(icon = Icons.Default.LocalHospital, Rout.HomeScreen.name),
    Notification(icon = Icons.Default.Notifications, Rout.NotificationScreen.name)
}

fun Modifier.noRippleClickable (onClick:() -> Unit): Modifier =composed {
    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }
    ) {
        onClick()
    }
}
