package com.Tom.uceva_dengue.ui.Menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
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
        "Inicio",
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
    object Item_Menu_Lateral9: Items_Menu_lateral(
        Icons.Default.Bookmark,
        "Mis Guardados",
        Rout.SavedPublicationsScreen.name
    )
    object Item_Menu_Lateral10: Items_Menu_lateral(
        Icons.Default.AdminPanelSettings,
        "Gestión de Permisos",
        Rout.PermissionsManagementScreen.name
    )
    object Item_Menu_Lateral11: Items_Menu_lateral(
        Icons.Default.GroupWork,
        "Gestión de Roles",
        Rout.RoleManagementScreen.name
    )
    object Item_Menu_Lateral12: Items_Menu_lateral(
        Icons.Default.Upload,
        "Importar Casos",
        Rout.ImportCasesScreen.name
    )
    object Item_Menu_Lateral13: Items_Menu_lateral(
        Icons.Default.CheckCircle,
        "Aprobar Usuarios",
        Rout.UserApprovalScreen.name
    )

}