package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar

@Composable
fun MainScreen(){
    val navController = rememberNavController()
    Scaffold (
        bottomBar = {
            BottomNavigationBar(navController)      }
    ){padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()){

        }
    }
}

