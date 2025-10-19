package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animación de explosión de corazones/partículas al dar reacción
 */
@Composable
fun ReactionExplosionAnimation(
    isVisible: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var particles by remember { mutableStateOf(createParticles()) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            particles = createParticles()
            delay(800) // Duración de la animación
            onAnimationEnd()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(100)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            particles.forEach { particle ->
                AnimatedParticle(particle = particle)
            }
        }
    }
}

/**
 * Partícula animada individual
 */
@Composable
private fun AnimatedParticle(particle: Particle) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = particle.targetX,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetX"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = particle.targetY,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )

    Canvas(
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .size(particle.size.dp)
    ) {
        drawCircle(
            color = particle.color,
            radius = size.minDimension / 2 * scale,
            center = center
        )
    }
}

/**
 * Animación de corazón flotante
 */
@Composable
fun FloatingHeartAnimation(
    show: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shouldShow by remember { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (show) {
            shouldShow = true
            delay(1500)
            shouldShow = false
            onComplete()
        }
    }

    AnimatedVisibility(
        visible = shouldShow,
        enter = fadeIn() + scaleIn(initialScale = 0.5f),
        exit = fadeOut() + scaleOut(targetScale = 1.5f) + slideOutVertically { -it },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

/**
 * Animación de confeti
 */
@Composable
fun ConfettiAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    var confettiPieces by remember { mutableStateOf(createConfetti()) }

    LaunchedEffect(isActive) {
        if (isActive) {
            confettiPieces = createConfetti()
        }
    }

    AnimatedVisibility(
        visible = isActive,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Canvas(modifier = modifier.fillMaxSize()) {
            confettiPieces.forEach { piece ->
                drawConfettiPiece(piece)
            }
        }
    }
}

/**
 * Animación de guardado con bookmark
 */
@Composable
fun BookmarkSaveAnimation(
    show: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shouldShow by remember { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (show) {
            shouldShow = true
            delay(1000)
            shouldShow = false
            onComplete()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (shouldShow) 1.2f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bookmark_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (shouldShow) 15f else -15f,
        animationSpec = repeatable(
            iterations = 2,
            animation = tween(150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bookmark_rotation"
    )

    AnimatedVisibility(
        visible = shouldShow,
        enter = fadeIn() + scaleIn(initialScale = 0.5f),
        exit = fadeOut() + slideOutVertically { -it / 2 },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation
                            )
                    )
                }
            }
        }
    }
}

/**
 * Pulso de corazón (heartbeat)
 */
@Composable
fun HeartbeatAnimation(
    isBeating: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isBeating) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "heartbeat"
    )

    Box(
        modifier = modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale
        )
    ) {
        content()
    }
}

// ===== DATA CLASSES Y HELPERS =====

data class Particle(
    val targetX: Float,
    val targetY: Float,
    val color: Color,
    val size: Float
)

data class ConfettiPiece(
    val x: Float,
    val y: Float,
    val color: Color,
    val rotation: Float,
    val size: Float
)

private fun createParticles(): List<Particle> {
    val colors = listOf(
        Color(0xFFE91E63), // Rosa
        Color(0xFFFF4081), // Rosa oscuro
        Color(0xFFF50057), // Rosa fuerte
        Color(0xFFFF80AB), // Rosa claro
        Color(0xFFFF5252)  // Rojo
    )

    return List(20) {
        val angle = Random.nextFloat() * 2 * Math.PI
        val distance = Random.nextFloat() * 150 + 50
        Particle(
            targetX = (cos(angle) * distance).toFloat(),
            targetY = (sin(angle) * distance).toFloat(),
            color = colors.random(),
            size = Random.nextFloat() * 8 + 4
        )
    }
}

private fun createConfetti(): List<ConfettiPiece> {
    val colors = listOf(
        Color(0xFFE91E63),
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFFC107),
        Color(0xFF9C27B0)
    )

    return List(30) {
        ConfettiPiece(
            x = Random.nextFloat() * 1000,
            y = Random.nextFloat() * -500,
            color = colors.random(),
            rotation = Random.nextFloat() * 360,
            size = Random.nextFloat() * 10 + 5
        )
    }
}

private fun DrawScope.drawConfettiPiece(piece: ConfettiPiece) {
    val path = Path().apply {
        moveTo(piece.x, piece.y)
        lineTo(piece.x + piece.size, piece.y)
        lineTo(piece.x + piece.size, piece.y + piece.size * 2)
        lineTo(piece.x, piece.y + piece.size * 2)
        close()
    }
    drawPath(
        path = path,
        color = piece.color
    )
}
