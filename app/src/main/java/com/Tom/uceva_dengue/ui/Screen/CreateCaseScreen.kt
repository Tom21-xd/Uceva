package com.Tom.uceva_dengue.ui.Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCaseScreen() {
    var isPatientSectionExpanded by remember { mutableStateOf(false) }
    var isDengueSectionExpanded by remember { mutableStateOf(false) }
    var isLocationSectionExpanded by remember { mutableStateOf(false) }

    var existingUser by remember { mutableStateOf(true) }
    var patientFirstName by remember { mutableStateOf("") }
    var patientLastName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Genero") }
    var address by remember { mutableStateOf("") }

    var dengueType by remember { mutableStateOf("Dengue 1") }
    var selectedSymptoms = remember { mutableStateListOf<String>() }

    var hospital by remember { mutableStateOf("Hospital 1") }
    var description by remember { mutableStateOf(TextFieldValue("")) }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),  // Espacio entre los elementos
            horizontalAlignment = Alignment.CenterHorizontally  // Centramos todo horizontalmente
        ) {
            // Sección Paciente
            SectionHeader(
                title = "Paciente",
                expanded = isPatientSectionExpanded,
                onExpandChanged = { isPatientSectionExpanded = !isPatientSectionExpanded }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¿Usuario existente?")
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.Center, // Alineamos horizontalmente
                        verticalAlignment = Alignment.CenterVertically // Alineamos verticalmente
                    ) {
                        RadioButton(
                            selected = existingUser,
                            onClick = { existingUser = true }
                        )
                        Text("Sí")
                        Spacer(modifier = Modifier.width(8.dp)) // Agregamos un espacio entre Sí y No
                        RadioButton(
                            selected = !existingUser,
                            onClick = { existingUser = false }
                        )
                        Text("No")
                    }

                    if (!existingUser) {
                        // Campos adicionales si es un nuevo usuario
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Nombres")
                                OutlinedTextField(
                                    value = patientFirstName,
                                    onValueChange = { patientFirstName = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Apellidos")
                                OutlinedTextField(
                                    value = patientLastName,
                                    onValueChange = { patientLastName = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Género")
                            OutlinedTextField(
                                value = gender,
                                onValueChange = { gender = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Dirección")
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Sección Tipo de Dengue
            SectionHeader(
                title = "Tipo Dengue",
                expanded = isDengueSectionExpanded,
                onExpandChanged = { isDengueSectionExpanded = !isDengueSectionExpanded }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Síntomas")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            repeat(5) { index ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically // Alinea verticalmente Checkbox y Texto
                                ) {
                                    Checkbox(
                                        checked = selectedSymptoms.contains("Síntoma ${index + 1}"),
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) selectedSymptoms.add("Síntoma ${index + 1}")
                                            else selectedSymptoms.remove("Síntoma ${index + 1}")
                                        }
                                    )
                                    Text("Síntoma ${index + 1}")
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            repeat(5) { i ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically // Alinea verticalmente Checkbox y Texto
                                ) {
                                    Checkbox(
                                        checked = selectedSymptoms.contains("Síntoma ${i + 6}"),
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) selectedSymptoms.add("Síntoma ${i + 6}")
                                            else selectedSymptoms.remove("Síntoma ${i + 6}")
                                        }
                                    )
                                    Text("Síntoma ${i + 6}")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("El tipo de dengue podría ser:")
                    OutlinedTextField(
                        value = dengueType,
                        onValueChange = { dengueType = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Sección Ubicación
            SectionHeader(
                title = "Ubicación",
                expanded = isLocationSectionExpanded,
                onExpandChanged = { isLocationSectionExpanded = !isLocationSectionExpanded }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hospital")
                    OutlinedTextField(
                        value = hospital,
                        onValueChange = { hospital = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Descripción")
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Mapa (Placeholder, en tu caso podría ser una imagen o un mapa real)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center // Centrar texto dentro del mapa
                    ) {
                        MapScreen()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Reportar
            Button(
                onClick = { /* Acción de reporte */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reportar")
            }
        }

}

@Composable
fun SectionHeader(
    title: String,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onExpandChanged() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 20.sp)
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        if (expanded) {
            content()
        }
    }
}

@Composable
@Preview
fun previ(){
    CreateCaseScreen()
}
