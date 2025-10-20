package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.NotificationModel
import com.Tom.uceva_dengue.ui.viewModel.NotificationViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val error by viewModel.error.collectAsState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                    isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    error != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(dimensions.iconExtraLarge)
                                )
                                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                                Text(text = error ?: "Error inesperado", color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                                Button(onClick = { viewModel.fetchNotifications() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                    notifications.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(dimensions.iconExtraLarge)
                                )
                                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                                Text(
                                    text = "No hay notificaciones",
                                    fontSize = dimensions.textSizeLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                                Text(
                                    text = "Desliza hacia abajo para actualizar",
                                    fontSize = dimensions.textSizeMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
                        contentPadding = PaddingValues(dimensions.paddingMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notifications) { notif ->
                            NotificationCard(notif, dimensions)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun NotificationCard(notification: NotificationModel, dimensions: com.Tom.uceva_dengue.utils.AppDimensions) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(dimensions.cardCornerRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            verticalAlignment = Alignment.Top
        ) {
            // Icono circular
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(dimensions.iconExtraLarge)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensions.iconLarge)
                    )
                }
            }

            Spacer(modifier = Modifier.width(dimensions.spacingMedium))

            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificación",
                        fontSize = dimensions.textSizeLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(dimensions.paddingSmall),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = formatNotificationDate(notification.FECHA_NOTIFICACION),
                            fontSize = dimensions.textSizeSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = dimensions.paddingSmall, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.paddingSmall))

                Text(
                    text = notification.CONTENIDO_NOTIFICACION.ifEmpty { "Sin contenido" },
                    fontSize = dimensions.textSizeMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = dimensions.paddingLarge
                )
            }
        }
    }
}

private fun formatNotificationDate(dateString: String): String {
    if (dateString.isEmpty()) return ""

    try {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val date = formatter.parse(dateString) ?: return dateString
        val now = java.util.Date()
        val diffInMillis = now.time - date.time

        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Ahora"
            minutes < 60 -> "${minutes}m"
            hours < 24 -> "${hours}h"
            days < 7 -> "${days}d"
            else -> {
                val displayFormatter = java.text.SimpleDateFormat("dd MMM", java.util.Locale("es", "ES"))
                displayFormatter.format(date)
            }
        }
    } catch (e: Exception) {
        return dateString
    }
}
