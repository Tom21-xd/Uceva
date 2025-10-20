package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

/**
 * Componente de búsqueda con autocompletado
 * Permite buscar y seleccionar items sin perder el foco del teclado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdown(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    label: String = "Buscar",
    placeholder: String = "Escribe para buscar...",
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Filtrar items basado en la búsqueda
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            items.filter { item ->
                itemLabel(item).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Mostrar dropdown cuando hay texto y hay resultados
    LaunchedEffect(searchQuery, filteredItems, isFocused) {
        isDropdownExpanded = isFocused && searchQuery.isNotBlank() && filteredItems.isNotEmpty()
    }

    // Actualizar el campo cuando se selecciona un item
    LaunchedEffect(selectedItem) {
        if (selectedItem != null) {
            searchQuery = itemLabel(selectedItem)
        }
    }

    Box(modifier = modifier) {
        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    // Si el campo se limpia, deseleccionar el item
                    if (query.isBlank() && selectedItem != null) {
                        onItemSelected(null)
                    }
                },
                label = { Text(label) },
                placeholder = { Text(placeholder, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                enabled = enabled,
                isError = isError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = if (isError) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Row {
                        // Botón para limpiar
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    onItemSelected(null)
                                    isDropdownExpanded = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Icono de dropdown
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError) MaterialTheme.colorScheme.error
                                         else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedLabelColor = if (isError) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        isDropdownExpanded = false
                    }
                )
            )

            // Texto de soporte/error
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    color = if (isError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            // Dropdown con resultados
            AnimatedVisibility(
                visible = isDropdownExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredItems) { item ->
                            DropdownItemRow(
                                item = item,
                                label = itemLabel(item),
                                searchQuery = searchQuery,
                                onClick = {
                                    onItemSelected(item)
                                    searchQuery = itemLabel(item)
                                    isDropdownExpanded = false
                                    focusManager.clearFocus()
                                }
                            )
                        }

                        // Mensaje si no hay resultados
                        if (filteredItems.isEmpty() && searchQuery.isNotBlank()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No se encontraron resultados",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Item del dropdown con highlight del texto buscado
 */
@Composable
private fun <T> DropdownItemRow(
    item: T,
    label: String,
    searchQuery: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
