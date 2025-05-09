package com.Tom.uceva_dengue.ui.Screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.viewModel.CreatePublicationViewModel

@Composable
fun CreatePublicationScreen(viewModel: CreatePublicationViewModel) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val userId = "123"  // Cambiar por el ID real del usuario

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear Publicación", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Campo de título
        BasicTextField(
            value = titulo,
            onValueChange = { titulo = it },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0)).padding(12.dp)) {
                    if (titulo.isEmpty()) Text("Título", color = Color.Gray)
                    innerTextField()
                }
            }
        )

        // Campo de descripción
        BasicTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            modifier = Modifier.fillMaxWidth().padding(8.dp).height(150.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0)).padding(12.dp)) {
                    if (descripcion.isEmpty()) Text("Descripción", color = Color.Gray)
                    innerTextField()
                }
            }
        )

        // Selector de imagen
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFF0F0F0)),
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
                Text("Seleccionar imagen", color = Color.Gray)
            }
        }

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Seleccionar Imagen")
        }

        // Botón para publicar
        Button(
            onClick = {
                if (titulo.isNotEmpty() && descripcion.isNotEmpty() && selectedImageUri != null) {
                    isLoading = true
                    viewModel.createPost(
                        context = context,
                        title = titulo,
                        description = descripcion,
                        userId = userId,
                        imageUri = selectedImageUri,
                        onSuccess = {
                            isLoading = false
                            titulo = ""
                            descripcion = ""
                            selectedImageUri = null
                        },
                        onError = { errorMessage ->
                            isLoading = false
                            println(errorMessage)
                        }
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Publicar")
            }
        }
    }
}
