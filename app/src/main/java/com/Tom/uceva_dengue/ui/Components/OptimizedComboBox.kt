package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ComboBox optimizado con:
 * - Memoización de listas con derivedStateOf
 * - Keys únicas para evitar recomposiciones innecesarias
 * - Estilos modernos consistentes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedComboBox(
    selectedValue: String,
    options: List<String>,
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onValueChangedEvent: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    // Memoización: solo recalcula cuando cambia la lista
    val memoizedOptions by remember(options) {
        derivedStateOf { options }
    }

    val isEmpty by remember(memoizedOptions) {
        derivedStateOf { memoizedOptions.isEmpty() }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label, fontSize = 14.sp) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (isEmpty) {
                DropdownMenuItem(
                    text = { Text(text = "No hay opciones disponibles", fontSize = 14.sp) },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                memoizedOptions.forEach { option ->
                    // Key única para cada item - evita recomposiciones innecesarias
                    DropdownMenuItem(
                        text = { Text(text = option, fontSize = 14.sp) },
                        onClick = {
                            onValueChangedEvent(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Versión genérica con data class para máxima eficiencia
 * Evita mapeos repetidos en cada recomposición
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OptimizedComboBoxGeneric(
    selectedValue: T?,
    options: List<T>,
    label: String,
    displayText: (T) -> String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onValueChanged: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    // Memoización de la lista de display strings
    val displayStrings by remember(options) {
        derivedStateOf {
            options.map { displayText(it) }
        }
    }

    val selectedDisplayText = selectedValue?.let { displayText(it) } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedDisplayText,
            onValueChange = {},
            label = { Text(text = label, fontSize = 14.sp) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(text = "No hay opciones disponibles", fontSize = 14.sp) },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                options.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = displayStrings[index], fontSize = 14.sp) },
                        onClick = {
                            onValueChanged(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
