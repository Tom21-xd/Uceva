package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.Tom.uceva_dengue.Data.Model.PublicationCategoryModel

enum class PublicationFilter {
    ALL, URGENT, PINNED, TRENDING, CATEGORY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationFiltersRow(
    selectedFilter: PublicationFilter,
    onFilterSelected: (PublicationFilter) -> Unit,
    categories: List<PublicationCategoryModel> = emptyList(),
    selectedCategoryId: Int? = null,
    onCategorySelected: (Int?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Todo en una sola fila horizontal con scroll
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Filtros principales
        FilterChip(
            filter = PublicationFilter.ALL,
            label = "Todas",
            icon = Icons.Default.List,
            selected = selectedFilter == PublicationFilter.ALL,
            onClick = { onFilterSelected(PublicationFilter.ALL) }
        )

        FilterChip(
            filter = PublicationFilter.URGENT,
            label = "Urgentes",
            icon = Icons.Default.PriorityHigh,
            selected = selectedFilter == PublicationFilter.URGENT,
            onClick = { onFilterSelected(PublicationFilter.URGENT) }
        )

        FilterChip(
            filter = PublicationFilter.PINNED,
            label = "Fijadas",
            icon = Icons.Default.PushPin,
            selected = selectedFilter == PublicationFilter.PINNED,
            onClick = { onFilterSelected(PublicationFilter.PINNED) }
        )

        FilterChip(
            filter = PublicationFilter.TRENDING,
            label = "Populares",
            icon = Icons.Default.Whatshot,
            selected = selectedFilter == PublicationFilter.TRENDING,
            onClick = { onFilterSelected(PublicationFilter.TRENDING) }
        )

        // Filtro por categoría en la misma fila
        if (categories.isNotEmpty()) {
            Box {
                FilterChip(
                    selected = selectedCategoryId != null,
                    onClick = { expanded = true },
                    label = {
                        Text(
                            selectedCategoryId?.let { id ->
                                categories.find { it.ID_CATEGORIA_PUBLICACION == id }?.NOMBRE_CATEGORIA
                            } ?: "Categorías"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Categoría",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Opción "Todas"
                    DropdownMenuItem(
                        text = { Text("Todas las categorías") },
                        onClick = {
                            onCategorySelected(null)
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ClearAll, contentDescription = null)
                        }
                    )

                    HorizontalDivider()

                    // Categorías disponibles
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.NOMBRE_CATEGORIA) },
                            onClick = {
                                onCategorySelected(category.ID_CATEGORIA_PUBLICACION)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIcon(category.ICONO),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper para obtener el icono según el nombre
fun getCategoryIcon(iconName: String?): ImageVector {
    return when (iconName?.lowercase()) {
        "alert" -> Icons.Default.Warning
        "shield" -> Icons.Default.Shield
        "book" -> Icons.Default.Book
        "news" -> Icons.Outlined.Newspaper
        "event" -> Icons.Default.Event
        "science" -> Icons.Default.Science
        else -> Icons.Default.Category
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChip(
    filter: PublicationFilter,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
