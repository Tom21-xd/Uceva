package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.Tom.uceva_dengue.Data.Model.PublicationModel

@Composable
fun PostCard(
    publicacion: PublicationModel,
    currentUserId: Int? = null,
    role: Int = 0,
    onEdit: ((PublicationModel) -> Unit)? = null,
    onDelete: ((PublicationModel) -> Unit)? = null
) {
    val imageUrl = "https://api.prometeondev.com/Image/getImage/${publicacion.IMAGEN_PUBLICACION}"
    var showMenu by remember { mutableStateOf(false) }

    // Mostrar opciones si es admin/personal médico O si es el autor de la publicación
    val canEdit = (role == 2 || role == 3) || (currentUserId == publicacion.FK_ID_USUARIO)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Imagen de la publicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Fit,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Error al cargar imagen",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )

                // Menú de opciones
                if (canEdit) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = Color(0xFF333333)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = Color(0xFF5E81F4)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Editar")
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit?.invoke(publicacion)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color(0xFFE53935)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Eliminar", color = Color(0xFFE53935))
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete?.invoke(publicacion)
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = publicacion.TITULO_PUBLICACION,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = publicacion.DESCRIPCION_PUBLICACION,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4A4A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.LightGray, thickness = 1.dp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👤 ${publicacion.NOMBRE_USUARIO}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = publicacion.FECHA_PUBLICACION,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
