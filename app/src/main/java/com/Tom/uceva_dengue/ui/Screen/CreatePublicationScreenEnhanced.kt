package com.Tom.uceva_dengue.ui.Screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.PublicationCategoryModel
import com.Tom.uceva_dengue.Data.Model.PublicationTagModel
import com.Tom.uceva_dengue.ui.Components.getCategoryIcon
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.CreatePublicationViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePublicationScreenEnhanced(
    viewModel: CreatePublicationViewModel,
    role: Int,
    user: String?,
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados básicos
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Estados nuevos
    var categories by remember { mutableStateOf<List<PublicationCategoryModel>>(emptyList()) }
    var tags by remember { mutableStateOf<List<PublicationTagModel>>(emptyList()) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedTagsIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedPrioridad by remember { mutableStateOf("Normal") }
    var isFijada by remember { mutableStateOf(false) }

    // Dropdowns states
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }

    val isAdminOrMedico = role == 2 || role == 3

    // Cargar categorías y etiquetas
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val catResponse = RetrofitClient.publicationCategoryService.getAllCategories()
                if (catResponse.isSuccessful && catResponse.body() != null) {
                    categories = catResponse.body()!!.filter { it.ESTADO_CATEGORIA }
                }

                val tagResponse = RetrofitClient.publicationTagService.getAllTags()
                if (tagResponse.isSuccessful && tagResponse.body() != null) {
                    tags = tagResponse.body()!!.filter { it.ESTADO_ETIQUETA }
                }
            } catch (e: Exception) {
                Log.e("CreatePublication", "Error loading data", e)
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Título",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe un título llamativo") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Descripción",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp),
                        placeholder = { Text("Describe el contenido de la publicación") },
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Categoría (NUEVO)
            if (categories.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Categoría *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategoryId?.let { id ->
                                    categories.find { it.ID_CATEGORIA_PUBLICACION == id }?.NOMBRE_CATEGORIA
                                } ?: "Selecciona una categoría",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false }
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.NOMBRE_CATEGORIA) },
                                        onClick = {
                                            selectedCategoryId = category.ID_CATEGORIA_PUBLICACION
                                            expandedCategory = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = getCategoryIcon(category.ICONO),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Etiquetas (NUEVO)
            if (tags.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Etiquetas",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${selectedTagsIds.size} seleccionadas",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Tags horizontales scrollables
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tags) { tag ->
                                FilterChip(
                                    selected = selectedTagsIds.contains(tag.ID_ETIQUETA),
                                    onClick = {
                                        selectedTagsIds = if (selectedTagsIds.contains(tag.ID_ETIQUETA)) {
                                            selectedTagsIds - tag.ID_ETIQUETA
                                        } else {
                                            selectedTagsIds + tag.ID_ETIQUETA
                                        }
                                    },
                                    label = { Text(tag.NOMBRE_ETIQUETA) }
                                )
                            }
                        }
                    }
                }
            }

            // Prioridad y Fijar (NUEVO - Solo admin/médico)
            if (isAdminOrMedico) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Opciones Avanzadas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Prioridad
                        Text("Prioridad", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedPrioridad,
                            onExpandedChange = { expandedPrioridad = it }
                        ) {
                            OutlinedTextField(
                                value = selectedPrioridad,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandedPrioridad,
                                onDismissRequest = { expandedPrioridad = false }
                            ) {
                                listOf("Baja", "Normal", "Alta", "Urgente").forEach { prioridad ->
                                    DropdownMenuItem(
                                        text = { Text(prioridad) },
                                        onClick = {
                                            selectedPrioridad = prioridad
                                            expandedPrioridad = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fijar publicación
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Fijar publicación", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "Aparecerá siempre primero",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isFijada,
                                onCheckedChange = { isFijada = it }
                            )
                        }
                    }
                }
            }

            // Imagen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Imagen *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (selectedImageUri != null) {
                            TextButton(onClick = { selectedImageUri = null }) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quitar")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false)
                            .background(
                                color = if (selectedImageUri != null) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Toca para seleccionar imagen",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de publicar
            Button(
                onClick = {
                    if (titulo.isNotEmpty() && descripcion.isNotEmpty() && selectedImageUri != null) {
                        if (selectedCategoryId == null) {
                            Toast.makeText(context, "Por favor selecciona una categoría", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        viewModel.createPost(
                            context = context,
                            title = titulo,
                            description = descripcion,
                            userId = user ?: "",
                            imageUri = selectedImageUri,
                            categoriaId = selectedCategoryId,
                            etiquetasIds = selectedTagsIds.toList(),
                            prioridad = if (isAdminOrMedico) selectedPrioridad else null,
                            fijada = if (isAdminOrMedico) isFijada else false,
                            onSuccess = {
                                isLoading = false
                                titulo = ""
                                descripcion = ""
                                selectedImageUri = null
                                selectedCategoryId = null
                                selectedTagsIds = emptySet()
                                selectedPrioridad = "Normal"
                                isFijada = false
                                navController.navigate(Rout.HomeScreen.name)
                                Toast.makeText(context, "Publicación creada correctamente", Toast.LENGTH_SHORT).show()
                            },
                            onError = { errorMessage ->
                                isLoading = false
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
