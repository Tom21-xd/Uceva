package com.Tom.uceva_dengue.ui.Components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Colores para el efecto shimmer
 */
private val shimmerColors = listOf(
    Color(0xFFE0E0E0),
    Color(0xFFF5F5F5),
    Color(0xFFE0E0E0)
)

/**
 * Animación shimmer para skeleton loaders
 */
@Composable
fun rememberShimmerBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(offset, offset),
        end = Offset(offset + 200f, offset + 200f)
    )
}

/**
 * Skeleton básico - caja rectangular
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 20.dp,
    cornerRadius: Dp = 4.dp
) {
    val brush = rememberShimmerBrush()

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * Skeleton circular (para avatares)
 */
@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val brush = rememberShimmerBrush()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush)
    )
}

/**
 * Skeleton para un item de lista de casos
 */
@Composable
fun SkeletonCaseItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(width = 100.dp, height = 24.dp)
                SkeletonBox(width = 80.dp, height = 20.dp, cornerRadius = 10.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            SkeletonBox(width = 200.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBox(width = 150.dp, height = 16.dp)
        }
    }
}

/**
 * Skeleton para un item de lista de publicaciones
 */
@Composable
fun SkeletonPublicationItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Imagen skeleton
            SkeletonBox(
                modifier = Modifier.fillMaxWidth(),
                width = 0.dp, // fillMaxWidth lo maneja
                height = 200.dp,
                cornerRadius = 0.dp
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SkeletonCircle(size = 40.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        SkeletonBox(width = 120.dp, height = 16.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        SkeletonBox(width = 80.dp, height = 14.dp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(),
                    width = 0.dp,
                    height = 20.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    width = 0.dp,
                    height = 16.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    width = 0.dp,
                    height = 16.dp
                )
            }
        }
    }
}

/**
 * Skeleton para item de hospital
 */
@Composable
fun SkeletonHospitalItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(width = 60.dp, height = 60.dp, cornerRadius = 8.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(width = 150.dp, height = 18.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(width = 100.dp, height = 14.dp)
            }
        }
    }
}

/**
 * Lista de skeletons para mostrar mientras se carga
 */
@Composable
fun SkeletonList(
    itemCount: Int = 5,
    skeletonItem: @Composable (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(itemCount) { index ->
            skeletonItem(index)
        }
    }
}
