package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    var firstName by remember { mutableStateOf(TextFieldValue("Johan")) }
    var lastName by remember { mutableStateOf(TextFieldValue("Ramirez")) }
    var address by remember { mutableStateOf(TextFieldValue("Calle 18 n 2b-53")) }
    var email by remember { mutableStateOf(TextFieldValue("johans.ramirez@udla.edu.co")) }
    var isMedicalStaff by remember { mutableStateOf(false) }
    var bloodType by remember { mutableStateOf("O+") }
    var phone by remember { mutableStateOf(TextFieldValue("3204440787")) }
    var username by remember { mutableStateOf(TextFieldValue("tom21xd")) }
    var role by remember { mutableStateOf("Admin") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back action */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFADD8E6))
            )
        },
        bottomBar = {
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Foto de perfil
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                        .background(Color.Gray), // Imagen de perfil ficticia
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_camera),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Información
                Text("Información", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                // Campos de nombre y apellido en inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nombres", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Apellidos", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dirección en un input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Dirección", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Correo en un input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Correo Electrónico", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personal médico
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isMedicalStaff,
                        onCheckedChange = { isMedicalStaff = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Personal médico", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campos de RH y género
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Selector de RH
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RH", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownSelector(
                            options = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+"),
                            selectedOption = bloodType,
                            onOptionSelected = { bloodType = it }
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    // Selector de género
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Rol", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownSelector(
                            options = listOf("Admin", "User", "Guest"),
                            selectedOption = role,
                            onOptionSelected = { role = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Teléfono y username en inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Teléfono", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Username", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    )
}
@Composable
fun DropdownSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menú")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },  // Nuevo formato
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun prev(){
    ProfileScreen()
}

