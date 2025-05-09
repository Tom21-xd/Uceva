package com.Tom.uceva_dengue.ui.Screen


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.Components.PostCard
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel


@Composable
fun HomeScreen(viewModel: PublicacionViewModel, role: Int, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding para todo el contenido
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SearchField(viewModel = viewModel)

            val publicaciones by remember { viewModel.publicaciones }.collectAsState()

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(publicaciones) { publicacion ->
                    PostCard(publicacion = publicacion)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (role == 1 || role == 3) {
            Log.d("NavigationCon", "Role: $role")
            FloatingActionButton(
                onClick = { navController.navigate(Rout.CreatePublicationScreen.name) },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xFF92C5FC) // Establece el color de fondo
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Crear publicación",
                    tint = Color.Black // Cambia el color del icono a negro
                )
            }
        }

    }
}

@Composable
fun SearchField(viewModel: PublicacionViewModel) {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = {
            searchText = it
            if (it.isNotBlank()) {
                viewModel.buscarPublicacion(it)
            } else {
                viewModel.obtenerPublicaciones()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color.Transparent, RoundedCornerShape(20.dp)),
        placeholder = {
            Text(
                text = "Buscar título...",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0)) // Placeholder gris suave
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF8A8A8A) // Ícono gris suave
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black), // Estilo de texto claro
        singleLine = true,
        maxLines = 1
    )
}
