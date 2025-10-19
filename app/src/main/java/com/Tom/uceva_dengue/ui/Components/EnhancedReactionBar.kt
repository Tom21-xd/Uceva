package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de interacciones mejorada con animaciones y feedback visual
 */
@Composable
fun EnhancedReactionBar(
    totalReacciones: Int = 0,
    totalComentarios: Int = 0,
    totalVistas: Int = 0,
    totalGuardados: Int = 0,
    usuarioHaReaccionado: Boolean = false,
    usuarioHaGuardado: Boolean = false,
    onReactionClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onShareClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reacción (Me gusta) con animación
            AnimatedReactionButton(
                icon = if (usuarioHaReaccionado) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                filledIcon = Icons.Filled.Favorite,
                count = totalReacciones,
                isActive = usuarioHaReaccionado,
                onClick = onReactionClick,
                label = "Me gusta",
                activeColor = Color(0xFFE91E63) // Rosa para corazón
            )

            VerticalDivider(
                modifier = Modifier.height(32.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Comentarios
            AnimatedInteractionButton(
                icon = Icons.Outlined.ChatBubbleOutline,
                count = totalComentarios,
                onClick = onCommentClick,
                label = "Comentar"
            )

            VerticalDivider(
                modifier = Modifier.height(32.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Guardar con animación
            AnimatedReactionButton(
                icon = if (usuarioHaGuardado) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                filledIcon = Icons.Filled.Bookmark,
                count = if (totalGuardados > 0) totalGuardados else null,
                isActive = usuarioHaGuardado,
                onClick = onSaveClick,
                label = if (usuarioHaGuardado) "Guardado" else "Guardar",
                activeColor = MaterialTheme.colorScheme.primary
            )

            if (onShareClick != null) {
                VerticalDivider(
                    modifier = Modifier.height(32.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Compartir
                AnimatedInteractionButton(
                    icon = Icons.Outlined.Share,
                    count = null,
                    onClick = onShareClick,
                    label = "Compartir"
                )
            }
        }
    }

    // Barra de vistas (separada, más sutil)
    if (totalVistas > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${formatCount(totalVistas)} vista${if (totalVistas > 1) "s" else ""}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Botón de reacción animado (para acciones que se activan/desactivan)
 */
@Composable
fun AnimatedReactionButton(
    icon: ImageVector,
    filledIcon: ImageVector,
    count: Int?,
    isActive: Boolean,
    onClick: () -> Unit,
    label: String,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    // Animación de escala al presionar
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "reaction_scale"
    )

    // Animación de color
    val color by animateColorAsState(
        targetValue = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "reaction_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                isPressed = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .scale(scale)
    ) {
        // Icono con efecto de aparecer
        AnimatedContent(
            targetState = isActive,
            transitionSpec = {
                if (targetState) {
                    (scaleIn(animationSpec = tween(200)) + fadeIn()).togetherWith(
                        scaleOut(animationSpec = tween(100)) + fadeOut()
                    )
                } else {
                    (fadeIn(animationSpec = tween(200))).togetherWith(
                        fadeOut(animationSpec = tween(100))
                    )
                }
            },
            label = "icon_animation"
        ) { active ->
            Icon(
                imageVector = if (active) filledIcon else icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        // Contador animado
        if (count != null && count > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                    } else {
                        slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
                    }
                },
                label = "count_animation"
            ) { animatedCount ->
                Text(
                    text = formatCount(animatedCount),
                    fontSize = 12.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = color
                )
            }
        } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = color
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

/**
 * Botón de interacción simple (sin estado activo/inactivo)
 */
@Composable
fun AnimatedInteractionButton(
    icon: ImageVector,
    count: Int?,
    onClick: () -> Unit,
    label: String
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                isPressed = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .scale(scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        // Solo mostrar contador si existe
        if (count != null && count > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCount(count),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

/**
 * Estadísticas compactas con animación
 */
@Composable
fun AnimatedStatsRow(
    totalReacciones: Int = 0,
    totalComentarios: Int = 0,
    totalVistas: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (totalReacciones > 0) {
            AnimatedStatItem(
                icon = Icons.Filled.Favorite,
                count = totalReacciones,
                label = "Me gusta",
                color = Color(0xFFE91E63)
            )
        }

        if (totalComentarios > 0) {
            AnimatedStatItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                count = totalComentarios,
                label = "Comentarios",
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (totalVistas > 0) {
            AnimatedStatItem(
                icon = Icons.Outlined.Visibility,
                count = totalVistas,
                label = "Vistas",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedStatItem(
    icon: ImageVector,
    count: Int,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                } else {
                    fadeIn() togetherWith fadeOut()
                }
            },
            label = "stat_count"
        ) { animatedCount ->
            Text(
                text = formatCount(animatedCount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${(count / 100) / 10.0}K"
        else -> count.toString()
    }
}
