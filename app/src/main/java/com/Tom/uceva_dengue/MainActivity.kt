package com.Tom.uceva_dengue

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.Tom.uceva_dengue.ui.Navigation.NavigationCon
import com.Tom.uceva_dengue.ui.theme.Uceva_dengueTheme
import com.Tom.uceva_dengue.utils.ThemeMode
import com.Tom.uceva_dengue.utils.UserPreferences

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
            val userPreferences = UserPreferences(applicationContext)
            val settings by userPreferences.appSettings.collectAsState(initial = com.Tom.uceva_dengue.utils.AppSettings())

            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            Uceva_dengueTheme(
                darkTheme = darkTheme,
                fontSize = settings.fontSize
            ) {
                NavigationCon(this)
            }
        }
    }
}

