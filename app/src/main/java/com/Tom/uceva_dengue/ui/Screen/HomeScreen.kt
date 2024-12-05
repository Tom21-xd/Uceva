package com.Tom.uceva_dengue.ui.Screen


import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar
import com.Tom.uceva_dengue.ui.theme.fondo

//import com.Tom.uceva_dengue.ui.Components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchField()

        Spacer(modifier = Modifier.height(16.dp))

        PostCard()

        Spacer(modifier = Modifier.height(16.dp))

        PostCard()

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SearchField() {
    var text = remember { TextFieldValue("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White),  // Fondo blanco solo para el TextField
        placeholder = { Text("Título") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar")
        }
    )
}

@Composable
fun PostCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Fondo blanco solo para la tarjeta
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Título alineado a la izquierda
            Text(
                "Publicación",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido del texto centrado
            Text(
                "Este es el contenido de la publicación. Se verá como texto simulado en esta área.",
                modifier = Modifier.align(Alignment.CenterHorizontally) // Centrar solo este texto
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Imagen centrada
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(1.dp, Color.Black)
                    .align(Alignment.CenterHorizontally), // Centrar la imagen
                contentAlignment = Alignment.Center
            ) {
                // Imagen ficticia
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del pie de la tarjeta (publicado por, fecha)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Publicado por: Médico")
                Text(text = "04/04/2024")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchField() {
    HomeScreen()
}