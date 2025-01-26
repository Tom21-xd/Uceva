package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun Campo(dato: String,nombre:String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(value = dato,
        onValueChange = { onTextFieldChanged(it)},
        Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Text),
        singleLine = true,
        label = { Text(nombre) },
        maxLines = 1)
}