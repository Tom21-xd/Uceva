package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosDengueScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Casos dengue") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de menú */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFADD8E6))
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = rememberNavController())
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Acción para agregar caso */ }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar caso")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Filtros de búsqueda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Campo de búsqueda por nombre o ID
                    SearchFieldUsuario()

                    // Filtro de estado
                    EstadoDropdown()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de casos de dengue
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(4) { index ->
                        CasoDengueCard("Usuario ${index + 1}", "Detectado: Hospital ${index + 1}")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchFieldUsuario() {
    var searchQuery by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Nombre/Id") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        singleLine = true
    )
}

@Composable
fun EstadoDropdown() {
    val estados = listOf("Activo", "Superado", "En tratamiento")
    var selectedEstado by remember { mutableStateOf(estados[0]) }
    var expanded by remember { mutableStateOf(false) }

    Box() {
        OutlinedTextField(
            value = selectedEstado,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            placeholder = { Text("Estado") },
            readOnly = true

        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            estados.forEach { estado ->
                DropdownMenuItem(
                    text = { Text(estado) },
                    onClick = {
                        selectedEstado = estado
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CasoDengueCard(nombreUsuario: String, hospitalDeteccion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Imagen de perfil del usuario
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dengue),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Información del usuario y hospital
                Column {
                    Text(
                        text = nombreUsuario,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = hospitalDeteccion,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Botón de editar
            IconButton(onClick = { /* Acción para editar caso */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar caso")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CasosDenguePreview() {
    CasosDengueScreen()
}
