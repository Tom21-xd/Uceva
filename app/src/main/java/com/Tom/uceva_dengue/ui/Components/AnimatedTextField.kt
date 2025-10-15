package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * TextField animado con efectos de focus
 */
@Composable
fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> Color(0xFFE53935)
            isFocused -> Color(0xFF5E81F4)
            else -> Color(0xFFE0E0E0)
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    val labelColor by animateColorAsState(
        targetValue = when {
            isError -> Color(0xFFE53935)
            isFocused -> Color(0xFF5E81F4)
            else -> Color(0xFF757575)
        },
        animationSpec = tween(300),
        label = "labelColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .onFocusChanged { isFocused = it.isFocused },
            label = if (label.isNotEmpty()) {
                { Text(label, color = labelColor) }
            } else null,
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = Color(0xFF9E9E9E)) }
            } else null,
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null, tint = labelColor) }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5E81F4),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = Color(0xFFE53935),
                focusedLabelColor = Color(0xFF5E81F4),
                unfocusedLabelColor = Color(0xFF757575),
                cursorColor = Color(0xFF5E81F4)
            )
        )

        // Mensaje de error animado
        androidx.compose.animation.AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            Text(
                text = errorMessage ?: "",
                color = Color(0xFFE53935),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * TextField con efecto de brillo al escribir
 */
@Composable
fun GlowingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    leadingIcon: ImageVector? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(modifier = modifier) {
        if (isFocused && value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 2.dp,
                        color = Color(0xFF5E81F4).copy(alpha = glowAlpha),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            label = { Text(label) },
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null) }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5E81F4),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )
    }
}

/**
 * TextField con contador de caracteres animado
 */
@Composable
fun TextFieldWithCounter(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    maxLength: Int = 100,
    minLines: Int = 3
) {
    val progress = value.length.toFloat() / maxLength
    val counterColor by animateColorAsState(
        targetValue = when {
            progress >= 0.9f -> Color(0xFFE53935)
            progress >= 0.7f -> Color(0xFFFFA000)
            else -> Color(0xFF757575)
        },
        animationSpec = tween(300),
        label = "counterColor"
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5E81F4),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${value.length}/$maxLength",
                style = MaterialTheme.typography.bodySmall,
                color = counterColor
            )
        }
    }
}
