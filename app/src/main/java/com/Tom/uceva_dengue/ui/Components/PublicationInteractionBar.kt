package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de interacciones de una publicación
 * Muestra reacciones, comentarios, vistas y guardados
 * Soporta tema claro/oscuro automáticamente
 */
@Composable
fun PublicationInteractionBar(
    totalReacciones: Int = 0,
    totalComentarios: Int = 0,
    totalVistas: Int = 0,
    totalGuardados: Int = 0,
    usuarioHaReaccionado: Boolean = false,
    usuarioHaGuardado: Boolean = false,
    onReactionClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de Reacción (Me gusta)
        InteractionButton(
            icon = if (usuarioHaReaccionado) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            count = totalReacciones,
            isActive = usuarioHaReaccionado,
            onClick = onReactionClick,
            contentDescription = "Me gusta"
        )

        // Botón de Comentarios
        InteractionButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            count = totalComentarios,
            isActive = false,
            onClick = onCommentClick,
            contentDescription = "Comentarios"
        )

        // Indicador de Vistas (no clickable)
        InteractionIndicator(
            icon = Icons.Outlined.Visibility,
            count = totalVistas,
            contentDescription = "Vistas"
        )

        // Botón de Guardar
        InteractionButton(
            icon = if (usuarioHaGuardado) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            count = totalGuardados,
            isActive = usuarioHaGuardado,
            onClick = onSaveClick,
            contentDescription = "Guardar"
        )
    }
}

/**
 * Botón de interacción individual (clickable)
 */
@Composable
private fun InteractionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    isActive: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatCount(count),
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Indicador de interacción (no clickable)
 */
@Composable
private fun InteractionIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    contentDescription: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatCount(count),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Formatea números grandes (1.2K, 10K, etc.)
 */
private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
    }
}

/**
 * Barra compacta de estadísticas (solo números, sin botones)
 * Útil para mostrar stats sin interacción
 */
@Composable
fun PublicationStatsBar(
    totalReacciones: Int = 0,
    totalComentarios: Int = 0,
    totalVistas: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (totalReacciones > 0) {
            StatItem(
                icon = Icons.Filled.Favorite,
                count = totalReacciones,
                label = "reacciones"
            )
        }

        if (totalComentarios > 0) {
            StatItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                count = totalComentarios,
                label = "comentarios"
            )
        }

        if (totalVistas > 0) {
            StatItem(
                icon = Icons.Outlined.Visibility,
                count = totalVistas,
                label = "vistas"
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = formatCount(count),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
