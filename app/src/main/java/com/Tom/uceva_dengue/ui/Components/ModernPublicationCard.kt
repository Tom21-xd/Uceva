package com.Tom.uceva_dengue.ui.Components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card de publicación completamente rediseñada con:
 * - Gradientes según prioridad
 * - Categorías visuales con iconos
 * - Animaciones de pulsación para urgentes
 * - Diseño más moderno y atractivo
 * - Mejores indicadores visuales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPublicationCard(
    publicacion: PublicationModel,
    currentUserId: Int? = null,
    role: Int = 0,
    onCardClick: ((PublicationModel) -> Unit)? = null,
    onReactionClick: ((PublicationModel) -> Unit)? = null,
    onCommentClick: ((PublicationModel) -> Unit)? = null,
    onSaveClick: ((PublicationModel) -> Unit)? = null,
    onEdit: ((PublicationModel) -> Unit)? = null,
    onDelete: ((PublicationModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val category = PublicationCategory.fromId(publicacion.FK_ID_CATEGORIA)
    val priority = PriorityLevel.fromString(publicacion.NIVEL_PRIORIDAD)
    var showMenu by remember { mutableStateOf(false) }
    val canEdit = (role == 2 || role == 3) || (currentUserId == publicacion.FK_ID_USUARIO)

    // Estados para las animaciones
    var showHeartAnimation by remember { mutableStateOf(false) }
    var showBookmarkAnimation by remember { mutableStateOf(false) }

    // Animación de pulsación para publicaciones urgentes
    val infiniteTransition = rememberInfiniteTransition(label = "urgent")
    val urgentScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (priority == PriorityLevel.URGENTE) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "urgentScale"
    )

    val imageUrl = if (!publicacion.IMAGEN_PUBLICACION.isNullOrBlank()) {
        "https://api.prometeondev.com/Image/getImage/${publicacion.IMAGEN_PUBLICACION}"
    } else null

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scale(urgentScale)
                .then(
                    if (onCardClick != null) {
                        Modifier.clickable { onCardClick(publicacion) }
                    } else Modifier
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (priority == PriorityLevel.URGENTE) 8.dp else 4.dp
            )
        ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ===== ENCABEZADO CON GRADIENTE =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                priority.gradientStart.copy(alpha = 0.15f),
                                priority.gradientEnd.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Categoría con ícono
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = category.lightColor,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = category.color,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = category.color
                        )
                    }

                    // Badge de prioridad
                    if (priority != PriorityLevel.NORMAL) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = priority.color.copy(alpha = 0.15f),
                            modifier = Modifier
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (priority == PriorityLevel.URGENTE) {
                                    Icon(
                                        imageVector = Icons.Default.PriorityHigh,
                                        contentDescription = null,
                                        tint = priority.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = priority.displayName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = priority.color,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Menú de opciones
                    if (canEdit) {
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Opciones",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    onClick = {
                                        showMenu = false
                                        onEdit?.invoke(publicacion)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {
                                        showMenu = false
                                        onDelete?.invoke(publicacion)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ===== IMAGEN (SI EXISTE) =====
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen de publicación",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = category.color
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(category.lightColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = null,
                                tint = category.color,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                )
            }

            // ===== CONTENIDO =====
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título
                Text(
                    text = publicacion.TITULO_PUBLICACION ?: "Sin título",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Descripción con preview inteligente y enlaces clicables
                if (!publicacion.DESCRIPCION_PUBLICACION.isNullOrBlank()) {
                    ClickableTextWithLinks(
                        text = publicacion.DESCRIPCION_PUBLICACION,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        linkColor = MaterialTheme.colorScheme.primary
                    )
                }

                // Información del autor y fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = category.color.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = category.color,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Column {
                            Text(
                                text = publicacion.USUARIO?.NOMBRE_USUARIO ?: "Anónimo",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = formatDate(publicacion.FECHA_PUBLICACION),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Indicador de publicación fijada
                    if (publicacion.FIJADA == true) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFB300).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Fijada",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFFB300),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp
                )

                // ===== BARRA DE INTERACCIONES =====
                EnhancedReactionBar(
                    totalReacciones = publicacion.TOTAL_REACCIONES ?: 0,
                    totalComentarios = publicacion.TOTAL_COMENTARIOS ?: 0,
                    totalVistas = publicacion.TOTAL_VISTAS ?: 0,
                    totalGuardados = publicacion.TOTAL_GUARDADOS ?: 0,
                    usuarioHaReaccionado = publicacion.USUARIO_HA_REACCIONADO ?: false,
                    usuarioHaGuardado = publicacion.USUARIO_HA_GUARDADO ?: false,
                    onReactionClick = {
                        // Solo mostrar animación si NO había reaccionado (está dando like)
                        if (publicacion.USUARIO_HA_REACCIONADO != true) {
                            showHeartAnimation = true
                        }
                        onReactionClick?.invoke(publicacion)
                    },
                    onCommentClick = { onCommentClick?.invoke(publicacion) },
                    onSaveClick = {
                        // Solo mostrar animación si NO había guardado (está guardando)
                        if (publicacion.USUARIO_HA_GUARDADO != true) {
                            showBookmarkAnimation = true
                        }
                        onSaveClick?.invoke(publicacion)
                    }
                )
            }
        }
        }

        // Animaciones flotantes (versión compacta para tarjetas)
        CompactHeartAnimation(
            show = showHeartAnimation,
            onComplete = { showHeartAnimation = false }
        )

        CompactBookmarkAnimation(
            show = showBookmarkAnimation,
            onComplete = { showBookmarkAnimation = false }
        )
    }
}

/**
 * Formatea la fecha de manera legible
 */
private fun formatDate(dateString: String?): String {
    if (dateString == null) return "Fecha desconocida"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        // Intentar otros formatos
        try {
            val altFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = altFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e2: Exception) {
            dateString
        }
    }
}
