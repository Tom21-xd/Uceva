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
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            Log.e("APP_CRASH", "Error inesperado", throwable)
        }
        // Inicializar Firebase Cloud Messaging y obtener el token
        initializeFCM()
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

    private fun initializeFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Error al obtener FCM token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "FCM Token obtenido: $token")
        }
    }
}

