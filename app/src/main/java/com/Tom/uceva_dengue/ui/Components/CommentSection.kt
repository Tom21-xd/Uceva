package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tom.uceva_dengue.Data.Model.PublicationCommentModel

/**
 * Sección completa de comentarios con input y lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSection(
    comments: List<PublicationCommentModel>,
    isLoading: Boolean = false,
    onSendComment: (String) -> Unit,
    onReplyToComment: ((Int, String) -> Unit)? = null,
    onDeleteComment: ((Int) -> Unit)? = null,
    currentUserId: Int? = null,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<PublicationCommentModel?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comentarios (${comments.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input de comentario
        CommentInputField(
            value = commentText,
            onValueChange = { commentText = it },
            onSend = {
                if (commentText.isNotBlank()) {
                    if (replyingTo != null && onReplyToComment != null) {
                        onReplyToComment(replyingTo!!.ID_COMENTARIO, commentText)
                    } else {
                        onSendComment(commentText)
                    }
                    commentText = ""
                    replyingTo = null
                }
            },
            replyingTo = replyingTo,
            onCancelReply = { replyingTo = null },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de comentarios
        if (comments.isEmpty() && !isLoading) {
            EmptyCommentsPlaceholder()
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(comments.filter { it.FK_ID_COMENTARIO_PADRE == null }) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUserId,
                        onReply = { replyingTo = comment },
                        onDelete = onDeleteComment
                    )
                }
            }
        }
    }
}

/**
 * Campo de input para escribir comentarios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    replyingTo: PublicationCommentModel? = null,
    onCancelReply: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Indicador de respuesta
        AnimatedVisibility(
            visible = replyingTo != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Respondiendo a ${replyingTo?.USUARIO?.NOMBRE_USUARIO ?: "Usuario"}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onCancelReply,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancelar respuesta",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Campo de texto
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = if (replyingTo != null) {
                RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            } else {
                RoundedCornerShape(12.dp)
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = if (replyingTo != null) "Escribe tu respuesta..." else "Escribe un comentario...",
                            fontSize = 14.sp
                        )
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Botón enviar con animación
                AnimatedSendButton(
                    enabled = value.isNotBlank() && !isLoading,
                    isLoading = isLoading,
                    onClick = onSend
                )
            }
        }
    }
}

/**
 * Botón de envío animado
 */
@Composable
fun AnimatedSendButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        containerColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (enabled) 4.dp else 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar comentario",
                tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Item individual de comentario
 */
@Composable
fun CommentItem(
    comment: PublicationCommentModel,
    currentUserId: Int?,
    onReply: () -> Unit,
    onDelete: ((Int) -> Unit)? = null,
    isReply: Boolean = false
) {
    var showReplies by remember { mutableStateOf(false) }
    val canDelete = currentUserId == comment.FK_ID_USUARIO

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isReply) Modifier.padding(start = 32.dp) else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isReply)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header del comentario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = comment.USUARIO?.NOMBRE_USUARIO ?: "Usuario #${comment.FK_ID_USUARIO}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (comment.FECHA_COMENTARIO.isNotBlank()) {
                            Text(
                                text = formatCommentDate(comment.FECHA_COMENTARIO),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (canDelete && onDelete != null) {
                    IconButton(
                        onClick = { onDelete(comment.ID_COMENTARIO) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido del comentario
            Text(
                text = comment.CONTENIDO_COMENTARIO,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // Botón de responder
            if (!isReply) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = onReply,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Responder", fontSize = 12.sp)
                    }

                    // Mostrar respuestas si las hay
                    if (!comment.RESPUESTAS.isNullOrEmpty()) {
                        TextButton(
                            onClick = { showReplies = !showReplies },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (showReplies) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${comment.RESPUESTAS.size} respuesta${if (comment.RESPUESTAS.size > 1) "s" else ""}",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Respuestas anidadas
            AnimatedVisibility(
                visible = showReplies && !comment.RESPUESTAS.isNullOrEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    comment.RESPUESTAS?.forEach { reply ->
                        CommentItem(
                            comment = reply,
                            currentUserId = currentUserId,
                            onReply = {},
                            onDelete = onDelete,
                            isReply = true
                        )
                    }
                }
            }
        }
    }
}

/**
 * Placeholder cuando no hay comentarios
 */
@Composable
fun EmptyCommentsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay comentarios aún",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Sé el primero en comentar",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

/**
 * Formatea la fecha del comentario de manera legible
 */
private fun formatCommentDate(dateString: String): String {
    if (dateString.isBlank()) return ""
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val now = java.util.Date()
        val commentDate = inputFormat.parse(dateString)

        if (commentDate != null) {
            val diff = now.time - commentDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                seconds < 60 -> "Ahora"
                minutes < 60 -> "${minutes}m"
                hours < 24 -> "${hours}h"
                days < 7 -> "${days}d"
                else -> {
                    val outputFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale("es", "ES"))
                    outputFormat.format(commentDate)
                }
            }
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}
