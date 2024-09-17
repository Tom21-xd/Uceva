package com.Tom.uceva_dengue.ui.Components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.Tom.uceva_dengue.ui.Menus.Items_Menu_lateral.*
import androidx.navigation.NavHostController

@Composable
fun MenuLateral(navController: NavHostController,drawerState: DrawerState,contenido: @Composable () -> Unit){
    val menu_items =  listOf(
        Item_Menu_Lateral1,
        Item_Menu_Lateral2,
        Item_Menu_Lateral3
    )
    /*ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                menu_items.forEach{ item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = ,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    ) {

    }*/
}