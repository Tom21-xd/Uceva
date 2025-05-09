package  com.Tom.uceva_dengue.ui.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.Components.Campo
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.theme.fondo
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen (viewModel: AuthViewModel, navController: NavController){
    Box(Modifier.background(color = fondo)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .clickable { viewModel.log_regis.value = false }
                            .padding(10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.34f),
                        thickness = 2.dp,
                        color = Color(0xFF000000)
                    )
                }

                Spacer(modifier = Modifier.width(25.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .clickable { viewModel.log_regis.value = true }
                            .padding(10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.66f),
                        thickness = 2.dp,
                        color = Color(0xFF000000)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Permite distribuir el espacio y evitar solapamiento
            ) {
                val logRegis: Boolean by viewModel.log_regis.observeAsState(initial = false)

                this@Column.AnimatedVisibility(visible = !logRegis) {
                    Login(Modifier.align(Alignment.Center), viewModel, navController)
                }
                this@Column.AnimatedVisibility(visible = logRegis) {
                    Registro(Modifier.align(Alignment.Center), viewModel, navController)
                }
            }
        }
    }

}

@Composable
fun Login(modifier: Modifier, viewModel: AuthViewModel, navController: NavController) {
    val correo: String by viewModel.correo.observeAsState(initial = "")
    val contra: String by viewModel.contra.observeAsState(initial = "")
    val loginEnabled: Boolean by viewModel.loginEnabled.observeAsState(initial = false)
    val contravisible: Boolean by viewModel.contravisible.observeAsState(initial = false)
    val loading: Boolean by viewModel.loading.observeAsState(initial = false)
    val loginError: String? by viewModel.loginError.observeAsState()

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(16.dp))

            CampoCorreo(correo) { viewModel.onLoginChange(it, contra) }
            Spacer(modifier = Modifier.height(8.dp))

            CampoContra(
                contra = contra,
                contravisible = contravisible,
                onTextFieldChanged = { viewModel.onLoginChange(correo, it) },
                onToggleVisibility = { viewModel.onContraVisibilityChange(!contravisible) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            BotonInicio(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                loginEnabled = loginEnabled,
                loading = loading,
                onClick = {
                    viewModel.iniciosesioncorreo(correo, contra) {
                        navController.navigate(Rout.HomeScreen.name)
                    }
                }
            )
        }

        OlvContra(Modifier.align(Alignment.BottomCenter))

        if (!loginError.isNullOrEmpty()) {
            AlertDialog(
                onDismissRequest = { viewModel.clearLoginError() },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearLoginError() }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Error de inicio de sesión") },
                text = { Text(loginError ?: "Error desconocido") }
            )
        }
    }
}
@Composable
fun BotonInicio(
    modifier: Modifier,
    loginEnabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(200.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1E3DA)),
        enabled = loginEnabled && !loading
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
        } else {
            Text(text = "Iniciar sesión", color = Color(0xFF000000))
        }
    }
}


@Composable
fun OlvContra(modifier: Modifier) {
    Text(
        text = "¿Olvidaste la contraseña?",
        modifier = modifier.clickable {},
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF666057)
    )
}


@Composable
fun CampoContra(contra: String, contravisible: Boolean, onTextFieldChanged: (String) -> Unit, onToggleVisibility: () -> Unit) {
    val visualTransformation = if (contravisible) VisualTransformation.None else PasswordVisualTransformation()

    OutlinedTextField(
        value = contra,
        onValueChange = onTextFieldChanged,
        label = { Text("Contraseña") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")
        },
        trailingIcon = {
            IconButton(onClick = { onToggleVisibility() }) {
                Icon(
                    imageVector = if (contravisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Alternar visibilidad"
                )
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun Contravisibleicono(viewModel: AuthViewModel, contravisible: Boolean) {
    val image =
        if (viewModel.contravisible.value == true){
            Icons.Default.VisibilityOff
        } else {
            Icons.Default.Visibility
        }
    IconButton(onClick = {viewModel.onContraVisibilityChange(!contravisible) }){
        Icon(imageVector = image, contentDescription ="a" )

    }
}

@Composable
fun CampoCorreo(correo: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = correo,
        onValueChange = onTextFieldChanged,
        label = { Text("Correo electrónico") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(painter = painterResource(id = R.drawable.salud ), contentDescription = "Header" ,modifier=modifier)
}
/*-----------------------------------------------Registro---------------------------------------------------------*/

data class TipoDocumento(val nombre: String, val codigo: String)

val tipoIdentificaciones = listOf(
    TipoDocumento("Cedula de Ciudadania", "CC"),
    TipoDocumento("Cedula de Extranjeria", "CE"),
    TipoDocumento("Permiso por protección temporal", "PT"),
    TipoDocumento("Tarjeta de Identidad", "TI")
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(modifier: Modifier, viewModel: AuthViewModel, navController: NavController) {

    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()
    val generos by viewModel.generos.collectAsState()
    val tiposSangre by viewModel.tiposSangre.collectAsState()

    val firstName: String by viewModel.firstName.observeAsState(initial = "")
    val lastName: String by viewModel.lastName.observeAsState(initial = "")
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val address: String by viewModel.address.observeAsState(initial = "")
    val genderId: Int by viewModel.genderId.observeAsState(initial = 0)
    val genderName: String by viewModel.genderName.observeAsState(initial = "")
    val bloodTypeId: Int by viewModel.bloodTypeId.observeAsState(initial = 0)
    val bloodTypeName: String by viewModel.bloodTypeName.observeAsState(initial = "")
    val cityId: Int by viewModel.cityId.observeAsState(initial = 0)
    val cityName: String by viewModel.cityName.observeAsState(initial = "")
    val department: String by viewModel.department.observeAsState(initial = "")

    // Tipo de documento
    var tipoDocumentoSeleccionado by remember { mutableStateOf(tipoIdentificaciones.first().codigo) }
    var numeroDocumento by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var esPersonalMedico by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registro", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            // Campos de texto
            Campo(firstName, "Nombres") {
                viewModel.onRegisterChange(email, password, it, lastName, bloodTypeId, cityId, address, genderId)
            }
            Campo(lastName, "Apellidos") {
                viewModel.onRegisterChange(email, password, firstName, it, bloodTypeId, cityId, address, genderId)
            }
            Campo(email, "Correo") {
                viewModel.onRegisterChange(it, password, firstName, lastName, bloodTypeId, cityId, address, genderId)
            }
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.onRegisterChange(email, it, firstName, lastName, bloodTypeId, cityId, address, genderId)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                        Icon(
                            imageVector = if (contrasenaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                }
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


            // Personal Médico
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = esPersonalMedico,
                    onCheckedChange = { esPersonalMedico = it }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Personal Médico", fontSize = 14.sp, color = Color.Black)
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

                Campo(numeroDocumento, "Número de Documento") {
                    numeroDocumento = it
                }
            }

            // Botón de Registro
            Button(
                onClick = {
                    viewModel.registrarUsuario(
                        navController,
                        esPersonalMedico,
                        tipoIdentificacion = tipoDocumentoSeleccionado,
                        numeroDocumento = numeroDocumento
                    )
                },
                modifier = Modifier.fillMaxWidth(0.9f).height(48.dp),
                enabled = true
            ) {
                Text("Registrarme")
            }
        }
    }
}
