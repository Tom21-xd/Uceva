package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberWindowSize
import com.Tom.uceva_dengue.utils.WindowSize

/**
 * PLANTILLA DE PANTALLA RESPONSIVA
 *
 * Esta es una plantilla que puedes copiar y modificar para crear nuevas pantallas
 * o actualizar pantallas existentes con diseño responsivo.
 *
 * Características:
 * - Se adapta automáticamente a diferentes tamaños de pantalla
 * - Usa dimensiones responsivas en lugar de valores fijos
 * - Incluye ejemplos de componentes comunes
 */

@Composable
fun ResponsiveScreenTemplate() {
    // 1. SIEMPRE obtén las dimensiones al inicio del Composable principal
    val dimensions = rememberAppDimensions()
    val windowSize = rememberWindowSize()

    // 2. Define los colores del tema
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // 3. Estructura de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.paddingMedium), // Padding responsivo
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium) // Spacing responsivo
        ) {
            // Header Card
            item {
                HeaderCardExample(dimensions, primaryColor)
            }

            // Content Card
            item {
                ContentCardExample(dimensions, surfaceColor, onSurfaceColor)
            }

            // Form Example
            item {
                FormExample(dimensions)
            }

            // Button Example
            item {
                ButtonExample(dimensions, primaryColor)
            }

            // Layout condicional por tamaño de pantalla
            item {
                ConditionalLayoutExample(windowSize, dimensions)
            }
        }
    }
}

/**
 * Ejemplo de Card de Header con gradiente
 */
@Composable
private fun HeaderCardExample(
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(dimensions.cardElevation, RoundedCornerShape(dimensions.cardCornerRadius)),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = primaryColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(dimensions.iconExtraLarge),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Column {
                Text(
                    text = "Título Principal",
                    fontSize = dimensions.textSizeHeader,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Subtítulo descriptivo",
                    fontSize = dimensions.textSizeMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

/**
 * Ejemplo de Card de Contenido
 */
@Composable
private fun ContentCardExample(
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    surfaceColor: Color,
    onSurfaceColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(dimensions.cardElevation, RoundedCornerShape(dimensions.cardCornerRadius)),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
        ) {
            // Título de sección
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.iconLarge),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sección de Contenido",
                    fontSize = dimensions.textSizeLarge,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = dimensions.spacingSmall),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Items de lista
            repeat(3) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconMedium),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Item ${index + 1}",
                            fontSize = dimensions.textSizeMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = onSurfaceColor
                        )
                        Text(
                            text = "Descripción del item",
                            fontSize = dimensions.textSizeSmall,
                            color = onSurfaceColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ejemplo de Formulario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormExample(dimensions: com.Tom.uceva_dengue.utils.AppDimensions) {
    var textValue by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(dimensions.cardElevation, RoundedCornerShape(dimensions.cardCornerRadius)),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            Text(
                text = "Formulario Ejemplo",
                fontSize = dimensions.textSizeLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text("Campo de Texto", fontSize = dimensions.textSizeMedium) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconMedium),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.cardCornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

/**
 * Ejemplo de Botón
 */
@Composable
private fun ButtonExample(
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions,
    primaryColor: Color
) {
    Button(
        onClick = { /* Acción */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.buttonHeight)
            .shadow(dimensions.cardElevation, RoundedCornerShape(dimensions.buttonHeight / 2)),
        shape = RoundedCornerShape(dimensions.buttonHeight / 2),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Icon(
            Icons.Default.Send,
            contentDescription = null,
            modifier = Modifier.size(dimensions.iconMedium),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(dimensions.spacingSmall))
        Text(
            text = "Acción Principal",
            fontSize = dimensions.textSizeLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * Ejemplo de Layout Condicional según tamaño de pantalla
 */
@Composable
private fun ConditionalLayoutExample(
    windowSize: WindowSize,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(dimensions.cardElevation, RoundedCornerShape(dimensions.cardCornerRadius)),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        when (windowSize) {
            WindowSize.COMPACT -> {
                // Layout vertical para pantallas pequeñas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                ) {
                    Text(
                        text = "Modo Compacto",
                        fontSize = dimensions.textSizeLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Diseño vertical para teléfonos pequeños",
                        fontSize = dimensions.textSizeSmall
                    )
                    Icon(
                        Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconLarge)
                    )
                }
            }
            WindowSize.MEDIUM -> {
                // Layout con más espacio para pantallas medianas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                ) {
                    Text(
                        text = "Modo Mediano",
                        fontSize = dimensions.textSizeLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TabletAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(dimensions.iconExtraLarge)
                        )
                        Text(
                            text = "Diseño optimizado para teléfonos grandes",
                            fontSize = dimensions.textSizeMedium
                        )
                    }
                }
            }
            WindowSize.EXPANDED -> {
                // Layout horizontal para tablets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingExtraLarge),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Tablet,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconExtraLarge)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Expandido",
                            fontSize = dimensions.textSizeHeader,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Diseño horizontal aprovechando el espacio de tablets",
                            fontSize = dimensions.textSizeLarge
                        )
                    }
                }
            }
        }
    }
}

/**
 * GUÍA DE USO:
 *
 * 1. Copia esta plantilla y renómbrala según tu pantalla
 * 2. Reemplaza el contenido con tus componentes específicos
 * 3. SIEMPRE usa dimensions.* en lugar de valores fijos (dp, sp)
 * 4. Para layouts condicionales, usa when(windowSize)
 * 5. Prueba en diferentes tamaños de emulador
 *
 * Valores más comunes:
 * - Padding general: dimensions.paddingMedium
 * - Padding de cards: dimensions.paddingMedium o paddingLarge
 * - Spacing entre items: dimensions.spacingMedium
 * - Íconos pequeños: dimensions.iconSmall
 * - Íconos normales: dimensions.iconMedium
 * - Íconos grandes: dimensions.iconLarge
 * - Íconos muy grandes: dimensions.iconExtraLarge
 * - Texto pequeño: dimensions.textSizeSmall
 * - Texto normal: dimensions.textSizeMedium
 * - Texto grande: dimensions.textSizeLarge
 * - Títulos: dimensions.textSizeTitle
 * - Headers: dimensions.textSizeHeader
 * - Altura de botones: dimensions.buttonHeight
 * - Border radius de cards: dimensions.cardCornerRadius
 * - Elevación de cards: dimensions.cardElevation
 */
