package com.Tom.uceva_dengue.ui.Screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.viewModel.CreateHospitalViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHospitalScreen(
    navController: NavHostController,
    viewModel: CreateHospitalViewModel = viewModel()
) {
    val context = LocalContext.current
    val departamentos by viewModel.departamentos.collectAsState()
    val municipios by viewModel.municipios.collectAsState()
    val department by viewModel.department.observeAsState(initial = "")
    val cityName by viewModel.cityName.observeAsState(initial = "")
    val nombre by viewModel.nombre.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val latitud by viewModel.latitud.collectAsState()
    val longitud by viewModel.longitud.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de imagen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Seleccionar Imagen",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Seleccionar Imagen (Opcional)", color = Color.Gray)
                        }
                    }
                }
            }

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.setNombre(it) },
                label = { Text("Nombre del Hospital *") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5E81F4),
                    focusedLabelColor = Color(0xFF5E81F4)
                )
            )

            // Campo Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { viewModel.setDireccion(it) },
                label = { Text("Dirección *") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5E81F4),
                    focusedLabelColor = Color(0xFF5E81F4)
                )
            )

            // ComboBox Departamento
            ComboBox(
                selectedValue = department,
                options = departamentos.map { it.NOMBRE_DEPARTAMENTO },
                label = "Departamento *"
            ) { nuevoDepartamento ->
                val departamentoSeleccionado = departamentos.firstOrNull {
                    it.NOMBRE_DEPARTAMENTO == nuevoDepartamento
                }
                departamentoSeleccionado?.ID_DEPARTAMENTO?.let {
                    viewModel.fetchMunicipios(it.toString())
                    viewModel.setDepartment(nuevoDepartamento)
                }
            }

            // ComboBox Municipio
            ComboBox(
                selectedValue = cityName,
                options = municipios.map { it.NOMBRE_MUNICIPIO ?: "" },
                label = "Municipio *"
            ) { nuevaCiudad ->
                val ciudadSeleccionada = municipios.firstOrNull {
                    it.NOMBRE_MUNICIPIO == nuevaCiudad
                }
                val idCiudad = ciudadSeleccionada?.ID_MUNICIPIO ?: 0
                viewModel.setCityId(idCiudad)
                viewModel.setCityName(nuevaCiudad)
            }

            // Campos de Coordenadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitud,
                    onValueChange = { viewModel.setLatitud(it) },
                    label = { Text("Latitud *") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E81F4),
                        focusedLabelColor = Color(0xFF5E81F4)
                    )
                )

                OutlinedTextField(
                    value = longitud,
                    onValueChange = { viewModel.setLongitud(it) },
                    label = { Text("Longitud *") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E81F4),
                        focusedLabelColor = Color(0xFF5E81F4)
                    )
                )
            }

            Text(
                text = "Tip: Puedes obtener las coordenadas desde Google Maps",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Crear
            Button(
                onClick = {
                    isLoading = true
                    viewModel.createHospital(
                        context = context,
                        imageUri = selectedImageUri,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Hospital creado con éxito",
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E81F4))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Crear Hospital",
                        color = Color.White,
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
                    contentColor = Color(0xFF5E81F4)
                )
            ) {
                Text("Cancelar", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
