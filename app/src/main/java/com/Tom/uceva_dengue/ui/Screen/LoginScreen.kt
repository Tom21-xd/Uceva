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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
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
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen (viewModel: AuthViewModel, navController: NavController){
    Box(Modifier.background(color = MaterialTheme.colorScheme.background)) {
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
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .clickable { viewModel.log_regis.value = false }
                            .padding(10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.34f),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(25.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .clickable { viewModel.log_regis.value = true }
                            .padding(10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.66f),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp), // Espaciado para evitar superposición con menú inferior
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeaderImage(Modifier.size(120.dp))
        Spacer(modifier = Modifier.height(24.dp))

        CampoCorreo(correo) { viewModel.onLoginChange(it, contra) }
        Spacer(modifier = Modifier.height(16.dp))

        CampoContra(
            contra = contra,
            contravisible = contravisible,
            onTextFieldChanged = { viewModel.onLoginChange(correo, it) },
            onToggleVisibility = { viewModel.onContraVisibilityChange(!contravisible) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OlvContra(Modifier.padding(vertical = 8.dp))
        Spacer(modifier = Modifier.height(16.dp))

        BotonInicio(
            modifier = Modifier,
            loginEnabled = loginEnabled,
            loading = loading,
            onClick = {
                viewModel.iniciosesioncorreo(correo, contra) {
                    navController.navigate(Rout.HomeScreen.name)
                }
            }
        )

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
    val animatedAlpha = androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (loginEnabled && !loading) 1f else 0.5f,
        animationSpec = androidx.compose.animation.core.tween(300),
        label = "alpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .width(220.dp)
            .height(52.dp)
            .alpha(animatedAlpha.value),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        enabled = loginEnabled && !loading,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = loading,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Cargando...", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }

        androidx.compose.animation.AnimatedVisibility(
            visible = !loading,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
        ) {
            Text(
                text = "Iniciar sesión",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
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
        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val registerMessage: String? by viewModel.registerMessage.observeAsState()

    val medicalPersonnelData = remember { mutableStateOf(com.Tom.uceva_dengue.ui.Components.MedicalPersonnelData(false, "", "")) }

    Box(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            com.Tom.uceva_dengue.ui.Components.RegistrationForm(
                viewModel = viewModel,
                showMedicalPersonnelOption = true,
                showTitle = true,
                medicalPersonnelData = medicalPersonnelData
            )

            // Botón de Registro
            Button(
                onClick = {
                    viewModel.registrarUsuario(
                        navController,
                        medicalPersonnelData.value.isMedicalPersonnel,
                        tipoIdentificacion = medicalPersonnelData.value.documentType,
                        numeroDocumento = medicalPersonnelData.value.documentNumber
                    )
                },
                modifier = Modifier.fillMaxWidth(0.9f).height(48.dp),
                enabled = true
            ) {
                Text("Registrarme")
            }

            // Mostrar mensaje de éxito en texto (sin diálogo)
            registerMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
