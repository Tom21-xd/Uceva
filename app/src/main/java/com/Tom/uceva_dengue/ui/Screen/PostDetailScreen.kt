package com.Tom.uceva_dengue.ui.Screen


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import com.Tom.uceva_dengue.Data.Model.PublicationCommentModel
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.ui.Components.CommentSection
import com.Tom.uceva_dengue.ui.Components.EnhancedReactionBar
import com.Tom.uceva_dengue.ui.Components.FloatingHeartAnimation
import com.Tom.uceva_dengue.ui.Components.BookmarkSaveAnimation
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel

@Composable
fun PostDetailScreen(
    publicationId: Int,
    viewModel: PublicacionViewModel,
    navController: NavController
) {
    var publicacion by remember { mutableStateOf<PublicationModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var comments by remember { mutableStateOf<List<PublicationCommentModel>>(emptyList()) }
    var isLoadingComments by remember { mutableStateOf(false) }
    var isSendingComment by remember { mutableStateOf(false) }
    var showHeartAnimation by remember { mutableStateOf(false) }
    var showBookmarkAnimation by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val authRepository = AuthRepository(context)
    val currentUserId = authRepository.getUser()?.toIntOrNull()
    val currentUserName = authRepository.getUserDisplayName()

    // Función para cargar comentarios
    fun loadComments() {
        if (currentUserId != null) {
            viewModel.loadComments(
                publicationId = publicationId,
                onSuccess = { loadedComments ->
                    comments = loadedComments
                    isLoadingComments = false
                },
                onError = { error ->
                    Toast.makeText(context, "Error al cargar comentarios: $error", Toast.LENGTH_SHORT).show()
                    isLoadingComments = false
                }
            )
        }
    }

    // Función para cargar interacciones del usuario
    fun loadUserInteractions() {
        if (currentUserId != null) {
            viewModel.loadUserInteractions(
                publicationId = publicationId,
                userId = currentUserId,
                onSuccess = { hasReacted, hasSaved ->
                    publicacion = publicacion?.copy(
                        USUARIO_HA_REACCIONADO = hasReacted,
                        USUARIO_HA_GUARDADO = hasSaved
                    )
                },
                onError = { }
            )
        }
    }

    LaunchedEffect(publicationId) {
        viewModel.getPublicationById(
            id = publicationId,
            onSuccess = { publication ->
                publicacion = publication
                isLoading = false
                // Cargar comentarios y interacciones
                loadComments()
                loadUserInteractions()
            },
            onError = { error ->
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                isLoading = false
                navController.popBackStack()
            }
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (publicacion == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No se encontró la publicación")
        }
        return
    }

    val imageUrl = if (!publicacion!!.IMAGEN_PUBLICACION.isNullOrBlank()) {
        "https://api.prometeondev.com/Image/getImage/${publicacion!!.IMAGEN_PUBLICACION}"
    } else {
        null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen destacada con overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                if (imageUrl != null) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen de la publicación",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Error al cargar imagen",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No se pudo cargar la imagen", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                                }
                            }
                        }
                    )
                } else {
                    // Placeholder cuando no hay imagen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Sin imagen",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Sin imagen disponible", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                        }
                    }
                }

                // Overlay gradiente en la parte inferior de la imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Card del título y descripción
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = publicacion!!.TITULO_PUBLICACION,
                            fontSize = 24.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = publicacion!!.DESCRIPCION_PUBLICACION,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card de información del autor
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = publicacion!!.USUARIO?.NOMBRE_USUARIO ?: publicacion!!.NOMBRE_USUARIO ?: "Usuario desconocido",
                                    fontSize = 16.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = publicacion!!.FECHA_PUBLICACION,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de reacciones mejorada
                EnhancedReactionBar(
                    totalReacciones = publicacion!!.TOTAL_REACCIONES ?: 0,
                    totalComentarios = publicacion!!.TOTAL_COMENTARIOS ?: 0,
                    totalVistas = publicacion!!.TOTAL_VISTAS ?: 0,
                    totalGuardados = publicacion!!.TOTAL_GUARDADOS ?: 0,
                    usuarioHaReaccionado = publicacion!!.USUARIO_HA_REACCIONADO ?: false,
                    usuarioHaGuardado = publicacion!!.USUARIO_HA_GUARDADO ?: false,
                    onReactionClick = {
                        if (currentUserId != null) {
                            // Solo mostrar animación si NO había reaccionado (está dando like)
                            val shouldAnimate = publicacion?.USUARIO_HA_REACCIONADO != true

                            viewModel.toggleReaction(
                                publicationId = publicationId,
                                userId = currentUserId,
                                onSuccess = { hasReacted ->
                                    publicacion = publicacion?.copy(
                                        USUARIO_HA_REACCIONADO = hasReacted,
                                        TOTAL_REACCIONES = (publicacion?.TOTAL_REACCIONES ?: 0) + if (hasReacted) 1 else -1
                                    )
                                    if (hasReacted && shouldAnimate) {
                                        showHeartAnimation = true
                                    }
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    onCommentClick = {
                        Toast.makeText(context, "Ver comentarios abajo", Toast.LENGTH_SHORT).show()
                    },
                    onSaveClick = {
                        if (currentUserId != null) {
                            // Solo mostrar animación si NO había guardado (está guardando)
                            val shouldAnimate = publicacion?.USUARIO_HA_GUARDADO != true

                            viewModel.toggleSave(
                                publicationId = publicationId,
                                userId = currentUserId,
                                onSuccess = { isSaved ->
                                    publicacion = publicacion?.copy(
                                        USUARIO_HA_GUARDADO = isSaved,
                                        TOTAL_GUARDADOS = (publicacion?.TOTAL_GUARDADOS ?: 0) + if (isSaved) 1 else -1
                                    )
                                    if (isSaved && shouldAnimate) {
                                        showBookmarkAnimation = true
                                    }
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    onShareClick = {
                        Toast.makeText(context, "Compartir próximamente", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de comentarios
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(8.dp))

                CommentSection(
                    comments = comments,
                    isLoading = isSendingComment,
                    onSendComment = { commentText ->
                        if (currentUserId != null) {
                            isSendingComment = true
                            viewModel.createComment(
                                publicationId = publicationId,
                                userId = currentUserId,
                                content = commentText,
                                parentCommentId = null,
                                userName = currentUserName,
                                onSuccess = { newComment ->
                                    comments = comments + newComment
                                    publicacion = publicacion?.copy(
                                        TOTAL_COMENTARIOS = (publicacion?.TOTAL_COMENTARIOS ?: 0) + 1
                                    )
                                    Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show()
                                    isSendingComment = false
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                    isSendingComment = false
                                }
                            )
                        }
                    },
                    onReplyToComment = { commentId, replyText ->
                        if (currentUserId != null) {
                            viewModel.createComment(
                                publicationId = publicationId,
                                userId = currentUserId,
                                content = replyText,
                                parentCommentId = commentId,
                                userName = currentUserName,
                                onSuccess = { newComment ->
                                    loadComments() // Recargar comentarios para mostrar la respuesta
                                    Toast.makeText(context, "Respuesta agregada", Toast.LENGTH_SHORT).show()
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    onDeleteComment = { commentId ->
                        viewModel.deleteComment(
                            commentId = commentId,
                            onSuccess = {
                                comments = comments.filter { it.ID_COMENTARIO != commentId }
                                publicacion = publicacion?.copy(
                                    TOTAL_COMENTARIOS = (publicacion?.TOTAL_COMENTARIOS ?: 1) - 1
                                )
                                Toast.makeText(context, "Comentario eliminado", Toast.LENGTH_SHORT).show()
                            },
                            onError = { error ->
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    currentUserId = currentUserId
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Animaciones flotantes
        FloatingHeartAnimation(
            show = showHeartAnimation,
            onComplete = { showHeartAnimation = false }
        )

        BookmarkSaveAnimation(
            show = showBookmarkAnimation,
            onComplete = { showBookmarkAnimation = false }
        )
    }
}
