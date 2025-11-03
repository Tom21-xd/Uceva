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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.R
import com.Tom.uceva_dengue.ui.Components.Campo
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel
import com.Tom.uceva_dengue.utils.WindowSize
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreenModern(viewModel: AuthViewModel, navController: NavController) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()

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
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                    Text(
                        text = "Bienvenido",
                        fontSize = dimensions.textSizeTitle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                    Text(
                        text = "Sistema de Monitoreo de Dengue",
                        fontSize = dimensions.textSizeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingLarge))

                    // Login form
                    ModernLogin(viewModel, navController)
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            // Footer
            Text(
                text = "© 2025 UCEVA - Todos los derechos reservados",
                fontSize = dimensions.textSizeSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

// ModernTabs removed - tabs no longer needed as register is now a separate screen

@Composable
fun ModernLogin(viewModel: AuthViewModel, navController: NavController) {
    val dimensions = rememberAppDimensions()
    val context = LocalContext.current
    // val activity = remember(context) { context.findFragmentActivity() }

    // Estados existentes
    val correo by viewModel.correo.observeAsState(initial = "")
    val contra by viewModel.contra.observeAsState(initial = "")
    val loginEnabled by viewModel.loginEnabled.observeAsState(initial = false)
    val contravisible by viewModel.contravisible.observeAsState(initial = false)
    val loading by viewModel.loading.observeAsState(initial = false)
    val loginError by viewModel.loginError.observeAsState()

    // NUEVO: Estados para biometría - COMENTADO TEMPORALMENTE (falta implementación)
    /*
    var rememberWithBiometric by remember { mutableStateOf(false) }
    val biometricAuthenticator = remember { BiometricAuthenticator(context) }
    val authRepository = remember { AuthRepository(context) }
    val biometricStatus = remember { biometricAuthenticator.canAuthenticate() }
    var showBiometricError by remember { mutableStateOf(false) }
    var biometricErrorMessage by remember { mutableStateOf("") }

    // NUEVO: Detectar si hay biometría habilitada al abrir la pantalla
    LaunchedEffect(Unit) {
        val biometricEnabled = authRepository.isBiometricEnabled()
        val savedEmail = authRepository.getSavedEmail()
        val refreshToken = authRepository.getRefreshToken()

        if (biometricEnabled &&
            savedEmail != null &&
            refreshToken != null &&
            biometricStatus == BiometricAuthStatus.READY &&
            activity != null) {

            // Mostrar prompt biométrico automáticamente
            biometricAuthenticator.authenticate(
                activity = activity,
                title = "Inicio de sesión rápido",
                subtitle = "Usa tu huella o PIN para continuar",
                onSuccess = {
                    // Autenticación exitosa, usar refresh token
                    viewModel.loginWithRefreshToken(
                        refreshToken = refreshToken,
                        onSuccess = {
                            navController.navigate(Rout.HomeScreen.name) {
                                popUpTo(Rout.LoginScreen.name) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            biometricErrorMessage = error
                            showBiometricError = true
                        }
                    )
                },
                onError = { code, message ->
                    // Usuario canceló o error biométrico - mostrar login normal
                    if (code != androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        code != androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED) {
                        biometricErrorMessage = "Error biométrico: $message"
                        showBiometricError = true
                    }
                }
            )
        }
    }
    */

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
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

        // NUEVO: Checkbox de biometría (solo si está disponible) - COMENTADO
        /* if (biometricStatus == BiometricAuthStatus.READY) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberWithBiometric,
                    onCheckedChange = { rememberWithBiometric = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Recordar con huella/PIN",
                        fontSize = dimensions.textSizeMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } */

        // Olvidaste contraseña
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = dimensions.textSizeMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    navController.navigate(Rout.OlvContraseniaScreen.name)
                }
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

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

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

        // Enlace a registro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                fontSize = dimensions.textSizeMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    navController.navigate(Rout.RegisterScreen.name)
                }
            )
        }

        // NUEVO: Error de biometría - COMENTADO
        /* if (showBiometricError) {
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
                        text = biometricErrorMessage,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        } */

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
                            modifier = Modifier.size(dimensions.iconLarge)
                        )
                        Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                        Text("Error de inicio de sesión", fontSize = dimensions.textSizeLarge)
                    }
                },
                text = { Text(error, fontSize = dimensions.textSizeMedium) },
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            )
        }
    }
}


// ModernRegister moved to RegisterScreenModern.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val dimensions = rememberAppDimensions()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = dimensions.textSizeMedium) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconMedium)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
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
    val dimensions = rememberAppDimensions()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = dimensions.textSizeMedium) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconMedium)
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle visibility",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimensions.iconMedium)
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
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
    val dimensions = rememberAppDimensions()
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled && !loading) 1f else 0.5f,
        animationSpec = tween(300),
        label = "button_alpha"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.buttonHeight)
            .alpha(animatedAlpha)
            .shadow(if (enabled) dimensions.cardElevation else 0.dp, RoundedCornerShape(dimensions.buttonHeight / 2)),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(dimensions.buttonHeight / 2),
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
                    modifier = Modifier.size(dimensions.iconMedium),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                Text(
                    text = "Cargando...",
                    color = Color.White,
                    fontSize = dimensions.textSizeLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = dimensions.textSizeLarge,
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
