package com.Tom.uceva_dengue.utils

/**
 * Enum para manejar los modos de tema de la aplicación
 */
enum class ThemeMode {
    LIGHT,      // Tema claro
    DARK,       // Tema oscuro
    SYSTEM      // Seguir configuración del sistema
}

/**
 * Enum para manejar el tamaño de fuente
 */
enum class FontSize {
    SMALL,
    MEDIUM,
    LARGE
}

/**
 * Enum para tipo de mapa
 */
enum class MapType {
    NORMAL,
    SATELLITE,
    HYBRID,
    TERRAIN
}

/**
 * Enum para precisión de ubicación
 */
enum class LocationPrecision {
    HIGH,       // Alta precisión (GPS)
    BALANCED,   // Balanceado (WiFi + GPS)
    LOW         // Baja precisión (solo red)
}

/**
 * Data class que representa todas las configuraciones de la app
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val fontSize: FontSize = FontSize.MEDIUM,
    val notificationsEnabled: Boolean = true,
    val caseNotificationsEnabled: Boolean = true,
    val publicationNotificationsEnabled: Boolean = true,
    val locationEnabled: Boolean = true,
    val locationPrecision: LocationPrecision = LocationPrecision.BALANCED,
    val shareLocationAutomatically: Boolean = false,
    val showPublicProfile: Boolean = true,
    val shareStatistics: Boolean = true,
    val mapType: MapType = MapType.NORMAL,
    val showHeatMapByDefault: Boolean = true,
    val autoSync: Boolean = true,
    val syncIntervalMinutes: Int = 30,
    val language: String = "es" // "es" para español, "en" para inglés
)
