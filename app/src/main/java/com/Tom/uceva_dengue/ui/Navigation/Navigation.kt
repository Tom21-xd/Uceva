package com.Tom.uceva_dengue.ui.Navigation

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar
import com.Tom.uceva_dengue.ui.Components.MenuLateral
import com.Tom.uceva_dengue.ui.Screen.CreatePublicationScreen
import com.Tom.uceva_dengue.ui.Screen.HomeScreen
import com.Tom.uceva_dengue.ui.Screen.LoginScreen
import com.Tom.uceva_dengue.ui.Screen.MapScreen
import com.Tom.uceva_dengue.ui.Screen.NotificationScreen
import com.Tom.uceva_dengue.ui.Screen.ProfileScreen
import com.Tom.uceva_dengue.ui.theme.fondo
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel
import com.Tom.uceva_dengue.ui.viewModel.CreatePublicationViewModel
import com.Tom.uceva_dengue.ui.viewModel.MapViewModel
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationCon(context: Context) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null)

    val authRepository = AuthRepository(context)
    val user = authRepository.getUser()
    val role = authRepository.getRole()
    Log.d("NavigationCon", "User: $user, Role: $role")
    val startDestination = if (user == null) {
        Rout.LoginScreen.name
    } else {
        Rout.HomeScreen.name
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            MenuLateral(navController = navController, drawerState = drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                currentRoute.value?.destination?.route?.let { route ->
                    if (route != Rout.LoginScreen.name) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = getTopBarTitle(route),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 0.dp, 60.dp, 0.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = fondo)
                        )
                    }
                }
            },
            bottomBar = {
                currentRoute.value?.destination?.route?.let { route ->
                    if (route != Rout.LoginScreen.name) {
                        BottomNavigationBar(navController)
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Rout.LoginScreen.name) {
                    LoginScreen(viewModel = viewModel<AuthViewModel>(), navController = navController)
                }
                composable(Rout.HomeScreen.name) {
                    HomeScreen(viewModel = PublicacionViewModel(),role = role ,navController)
                }
                composable(Rout.MapScreen.name) {
                    MapScreen(viewModel = MapViewModel())
                }
                composable(Rout.NotificationScreen.name) {
                    NotificationScreen()
                }
                composable(Rout.ProfileScreen.name) {
                    ProfileScreen()
                }
                composable(Rout.OptionScreen.name) {
                }
                composable(Rout.InfoScreen.name) {

                }
                composable(Rout.CreatePublicationScreen.name) {
                    CreatePublicationScreen(viewModel = CreatePublicationViewModel())
                }

            }
        }
    }
}

fun getTopBarTitle(route: String): String {
    return when (route) {
        Rout.HomeScreen.name -> "Publicaciones"
        Rout.MapScreen.name -> "Mapa de calor"
        Rout.NotificationScreen.name -> "Notificaciones"
        else -> "Mi Aplicación"
    }
}
