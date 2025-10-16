package com.Tom.uceva_dengue.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.utils.AppSettings
import com.Tom.uceva_dengue.utils.FontSize
import com.Tom.uceva_dengue.utils.LocationPrecision
import com.Tom.uceva_dengue.utils.MapType
import com.Tom.uceva_dengue.utils.ThemeMode
import com.Tom.uceva_dengue.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el estado y lógica de la pantalla de configuraciones
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application.applicationContext)

    // Estado de las configuraciones
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensaje de éxito/error
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        // Cargar configuraciones al inicializar
        loadSettings()
    }

    /**
     * Carga las configuraciones desde DataStore
     */
    private fun loadSettings() {
        viewModelScope.launch {
            userPreferences.appSettings.collect { settings ->
                _settings.value = settings
            }
        }
    }

    // ========== Métodos para actualizar configuraciones individuales ==========

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                userPreferences.updateThemeMode(themeMode)
                showMessage("Tema actualizado")
            } catch (e: Exception) {
                showMessage("Error al actualizar el tema")
            }
        }
    }

    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            try {
                userPreferences.updateFontSize(fontSize)
                showMessage("Tamaño de fuente actualizado")
            } catch (e: Exception) {
                showMessage("Error al actualizar tamaño de fuente")
            }
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateNotificationsEnabled(enabled)
                // Si se deshabilitan todas las notificaciones, deshabilitar también las específicas
                if (!enabled) {
                    userPreferences.updateCaseNotificationsEnabled(false)
                    userPreferences.updatePublicationNotificationsEnabled(false)
                }
            } catch (e: Exception) {
                showMessage("Error al actualizar notificaciones")
            }
        }
    }

    fun updateCaseNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateCaseNotificationsEnabled(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar notificaciones de casos")
            }
        }
    }

    fun updatePublicationNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updatePublicationNotificationsEnabled(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar notificaciones de publicaciones")
            }
        }
    }

    fun updateLocationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateLocationEnabled(enabled)
                if (!enabled) {
                    // Si se deshabilita ubicación, también deshabilitar compartir automáticamente
                    userPreferences.updateShareLocationAutomatically(false)
                }
            } catch (e: Exception) {
                showMessage("Error al actualizar configuración de ubicación")
            }
        }
    }

    fun updateLocationPrecision(precision: LocationPrecision) {
        viewModelScope.launch {
            try {
                userPreferences.updateLocationPrecision(precision)
                showMessage("Precisión de ubicación actualizada")
            } catch (e: Exception) {
                showMessage("Error al actualizar precisión de ubicación")
            }
        }
    }

    fun updateShareLocationAutomatically(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateShareLocationAutomatically(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar compartir ubicación")
            }
        }
    }

    fun updateShowPublicProfile(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateShowPublicProfile(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar visibilidad de perfil")
            }
        }
    }

    fun updateShareStatistics(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateShareStatistics(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar compartir estadísticas")
            }
        }
    }

    fun updateMapType(mapType: MapType) {
        viewModelScope.launch {
            try {
                userPreferences.updateMapType(mapType)
                showMessage("Tipo de mapa actualizado")
            } catch (e: Exception) {
                showMessage("Error al actualizar tipo de mapa")
            }
        }
    }

    fun updateShowHeatMapByDefault(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateShowHeatMapByDefault(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar configuración de mapa de calor")
            }
        }
    }

    fun updateAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.updateAutoSync(enabled)
            } catch (e: Exception) {
                showMessage("Error al actualizar sincronización automática")
            }
        }
    }

    fun updateSyncInterval(minutes: Int) {
        viewModelScope.launch {
            try {
                userPreferences.updateSyncInterval(minutes)
                showMessage("Intervalo de sincronización actualizado")
            } catch (e: Exception) {
                showMessage("Error al actualizar intervalo de sincronización")
            }
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            try {
                userPreferences.updateLanguage(language)
                showMessage("Idioma actualizado. Reinicia la app para aplicar los cambios")
            } catch (e: Exception) {
                showMessage("Error al actualizar idioma")
            }
        }
    }

    /**
     * Restaura configuraciones a valores por defecto
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                userPreferences.updateSettings(AppSettings())
                showMessage("Configuraciones restauradas")
            } catch (e: Exception) {
                showMessage("Error al restaurar configuraciones")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Muestra un mensaje temporal
     */
    private fun showMessage(msg: String) {
        _message.value = msg
    }

    /**
     * Limpia el mensaje
     */
    fun clearMessage() {
        _message.value = null
    }

    /**
     * Obtiene el texto descriptivo del tema actual
     */
    fun getThemeModeText(themeMode: ThemeMode): String {
        return when (themeMode) {
            ThemeMode.LIGHT -> "Claro"
            ThemeMode.DARK -> "Oscuro"
            ThemeMode.SYSTEM -> "Mismo del sistema"
        }
    }

    /**
     * Obtiene el texto descriptivo del tamaño de fuente
     */
    fun getFontSizeText(fontSize: FontSize): String {
        return when (fontSize) {
            FontSize.SMALL -> "Pequeño"
            FontSize.MEDIUM -> "Mediano"
            FontSize.LARGE -> "Grande"
        }
    }

    /**
     * Obtiene el texto descriptivo de la precisión de ubicación
     */
    fun getLocationPrecisionText(precision: LocationPrecision): String {
        return when (precision) {
            LocationPrecision.HIGH -> "Alta (GPS)"
            LocationPrecision.BALANCED -> "Balanceada (WiFi + GPS)"
            LocationPrecision.LOW -> "Baja (Solo red)"
        }
    }

    /**
     * Obtiene el texto descriptivo del tipo de mapa
     */
    fun getMapTypeText(mapType: MapType): String {
        return when (mapType) {
            MapType.NORMAL -> "Normal"
            MapType.SATELLITE -> "Satélite"
            MapType.HYBRID -> "Híbrido"
            MapType.TERRAIN -> "Terreno"
        }
    }
}
