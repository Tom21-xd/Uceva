package com.Tom.uceva_dengue.ui.Menus

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.Tom.uceva_dengue.ui.Navigation.Routes

sealed  class Items_Menu_lateral (
    val icon : ImageVector,
    val title: String,
    val route: String
){
    object Item_Menu_Lateral1: Items_Menu_lateral(
        Icons.Default.Person,
        "Perfil",
        Routes.ProfileScreen.name
    )
    object Item_Menu_Lateral2: Items_Menu_lateral(
        Icons.Default.Settings,
        "Opciones",
        Routes.OptionScreen.name
    )
    object Item_Menu_Lateral3: Items_Menu_lateral(
        Icons.Default.Info,
        "Informacion",
        Routes.InfoScreen.name
    )

}