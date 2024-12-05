package com.Tom.uceva_dengue.ui.Navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar
import com.Tom.uceva_dengue.ui.Components.MenuLateral
import com.Tom.uceva_dengue.ui.Screen.HomeScreen
import com.Tom.uceva_dengue.ui.Screen.LoginScreen
import com.Tom.uceva_dengue.ui.Screen.MainScreen
import com.Tom.uceva_dengue.ui.Screen.MapScreen
import com.Tom.uceva_dengue.ui.Screen.NotificationScreen
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationCon(context: Context) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Scaffold(
        topBar = {
            if (shouldShowTopBar(navController)) {
                MenuLateral(navController,drawerState)
            }
        },
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
            NavHost(navController = navController,startDestination = Rutas.LoginScreen.name,modifier = Modifier.padding(innerPadding)
            ) {
                composable(Rutas.LoginScreen.name ) {
                    LoginScreen(viewModel = AuthViewModel(), navController = navController)
                }
                composable(Rutas.HomeScreen.name) {
                    HomeScreen()
                }
                composable (Rutas.MapScreen.name){
                    MapScreen()
                }
                composable (Rutas.NotificationScreen.name){
                    NotificationScreen()
                }
                composable (Rutas.MainScreen.name){
                    MainScreen()
                }

            }
    }
}
fun shouldShowTopBar(navController: NavController): Boolean {
    val currentRoute = navController.currentDestination?.route
    return currentRoute != Rutas.LoginScreen.name // Ocultar en el login
}

@Composable
fun shouldShowBottomBar(navController: NavController): Boolean {
    val currentRoute = navController.currentDestination?.route
    return currentRoute != Rutas.LoginScreen.name // Ocultar en el login
}
