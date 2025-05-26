package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tom.uceva_dengue.ui.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val user by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error ?: "Error desconocido", color = Color.Red)
        }
        return
    }

    user?.let { userModel ->
        var firstName by remember { mutableStateOf(TextFieldValue(userModel.NOMBRE_USUARIO ?: "")) }
        var address by remember { mutableStateOf(TextFieldValue(userModel.DIRECCION_USUARIO ?: "")) }
        var email by remember { mutableStateOf(TextFieldValue(userModel.CORREO_USUARIO ?: "")) }
        var role by remember { mutableStateOf(userModel.NOMBRE_ROL ?: "Sin rol") }
        var bloodType by remember { mutableStateOf(userModel.NOMBRE_TIPOSANGRE ?: "Sin tipo de sangre") }
        var gender by remember { mutableStateOf(userModel.NOMBRE_GENERO ?: "Sin género") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(text = firstName.text.take(1).uppercase(), fontSize = 48.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de perfil
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Nombres", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Dirección", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Correo Electrónico", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rol", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = TextFieldValue(role),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tipo de Sangre", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = TextFieldValue(bloodType),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Género", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = TextFieldValue(gender),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
