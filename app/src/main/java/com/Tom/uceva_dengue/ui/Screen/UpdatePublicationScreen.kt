package com.Tom.uceva_dengue.ui.Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.PublicationCategoryModel
import com.Tom.uceva_dengue.Data.Model.PublicationTagModel
import com.Tom.uceva_dengue.ui.Components.getCategoryIcon
import com.Tom.uceva_dengue.ui.viewModel.PublicacionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePublicationScreen(
    publicationId: Int,
    navController: NavHostController,
    role: Int = 1,
    viewModel: PublicacionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados básicos
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingData by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

    // Cargar categorías, etiquetas y datos de la publicación
    LaunchedEffect(publicationId) {
        scope.launch {
            try {
                // Primero cargar categorías y etiquetas
                Log.d("UpdatePublication", "Iniciando carga de categorías y etiquetas...")

                val catResponse = RetrofitClient.publicationCategoryService.getAllCategories()
                Log.d("UpdatePublication", "Response categorías: ${catResponse.code()}")
                if (catResponse.isSuccessful && catResponse.body() != null) {
                    val allCategories = catResponse.body()!!
                    Log.d("UpdatePublication", "Categorías totales: ${allCategories.size}")
                    categories = allCategories.filter { it.ESTADO_CATEGORIA }
                    Log.d("UpdatePublication", "Categorías activas: ${categories.size}")
                } else {
                    Log.e("UpdatePublication", "Error al cargar categorías: ${catResponse.errorBody()?.string()}")
                }

                val tagResponse = RetrofitClient.publicationTagService.getAllTags()
                Log.d("UpdatePublication", "Response etiquetas: ${tagResponse.code()}")
                if (tagResponse.isSuccessful && tagResponse.body() != null) {
                    val allTags = tagResponse.body()!!
                    Log.d("UpdatePublication", "Etiquetas totales: ${allTags.size}")
                    tags = allTags.filter { it.ESTADO_ETIQUETA }
                    Log.d("UpdatePublication", "Etiquetas activas: ${tags.size}")
                } else {
                    Log.e("UpdatePublication", "Error al cargar etiquetas: ${tagResponse.errorBody()?.string()}")
                }

                // Luego cargar los datos de la publicación
                Log.d("UpdatePublication", "Cargando datos de la publicación ID: $publicationId")
                viewModel.getPublicationById(
                    id = publicationId,
                    onSuccess = { publication ->
                        Log.d("UpdatePublication", "Publicación cargada: ${publication.TITULO_PUBLICACION}")
                        titulo = publication.TITULO_PUBLICACION
                        descripcion = publication.DESCRIPCION_PUBLICACION
                        imageUrl = "https://api.prometeondev.com/Image/getImage/${publication.IMAGEN_PUBLICACION}"

                        // Cargar datos adicionales
                        selectedCategoryId = publication.FK_ID_CATEGORIA
                        selectedPrioridad = publication.NIVEL_PRIORIDAD ?: "Normal"
                        isFijada = publication.FIJADA

                        Log.d("UpdatePublication", "Categoría seleccionada: $selectedCategoryId")
                        Log.d("UpdatePublication", "Prioridad: $selectedPrioridad")
                        Log.d("UpdatePublication", "Fijada: $isFijada")

                        // Cargar etiquetas seleccionadas
                        publication.ETIQUETAS?.let { etiquetas ->
                            selectedTagsIds = etiquetas.map { it.ID_ETIQUETA }.toSet()
                            Log.d("UpdatePublication", "Etiquetas seleccionadas: $selectedTagsIds")
                        }

                        isLoadingData = false
                    },
                    onError = { error ->
                        Log.e("UpdatePublication", "Error al cargar publicación: $error")
                        errorMessage = error
                        isLoadingData = false
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                Log.e("UpdatePublication", "Error general al cargar datos", e)
                errorMessage = e.message
                isLoadingData = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Error al cargar la publicación", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigateUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Volver")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card de imagen (solo lectura)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Imagen",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "Solo lectura",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp)
                                .aspectRatio(16f / 9f, matchHeightConstraintsFirst = false),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            SubcomposeAsyncImage(
                                model = imageUrl,
                                contentDescription = "Imagen de la publicación",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.BrokenImage,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Text("Error al cargar imagen", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // Card de título
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

                // Card de descripción
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

                // Categoría
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Categoría",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (categories.isEmpty()) {
                            Text(
                                text = "⚠️ No hay categorías disponibles",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
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

                // Etiquetas
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

                        if (tags.isEmpty()) {
                            Text(
                                text = "⚠️ No hay etiquetas disponibles",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
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

                // Prioridad y Fijar (Solo admin/médico)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de Actualizar
                Button(
                    onClick = {
                        if (titulo.isNotEmpty() && descripcion.isNotEmpty()) {
                            isLoading = true
                            viewModel.updatePublicationEnhanced(
                                id = publicationId,
                                titulo = titulo,
                                descripcion = descripcion,
                                categoriaId = selectedCategoryId,
                                etiquetasIds = selectedTagsIds.toList(),
                                prioridad = if (isAdminOrMedico) selectedPrioridad else null,
                                fijada = if (isAdminOrMedico) isFijada else null,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Publicación actualizada con éxito",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigateUp()
                                },
                                onError = { error ->
                                    isLoading = false
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor completa todos los campos",
                                Toast.LENGTH_SHORT
                            ).show()
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
                        Text(
                            "Actualizar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Botón de Cancelar
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
