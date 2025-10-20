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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreenModern(viewModel: AuthViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f, fill = false)) // Centra cuando hay espacio
            // Card contenedor con diseño moderno
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), // Reducido de 32dp para pantallas pequeñas
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo y título
                    Image(
                        painter = painterResource(id = R.drawable.salud),
                        contentDescription = "Logo",
                        modifier = Modifier.size(64.dp) // Reducido de 80dp
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // Reducido de 16dp

                    Text(
                        text = "Bienvenido",
                        fontSize = 22.sp, // Reducido de 28sp para pantallas pequeñas
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sistema de Monitoreo de Dengue",
                        fontSize = 13.sp, // Reducido de 14sp
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp)) // Reducido de 32dp

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

            Spacer(modifier = Modifier.weight(1f, fill = false)) // Centra cuando hay espacio

            // Footer
            Text(
                text = "© 2025 UCEVA - Todos los derechos reservados",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espacio inferior
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
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Tab Iniciar Sesión
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(25.dp))
                .background(if (!logRegis) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable { viewModel.log_regis.value = false },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Iniciar Sesión",
                color = if (!logRegis) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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
                .background(if (logRegis) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable { viewModel.log_regis.value = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Registrarse",
                color = if (logRegis) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = MaterialTheme.colorScheme.primary,
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
                        Text("Aceptar", color = MaterialTheme.colorScheme.primary)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
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
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val isValidatingRethus by viewModel.isValidatingRethus.observeAsState(initial = false)

    var contrasenaVisible by remember { mutableStateOf(false) }
    var esPersonalMedico by remember { mutableStateOf(false) }
    var tipoDocumentoSeleccionado by remember { mutableStateOf(tipoIdentificaciones.first().codigo) }
    var numeroDocumento by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

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
            color = MaterialTheme.colorScheme.onSurface
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = esPersonalMedico,
                onCheckedChange = { esPersonalMedico = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Soy personal médico",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
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
                label = "Numero de Documento",
                leadingIcon = Icons.Default.Badge,
                keyboardType = KeyboardType.Number
            )
        }

        // Loader de validación RETHUS
        if (isValidatingRethus) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Verificando en RETHUS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Validando credenciales de personal médico...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        registerError?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
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
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }


        // Checkbox de Protección de Datos Personales (Ley 1581 de 2012)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "He leído y acepto la Política de Tratamiento de Datos Personales",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Autorizo el tratamiento de mis datos de salud con fines de vigilancia epidemiológica del dengue, conforme a la Ley 1581 de 2012.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Leer Política Completa",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showPrivacyDialog = true }
                        )
                    }
                }
            }
        }

        if (!acceptedTerms) {
            Text(
                text = "Debe aceptar la Política de Tratamiento de Datos para continuar",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        ModernButton(
            text = "Registrarme",
            enabled = !isLoading && !isValidatingRethus && acceptedTerms,
            loading = isLoading,
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Diálogo de Política de Privacidad Completa
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = {
                    Text(
                        "Política de Tratamiento de Datos Personales",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Ley 1581 de 2012 - Protección de Datos Personales", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Finalidad del Tratamiento:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(text = "Los datos personales recolectados serán utilizados para:", fontSize = 12.sp, lineHeight = 18.sp)
                        Text(text = "• Vigilancia epidemiológica del dengue\n• Notificaciones sobre casos y prevención\n• Gestión de información de salud pública\n• Análisis estadísticos de salud", fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(start = 8.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Datos Recolectados:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(text = "Nombres, apellidos, correo, dirección, tipo de sangre, género, ubicación y datos de salud relacionados con dengue.", fontSize = 11.sp, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Sus Derechos:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(text = "Conforme a la Ley 1581 de 2012, usted tiene derecho a conocer, actualizar, rectificar y suprimir sus datos personales, así como revocar la autorización otorgada.", fontSize = 11.sp, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Medidas de Seguridad:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(text = "UCEVA implementa medidas técnicas y organizativas para proteger sus datos contra acceso no autorizado, pérdida o alteración.", fontSize = 11.sp, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Vigencia y Contacto:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(text = "Esta política está vigente desde su aceptación. Para ejercer sus derechos o consultas, contáctenos a través de la aplicación.", fontSize = 11.sp, lineHeight = 16.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
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
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary
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
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle visibility",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary
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
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
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

// Data class para tipos de identificación RETHUS
data class TipoIdentificacion(val nombre: String, val codigo: String)

// Lista de tipos de identificación según RETHUS
val tipoIdentificaciones = listOf(
    TipoIdentificacion("Cédula de Ciudadanía", "CC"),
    TipoIdentificacion("Cédula de Extranjería", "CE"),
    TipoIdentificacion("Pasaporte", "PA"),
    TipoIdentificacion("Permiso Especial de Permanencia", "PE")
)
