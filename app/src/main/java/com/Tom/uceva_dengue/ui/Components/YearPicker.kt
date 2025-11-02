package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPicker(
    label: String,
    selectedYear: Int?,
    onYearSelected: (Int) -> Unit,
    availableYears: List<Int> = rememberAvailableYears()
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember(selectedYear) { mutableStateOf(selectedYear?.toString() ?: "") }

    LaunchedEffect(selectedYear) {
        textFieldValue = selectedYear?.toString() ?: ""
    }

    Column(modifier = Modifier.wrapContentHeight()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = textFieldValue,
                onValueChange = {},
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableYears.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            textFieldValue = year.toString()
                            onYearSelected(year)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun rememberAvailableYears(minYear: Int = 1980): List<Int> {
    val current = Calendar.getInstance().get(Calendar.YEAR)
    val safeMin = minOf(minYear, current)
    return remember(current to safeMin) {
        (current downTo safeMin).toList()
    }
}
