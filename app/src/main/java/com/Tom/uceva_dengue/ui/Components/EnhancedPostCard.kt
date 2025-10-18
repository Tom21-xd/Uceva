package com.Tom.uceva_dengue.ui.Components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.Tom.uceva_dengue.Data.Model.PublicationModel

/**
 * Tarjeta de publicación mejorada con todas las nuevas funcionalidades:
 * - Badge de prioridad
 * - Indicador de publicación fijada
 * - Etiquetas/Tags
 * - Barra de interacciones (reacciones, comentarios, guardados)
 * - Categoría
 * - Soporte completo de tema claro/oscuro
 */
@Composable
fun EnhancedPostCard(
    publicacion: PublicationModel,
    currentUserId: Int? = null,
    role: Int = 0,
    onCardClick: ((PublicationModel) -> Unit)? = null,
    onReactionClick: ((PublicationModel) -> Unit)? = null,
    onCommentClick: ((PublicationModel) -> Unit)? = null,
    onSaveClick: ((PublicationModel) -> Unit)? = null,
    onTagClick: ((String) -> Unit)? = null,
    onEdit: ((PublicationModel) -> Unit)? = null,
    onDelete: ((PublicationModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val imageUrl = if (!publicacion.IMAGEN_PUBLICACION.isNullOrBlank()) {
        "https://api.prometeondev.com/Image/getImage/${publicacion.IMAGEN_PUBLICACION}"
    } else {
        null
    }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Mostrar opciones si es admin/personal médico O si es el autor de la publicación
    val canEdit = (role == 2 || role == 3) || (currentUserId == publicacion.FK_ID_USUARIO)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .then(
                if (onCardClick != null) {
                    Modifier.clickable { onCardClick(publicacion) }
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ===== IMAGEN CON BADGES SUPERPUESTOS =====
            Box(modifier = Modifier.fillMaxWidth()) {
                if (imageUrl != null) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .listener(
                                onError = { _, result ->
                                    Log.e("EnhancedPostCard", "Error loading image: ${result.throwable.message}")
                                },
                                onSuccess = { _, _ ->
                                    Log.d("EnhancedPostCard", "Image loaded successfully: $imageUrl")
                                }
                            )
                            .build(),
                        contentDescription = "Imagen de la publicación",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Error al cargar imagen",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Error al cargar",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                } else {
                    // Placeholder cuando no hay imagen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Sin imagen",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sin imagen",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Badges superiores (Prioridad y Fijada)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (publicacion.FIJADA) {
                        PinnedIndicator()
                    }
                    if (publicacion.NIVEL_PRIORIDAD != null && publicacion.NIVEL_PRIORIDAD != "Normal") {
                        PriorityBadge(priority = publicacion.NIVEL_PRIORIDAD)
                    }
                }

                // Menú de opciones (esquina superior derecha)
                if (canEdit) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    RoundedCornerShape(50)
                                )
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Editar")
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit?.invoke(publicacion)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete?.invoke(publicacion)
                                }
                            )
                        }
                    }
                }

                // Categoría (esquina inferior izquierda de la imagen)
                if (publicacion.CATEGORIA != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = publicacion.CATEGORIA.NOMBRE_CATEGORIA,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // ===== CONTENIDO DE LA TARJETA =====
            Column(modifier = Modifier.padding(16.dp)) {
                // Título
                Text(
                    text = publicacion.TITULO_PUBLICACION,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Descripción
                Text(
                    text = publicacion.DESCRIPCION_PUBLICACION,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )

                // Etiquetas/Tags
                if (!publicacion.ETIQUETAS.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    PublicationTagsRow(
                        tags = publicacion.ETIQUETAS,
                        onTagClick = if (onTagClick != null) {
                            { tag -> onTagClick(tag.NOMBRE_ETIQUETA) }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                // Barra de interacciones
                PublicationInteractionBar(
                    totalReacciones = publicacion.TOTAL_REACCIONES ?: 0,
                    totalComentarios = publicacion.TOTAL_COMENTARIOS ?: 0,
                    totalVistas = publicacion.TOTAL_VISTAS ?: 0,
                    totalGuardados = publicacion.TOTAL_GUARDADOS ?: 0,
                    usuarioHaReaccionado = publicacion.USUARIO_HA_REACCIONADO ?: false,
                    usuarioHaGuardado = publicacion.USUARIO_HA_GUARDADO ?: false,
                    onReactionClick = { onReactionClick?.invoke(publicacion) },
                    onCommentClick = { onCommentClick?.invoke(publicacion) },
                    onSaveClick = { onSaveClick?.invoke(publicacion) }
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Footer: Autor y Fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Autor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = publicacion.USUARIO?.NOMBRE_USUARIO
                                ?: publicacion.NOMBRE_USUARIO
                                ?: "Usuario desconocido",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = publicacion.FECHA_PUBLICACION,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
