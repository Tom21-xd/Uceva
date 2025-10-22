package com.Tom.uceva_dengue.ui.Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import com.Tom.uceva_dengue.ui.Components.EnhancedPostCard
import com.Tom.uceva_dengue.ui.Components.ModernPublicationCard
import com.Tom.uceva_dengue.ui.Components.PublicationFilter
import com.Tom.uceva_dengue.ui.Components.PublicationFiltersRow
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PublicacionViewModel = viewModel(),
    role: Int,
    userId: Int? = null,
    navController: NavController
) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()

    var searchText by remember { mutableStateOf("") }
    val publicaciones by viewModel.publicaciones.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var publicationToDelete by remember { mutableStateOf<PublicationModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(PublicationFilter.ALL) }
    var filteredPublications by remember { mutableStateOf<List<PublicationModel>>(emptyList()) }
    var isLoadingFilter by remember { mutableStateOf(false) }
    var categories by remember { mutableStateOf<List<com.Tom.uceva_dengue.Data.Model.PublicationCategoryModel>>(emptyList()) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    // Load categories
    LaunchedEffect(Unit) {
        try {
            val response = com.Tom.uceva_dengue.Data.Api.RetrofitClient.publicationCategoryService.getAllCategories()
            if (response.isSuccessful && response.body() != null) {
                categories = response.body()!!.filter { it.ESTADO_CATEGORIA }
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error loading categories", e)
        }
    }

    // Load publications with userId using intelligent feed (ordered by priority)
    LaunchedEffect(userId, selectedCategoryId) {
        // Use intelligent feed: Pinned > Priority (Urgent > High > Normal > Low) > Date
        viewModel.obtenerFeedInteligente(
            userId = userId,
            ciudadId = null,
            categoriaId = selectedCategoryId,
            limit = 50
        )
    }

    // Load publications based on selected filter
    fun loadFilteredPublications(filter: PublicationFilter) {
        isLoadingFilter = true
        when (filter) {
            PublicationFilter.ALL -> {
                viewModel.obtenerFeedInteligente(userId = userId, limit = 50)
                isLoadingFilter = false
            }
            PublicationFilter.URGENT -> {
                viewModel.getUrgentPublications(
                    userId = userId,
                    onSuccess = { pubs ->
                        filteredPublications = pubs
                        isLoadingFilter = false
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        isLoadingFilter = false
                    }
                )
            }
            PublicationFilter.PINNED -> {
                viewModel.getPinnedPublications(
                    userId = userId,
                    onSuccess = { pubs ->
                        filteredPublications = pubs
                        isLoadingFilter = false
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        isLoadingFilter = false
                    }
                )
            }
            PublicationFilter.TRENDING -> {
                viewModel.getTrendingPublications(
                    limit = 20,
                    days = 7,
                    userId = userId,
                    onSuccess = { pubs ->
                        filteredPublications = pubs
                        isLoadingFilter = false
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        isLoadingFilter = false
                    }
                )
            }
            PublicationFilter.CATEGORY -> {
                // Category filtering is handled separately via selectedCategoryId in LaunchedEffect
                viewModel.obtenerFeedInteligente(
                    userId = userId,
                    ciudadId = null,
                    categoriaId = selectedCategoryId,
                    limit = 50
                )
                isLoadingFilter = false
            }
        }
    }

    // Determine which publications to show
    val displayPublications = if (selectedFilter == PublicationFilter.ALL) {
        publicaciones
    } else {
        filteredPublications
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshData(userId) },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(dimensions.paddingMedium)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // → Buscador idéntico al de HospitalScreen
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { query ->
                        searchText = query
                        if (query.isNotBlank()) viewModel.buscarPublicacion(query)
                        else viewModel.obtenerPublicaciones()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensions.spacingMedium),
                    placeholder = { Text("Buscar título...", fontSize = dimensions.textSizeMedium) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    },
                    shape = RoundedCornerShape(dimensions.cardCornerRadius * 2)
                )

                // Filtros de publicaciones
                PublicationFiltersRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        searchText = "" // Clear search when changing filter
                        loadFilteredPublications(filter)
                    },
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = { categoryId ->
                        selectedCategoryId = categoryId
                        searchText = ""
                        // Reload with category filter
                        if (selectedFilter == PublicationFilter.ALL) {
                            viewModel.obtenerFeedInteligente(
                                userId = userId,
                                categoriaId = categoryId,
                                limit = 50
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                // Estado de carga de filtros
                if (isLoadingFilter) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensions.paddingExtraLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // Estado de lista vacía
                else if (displayPublications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensions.paddingExtraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Sin resultados",
                            modifier = Modifier.size(dimensions.iconExtraLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                        Text(
                            text = if (searchText.isNotBlank()) "No se encontraron publicaciones" else "No hay publicaciones disponibles",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = dimensions.textSizeLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                        Text(
                            text = if (searchText.isNotBlank()) "Intenta con otro término de búsqueda" else "Sé el primero en publicar",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = dimensions.textSizeSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                    contentPadding = PaddingValues(bottom = dimensions.paddingExtraLarge * 2), // Espacio para el FAB
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayPublications) { publicacion ->
                        ModernPublicationCard(
                            publicacion = publicacion,
                            currentUserId = userId,
                            role = role,
                            onCardClick = {
                                // Navegar a detalle de publicación
                                navController.navigate("${Rout.PostDetailScreen.name}/${it.ID_PUBLICACION}")
                            },
                            onReactionClick = { pub ->
                                if (userId != null) {
                                    viewModel.toggleReaction(
                                        publicationId = pub.ID_PUBLICACION,
                                        userId = userId,
                                        onSuccess = { hasReacted ->
                                            // Actualizar estado local en lugar de recargar
                                            viewModel.updatePublicationState(
                                                publicationId = pub.ID_PUBLICACION,
                                                updateReaction = true,
                                                hasReacted = hasReacted,
                                                userId = userId
                                            )
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            },
                            onCommentClick = { pub ->
                                // Navegar al detalle donde están los comentarios
                                navController.navigate("${Rout.PostDetailScreen.name}/${pub.ID_PUBLICACION}")
                            },
                            onSaveClick = { pub ->
                                if (userId != null) {
                                    viewModel.toggleSave(
                                        publicationId = pub.ID_PUBLICACION,
                                        userId = userId,
                                        onSuccess = { isSaved ->
                                            // Actualizar estado local en lugar de recargar
                                            viewModel.updatePublicationState(
                                                publicationId = pub.ID_PUBLICACION,
                                                updateSave = true,
                                                hasSaved = isSaved,
                                                userId = userId
                                            )
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            },
                            onEdit = {
                                navController.navigate("${Rout.UpdatePublicationScreen.name}/${it.ID_PUBLICACION}")
                            },
                            onDelete = {
                                publicationToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Administrador (2) y Personal Médico (3) pueden crear publicaciones
        if (role == 2 || role == 3) {
            FloatingActionButton(
                onClick = { navController.navigate(Rout.CreatePublicationScreen.name) },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear publicación",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && publicationToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar la publicación '${publicationToDelete?.TITULO_PUBLICACION}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        publicationToDelete?.let { pub ->
                            viewModel.deletePublication(
                                id = pub.ID_PUBLICACION,
                                onSuccess = {
                                    Toast.makeText(context, "Publicación eliminada con éxito", Toast.LENGTH_SHORT).show()
                                    showDeleteDialog = false
                                    publicationToDelete = null
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    showDeleteDialog = false
                                }
                            )
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
