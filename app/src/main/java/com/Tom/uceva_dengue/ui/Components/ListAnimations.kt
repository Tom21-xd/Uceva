package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

/**
 * Item de lista con animación de entrada
 */
@Composable
fun AnimatedListItem(
    index: Int,
    delayPerItem: Int = 50,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * delayPerItem).toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
    ) {
        content()
    }
}

/**
 * Item con animación slide desde la izquierda
 */
@Composable
fun SlideInFromLeftItem(
    index: Int,
    delayPerItem: Int = 50,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * delayPerItem).toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
    ) {
        content()
    }
}

/**
 * Item con animación desde abajo
 */
@Composable
fun SlideInFromBottomItem(
    index: Int,
    delayPerItem: Int = 50,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * delayPerItem).toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
    ) {
        content()
    }
}

/**
 * Extensión para LazyColumn con animaciones automáticas
 */
fun <T> LazyListScope.animatedItems(
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    delayPerItem: Int = 50,
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    itemsIndexed(
        items = items,
        key = if (key != null) { index, item -> key(item) } else null
    ) { index, item ->
        AnimatedListItem(index = index, delayPerItem = delayPerItem) {
            itemContent(index, item)
        }
    }
}

/**
 * LazyColumn con animaciones de entrada escalonadas
 */
@Composable
fun <T> AnimatedLazyColumn(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    delayPerItem: Int = 50,
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, item ->
            AnimatedListItem(index = index, delayPerItem = delayPerItem) {
                itemContent(index, item)
            }
        }
    }
}

/**
 * Grid animado (simula un grid con Column)
 */
@Composable
fun <T> AnimatedGrid(
    items: List<T>,
    columns: Int = 2,
    modifier: Modifier = Modifier,
    delayPerItem: Int = 50,
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    Column(modifier = modifier) {
        items.chunked(columns).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEachIndexed { columnIndex, item ->
                    val index = rowIndex * columns + columnIndex
                    Box(modifier = Modifier.weight(1f)) {
                        AnimatedListItem(index = index, delayPerItem = delayPerItem) {
                            itemContent(index, item)
                        }
                    }
                }

                // Espacios vacíos si la última fila no está completa
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Empty state animado (cuando no hay datos)
 */
@Composable
fun AnimatedEmptyState(
    visible: Boolean,
    message: String = "No hay datos disponibles",
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Text(
                text = message,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = androidx.compose.ui.graphics.Color(0xFF757575),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Efecto de rebote al cargar datos
 */
@Composable
fun BounceLoadingEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var trigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        trigger = true
    }

    val scale by animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceScale"
    )

    Box(modifier = modifier.scale(scale)) {
        content()
    }
}
