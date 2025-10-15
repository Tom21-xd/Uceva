package com.Tom.uceva_dengue.ui.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.Components.Campo
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel

// Colores modernos
private val PrimaryBlue = Color(0xFF5E81F4)
private val SecondaryBlue = Color(0xFF667EEA)
private val AccentPurple = Color(0xFF764BA2)
private val LightGray = Color(0xFFF5F7FA)
private val DarkGray = Color(0xFF2D3748)
private val SuccessGreen = Color(0xFF48BB78)
private val ErrorRed = Color(0xFFE53E3E)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreenModern(viewModel: AuthViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card contenedor con diseño moderno
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo y título
                    Image(
                        painter = painterResource(id = R.drawable.salud),
                        contentDescription = "Logo",
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bienvenido",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sistema de Monitoreo de Dengue",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Tabs modernos
                    ModernTabs(viewModel)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contenido animado
                    val logRegis by viewModel.log_regis.observeAsState(initial = false)

                    AnimatedVisibility(
                        visible = !logRegis,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        ModernLogin(viewModel, navController)
                    }

                    AnimatedVisibility(
                        visible = logRegis,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        ModernRegister(viewModel, navController)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Text(
                text = "© 2025 UCEVA - Todos los derechos reservados",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ModernTabs(viewModel: AuthViewModel) {
    val logRegis by viewModel.log_regis.observeAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Tab Iniciar Sesión
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(25.dp))
                .background(if (!logRegis) PrimaryBlue else Color.Transparent)
                .clickable { viewModel.log_regis.value = false },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Iniciar Sesión",
                color = if (!logRegis) Color.White else DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Tab Registro
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(25.dp))
                .background(if (logRegis) PrimaryBlue else Color.Transparent)
                .clickable { viewModel.log_regis.value = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Registrarse",
                color = if (logRegis) Color.White else DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ModernLogin(viewModel: AuthViewModel, navController: NavController) {
    val correo by viewModel.correo.observeAsState(initial = "")
    val contra by viewModel.contra.observeAsState(initial = "")
    val loginEnabled by viewModel.loginEnabled.observeAsState(initial = false)
    val contravisible by viewModel.contravisible.observeAsState(initial = false)
    val loading by viewModel.loading.observeAsState(initial = false)
    val loginError by viewModel.loginError.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo Email Moderno
        ModernTextField(
            value = correo,
            onValueChange = { viewModel.onLoginChange(it, contra) },
            label = "Correo Electrónico",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        // Campo Contraseña Moderno
        ModernPasswordField(
            value = contra,
            onValueChange = { viewModel.onLoginChange(correo, it) },
            label = "Contraseña",
            passwordVisible = contravisible,
            onToggleVisibility = { viewModel.onContraVisibilityChange(!contravisible) }
        )

        // Olvidaste contraseña
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 13.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    navController.navigate(Rout.OlvContraseniaScreen.name)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de inicio de sesión moderno
        ModernButton(
            text = "Iniciar Sesión",
            enabled = loginEnabled && !loading,
            loading = loading,
            onClick = {
                viewModel.iniciosesioncorreo(correo, contra) {
                    navController.navigate(Rout.HomeScreen.name)
                }
            }
        )

        // Error Dialog
        loginError?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.clearLoginError() },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearLoginError() }) {
                        Text("Aceptar", color = PrimaryBlue)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Error de inicio de sesión")
                    }
                },
                text = { Text(error) },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun ModernRegister(viewModel: AuthViewModel, navController: NavController) {
    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()
    val generos by viewModel.generos.collectAsState()
    val tiposSangre by viewModel.tiposSangre.collectAsState()

    val firstName by viewModel.firstName.observeAsState(initial = "")
    val lastName by viewModel.lastName.observeAsState(initial = "")
    val email by viewModel.email.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val address by viewModel.address.observeAsState(initial = "")
    val genderId by viewModel.genderId.observeAsState(initial = 0)
    val genderName by viewModel.genderName.observeAsState(initial = "")
    val bloodTypeId by viewModel.bloodTypeId.observeAsState(initial = 0)
    val bloodTypeName by viewModel.bloodTypeName.observeAsState(initial = "")
    val cityId by viewModel.cityId.observeAsState(initial = 0)
    val cityName by viewModel.cityName.observeAsState(initial = "")
    val department by viewModel.department.observeAsState(initial = "")
    val registerMessage by viewModel.registerMessage.observeAsState()
    val registerError by viewModel.registerError.observeAsState()

    var contrasenaVisible by remember { mutableStateOf(false) }
    var esPersonalMedico by remember { mutableStateOf(false) }
    var tipoDocumentoSeleccionado by remember { mutableStateOf(tipoIdentificaciones.first().codigo) }
    var numeroDocumento by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Crear cuenta nueva",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModernTextField(
            value = firstName,
            onValueChange = {
                viewModel.onRegisterChange(email, password, it, lastName, bloodTypeId, cityId, address, genderId)
            },
            label = "Nombres",
            leadingIcon = Icons.Default.Person
        )

        ModernTextField(
            value = lastName,
            onValueChange = {
                viewModel.onRegisterChange(email, password, firstName, it, bloodTypeId, cityId, address, genderId)
            },
            label = "Apellidos",
            leadingIcon = Icons.Default.Person
        )

        ModernTextField(
            value = email,
            onValueChange = {
                viewModel.onRegisterChange(it, password, firstName, lastName, bloodTypeId, cityId, address, genderId)
            },
            label = "Correo Electrónico",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        ModernPasswordField(
            value = password,
            onValueChange = {
                viewModel.onRegisterChange(email, it, firstName, lastName, bloodTypeId, cityId, address, genderId)
            },
            label = "Contraseña",
            passwordVisible = contrasenaVisible,
            onToggleVisibility = { contrasenaVisible = !contrasenaVisible }
        )

        ModernTextField(
            value = address,
            onValueChange = {
                viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, it, genderId)
            },
            label = "Dirección",
            leadingIcon = Icons.Default.Home
        )

        ComboBox(
            selectedValue = bloodTypeName,
            options = tiposSangre.map { it.NOMBRE_TIPOSANGRE },
            label = "Tipo de Sangre"
        ) { seleccion ->
            val selectedBloodType = tiposSangre.firstOrNull { it.NOMBRE_TIPOSANGRE == seleccion }
            val id = selectedBloodType?.ID_TIPOSANGRE ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, id, cityId, address, genderId)
            viewModel.setBloodTypeName(seleccion)
        }

        ComboBox(
            selectedValue = department,
            options = departamentos.map { it.NOMBRE_DEPARTAMENTO },
            label = "Departamento"
        ) { nuevoDepartamento ->
            val departamentoSeleccionado = departamentos.firstOrNull { it.NOMBRE_DEPARTAMENTO == nuevoDepartamento }
            departamentoSeleccionado?.ID_DEPARTAMENTO?.let {
                viewModel.fetchMunicipios(it.toString())
                viewModel.setDepartment(nuevoDepartamento)
            }
        }

        ComboBox(
            selectedValue = cityName,
            options = ciudades.map { it.NOMBRE_MUNICIPIO ?: "" },
            label = "Municipio"
        ) { nuevaCiudad ->
            val ciudadSeleccionada = ciudades.firstOrNull { it.NOMBRE_MUNICIPIO == nuevaCiudad }
            val idCiudad = ciudadSeleccionada?.ID_MUNICIPIO ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, idCiudad, address, genderId)
            viewModel.setCityName(nuevaCiudad)
        }

        ComboBox(
            selectedValue = genderName,
            options = generos.map { it.NOMBRE_GENERO },
            label = "Género"
        ) { seleccion ->
            val selectedGenero = generos.firstOrNull { it.NOMBRE_GENERO == seleccion }
            val id = selectedGenero?.ID_GENERO ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, address, id)
            viewModel.setGenderName(seleccion)
        }

        // Personal Médico checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = esPersonalMedico,
                onCheckedChange = { esPersonalMedico = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue
                )
            )
            Text(
                text = "Soy personal médico",
                fontSize = 14.sp,
                color = DarkGray
            )
        }

        if (esPersonalMedico) {
            ComboBox(
                selectedValue = tipoDocumentoSeleccionado,
                options = tipoIdentificaciones.map { "${it.nombre} (${it.codigo})" },
                label = "Tipo de Documento"
            ) { seleccion ->
                tipoDocumentoSeleccionado = tipoIdentificaciones.firstOrNull {
                    "${it.nombre} (${it.codigo})" == seleccion
                }?.codigo ?: ""
            }

            ModernTextField(
                value = numeroDocumento,
                onValueChange = { numeroDocumento = it },
                label = "Número de Documento",
                leadingIcon = Icons.Default.Badge,
                keyboardType = KeyboardType.Number
            )
        }

        registerError?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ErrorRed.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        fontSize = 13.sp,
                        color = ErrorRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ModernButton(
            text = "Registrarme",
            enabled = true,
            loading = false,
            onClick = {
                viewModel.registrarUsuario(
                    navController,
                    esPersonalMedico,
                    tipoIdentificacion = tipoDocumentoSeleccionado,
                    numeroDocumento = numeroDocumento
                )
            }
        )

        registerMessage?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = PrimaryBlue
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = PrimaryBlue
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = PrimaryBlue
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle visibility",
                    tint = Color.Gray
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = PrimaryBlue
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun ModernButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled && !loading) 1f else 0.5f,
        animationSpec = tween(300),
        label = "button_alpha"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(animatedAlpha)
            .shadow(if (enabled) 8.dp else 0.dp, RoundedCornerShape(26.dp)),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
        )
    ) {
        if (loading) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cargando...",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
