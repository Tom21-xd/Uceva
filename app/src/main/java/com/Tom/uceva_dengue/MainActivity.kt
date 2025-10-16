package com.Tom.uceva_dengue

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.Tom.uceva_dengue.ui.Navigation.NavigationCon

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge para que la app use toda la pantalla
        // El TopAppBar respetará el status bar automáticamente
        enableEdgeToEdge()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            Log.e("APP_CRASH", "Error inesperado", throwable)
        }
        setContent {
            NavigationCon(this)
        }
    }
}

