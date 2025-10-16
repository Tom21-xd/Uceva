package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tom.uceva_dengue.ui.viewModel.SettingsViewModel
import com.Tom.uceva_dengue.utils.FontSize
import com.Tom.uceva_dengue.utils.LocationPrecision
import com.Tom.uceva_dengue.utils.MapType
import com.Tom.uceva_dengue.utils.ThemeMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val message by viewModel.message.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Diálogos de selección
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showLocationPrecisionDialog by remember { mutableStateOf(false) }
    // var showMapTypeDialog by remember { mutableStateOf(false) } // TODO: Implementar aplicación de tipo de mapa
    var showSyncIntervalDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Mostrar mensajes con Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Sección: Apariencia
                item {
                    SettingsSectionHeader(
                        title = "Apariencia",
                        icon = Icons.Default.Palette
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.DarkMode,
                        title = "Tema",
                        subtitle = viewModel.getThemeModeText(settings.themeMode),
                        onClick = { showThemeDialog = true }
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.FormatSize,
                        title = "Tamaño de fuente",
                        subtitle = viewModel.getFontSizeText(settings.fontSize),
                        onClick = { showFontSizeDialog = true }
                    )
                }

                // Sección: Notificaciones
                item {
                    SettingsSectionHeader(
                        title = "Notificaciones",
                        icon = Icons.Default.Notifications
                    )
                }

                item {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Habilitar todas las notificaciones",
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.updateNotificationsEnabled(it) }
                    )
                }

                if (settings.notificationsEnabled) {
                    item {
                        SettingsSwitchItem(
                            icon = Icons.Default.MedicalServices,
                            title = "Notificaciones de casos",
                            subtitle = "Alertas sobre nuevos casos de dengue",
                            checked = settings.caseNotificationsEnabled,
                            onCheckedChange = { viewModel.updateCaseNotificationsEnabled(it) },
                            indent = true
                        )
                    }

                    item {
                        SettingsSwitchItem(
                            icon = Icons.Default.Article,
                            title = "Notificaciones de publicaciones",
                            subtitle = "Alertas sobre nuevas publicaciones",
                            checked = settings.publicationNotificationsEnabled,
                            onCheckedChange = { viewModel.updatePublicationNotificationsEnabled(it) },
                            indent = true
                        )
                    }
                }

                // Sección: Ubicación
                item {
                    SettingsSectionHeader(
                        title = "Ubicación",
                        icon = Icons.Default.LocationOn
                    )
                }

                item {
                    SettingsSwitchItem(
                        icon = Icons.Default.LocationOn,
                        title = "Servicios de ubicación",
                        subtitle = "Permitir acceso a la ubicación",
                        checked = settings.locationEnabled,
                        onCheckedChange = { viewModel.updateLocationEnabled(it) }
                    )
                }

                if (settings.locationEnabled) {
                    item {
                        SettingsItem(
                            icon = Icons.Default.GpsFixed,
                            title = "Precisión de ubicación",
                            subtitle = viewModel.getLocationPrecisionText(settings.locationPrecision),
                            onClick = { showLocationPrecisionDialog = true },
                            indent = true
                        )
                    }

                    item {
                        SettingsSwitchItem(
                            icon = Icons.Default.Share,
                            title = "Compartir ubicación automáticamente",
                            subtitle = "Al reportar casos",
                            checked = settings.shareLocationAutomatically,
                            onCheckedChange = { viewModel.updateShareLocationAutomatically(it) },
                            indent = true
                        )
                    }
                }

                // Sección: Mapas (TODO: Implementar aplicación de configuraciones de mapa)
                // item {
                //     SettingsSectionHeader(
                //         title = "Mapas",
                //         icon = Icons.Default.Map
                //     )
                // }
                //
                // item {
                //     SettingsItem(
                //         icon = Icons.Default.Map,
                //         title = "Tipo de mapa",
                //         subtitle = viewModel.getMapTypeText(settings.mapType),
                //         onClick = { showMapTypeDialog = true }
                //     )
                // }
                //
                // item {
                //     SettingsSwitchItem(
                //         icon = Icons.Default.Thermostat,
                //         title = "Mostrar mapa de calor por defecto",
                //         subtitle = "Visualización de densidad de casos",
                //         checked = settings.showHeatMapByDefault,
                //         onCheckedChange = { viewModel.updateShowHeatMapByDefault(it) }
                //     )
                // }

                // Sección: Sincronización
                item {
                    SettingsSectionHeader(
                        title = "Sincronización",
                        icon = Icons.Default.Sync
                    )
                }

                item {
                    SettingsSwitchItem(
                        icon = Icons.Default.Sync,
                        title = "Sincronización automática",
                        subtitle = "Actualizar datos automáticamente",
                        checked = settings.autoSync,
                        onCheckedChange = { viewModel.updateAutoSync(it) }
                    )
                }

                if (settings.autoSync) {
                    item {
                        SettingsItem(
                            icon = Icons.Default.Timer,
                            title = "Intervalo de sincronización",
                            subtitle = "${settings.syncIntervalMinutes} minutos",
                            onClick = { showSyncIntervalDialog = true },
                            indent = true
                        )
                    }
                }

                // Sección: General
                item {
                    SettingsSectionHeader(
                        title = "General",
                        icon = Icons.Default.Settings
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.RestartAlt,
                        title = "Restaurar valores por defecto",
                        subtitle = "Restablecer todas las configuraciones",
                        onClick = { showResetDialog = true },
                        textColor = MaterialTheme.colorScheme.error
                    )
                }

                // Espacio final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Indicador de carga
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Diálogos
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = settings.themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = { theme ->
                viewModel.updateThemeMode(theme)
                showThemeDialog = false
            }
        )
    }

    if (showFontSizeDialog) {
        FontSizeSelectionDialog(
            currentSize = settings.fontSize,
            onDismiss = { showFontSizeDialog = false },
            onSelect = { size ->
                viewModel.updateFontSize(size)
                showFontSizeDialog = false
            }
        )
    }

    if (showLocationPrecisionDialog) {
        LocationPrecisionDialog(
            currentPrecision = settings.locationPrecision,
            onDismiss = { showLocationPrecisionDialog = false },
            onSelect = { precision ->
                viewModel.updateLocationPrecision(precision)
                showLocationPrecisionDialog = false
            }
        )
    }

    // if (showMapTypeDialog) {
    //     MapTypeDialog(
    //         currentType = settings.mapType,
    //         onDismiss = { showMapTypeDialog = false },
    //         onSelect = { type ->
    //             viewModel.updateMapType(type)
    //             showMapTypeDialog = false
    //         }
    //     )
    // }

    if (showSyncIntervalDialog) {
        SyncIntervalDialog(
            currentInterval = settings.syncIntervalMinutes,
            onDismiss = { showSyncIntervalDialog = false },
            onConfirm = { interval ->
                viewModel.updateSyncInterval(interval)
                showSyncIntervalDialog = false
            }
        )
    }

    // if (showLanguageDialog) {
    //     LanguageDialog(
    //         currentLanguage = settings.language,
    //         onDismiss = { showLanguageDialog = false },
    //         onSelect = { language ->
    //             viewModel.updateLanguage(language)
    //             showLanguageDialog = false
    //         }
    //     )
    // }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Restaurar configuraciones") },
            text = { Text("¿Estás seguro de que deseas restaurar todas las configuraciones a sus valores por defecto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Componentes reutilizables
@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    indent: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = if (indent) 32.dp else 0.dp
            ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    indent: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (indent) 32.dp else 0.dp
            ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

// Diálogos de selección
@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onDismiss: () -> Unit,
    onSelect: (ThemeMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar tema") },
        text = {
            Column {
                ThemeMode.values().forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(theme) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = { onSelect(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (theme) {
                                ThemeMode.LIGHT -> "Claro"
                                ThemeMode.DARK -> "Oscuro"
                                ThemeMode.SYSTEM -> "Mismo del sistema"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun FontSizeSelectionDialog(
    currentSize: FontSize,
    onDismiss: () -> Unit,
    onSelect: (FontSize) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar tamaño de fuente") },
        text = {
            Column {
                FontSize.values().forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(size) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = size == currentSize,
                            onClick = { onSelect(size) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (size) {
                                FontSize.SMALL -> "Pequeño"
                                FontSize.MEDIUM -> "Mediano"
                                FontSize.LARGE -> "Grande"
                            },
                            fontSize = when (size) {
                                FontSize.SMALL -> 12.sp
                                FontSize.MEDIUM -> 16.sp
                                FontSize.LARGE -> 20.sp
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun LocationPrecisionDialog(
    currentPrecision: LocationPrecision,
    onDismiss: () -> Unit,
    onSelect: (LocationPrecision) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Precisión de ubicación") },
        text = {
            Column {
                LocationPrecision.values().forEach { precision ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(precision) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = precision == currentPrecision,
                            onClick = { onSelect(precision) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = when (precision) {
                                    LocationPrecision.HIGH -> "Alta"
                                    LocationPrecision.BALANCED -> "Balanceada"
                                    LocationPrecision.LOW -> "Baja"
                                },
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when (precision) {
                                    LocationPrecision.HIGH -> "GPS (Mayor consumo de batería)"
                                    LocationPrecision.BALANCED -> "WiFi + GPS (Recomendado)"
                                    LocationPrecision.LOW -> "Solo red (Menor precisión)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun MapTypeDialog(
    currentType: MapType,
    onDismiss: () -> Unit,
    onSelect: (MapType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tipo de mapa") },
        text = {
            Column {
                MapType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(type) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = type == currentType,
                            onClick = { onSelect(type) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (type) {
                                MapType.NORMAL -> "Normal"
                                MapType.SATELLITE -> "Satélite"
                                MapType.HYBRID -> "Híbrido"
                                MapType.TERRAIN -> "Terreno"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun SyncIntervalDialog(
    currentInterval: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val intervals = listOf(15, 30, 60, 120, 240)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Intervalo de sincronización") },
        text = {
            Column {
                intervals.forEach { interval ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onConfirm(interval) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = interval == currentInterval,
                            onClick = { onConfirm(interval) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (interval) {
                                in 1..59 -> "$interval minutos"
                                60 -> "1 hora"
                                else -> "${interval / 60} horas"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun LanguageDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar idioma") },
        text = {
            Column {
                listOf("es" to "Español", "en" to "English").forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(code) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = code == currentLanguage,
                            onClick = { onSelect(code) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
