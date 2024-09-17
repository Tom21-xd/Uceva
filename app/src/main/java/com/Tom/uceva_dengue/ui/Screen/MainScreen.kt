package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar
import java.lang.reflect.Modifier

@Composable
fun MainScreen(){
    val navController = rememberNavController()
    //val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // State to handle selected bottom navigation item
    //var selectedItem by remember { mutableStateOf(0) }


}

//@Composable
//fun NavigationHost(navController: NavHostController ) {
//    NavHost(navController, startDestination = "home") {
//        composable("home") { HomeScreen() }
//        composable("notifications") { NotificationScreen() }
//        composable("map") { MapScreen() }
//    }
//}

