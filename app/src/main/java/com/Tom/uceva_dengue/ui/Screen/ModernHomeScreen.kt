package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import com.Tom.uceva_dengue.ui.Components.*
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * HomeScreen completamente rediseñado con:
 * - Sistema de filtros moderno con chips
 * - Pull-to-refresh
 * - Animaciones fluidas
 * - Vista lista/grilla
 * - Mejores indicadores visuales
 * - Cards completamente rediseñadas
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ModernHomeScreen(
    viewModel: PublicacionViewModel = viewModel(),
    role: Int,
    userId: Int? = null,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estados
    var selectedFilter by remember { mutableStateOf(FilterType.TODAS) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var searchText by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var publicationToDelete by remember { mutableStateOf<PublicationModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val allPublications by viewModel.publicaciones.collectAsState()

    // Filtrar publicaciones según el filtro seleccionado
    val filteredPublications = remember(allPublications, selectedFilter, searchText) {
        var filtered = allPublications

        // Filtrar por búsqueda
        if (searchText.isNotBlank()) {
            filtered = filtered.filter {
                it.TITULO_PUBLICACION?.contains(searchText, ignoreCase = true) == true ||
                        it.DESCRIPCION_PUBLICACION?.contains(searchText, ignoreCase = true) == true
            }
        }

        // Filtrar por tipo (solo si tienen categoría asignada)
        filtered = when (selectedFilter) {
            FilterType.TODAS -> filtered
            FilterType.ALERTAS -> filtered.filter {
                it.FK_ID_CATEGORIA == PublicationCategory.ALERTA.id
            }
            FilterType.NOTICIAS -> filtered.filter {
                it.FK_ID_CATEGORIA == PublicationCategory.NOTICIA.id
            }
            FilterType.CONSEJOS -> filtered.filter {
                it.FK_ID_CATEGORIA == PublicationCategory.CONSEJO.id
            }
            FilterType.GUARDADAS -> filtered.filter {
                it.USUARIO_HA_GUARDADO == true
            }
        }

        // Ordenar: fijadas primero, luego por fecha (sin depender de prioridad si no existe)
        filtered.sortedWith(
            compareByDescending<PublicationModel> { it.FIJADA }
                .thenByDescending { it.FECHA_PUBLICACION }
        )
    }

    val listState = rememberLazyListState()

    // Scaffold con botón FAB
    Scaffold(
        floatingActionButton = {
            if (role == 2 || role == 3) {
                FloatingActionButton(
                    onClick = { navController.navigate(Rout.CreatePublicationScreen.name) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva publicación"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ===== BARRA SUPERIOR CON TÍTULO Y BÚSQUEDA =====
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Publicaciones",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Mantente informado sobre el dengue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { showSearch = !showSearch }) {
                                    Icon(
                                        imageVector = if (showSearch) Icons.Default.SearchOff else Icons.Default.Search,
                                        contentDescription = "Buscar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                ViewModeSelector(
                                    currentMode = viewMode,
                                    onModeChange = { viewMode = it }
                                )
                            }
                        }

                        // Barra de búsqueda expandible
                        AnimatedVisibility(
                            visible = showSearch,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                placeholder = { Text("Buscar publicaciones...") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    if (searchText.isNotEmpty()) {
                                        IconButton(onClick = { searchText = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Limpiar"
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }

                // ===== BARRA DE FILTROS =====
                ModernFilterBar(
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it }
                )

                // ===== INDICADOR DE RESULTADOS =====
                ResultsIndicator(
                    totalResults = filteredPublications.size,
                    currentFilter = selectedFilter
                )

                // ===== CONTENIDO PRINCIPAL =====
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        filteredPublications.isEmpty() -> {
                            EmptyStateView(
                                currentFilter = selectedFilter,
                                hasSearch = searchText.isNotEmpty()
                            )
                        }

                        viewMode == ViewMode.LIST -> {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(
                                    items = filteredPublications,
                                    key = { it.ID_PUBLICACION }
                                ) { publicacion ->
                                    ModernPublicationCard(
                                        publicacion = publicacion,
                                        currentUserId = userId,
                                        role = role,
                                        onCardClick = { pub ->
                                            navController.navigate("postDetail/${pub.ID_PUBLICACION}")
                                        },
                                        onReactionClick = { pub ->
                                            handleReaction(pub, userId, viewModel, context)
                                        },
                                        onCommentClick = { pub ->
                                            navController.navigate("postDetail/${pub.ID_PUBLICACION}")
                                        },
                                        onSaveClick = { pub ->
                                            handleSave(pub, userId, viewModel, context)
                                        },
                                        onEdit = { pub ->
                                            navController.navigate("editPublication/${pub.ID_PUBLICACION}")
                                        },
                                        onDelete = { pub ->
                                            publicationToDelete = pub
                                            showDeleteDialog = true
                                        },
                                        modifier = Modifier.animateItemPlacement()
                                    )
                                }
                            }
                        }

                        viewMode == ViewMode.GRID -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = 80.dp, start = 8.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = filteredPublications,
                                    key = { it.ID_PUBLICACION }
                                ) { publicacion ->
                                    ModernPublicationCard(
                                        publicacion = publicacion,
                                        currentUserId = userId,
                                        role = role,
                                        onCardClick = { pub ->
                                            navController.navigate("postDetail/${pub.ID_PUBLICACION}")
                                        },
                                        onReactionClick = { pub ->
                                            handleReaction(pub, userId, viewModel, context)
                                        },
                                        onCommentClick = { pub ->
                                            navController.navigate("postDetail/${pub.ID_PUBLICACION}")
                                        },
                                        onSaveClick = { pub ->
                                            handleSave(pub, userId, viewModel, context)
                                        },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && publicationToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta publicación? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        publicationToDelete?.let { pub ->
                            viewModel.deletePublication(
                                id = pub.ID_PUBLICACION,
                                onSuccess = {
                                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                                    showDeleteDialog = false
                                    publicationToDelete = null
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
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

/**
 * Vista de estado vacío según el filtro activo
 */
@Composable
private fun EmptyStateView(
    currentFilter: FilterType,
    hasSearch: Boolean
) {
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
                imageVector = if (hasSearch) Icons.Default.SearchOff else currentFilter.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = when {
                    hasSearch -> "No se encontraron resultados"
                    currentFilter == FilterType.GUARDADAS -> "No tienes publicaciones guardadas"
                    else -> "No hay publicaciones de ${currentFilter.displayName.lowercase()}"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = when {
                    hasSearch -> "Intenta con otros términos de búsqueda"
                    else -> "Selecciona otro filtro para ver más contenido"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Maneja la acción de reacción
 */
private fun handleReaction(
    publication: PublicationModel,
    userId: Int?,
    viewModel: PublicacionViewModel,
    context: android.content.Context
) {
    if (userId != null) {
        viewModel.toggleReaction(
            publicationId = publication.ID_PUBLICACION,
            userId = userId,
            onSuccess = { hasReacted ->
                Toast.makeText(
                    context,
                    if (hasReacted) "¡Me gusta!" else "Reacción removida",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.obtenerPublicaciones()
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Maneja la acción de guardar
 */
private fun handleSave(
    publication: PublicationModel,
    userId: Int?,
    viewModel: PublicacionViewModel,
    context: android.content.Context
) {
    if (userId != null) {
        viewModel.toggleSave(
            publicationId = publication.ID_PUBLICACION,
            userId = userId,
            onSuccess = { isSaved ->
                Toast.makeText(
                    context,
                    if (isSaved) "Guardado" else "Removido de guardados",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.obtenerPublicaciones()
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
