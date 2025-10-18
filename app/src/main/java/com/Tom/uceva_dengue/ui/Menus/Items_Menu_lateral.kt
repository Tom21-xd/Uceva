package com.Tom.uceva_dengue.ui.Menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.Tom.uceva_dengue.ui.Navigation.Rout

sealed  class Items_Menu_lateral (
    val icon : ImageVector,
    val title: String,
    val route: String
){
    object Item_Menu_Lateral1: Items_Menu_lateral(
        Icons.Default.Person,
        "Perfil",
        Rout.ProfileScreen.name
    )
    object Item_Menu_Lateral2: Items_Menu_lateral(
        Icons.Default.Settings,
        "Opciones",
        Rout.OptionScreen.name
    )
    object Item_Menu_Lateral3: Items_Menu_lateral(
        Icons.Default.Info,
        "Informacion",
        Rout.InfoScreen.name
    )
    object Item_Menu_Lateral4: Items_Menu_lateral(
        Icons.Default.Home,
        "Home",
        Rout.HomeScreen.name
    )
    object Item_Menu_Lateral5: Items_Menu_lateral(
        Icons.Default.Cases,
        "Casos de dengue",
        Rout.CaseScreen.name
    )
    object Item_Menu_Lateral6: Items_Menu_lateral(
        Icons.Default.LocalHospital,
        "Hospitales",
        Rout.HospitalScreen.name
    )
    object Item_Menu_Lateral7: Items_Menu_lateral(
        Icons.Default.ManageAccounts,
        "Gestión de Usuarios",
        Rout.UserManagementScreen.name
    )
    object Item_Menu_Lateral8: Items_Menu_lateral(
        Icons.Default.HealthAndSafety,
        "Guía de Prevención",
        Rout.PreventionGuideScreen.name
    )


}