package com.Tom.uceva_dengue.ui.Menus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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


}