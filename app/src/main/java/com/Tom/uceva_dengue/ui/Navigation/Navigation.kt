package com.Tom.uceva_dengue.ui.Navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.ui.Screen.HomeScreen
import com.Tom.uceva_dengue.ui.Screen.LoginScreen
import com.Tom.uceva_dengue.ui.Screen.MapScreen
import com.Tom.uceva_dengue.ui.Screen.NotificationScreen
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel


@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
    NavHost(navController = navController,startDestination = Rout.LoginScreen.name) {
        composable(Rout.LoginScreen.name ) {
            LoginScreen(viewModel = AuthViewModel(), navController = navController)
        }
        composable(Rout.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable (Rout.MapScreen.name){
            MapScreen(navController = navController)
        }
        composable (Rout.NotificationScreen.name){
            NotificationScreen(navController = navController)
        }

    }
}
