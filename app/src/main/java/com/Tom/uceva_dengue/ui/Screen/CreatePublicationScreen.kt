package com.Tom.uceva_dengue.ui.Screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.CreatePublicationViewModel


@Composable
fun CreatePublicationScreen(
    viewModel: CreatePublicationViewModel,
    role: Int,
    user: String?,
    navController: NavHostController
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nueva Publicación",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF444444),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Campo de Título
        BasicTextField(
            value = titulo,
            onValueChange = { titulo = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(vertical = 14.dp, horizontal = 20.dp),
            textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF333333)),
            decorationBox = { innerTextField ->
                Box {
                    if (titulo.isEmpty()) Text("Título", color = Color(0xFFAAAAAA))
                    innerTextField()
                }
            }
        )

        // Campo de Descripción
        BasicTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(vertical = 14.dp, horizontal = 20.dp)
                .height(150.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF333333)),
            decorationBox = { innerTextField ->
                Box {
                    if (descripcion.isEmpty()) Text("Descripción", color = Color(0xFFAAAAAA))
                    innerTextField()
                }
            }
        )

        // Selección de Imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .clickable { imagePickerLauncher.launch("image/*") }
                .padding(16.dp),
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
                        tint = Color(0xFFAAAAAA),
                        modifier = Modifier.size(48.dp)
                    )
                    Text("Seleccionar Imagen", color = Color(0xFFAAAAAA))
                }
            }
        }

        // Botón de Publicar
        Button(
            onClick = {
                if (titulo.isNotEmpty() && descripcion.isNotEmpty() && selectedImageUri != null) {
                    isLoading = true
                    viewModel.createPost(
                        context = context,
                        title = titulo,
                        description = descripcion,
                        userId = user ?: "",
                        imageUri = selectedImageUri,
                        onSuccess = {
                            isLoading = false
                            titulo = ""
                            descripcion = ""
                            selectedImageUri = null
                            navController.navigate(Rout.HomeScreen.name)
                            Toast.makeText(context, "Publicacion Creada correctamente", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMessage ->
                            isLoading = false
                            println(errorMessage)
                        }
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Publicar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}