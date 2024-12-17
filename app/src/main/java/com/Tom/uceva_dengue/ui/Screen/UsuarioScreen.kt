package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
fun UsuariosScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear caso") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de menú */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFADD8E6))
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = rememberNavController() )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Acción para agregar usuario */ }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar usuario")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Campo de búsqueda
                SearchFieldUsua()

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de usuarios
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(4) { index ->
                        UserCard("Usuario ${index + 1}")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchFieldUsua() {
    OutlinedTextField(
        value = "",
        onValueChange = { /* Lógica de búsqueda */ },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Nombre/Id") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        singleLine = true
    )
}

@Composable
fun UserCard(nombreUsuario: String) {
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
                        painter = painterResource(id = R.drawable.user), // Usa el ID correcto de tu imagen
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Nombre del usuario
                Text(
                    text = nombreUsuario,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón de editar
            IconButton(onClick = { /* Acción para editar usuario */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar usuario")
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun UsuariosPreview() {
    UsuariosScreen()
}
