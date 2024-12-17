package com.Tom.uceva_dengue.ui.Components

import android.media.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.ui.Menus.Items_Menu_lateral
import com.Tom.uceva_dengue.ui.Navigation.currentRoute
import kotlinx.coroutines.launch
@Composable
fun MenuLateral(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val menuItems = listOf(
        Items_Menu_lateral.Item_Menu_Lateral1,
        Items_Menu_lateral.Item_Menu_Lateral2,
        Items_Menu_lateral.Item_Menu_Lateral3
    )

    val coroutineScope = rememberCoroutineScope()

    ModalDrawerSheet(
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            menuItems.forEach { item ->
                NavigationDrawerItem(
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null, // Descripción opcional
                                modifier = Modifier.size(24.dp) // Tamaño del ícono
                            )
                            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre ícono y texto
                            Text(text = item.title)
                        }
                    },
                    selected = currentRoute(navController) == item.route,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close() // Cerrar el menú al seleccionar un ítem
                        }
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

