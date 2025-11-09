package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.ui.viewModel.HospitalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateHospitalScreen(
    hospitalId: Int,
    navController: NavHostController,
    viewModel: HospitalViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var selectedCityId by remember { mutableStateOf<Int?>(null) }
    var selectedCityName by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Estados de UI
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Para el dropdown de municipios
    var cities by remember { mutableStateOf<List<CityModel>>(emptyList()) }
    var expandedCityDropdown by remember { mutableStateOf(false) }

    // Cargar municipios
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.cityService.getCities("")
            cities = response
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar municipios: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Cargar datos del hospital
    LaunchedEffect(hospitalId) {
        viewModel.getHospitalById(
            id = hospitalId,
            onSuccess = { hospital ->
                nombre = hospital.NOMBRE_HOSPITAL
                direccion = hospital.DIRECCION_HOSPITAL ?: ""
                latitud = hospital.LATITUD_HOSPITAL ?: ""
                longitud = hospital.LONGITUD_HOSPITAL ?: ""
                selectedCityId = hospital.FK_ID_MUNICIPIO
                selectedCityName = hospital.NOMBRE_MUNICIPIO
                imageUrl = if (!hospital.IMAGEN_HOSPITAL.isNullOrBlank()) {
                    "https://api.prometeondev.com/Image/getImage/${hospital.IMAGEN_HOSPITAL}"
                } else ""
                isLoadingData = false
            },
            onError = { error ->
                errorMessage = error
                isLoadingData = false
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoadingData) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error al cargar el hospital", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigateUp() }) {
                        Text("Volver")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Editar Hospital",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Mostrar imagen actual
                if (imageUrl.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = "Imagen del hospital",
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Error al cargar imagen", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }

                    Text(
                        text = "Nota: La imagen no se puede modificar desde aquí",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Hospital *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Campo Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Dropdown de Municipios
                ExposedDropdownMenuBox(
                    expanded = expandedCityDropdown,
                    onExpandedChange = { expandedCityDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCityName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Municipio") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar municipio"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCityDropdown,
                        onDismissRequest = { expandedCityDropdown = false }
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city.NOMBRE_MUNICIPIO) },
                                onClick = {
                                    selectedCityId = city.ID_MUNICIPIO
                                    selectedCityName = city.NOMBRE_MUNICIPIO
                                    expandedCityDropdown = false
                                }
                            )
                        }
                    }
                }

                // Campos de coordenadas GPS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitud,
                        onValueChange = { latitud = it },
                        label = { Text("Latitud") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = longitud,
                        onValueChange = { longitud = it },
                        label = { Text("Longitud") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Text(
                    text = "* Campos obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Actualizar
                Button(
                    onClick = {
                        if (nombre.isEmpty()) {
                            Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        viewModel.updateHospital(
                            id = hospitalId,
                            nombre = nombre,
                            direccion = direccion.ifBlank { null },
                            latitud = latitud.ifBlank { null },
                            longitud = longitud.ifBlank { null },
                            idMunicipio = selectedCityId,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Hospital actualizado con éxito",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigateUp()
                            },
                            onError = { error ->
                                isLoading = false
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Actualizar Hospital",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Cancelar
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancelar", fontSize = 18.sp)
                }
            }
        }
    }
}
