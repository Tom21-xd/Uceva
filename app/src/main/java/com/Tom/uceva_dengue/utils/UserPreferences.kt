package com.Tom.uceva_dengue.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Configuración del DataStore como delegado de propiedad
val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "user_session")

object UserPreferencesKeys {
    // Sesión de usuario
    val USER_ROLE = stringPreferencesKey("user_role")
    val USER_TOKEN = stringPreferencesKey("user_token")

    // Configuraciones de apariencia
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val FONT_SIZE = stringPreferencesKey("font_size")

    // Notificaciones
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val CASE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("case_notifications_enabled")
    val PUBLICATION_NOTIFICATIONS_ENABLED = booleanPreferencesKey("publication_notifications_enabled")

    // Ubicación
    val LOCATION_ENABLED = booleanPreferencesKey("location_enabled")
    val LOCATION_PRECISION = stringPreferencesKey("location_precision")
    val SHARE_LOCATION_AUTOMATICALLY = booleanPreferencesKey("share_location_automatically")

    // Privacidad
    val SHOW_PUBLIC_PROFILE = booleanPreferencesKey("show_public_profile")
    val SHARE_STATISTICS = booleanPreferencesKey("share_statistics")

    // Mapas
    val MAP_TYPE = stringPreferencesKey("map_type")
    val SHOW_HEAT_MAP_BY_DEFAULT = booleanPreferencesKey("show_heat_map_by_default")

    // Sincronización
    val AUTO_SYNC = booleanPreferencesKey("auto_sync")
    val SYNC_INTERVAL_MINUTES = intPreferencesKey("sync_interval_minutes")

    // Idioma
    val LANGUAGE = stringPreferencesKey("language")
}

class UserPreferences(private val context: Context) {

    // ========== Flujos de Sesión de Usuario ==========
    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.USER_ROLE]
    }

    val userToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.USER_TOKEN]
    }

    // ========== Flujos de Configuraciones ==========
    val appSettings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            themeMode = ThemeMode.valueOf(
                preferences[UserPreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            ),
            fontSize = FontSize.valueOf(
                preferences[UserPreferencesKeys.FONT_SIZE] ?: FontSize.MEDIUM.name
            ),
            notificationsEnabled = preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            caseNotificationsEnabled = preferences[UserPreferencesKeys.CASE_NOTIFICATIONS_ENABLED] ?: true,
            publicationNotificationsEnabled = preferences[UserPreferencesKeys.PUBLICATION_NOTIFICATIONS_ENABLED] ?: true,
            locationEnabled = preferences[UserPreferencesKeys.LOCATION_ENABLED] ?: true,
            locationPrecision = LocationPrecision.valueOf(
                preferences[UserPreferencesKeys.LOCATION_PRECISION] ?: LocationPrecision.BALANCED.name
            ),
            shareLocationAutomatically = preferences[UserPreferencesKeys.SHARE_LOCATION_AUTOMATICALLY] ?: false,
            showPublicProfile = preferences[UserPreferencesKeys.SHOW_PUBLIC_PROFILE] ?: true,
            shareStatistics = preferences[UserPreferencesKeys.SHARE_STATISTICS] ?: true,
            mapType = MapType.valueOf(
                preferences[UserPreferencesKeys.MAP_TYPE] ?: MapType.NORMAL.name
            ),
            showHeatMapByDefault = preferences[UserPreferencesKeys.SHOW_HEAT_MAP_BY_DEFAULT] ?: true,
            autoSync = preferences[UserPreferencesKeys.AUTO_SYNC] ?: true,
            syncIntervalMinutes = preferences[UserPreferencesKeys.SYNC_INTERVAL_MINUTES] ?: 30,
            language = preferences[UserPreferencesKeys.LANGUAGE] ?: "es"
        )
    }

    // ========== Métodos de Sesión ==========
    suspend fun saveUserSession(role: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_ROLE] = role
            preferences[UserPreferencesKeys.USER_TOKEN] = token
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // ========== Métodos de Configuración ==========
    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateFontSize(fontSize: FontSize) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.FONT_SIZE] = fontSize.name
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateCaseNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.CASE_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updatePublicationNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.PUBLICATION_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateLocationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.LOCATION_ENABLED] = enabled
        }
    }

    suspend fun updateLocationPrecision(precision: LocationPrecision) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.LOCATION_PRECISION] = precision.name
        }
    }

    suspend fun updateShareLocationAutomatically(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SHARE_LOCATION_AUTOMATICALLY] = enabled
        }
    }

    suspend fun updateShowPublicProfile(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SHOW_PUBLIC_PROFILE] = enabled
        }
    }

    suspend fun updateShareStatistics(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SHARE_STATISTICS] = enabled
        }
    }

    suspend fun updateMapType(mapType: MapType) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.MAP_TYPE] = mapType.name
        }
    }

    suspend fun updateShowHeatMapByDefault(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SHOW_HEAT_MAP_BY_DEFAULT] = enabled
        }
    }

    suspend fun updateAutoSync(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.AUTO_SYNC] = enabled
        }
    }

    suspend fun updateSyncInterval(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SYNC_INTERVAL_MINUTES] = minutes
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.LANGUAGE] = language
        }
    }

    // Método para actualizar todas las configuraciones de una vez
    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.THEME_MODE] = settings.themeMode.name
            preferences[UserPreferencesKeys.FONT_SIZE] = settings.fontSize.name
            preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
            preferences[UserPreferencesKeys.CASE_NOTIFICATIONS_ENABLED] = settings.caseNotificationsEnabled
            preferences[UserPreferencesKeys.PUBLICATION_NOTIFICATIONS_ENABLED] = settings.publicationNotificationsEnabled
            preferences[UserPreferencesKeys.LOCATION_ENABLED] = settings.locationEnabled
            preferences[UserPreferencesKeys.LOCATION_PRECISION] = settings.locationPrecision.name
            preferences[UserPreferencesKeys.SHARE_LOCATION_AUTOMATICALLY] = settings.shareLocationAutomatically
            preferences[UserPreferencesKeys.SHOW_PUBLIC_PROFILE] = settings.showPublicProfile
            preferences[UserPreferencesKeys.SHARE_STATISTICS] = settings.shareStatistics
            preferences[UserPreferencesKeys.MAP_TYPE] = settings.mapType.name
            preferences[UserPreferencesKeys.SHOW_HEAT_MAP_BY_DEFAULT] = settings.showHeatMapByDefault
            preferences[UserPreferencesKeys.AUTO_SYNC] = settings.autoSync
            preferences[UserPreferencesKeys.SYNC_INTERVAL_MINUTES] = settings.syncIntervalMinutes
            preferences[UserPreferencesKeys.LANGUAGE] = settings.language
        }
    }
}
