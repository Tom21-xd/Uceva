package com.Tom.uceva_dengue.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkBluePrimary,
    secondary = BlueGrey80,
    tertiary = SkyBlue80,
    background = DarkBlueBackground,
    surface = DarkBlue,  // Para TopAppBar y BottomNav
    surfaceVariant = DarkBlueSurface,
    primaryContainer = Color(0xFF1E3A8A),  // Azul oscuro para contenedores
    secondaryContainer = Color(0xFF1E3A5F),
    onPrimary = blanco,
    onSecondary = blanco,
    onBackground = blanco,
    onSurface = blanco,
    onPrimaryContainer = Blue80,
    onSecondaryContainer = BlueGrey80
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = DeepBlue40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    primaryContainer = Blue80,
    secondaryContainer = BlueGrey80,
    onPrimary = blanco,
    onSecondary = blanco,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onPrimaryContainer = Color(0xFF1C3A6E),
    onSecondaryContainer = Color(0xFF2D3748)
)

@Composable
fun Uceva_dengueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontSize: com.Tom.uceva_dengue.utils.FontSize = com.Tom.uceva_dengue.utils.FontSize.MEDIUM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Deshabilitado para usar nuestros colores personalizados
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(fontSize),
        content = content
    )
}