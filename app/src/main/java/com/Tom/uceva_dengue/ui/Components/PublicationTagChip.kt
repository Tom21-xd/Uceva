package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tom.uceva_dengue.Data.Model.PublicationTagModel

/**
 * Chip para mostrar una etiqueta/tag de publicación
 * Soporta tema claro/oscuro automáticamente
 */
@Composable
fun PublicationTagChip(
    tag: PublicationTagModel,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isClickable = onClick != null
    val hasRemove = onRemove != null

    if (isClickable) {
        FilterChip(
            selected = false,
            onClick = { onClick?.invoke() },
            label = {
                Text(
                    text = tag.NOMBRE_ETIQUETA,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            trailingIcon = if (hasRemove) {
                {
                    IconButton(
                        onClick = { onRemove?.invoke() },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quitar etiqueta",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false,
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                borderWidth = 1.dp
            ),
            modifier = modifier
        )
    } else {
        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = tag.NOMBRE_ETIQUETA,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            trailingIcon = if (hasRemove) {
                {
                    IconButton(
                        onClick = { onRemove?.invoke() },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quitar etiqueta",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            } else null,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                borderWidth = 1.dp
            ),
            modifier = modifier
        )
    }
}

/**
 * Fila horizontal de etiquetas/tags
 * Se puede hacer scroll horizontal
 */
@Composable
fun PublicationTagsRow(
    tags: List<PublicationTagModel>,
    onTagClick: ((PublicationTagModel) -> Unit)? = null,
    onTagRemove: ((PublicationTagModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(tags) { tag ->
            PublicationTagChip(
                tag = tag,
                onClick = if (onTagClick != null) {
                    { onTagClick(tag) }
                } else null,
                onRemove = if (onTagRemove != null) {
                    { onTagRemove(tag) }
                } else null
            )
        }
    }
}

/**
 * Grid wrapping de etiquetas/tags (se ajustan automáticamente)
 * Ideal para formularios o vistas donde hay espacio vertical
 */
@Composable
fun PublicationTagsFlow(
    tags: List<PublicationTagModel>,
    onTagClick: ((PublicationTagModel) -> Unit)? = null,
    onTagRemove: ((PublicationTagModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) return

    // Flow layout simulado con Row wrapping
    Column(modifier = modifier) {
        var currentRow = mutableListOf<PublicationTagModel>()

        tags.forEach { tag ->
            currentRow.add(tag)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            tags.forEach { tag ->
                PublicationTagChip(
                    tag = tag,
                    onClick = if (onTagClick != null) {
                        { onTagClick(tag) }
                    } else null,
                    onRemove = if (onTagRemove != null) {
                        { onTagRemove(tag) }
                    } else null
                )
            }
        }
    }
}
