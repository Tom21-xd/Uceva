package com.Tom.uceva_dengue.ui.Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    var showFiltersPanel by remember { mutableStateOf(false) }
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
                // → Buscador compacto
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

        // Botón flotante de filtros
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
        ) {
            FloatingActionButton(
                onClick = { showFiltersPanel = !showFiltersPanel },
                containerColor = if (showFiltersPanel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (showFiltersPanel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtros"
                )
            }
        }

        // Administrador (2) y Personal Médico (3) pueden crear publicaciones
        if (role == 2 || role == 3) {
            FloatingActionButton(
                onClick = { navController.navigate(Rout.CreatePublicationScreen.name) },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensions.paddingMedium)
                    .size(56.dp)
                    .shadow(8.dp, CircleShape),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear publicación"
                )
            }
        }

        // Panel de filtros desplegable
        AnimatedVisibility(
            visible = showFiltersPanel,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.paddingMedium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium)
                ) {
                    // Header del panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filtros de Publicaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { showFiltersPanel = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                    // Filtros principales
                    Text(
                        "Tipo de Publicación",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedFilter == PublicationFilter.ALL,
                                onClick = {
                                    selectedFilter = PublicationFilter.ALL
                                    searchText = ""
                                    loadFilteredPublications(PublicationFilter.ALL)
                                },
                                label = { Text("Todas") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        items(listOf(
                            PublicationFilter.URGENT to "Urgentes",
                            PublicationFilter.PINNED to "Fijadas",
                            PublicationFilter.TRENDING to "Populares"
                        )) { (filter, label) ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = {
                                    selectedFilter = filter
                                    searchText = ""
                                    loadFilteredPublications(filter)
                                },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // Filtro por categorías
                    if (categories.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                        Text(
                            "Categoría",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedCategoryId == null,
                                    onClick = {
                                        selectedCategoryId = null
                                        searchText = ""
                                        viewModel.obtenerFeedInteligente(
                                            userId = userId,
                                            categoriaId = null,
                                            limit = 50
                                        )
                                    },
                                    label = { Text("Todas") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }

                            items(categories) { category ->
                                FilterChip(
                                    selected = selectedCategoryId == category.ID_CATEGORIA_PUBLICACION,
                                    onClick = {
                                        selectedCategoryId = category.ID_CATEGORIA_PUBLICACION
                                        searchText = ""
                                        viewModel.obtenerFeedInteligente(
                                            userId = userId,
                                            categoriaId = category.ID_CATEGORIA_PUBLICACION,
                                            limit = 50
                                        )
                                    },
                                    label = { Text(category.NOMBRE_CATEGORIA) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                }
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
