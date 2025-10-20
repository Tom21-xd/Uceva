package com.Tom.uceva_dengue.ui.Components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import com.Tom.uceva_dengue.utils.rememberMaxContentWidth

/**
 * Contenedor responsivo que limita el ancho del contenido en pantallas grandes
 * y añade padding horizontal apropiado según el tamaño de pantalla
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val dimensions = rememberAppDimensions()
    val maxWidth = rememberMaxContentWidth()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingMedium)
        ) {
            content()
        }
    }
}

/**
 * Column responsiva con padding apropiado
 */
@Composable
fun ResponsiveColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val dimensions = rememberAppDimensions()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        content()
    }
}

/**
 * Row responsiva con spacing apropiado
 */
@Composable
fun ResponsiveRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    val dimensions = rememberAppDimensions()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        content()
    }
}

/**
 * Spacer vertical responsivo
 */
@Composable
fun ResponsiveVerticalSpacer(size: SpacerSize = SpacerSize.MEDIUM) {
    val dimensions = rememberAppDimensions()
    val height = when (size) {
        SpacerSize.SMALL -> dimensions.spacingSmall
        SpacerSize.MEDIUM -> dimensions.spacingMedium
        SpacerSize.LARGE -> dimensions.spacingLarge
    }
    Spacer(modifier = Modifier.height(height))
}

/**
 * Spacer horizontal responsivo
 */
@Composable
fun ResponsiveHorizontalSpacer(size: SpacerSize = SpacerSize.MEDIUM) {
    val dimensions = rememberAppDimensions()
    val width = when (size) {
        SpacerSize.SMALL -> dimensions.spacingSmall
        SpacerSize.MEDIUM -> dimensions.spacingMedium
        SpacerSize.LARGE -> dimensions.spacingLarge
    }
    Spacer(modifier = Modifier.width(width))
}

enum class SpacerSize {
    SMALL,
    MEDIUM,
    LARGE
}
