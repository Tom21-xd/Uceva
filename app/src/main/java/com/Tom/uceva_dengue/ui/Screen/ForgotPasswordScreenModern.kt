package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.RecoverPasswordRequest
import com.Tom.uceva_dengue.R
import kotlinx.coroutines.launch
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize

@Composable
fun ForgotPasswordScreenModern(navController: NavController) {
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

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
        // Botón de volver
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(dimensions.paddingMedium)
                .size(dimensions.avatarSizeMedium)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(dimensions.paddingExtraLarge))
                .align(Alignment.TopStart)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
        ) {
            Spacer(modifier = Modifier.weight(1f, fill = false))
            // Card principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(dimensions.paddingMedium, RoundedCornerShape(dimensions.cardCornerRadiusLarge)),
                shape = RoundedCornerShape(dimensions.cardCornerRadiusLarge),
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
                    // Icono animado
                    Box(
                        modifier = Modifier
                            .size(dimensions.overlayHeight)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(dimensions.progressIndicatorSize)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(dimensions.progressIndicatorSize),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensions.paddingExtraLarge))

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = dimensions.fontSizeExtraExtraLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensions.iconBackgroundRadius))

                    Text(
                        text = "No te preocupes, ingresa tu correo electrónico y te enviaremos una nueva contraseña temporal.",
                        fontSize = dimensions.fontSizeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = dimensions.lineHeightSmall
                    )

                    Spacer(modifier = Modifier.height(dimensions.paddingExtraExtraLarge))

                    // Campo de email
                    ModernEmailField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        isError = emailError != null,
                        errorMessage = emailError,
                        dimensions = dimensions
                    )

                    Spacer(modifier = Modifier.height(dimensions.paddingExtraExtraLarge))

                    // Botón de recuperación
                    RecoveryButton(
                        enabled = email.isNotBlank() && !isLoading,
                        loading = isLoading,
                        dimensions = dimensions,
                        onClick = {
                            when {
                                email.isBlank() -> {
                                    emailError = "El correo es requerido"
                                }
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                    emailError = "Correo electrónico inválido"
                                }
                                else -> {
                                    scope.launch {
                                        isLoading = true
                                        try {
                                            val service = RetrofitClient.authService
                                            val request = RecoverPasswordRequest(email)
                                            val response = service.recoverPassword(request)

                                            if (response.isSuccessful) {
                                                showSuccessDialog = true
                                            } else {
                                                errorMessage = "No se encontró una cuenta con ese correo electrónico"
                                                showErrorDialog = true
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Error de conexión. Por favor, intenta nuevamente."
                                            showErrorDialog = true
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                    // Botón de volver
                    TextButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(dimensions.fontSizeLarge)
                            )
                            Spacer(modifier = Modifier.width(dimensions.paddingExtraSmall))
                            Text(
                                "Volver al inicio de sesión",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = dimensions.fontSizeMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensions.paddingExtraLarge))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(dimensions.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(dimensions.spacerMedium))
                    Text(
                        text = "La contraseña temporal llegará a tu correo en pocos minutos. Recuerda cambiarla después de iniciar sesión.",
                        fontSize = dimensions.fontSizeSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = dimensions.lineHeightSmall
                    )
                }
            }
        }

        // Diálogo de éxito moderno
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    navController.navigateUp()
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimensions.paddingExtraLarge),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(dimensions.bottomPadding)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(dimensions.dialogIconSize)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(dimensions.avatarSizeMedium)
                        )
                    }
                },
                title = {
                    Text(
                        "¡Correo Enviado!",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimensions.fontSizeExtraLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        "Hemos enviado una nueva contraseña temporal a tu correo electrónico. Por favor, revisa tu bandeja de entrada.",
                        textAlign = TextAlign.Center,
                        fontSize = dimensions.fontSizeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = dimensions.lineHeightSmall
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigateUp()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.progressIndicatorSize),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(dimensions.buttonCornerRadius)
                    ) {
                        Text(
                            "Entendido",
                            fontWeight = FontWeight.Bold,
                            fontSize = dimensions.fontSizeMedium
                        )
                    }
                }
            )
        }

        // Diálogo de error moderno
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimensions.paddingExtraLarge),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(dimensions.bottomPadding)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(dimensions.dialogIconSize)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(dimensions.avatarSizeMedium)
                        )
                    }
                },
                title = {
                    Text(
                        "Error",
                        fontWeight = FontWeight.Bold,
                        fontSize = dimensions.fontSizeExtraLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        errorMessage,
                        textAlign = TextAlign.Center,
                        fontSize = dimensions.fontSizeMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = dimensions.lineHeightSmall
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showErrorDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Intentar nuevamente",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = dimensions.fontSizeMedium
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Correo Electrónico", fontSize = dimensions.fontSizeMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensions.iconBackgroundRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorLabelColor = MaterialTheme.colorScheme.error
            ),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
            )
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Row(
                modifier = Modifier.padding(start = dimensions.paddingMedium, top = dimensions.paddingExtraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(dimensions.fontSizeMedium)
                )
                Spacer(modifier = Modifier.width(dimensions.paddingExtraSmall))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = dimensions.fontSizeSmall
                )
            }
        }
    }
}

@Composable
fun RecoveryButton(
    enabled: Boolean,
    loading: Boolean,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
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
            .height(dimensions.buttonHeightLarge)
            .alpha(animatedAlpha)
            .shadow(if (enabled && !loading) dimensions.paddingSmall else 0.dp, RoundedCornerShape(dimensions.buttonCornerRadius)),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(dimensions.buttonCornerRadius),
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
                    modifier = Modifier.size(dimensions.iconSizeSmall),
                    color = Color.White,
                    strokeWidth = dimensions.elevationSmall
                )
                Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                Text(
                    text = "Enviando...",
                    color = Color.White,
                    fontSize = dimensions.fontSizeMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                Text(
                    text = "Enviar Contraseña",
                    color = Color.White,
                    fontSize = dimensions.fontSizeMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
