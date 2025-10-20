package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.ui.Components.ModernPublicationCard
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPublicationsScreen(
    navController: NavController,
    viewModel: PublicacionViewModel
) {
    var savedPublications by remember { mutableStateOf<List<PublicationModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authRepository = AuthRepository(context)
    val currentUserId = authRepository.getUser()?.toIntOrNull()
    val userRole = authRepository.getRole()

    // Función para cargar publicaciones guardadas
    fun loadSavedPublications() {
        if (currentUserId != null) {
            viewModel.loadSavedPublications(
                userId = currentUserId,
                onSuccess = { publications ->
                    savedPublications = publications
                    isLoading = false
                    isRefreshing = false
                },
                onError = { error ->
                    Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                    isLoading = false
                    isRefreshing = false
                }
            )
        } else {
            isLoading = false
            isRefreshing = false
        }
    }

    // Cargar publicaciones guardadas al iniciar
    LaunchedEffect(Unit) {
        loadSavedPublications()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            loadSavedPublications()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    // Estado de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Cargando guardados...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                savedPublications.isEmpty() -> {
                    // Estado vacío
                    EmptySavedPublicationsPlaceholder()
                }

                else -> {
                    // Lista de publicaciones guardadas
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Tienes ${savedPublications.size} publicación${if (savedPublications.size > 1) "es" else ""} guardada${if (savedPublications.size > 1) "s" else ""}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Accede rápido a tu contenido favorito",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        items(savedPublications) { publicacion ->
                            ModernPublicationCard(
                                publicacion = publicacion,
                                currentUserId = currentUserId,
                                role = userRole,
                                onCardClick = { pub ->
                                    navController.navigate("postDetail/${pub.ID_PUBLICACION}")
                                },
                                onReactionClick = { pub ->
                                    if (currentUserId != null) {
                                        viewModel.toggleReaction(
                                            publicationId = pub.ID_PUBLICACION,
                                            userId = currentUserId,
                                            onSuccess = { hasReacted ->
                                                Toast.makeText(
                                                    context,
                                                    if (hasReacted) "¡Me gusta!" else "Reacción removida",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onError = { error ->
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                },
                                onCommentClick = { pub ->
                                    navController.navigate("postDetail/${pub.ID_PUBLICACION}")
                                },
                                onSaveClick = { pub ->
                                    if (currentUserId != null) {
                                        viewModel.toggleSave(
                                            publicationId = pub.ID_PUBLICACION,
                                            userId = currentUserId,
                                            onSuccess = { isSaved ->
                                                // Actualizar lista local
                                                if (!isSaved) {
                                                    savedPublications = savedPublications.filter {
                                                        it.ID_PUBLICACION != pub.ID_PUBLICACION
                                                    }
                                                }
                                                Toast.makeText(
                                                    context,
                                                    if (isSaved) "Guardado" else "Removido de guardados",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onError = { error ->
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
    }

@Composable
fun EmptySavedPublicationsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "No tienes publicaciones guardadas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Guarda publicaciones que te interesen para acceder a ellas rápidamente",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
