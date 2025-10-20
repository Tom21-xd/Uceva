package com.Tom.uceva_dengue.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Utilidades para hacer la aplicación responsiva en diferentes tamaños de pantalla
 */

enum class WindowSize {
    COMPACT,    // Teléfonos pequeños (< 600dp width)
    MEDIUM,     // Teléfonos grandes y tablets pequeñas (600-840dp width)
    EXPANDED    // Tablets grandes (> 840dp width)
}

/**
 * Obtiene el tamaño de ventana actual basado en el ancho de la pantalla
 */
@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp.dp

    return when {
        widthDp < 600.dp -> WindowSize.COMPACT
        widthDp < 840.dp -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

/**
 * Clase que contiene todas las dimensiones responsivas de la aplicación
 */
data class AppDimensions(
    // Padding general
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp,
    val paddingExtraLarge: Dp,

    // Spacing entre elementos
    val spacingSmall: Dp,
    val spacingMedium: Dp,
    val spacingLarge: Dp,

    // Tamaños de botones
    val buttonHeight: Dp,
    val iconButtonSize: Dp,

    // Tamaños de íconos
    val iconSmall: Dp,
    val iconMedium: Dp,
    val iconLarge: Dp,
    val iconExtraLarge: Dp,

    // Tamaños de Cards
    val cardElevation: Dp,
    val cardCornerRadius: Dp,

    // Tamaños de mapas
    val mapHeight: Dp,

    // Tamaños de imágenes
    val logoSize: Dp,
    val avatarSize: Dp,

    // Tamaños de texto
    val textSizeSmall: TextUnit,
    val textSizeMedium: TextUnit,
    val textSizeLarge: TextUnit,
    val textSizeExtraLarge: TextUnit,
    val textSizeTitle: TextUnit,
    val textSizeHeader: TextUnit,

    // Alturas específicas
    val textFieldHeight: Dp,
    val chipHeight: Dp,
    val tabHeight: Dp,

    // Anchos máximos para contenido
    val maxContentWidth: Dp
)

/**
 * Obtiene las dimensiones apropiadas según el tamaño de la ventana
 */
@Composable
fun rememberAppDimensions(): AppDimensions {
    val windowSize = rememberWindowSize()

    return when (windowSize) {
        WindowSize.COMPACT -> AppDimensions(
            // Padding - Teléfonos pequeños
            paddingSmall = 8.dp,
            paddingMedium = 16.dp,
            paddingLarge = 20.dp,
            paddingExtraLarge = 24.dp,

            // Spacing
            spacingSmall = 8.dp,
            spacingMedium = 12.dp,
            spacingLarge = 16.dp,

            // Botones
            buttonHeight = 48.dp,
            iconButtonSize = 48.dp,

            // Íconos
            iconSmall = 16.dp,
            iconMedium = 20.dp,
            iconLarge = 24.dp,
            iconExtraLarge = 48.dp,

            // Cards
            cardElevation = 4.dp,
            cardCornerRadius = 12.dp,

            // Mapa
            mapHeight = 280.dp,

            // Imágenes
            logoSize = 56.dp,
            avatarSize = 40.dp,

            // Texto
            textSizeSmall = 11.sp,
            textSizeMedium = 13.sp,
            textSizeLarge = 15.sp,
            textSizeExtraLarge = 18.sp,
            textSizeTitle = 20.sp,
            textSizeHeader = 24.sp,

            // Alturas
            textFieldHeight = 56.dp,
            chipHeight = 28.dp,
            tabHeight = 48.dp,

            // Ancho máximo
            maxContentWidth = 600.dp
        )

        WindowSize.MEDIUM -> AppDimensions(
            // Padding - Teléfonos grandes/tablets pequeñas
            paddingSmall = 12.dp,
            paddingMedium = 20.dp,
            paddingLarge = 28.dp,
            paddingExtraLarge = 32.dp,

            // Spacing
            spacingSmall = 12.dp,
            spacingMedium = 16.dp,
            spacingLarge = 20.dp,

            // Botones
            buttonHeight = 56.dp,
            iconButtonSize = 56.dp,

            // Íconos
            iconSmall = 18.dp,
            iconMedium = 24.dp,
            iconLarge = 28.dp,
            iconExtraLarge = 64.dp,

            // Cards
            cardElevation = 6.dp,
            cardCornerRadius = 16.dp,

            // Mapa
            mapHeight = 350.dp,

            // Imágenes
            logoSize = 72.dp,
            avatarSize = 48.dp,

            // Texto
            textSizeSmall = 13.sp,
            textSizeMedium = 15.sp,
            textSizeLarge = 17.sp,
            textSizeExtraLarge = 20.sp,
            textSizeTitle = 24.sp,
            textSizeHeader = 28.sp,

            // Alturas
            textFieldHeight = 64.dp,
            chipHeight = 32.dp,
            tabHeight = 56.dp,

            // Ancho máximo
            maxContentWidth = 720.dp
        )

        WindowSize.EXPANDED -> AppDimensions(
            // Padding - Tablets grandes
            paddingSmall = 16.dp,
            paddingMedium = 24.dp,
            paddingLarge = 32.dp,
            paddingExtraLarge = 40.dp,

            // Spacing
            spacingSmall = 16.dp,
            spacingMedium = 20.dp,
            spacingLarge = 24.dp,

            // Botones
            buttonHeight = 64.dp,
            iconButtonSize = 64.dp,

            // Íconos
            iconSmall = 20.dp,
            iconMedium = 28.dp,
            iconLarge = 32.dp,
            iconExtraLarge = 80.dp,

            // Cards
            cardElevation = 8.dp,
            cardCornerRadius = 20.dp,

            // Mapa
            mapHeight = 450.dp,

            // Imágenes
            logoSize = 96.dp,
            avatarSize = 56.dp,

            // Texto
            textSizeSmall = 15.sp,
            textSizeMedium = 17.sp,
            textSizeLarge = 19.sp,
            textSizeExtraLarge = 24.sp,
            textSizeTitle = 28.sp,
            textSizeHeader = 32.sp,

            // Alturas
            textFieldHeight = 72.dp,
            chipHeight = 36.dp,
            tabHeight = 64.dp,

            // Ancho máximo
            maxContentWidth = 1200.dp
        )
    }
}

/**
 * Obtiene el número de columnas para una grid basado en el tamaño de pantalla
 */
@Composable
fun rememberGridColumns(): Int {
    val windowSize = rememberWindowSize()
    return when (windowSize) {
        WindowSize.COMPACT -> 1
        WindowSize.MEDIUM -> 2
        WindowSize.EXPANDED -> 3
    }
}

/**
 * Obtiene el número de columnas para una grid de items (como síntomas)
 */
@Composable
fun rememberChipGridColumns(): Int {
    val windowSize = rememberWindowSize()
    return when (windowSize) {
        WindowSize.COMPACT -> 2
        WindowSize.MEDIUM -> 3
        WindowSize.EXPANDED -> 4
    }
}

/**
 * Helper para obtener padding horizontal basado en el tamaño de pantalla
 */
@Composable
fun rememberHorizontalPadding(): Dp {
    val dimensions = rememberAppDimensions()
    return dimensions.paddingMedium
}

/**
 * Helper para obtener el ancho máximo del contenido centrado
 */
@Composable
fun rememberMaxContentWidth(): Dp {
    val dimensions = rememberAppDimensions()
    return dimensions.maxContentWidth
}
