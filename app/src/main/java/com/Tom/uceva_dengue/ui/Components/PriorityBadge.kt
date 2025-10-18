package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Badge para mostrar la prioridad de una publicación
 * Colores adaptados al tema claro/oscuro
 */
@Composable
fun PriorityBadge(
    priority: String?,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val priorityData = when (priority?.uppercase()) {
        "URGENTE" -> PriorityStyle(
            label = "Urgente",
            icon = Icons.Default.Warning,
            containerColor = Color(0xFFDC2626),  // Rojo urgente
            contentColor = Color.White
        )
        "ALTA" -> PriorityStyle(
            label = "Alta",
            icon = Icons.Default.KeyboardArrowUp,
            containerColor = Color(0xFFEA580C),  // Naranja
            contentColor = Color.White
        )
        "NORMAL" -> PriorityStyle(
            label = "Normal",
            icon = Icons.Default.Info,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        "BAJA" -> PriorityStyle(
            label = "Baja",
            icon = Icons.Default.KeyboardArrowDown,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        else -> return  // No mostrar badge si no hay prioridad
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = priorityData.containerColor,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showIcon) {
                Icon(
                    imageVector = priorityData.icon,
                    contentDescription = "Prioridad ${priorityData.label}",
                    tint = priorityData.contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = priorityData.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = priorityData.contentColor
            )
        }
    }
}

/**
 * Clase de datos para estilos de prioridad
 */
private data class PriorityStyle(
    val label: String,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

/**
 * Badge compacto de prioridad (solo icono)
 * Útil para listas densas
 */
@Composable
fun CompactPriorityBadge(
    priority: String?,
    modifier: Modifier = Modifier
) {
    val priorityData = when (priority?.uppercase()) {
        "URGENTE" -> PriorityStyle(
            label = "Urgente",
            icon = Icons.Default.Warning,
            containerColor = Color(0xFFDC2626),
            contentColor = Color.White
        )
        "ALTA" -> PriorityStyle(
            label = "Alta",
            icon = Icons.Default.KeyboardArrowUp,
            containerColor = Color(0xFFEA580C),
            contentColor = Color.White
        )
        "NORMAL" -> PriorityStyle(
            label = "Normal",
            icon = Icons.Default.Info,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        "BAJA" -> PriorityStyle(
            label = "Baja",
            icon = Icons.Default.KeyboardArrowDown,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        else -> return
    }

    Box(
        modifier = modifier
            .size(28.dp)
            .background(priorityData.containerColor, RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = priorityData.icon,
            contentDescription = "Prioridad ${priorityData.label}",
            tint = priorityData.contentColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Indicador de publicación fijada (pinned)
 */
@Composable
fun PinnedIndicator(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Publicación fijada",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Fijada",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
