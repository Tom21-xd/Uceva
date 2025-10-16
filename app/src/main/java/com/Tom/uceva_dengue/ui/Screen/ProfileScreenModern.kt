package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tom.uceva_dengue.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreenModern(viewModel: ProfileViewModel, userId: String?) {
    val user by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        when {
            loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF5E81F4)
                )
            }
            error != null -> {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al cargar perfil",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE53E3E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error!!, color = Color(0xFF666666))
                    }
                }
            }
            user != null -> {
                user?.let { userInfo ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Header con avatar y nombre
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF5E81F4),
                                                Color(0xFF92C5FC)
                                            )
                                        )
                                    )
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (userInfo.NOMBRE_USUARIO?.take(1) ?: "U").uppercase(),
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = userInfo.NOMBRE_USUARIO ?: "Usuario",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = userInfo.NOMBRE_ROL ?: "Sin rol",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información Personal
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Información Personal",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D3748)
                                    )
                                    IconButton(onClick = { isEditing = !isEditing }) {
                                        Icon(
                                            if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                            contentDescription = if (isEditing) "Cancelar" else "Editar",
                                            tint = Color(0xFF5E81F4)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                InfoItem(
                                    icon = Icons.Default.Email,
                                    label = "Correo Electrónico",
                                    value = userInfo.CORREO_USUARIO ?: "N/A"
                                )

                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFE2E8F0)
                                )

                                InfoItem(
                                    icon = Icons.Default.LocationOn,
                                    label = "Dirección",
                                    value = userInfo.DIRECCION_USUARIO ?: "No especificada"
                                )

                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFE2E8F0)
                                )

                                InfoItem(
                                    icon = Icons.Default.Person,
                                    label = "Género",
                                    value = userInfo.NOMBRE_GENERO ?: "No especificado"
                                )

                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFE2E8F0)
                                )

                                InfoItem(
                                    icon = Icons.Default.Favorite,
                                    label = "Tipo de Sangre",
                                    value = userInfo.NOMBRE_TIPOSANGRE ?: "No especificado"
                                )

                                if (userInfo.NOMBRE_MUNICIPIO != null) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = Color(0xFFE2E8F0)
                                    )

                                    InfoItem(
                                        icon = Icons.Default.Place,
                                        label = "Municipio",
                                        value = userInfo.NOMBRE_MUNICIPIO ?: "No especificado"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Card de ID
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF0F4FF)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF5E81F4),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ID: ${userInfo.ID_USUARIO}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF5E81F4)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            else -> {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF718096),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay información disponible",
                            color = Color(0xFF718096)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F4FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5E81F4),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF718096),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
