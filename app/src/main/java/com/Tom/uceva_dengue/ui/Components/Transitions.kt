package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset

/**
 * Transiciones para navegación entre pantallas
 */

// Transición de deslizamiento desde la derecha
fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeIn(animationSpec = tween(300))

fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeOut(animationSpec = tween(300))

// Transición de deslizamiento desde la izquierda
fun slideInFromLeft() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeIn(animationSpec = tween(300))

fun slideOutToRight() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeOut(animationSpec = tween(300))

// Transición de deslizamiento desde abajo
fun slideInFromBottom() = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
) + fadeIn(animationSpec = tween(400))

fun slideOutToBottom() = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
) + fadeOut(animationSpec = tween(400))

// Transición de zoom
fun zoomIn() = scaleIn(
    initialScale = 0.8f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
) + fadeIn(animationSpec = tween(400))

fun zoomOut() = scaleOut(
    targetScale = 0.8f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
) + fadeOut(animationSpec = tween(400))

// Transición de expansión
fun expandIn() = expandIn(
    expandFrom = Alignment.Center,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeIn(animationSpec = tween(300))

fun shrinkOut() = shrinkOut(
    shrinkTowards = Alignment.Center,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) + fadeOut(animationSpec = tween(300))

// Combinaciones útiles
object ScreenTransitions {
    val slideRightToLeft = slideInFromRight() togetherWith slideOutToLeft()
    val slideLeftToRight = slideInFromLeft() togetherWith slideOutToRight()
    val slideBottomToTop = slideInFromBottom() togetherWith slideOutToBottom()
    val zoomInOut = zoomIn() togetherWith zoomOut()
    val expandShrink = expandIn() togetherWith shrinkOut()
    val fadeInOut = fadeIn(tween(300)) togetherWith fadeOut(tween(300))
}

/**
 * Wrapper para aplicar transiciones a Composables
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TransitionWrapper(
    visible: Boolean,
    enter: EnterTransition = fadeIn() + slideInVertically(),
    exit: ExitTransition = fadeOut() + slideOutVertically(),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = enter,
        exit = exit,
        content = content
    )
}

/**
 * Efecto de parpadeo
 */
@Composable
fun BlinkingEffect(
    enabled: Boolean = true,
    durationMillis: Int = 1000,
    content: @Composable (alpha: Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")

    val alpha = if (enabled) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blinkAlpha"
        ).value
    } else {
        1f
    }

    content(alpha)
}

/**
 * Efecto de sacudida (shake)
 */
@Composable
fun rememberShakeController(): ShakeController {
    return androidx.compose.runtime.remember { ShakeController() }
}

class ShakeController {
    private val _trigger = androidx.compose.runtime.mutableStateOf(0)

    fun shake() {
        _trigger.value++
    }

    @Composable
    fun getOffset(): IntOffset {
        val trigger = _trigger.value

        val offsetX = androidx.compose.animation.core.animateFloatAsState(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                0f at 0
                (-10f) at 50
                10f at 100
                (-10f) at 150
                10f at 200
                (-5f) at 250
                5f at 300
                0f at 400
            },
            label = "shakeOffset"
        ).value

        return IntOffset(offsetX.toInt(), 0)
    }
}

/**
 * Efecto de ondulación (wave)
 */
@Composable
fun WaveEffect(
    enabled: Boolean = true,
    content: @Composable (scale: Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val scale = if (enabled) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "waveScale"
        ).value
    } else {
        1f
    }

    content(scale)
}

/**
 * Efecto de rotación continua
 */
@Composable
fun RotatingEffect(
    enabled: Boolean = true,
    durationMillis: Int = 2000,
    content: @Composable (rotation: Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")

    val rotation = if (enabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        ).value
    } else {
        0f
    }

    content(rotation)
}
