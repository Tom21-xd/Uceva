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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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

    val correo : String by viewModel.correo.observeAsState(initial = "")
    val contra : String by viewModel.contra.observeAsState(initial = "")
    val loginEneable : Boolean by viewModel.loginEnabled.observeAsState(initial = false)
    val contravisible : Boolean by viewModel.contravisible.observeAsState(initial = false)

    Box(modifier = modifier.fillMaxSize()){

        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(10.dp))
            CampoCorreo(correo) { viewModel.onLoginChange(it, contra) }
            Spacer(modifier = Modifier.padding(5.dp))
            CampoContra(contra,viewModel,contravisible) { viewModel.onLoginChange(correo, it) }
            Spacer(modifier = Modifier.padding(15.dp))
            BotonInicio(Modifier.align(Alignment.CenterHorizontally),loginEneable,viewModel,correo,contra,navController)
        }
        OlvContra(Modifier.align(Alignment.BottomCenter))
    }

}

@Composable
fun BotonInicio(
    modifier: Modifier,
    loginEnabled: Boolean,
    viewModel: AuthViewModel,
    correo: String,
    contra: String,
    navController: NavController
) {
    val loading by viewModel.loading.observeAsState(initial = false)

    Button(
        onClick = { viewModel.iniciosesioncorreo(correo, contra) { navController.navigate(Rout.HomeScreen.name) } },
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
fun CampoContra(contra:String,  viewModel: AuthViewModel,contravisible: Boolean , onTextFieldChanged: (String) -> Unit) {

    val transformacionvisual = if (viewModel.contravisible.value == true){
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    OutlinedTextField(value = contra,
        onValueChange = { onTextFieldChanged(it)},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Contraseña") } ,
        visualTransformation = transformacionvisual,
        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Password),
        singleLine = true,
        trailingIcon = { Contravisibleicono(viewModel,contravisible) },
        maxLines = 1)
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
fun CampoCorreo(correo:String , onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(value = correo,
        onValueChange = {onTextFieldChanged(it)},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Correo") } ,
        keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Email),
        singleLine = true,
        maxLines = 1)
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(painter = painterResource(id = R.drawable.salud ), contentDescription = "Header" ,modifier=modifier)
}
/*-----------------------------------------------Registro---------------------------------------------------------*/


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(modifier: Modifier, viewModel:AuthViewModel, navController: NavController) {

    val state = rememberDatePickerState()
    var mostrarDate by remember { mutableStateOf(false) }

    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()
    val generos by viewModel.generos.collectAsState()

    val nombres: String by viewModel.nombres.observeAsState(initial = "")
    val correo: String by viewModel.CorreoR.observeAsState(initial = "")
    val contra: String by viewModel.contra.observeAsState(initial = "")
    val confirmacionContra: String by viewModel.confirmacionContra.observeAsState(initial = "")
    val apellidos: String by viewModel.apellidos.observeAsState(initial = "")
    val departamento: String by viewModel.departamento.observeAsState(initial = "Seleccione")
    val ciudadId: Int by viewModel.ciudadId.observeAsState(initial = 0)
    val ciudadNombre: String by viewModel.ciudadNombre.observeAsState(initial = "")

    val direccion: String by viewModel.direccion.observeAsState(initial = "")
    val personalMedico: Boolean by viewModel.personalMedico.observeAsState(initial = false)
    val profesion: String by viewModel.profesion.observeAsState(initial = "")
    val especialidadMedica: String by viewModel.especialidadMedica.observeAsState(initial = "")
    val registroMedico: String by viewModel.registroMedico.observeAsState(initial = "")
    val generoId: Int by viewModel.generoId.observeAsState(initial = 0)
    val generoNombre: String by viewModel.generoNombre.observeAsState(initial = "")

    val fechaNacimiento: String by viewModel.fechaNacimiento.observeAsState(initial = "")

    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmacionContrasenaVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Acerca de ti",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Campo(nombres, "Nombres") {
                            viewModel.OnRegisterChange(correo, contra, confirmacionContra, it, apellidos, departamento, ciudadId, direccion, personalMedico, profesion, especialidadMedica, registroMedico, generoId, fechaNacimiento)
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Campo(apellidos, "Apellidos") {
                            viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, it, departamento, ciudadId, direccion, personalMedico, profesion, especialidadMedica, registroMedico, generoId, fechaNacimiento)
                        }
                    }
                }

                Campo(correo, "Correo") {
                    viewModel.OnRegisterChange(it, contra, confirmacionContra, nombres, apellidos, departamento, ciudadId, direccion, personalMedico, profesion, especialidadMedica, registroMedico, generoId, fechaNacimiento)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = contra,
                            onValueChange = { viewModel.OnRegisterChange(correo, it, confirmacionContra, nombres, apellidos, departamento, ciudadId, direccion, personalMedico, profesion, especialidadMedica, registroMedico, generoId, fechaNacimiento) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Contraseña") },
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

                }
                Row(modifier = Modifier.fillMaxWidth()){
                    OutlinedTextField(
                        value = confirmacionContra,
                        onValueChange = { viewModel.OnRegisterChange(correo, contra, it, nombres, apellidos, departamento, ciudadId, direccion, personalMedico, profesion, especialidadMedica, registroMedico, generoId, fechaNacimiento) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirmacion") },
                        singleLine = true,
                        visualTransformation = if (confirmacionContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmacionContrasenaVisible = !confirmacionContrasenaVisible }) {
                                Icon(
                                    imageVector = if (confirmacionContrasenaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Mostrar/Ocultar contraseña"
                                )
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        ComboBox(selectedValue = generoNombre, options = generos.map { it.NOMBRE_GENERO }, label = "Género") {
                            val selectedGenero = generos.find { it.NOMBRE_GENERO == generoNombre}
                            val opc = selectedGenero?.ID_GENERO ?: 0
                            viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, departamento, ciudadId, direccion, personalMedico,profesion,especialidadMedica,registroMedico,
                                opc as Int, fechaNacimiento)

                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = fechaNacimiento,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(text = "Fecha Nac") },
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { mostrarDate = true }) {
                                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                                }
                            }
                        )
                    }
                }
                if (mostrarDate) {
                    SeleccionarFecha(
                        onDismiss = { mostrarDate = false },
                        onConfirm = {
                            val nuevaFecha = state.selectedDateMillis
                            nuevaFecha?.let {
                                val fechaFormateada = Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                                viewModel.OnRegisterChange(
                                    correo, contra, confirmacionContra, nombres, apellidos,
                                    departamento, ciudadId, direccion, personalMedico,
                                    profesion, especialidadMedica, registroMedico, generoId, fechaFormateada
                                )
                            }

                            mostrarDate = false
                        },
                        state = state
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "¿Dónde vives?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        ComboBox(
                            selectedValue = departamento,
                            options = if (departamentos.isNotEmpty()) departamentos.map { it.NOMBRE_DEPARTAMENTO } else listOf("Cargando..."),
                            label = "Departamento",
                            enabled = departamentos.isNotEmpty()
                        ) { nuevoDepartamento ->
                            viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, nuevoDepartamento, ciudadId, direccion, personalMedico,profesion,especialidadMedica,registroMedico, generoId, fechaNacimiento)
                            val departamentoSeleccionado = departamentos.firstOrNull { it.NOMBRE_DEPARTAMENTO == nuevoDepartamento }
                            departamentoSeleccionado?.ID_DEPARTAMENTO?.let { viewModel.fetchMunicipios(it.toString()) }
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ComboBox(
                            selectedValue = ciudadNombre,
                            options = if (ciudades.isNotEmpty()) ciudades.map { it.NOMBRE_CIUDAD ?: "" } else emptyList(),
                            label = "Municipio",
                            enabled = departamento.isNotEmpty() && ciudades.isNotEmpty()
                        ) { nuevaCiudad ->
                            val ciudadSeleccionada = ciudades.firstOrNull { it.NOMBRE_CIUDAD == nuevaCiudad }
                            val ciudadId = ciudadSeleccionada?.ID_CIUDAD ?: 0
                            viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, departamento, ciudadId as Int, direccion, personalMedico,profesion,especialidadMedica,registroMedico, generoId, fechaNacimiento)
                        }
                    }
                }

                Campo(direccion, "Dirección") { viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, departamento, ciudadId, it, personalMedico,profesion,especialidadMedica,registroMedico, generoId, fechaNacimiento) }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = personalMedico,
                        onCheckedChange = { viewModel.OnRegisterChange(correo, contra, confirmacionContra, nombres, apellidos, departamento, ciudadId, direccion, it, profesion, especialidadMedica, registroMedico, generoId, fechaNacimiento) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Personal médico", fontSize = 14.sp, color = Color.Black)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isLoading by viewModel.isLoading.observeAsState(initial = false)

                Button(
                    onClick = { viewModel.registrarUsuario(navController) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1E3DA)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Registrarme", color = Color(0xFF000000))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarFecha(onDismiss: () -> Unit, onConfirm: () -> Unit, state: DatePickerState) {
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Aceptar")
            }
        }
    ) {
        DatePicker(state = state)
    }
}


