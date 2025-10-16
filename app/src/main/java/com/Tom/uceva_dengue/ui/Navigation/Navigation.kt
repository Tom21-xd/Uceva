package com.Tom.uceva_dengue.ui.Navigation

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar
import com.Tom.uceva_dengue.ui.Components.MenuLateral
import com.Tom.uceva_dengue.ui.Screen.CaseDetailsScreen
import com.Tom.uceva_dengue.ui.Screen.CaseScreen
import com.Tom.uceva_dengue.ui.Screen.CreateCaseScreen
import com.Tom.uceva_dengue.ui.Screen.CreateCaseScreenModern
import com.Tom.uceva_dengue.ui.Screen.CreatePublicationScreen
import com.Tom.uceva_dengue.ui.Screen.UpdatePublicationScreen
import com.Tom.uceva_dengue.ui.Screen.HomeScreen
import com.Tom.uceva_dengue.ui.Screen.HospitalScreen
import com.Tom.uceva_dengue.ui.Screen.CreateHospitalScreen
import com.Tom.uceva_dengue.ui.Screen.UpdateHospitalScreen
import com.Tom.uceva_dengue.ui.Screen.InfoScreen
import com.Tom.uceva_dengue.ui.Screen.LoginScreen
import com.Tom.uceva_dengue.ui.Screen.LoginScreenModern
import com.Tom.uceva_dengue.ui.Screen.ForgotPasswordScreenModern
import com.Tom.uceva_dengue.ui.Screen.MapScreen
import com.Tom.uceva_dengue.ui.Screen.MapScreenModern
import com.Tom.uceva_dengue.ui.Screen.NotificationScreen
import com.Tom.uceva_dengue.ui.Screen.ProfileScreenModern
import com.Tom.uceva_dengue.ui.Screen.UserManagementScreen
import com.Tom.uceva_dengue.ui.Screen.EditUserScreenFunctional
import com.Tom.uceva_dengue.ui.Screen.SettingsScreen
import com.Tom.uceva_dengue.ui.theme.fondo
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel
import com.Tom.uceva_dengue.ui.viewModel.CaseDetailsViewModel
import com.Tom.uceva_dengue.ui.viewModel.CaseViewModel
import com.Tom.uceva_dengue.ui.viewModel.CreateCaseViewModel
import com.Tom.uceva_dengue.ui.viewModel.CreatePublicationViewModel
import com.Tom.uceva_dengue.ui.viewModel.CreateHospitalViewModel
import com.Tom.uceva_dengue.ui.viewModel.HospitalViewModel
import com.Tom.uceva_dengue.ui.viewModel.MapViewModel
import com.Tom.uceva_dengue.ui.viewModel.NotificationViewModel
import com.Tom.uceva_dengue.ui.viewModel.ProfileViewModel
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel
import com.Tom.uceva_dengue.ui.viewModel.UserManagementViewModel
import com.Tom.uceva_dengue.ui.viewModel.EditUserViewModel
import com.Tom.uceva_dengue.ui.viewModel.SettingsViewModel
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
            MenuLateral(navController = navController, drawerState = drawerState,authRepository)
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBars,
            topBar = {
                currentRoute.value?.destination?.route?.let { route ->
                    if (route != Rout.LoginScreen.name && route != Rout.OlvContraseniaScreen.name) {
                        // TopAppBar moderno con gradiente azul que respeta el status bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF5E81F4),
                                            Color(0xFF92C5FC)
                                        )
                                    )
                                )
                                .windowInsetsPadding(WindowInsets.statusBars)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botón de menú con superficie redondeada
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                if (drawerState.isClosed) {
                                                    drawerState.open()
                                                } else {
                                                    drawerState.close()
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = "Abrir menú",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Título centrado
                                Text(
                                    text = getTopBarTitle(route),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )

                                // Espacio para mantener el título centrado
                                Spacer(modifier = Modifier.width(60.dp))
                            }
                        }
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
                    LoginScreenModern(viewModel = viewModel<AuthViewModel>(), navController = navController)
                }
                composable(Rout.HomeScreen.name) {
                    HomeScreen(
                        viewModel = PublicacionViewModel(),
                        role = role,
                        userId = user?.toIntOrNull(),
                        navController = navController
                    )
                }
                composable(Rout.MapScreen.name) {
                    MapScreenModern(viewModel = MapViewModel())
                }
                composable(Rout.NotificationScreen.name) {
                    NotificationScreen(navController, NotificationViewModel())
                }
                composable(Rout.ProfileScreen.name) {
                    ProfileScreenModern(viewModel = viewModel(), userId = user)
                }
                composable(Rout.OptionScreen.name) {
                    SettingsScreen(viewModel = viewModel())
                }
                composable(Rout.InfoScreen.name) {
                    InfoScreen()
                }
                composable(Rout.CreatePublicationScreen.name) {
                    CreatePublicationScreen(viewModel = CreatePublicationViewModel(),role,user,navController)
                }
                composable("${Rout.UpdatePublicationScreen.name}/{publicationId}") { backStackEntry ->
                    val publicationId = backStackEntry.arguments?.getString("publicationId")?.toIntOrNull() ?: 0
                    UpdatePublicationScreen(publicationId = publicationId, navController = navController)
                }
                composable(Rout.CaseScreen.name){
                    CaseScreen(caseViewModel = CaseViewModel(),role,navController)
                }
                composable(Rout.CreateCaseScreen.name) {
                    CreateCaseScreenModern(CreateCaseViewModel(), viewModel<AuthViewModel>(), role, user, navController)
                }
                composable("${Rout.CaseDetailsScreen.name}/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    val viewModel: CaseDetailsViewModel = viewModel()
                    CaseDetailsScreen( id,viewModel,navController)
                }
                composable(Rout.HospitalScreen.name){
                    HospitalScreen(
                        navController = navController,
                        viewModel = HospitalViewModel(),
                        role = role
                    )
                }
                composable(Rout.CreateHospitalScreen.name) {
                    CreateHospitalScreen(
                        navController = navController,
                        viewModel = CreateHospitalViewModel()
                    )
                }
                composable("${Rout.UpdateHospitalScreen.name}/{hospitalId}") { backStackEntry ->
                    val hospitalId = backStackEntry.arguments?.getString("hospitalId")?.toIntOrNull() ?: 0
                    UpdateHospitalScreen(
                        hospitalId = hospitalId,
                        navController = navController
                    )
                }
                composable(Rout.UserManagementScreen.name) {
                    UserManagementScreen(
                        navController = navController,
                        viewModel = UserManagementViewModel()
                    )
                }
                composable(
                    route = "${Rout.EditUserScreen.name}?userId={userId}",
                    arguments = listOf(
                        navArgument("userId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                    EditUserScreenFunctional(
                        userId = userId,
                        navController = navController,
                        viewModel = EditUserViewModel()
                    )
                }
                composable(Rout.OlvContraseniaScreen.name) {
                    ForgotPasswordScreenModern(navController = navController)
                }

            }
        }
    }
}

fun getTopBarTitle(route: String): String {
    val cleanRoute = route.substringBefore("/") // solo se queda con el nombre base
    return when (cleanRoute) {
        Rout.HomeScreen.name -> "Publicaciones"
        Rout.MapScreen.name -> "Mapa de calor"
        Rout.NotificationScreen.name -> "Notificaciones"
        Rout.ProfileScreen.name -> "Perfil"
        Rout.OptionScreen.name -> "Opciones"
        Rout.InfoScreen.name -> "Información"
        Rout.CreatePublicationScreen.name -> "Crear Publicación"
        Rout.UpdatePublicationScreen.name -> "Editar Publicación"
        Rout.CaseScreen.name -> "Casos de dengue"
        Rout.CreateCaseScreen.name -> "Crear Caso"
        Rout.CaseDetailsScreen.name -> "Detalle del caso"
        Rout.HospitalScreen.name -> "Hospitales"
        Rout.CreateHospitalScreen.name -> "Crear Hospital"
        Rout.UpdateHospitalScreen.name -> "Editar Hospital"
        Rout.UserManagementScreen.name -> "Gestión de Usuarios"
        Rout.EditUserScreen.name -> "Editar Usuario"
        else -> "Mi Aplicación"
    }
}

