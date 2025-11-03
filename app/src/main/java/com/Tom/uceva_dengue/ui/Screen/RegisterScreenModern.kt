package com.Tom.uceva_dengue.ui.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenModern(viewModel: AuthViewModel, navController: NavController) {
    val dimensions = rememberAppDimensions()
    var isLoading by remember { mutableStateOf(true) }
    var showContent by remember { mutableStateOf(false) }

    // Efecto de carga inicial
    LaunchedEffect(Unit) {
        delay(300) // Pequeño delay para dar tiempo a la transición de navegación
        isLoading = false
        delay(50)
        showContent = true
    }

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
        // Loader inicial
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 4.dp
                )
            }
        }

        // Contenido principal con animación
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Card contenedor con diseño moderno
                Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(dimensions.cardElevation, RoundedCornerShape(dimensions.cardCornerRadius)),
                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo y título
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    Text(
                        text = "Crear Cuenta",
                        fontSize = dimensions.textSizeTitle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                    Text(
                        text = "Únete al Sistema de Monitoreo de Dengue",
                        fontSize = dimensions.textSizeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingLarge))

                    // Formulario de registro
                    ModernRegisterForm(viewModel, navController)
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            // Enlace para volver al login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión aquí",
                    fontSize = dimensions.textSizeMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            // Footer
            Text(
                text = "© 2025 UCEVA - Todos los derechos reservados",
                fontSize = dimensions.textSizeSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernRegisterForm(viewModel: AuthViewModel, navController: NavController) {
    val dimensions = rememberAppDimensions()

    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()
    val generos by viewModel.generos.collectAsState()
    val tiposSangre by viewModel.tiposSangre.collectAsState()

    // Estados del formulario
    val firstName by viewModel.firstName.observeAsState(initial = "")
    val lastName by viewModel.lastName.observeAsState(initial = "")
    val email by viewModel.email.observeAsState(initial = "")
    val password by viewModel.password.observeAsState(initial = "")
    val address by viewModel.address.observeAsState(initial = "")
    val birthDate by viewModel.birthDate.observeAsState(initial = "")
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

    // Estados locales
    var contrasenaVisible by remember { mutableStateOf(false) }
    var esPersonalMedico by remember { mutableStateOf(false) }
    var tipoDocumentoSeleccionado by remember { mutableStateOf(tipoIdentificaciones.first().codigo) }
    var numeroDocumento by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        ModernTextField(
            value = firstName,
            onValueChange = {
                viewModel.onRegisterChange(email, password, it, lastName, bloodTypeId, cityId, address, genderId, birthDate)
            },
            label = "Nombres",
            leadingIcon = Icons.Default.Person
        )

        ModernTextField(
            value = lastName,
            onValueChange = {
                viewModel.onRegisterChange(email, password, firstName, it, bloodTypeId, cityId, address, genderId, birthDate)
            },
            label = "Apellidos",
            leadingIcon = Icons.Default.Person
        )

        ModernTextField(
            value = email,
            onValueChange = {
                viewModel.onRegisterChange(it, password, firstName, lastName, bloodTypeId, cityId, address, genderId, birthDate)
            },
            label = "Correo Electrónico",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        ModernPasswordField(
            value = password,
            onValueChange = {
                viewModel.onRegisterChange(email, it, firstName, lastName, bloodTypeId, cityId, address, genderId, birthDate)
            },
            label = "Contraseña",
            passwordVisible = contrasenaVisible,
            onToggleVisibility = { contrasenaVisible = !contrasenaVisible }
        )

        ModernTextField(
            value = address,
            onValueChange = {
                viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, it, genderId, birthDate)
            },
            label = "Dirección",
            leadingIcon = Icons.Default.Home
        )

        // Campo de Fecha de Nacimiento
        OutlinedTextField(
            value = birthDate,
            onValueChange = { },
            label = { Text("Fecha de Nacimiento", fontSize = 14.sp) },
            readOnly = true,
            leadingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Seleccionar fecha", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // DatePicker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val formattedDate = dateFormat.format(java.util.Date(millis))
                            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, address, genderId, formattedDate)
                        }
                        showDatePicker = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        ComboBox(
            selectedValue = bloodTypeName,
            options = tiposSangre.map { it.NOMBRE_TIPOSANGRE },
            label = "Tipo de Sangre"
        ) { seleccion ->
            val selectedBloodType = tiposSangre.firstOrNull { it.NOMBRE_TIPOSANGRE == seleccion }
            val id = selectedBloodType?.ID_TIPOSANGRE ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, id, cityId, address, genderId, birthDate)
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
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, idCiudad, address, genderId, birthDate)
            viewModel.setCityName(nuevaCiudad)
        }

        ComboBox(
            selectedValue = genderName,
            options = generos.map { it.NOMBRE_GENERO },
            label = "Género"
        ) { seleccion ->
            val selectedGenero = generos.firstOrNull { it.NOMBRE_GENERO == seleccion }
            val id = selectedGenero?.ID_GENERO ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, address, id, birthDate)
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
