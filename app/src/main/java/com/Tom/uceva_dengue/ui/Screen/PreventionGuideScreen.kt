package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.Tom.uceva_dengue.Data.Model.PreventionCategory
import com.Tom.uceva_dengue.Data.Model.PreventionImage
import com.Tom.uceva_dengue.Data.Model.PreventionItem
import com.Tom.uceva_dengue.Data.Model.PreventionItemImage
import com.Tom.uceva_dengue.ui.viewModel.PreventionViewModel
import com.Tom.uceva_dengue.ui.viewModel.QuizViewModel

// Colores modernos del dengue
private val DengueRed = Color(0xFFE53935)
private val DengueOrange = Color(0xFFFF6F00)
private val DengueBlue = Color(0xFF1976D2)
private val DenguePurple = Color(0xFF7B1FA2)
private val DengueGreen = Color(0xFF388E3C)
private val DengueCyan = Color(0xFF00ACC1)

private const val BASE_IMAGE_URL = "https://api.prometeondev.com/Image/getImage/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreventionGuideScreen(
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToCertificate: () -> Unit = {},
    userId: Int = 0,
    quizViewModel: QuizViewModel = viewModel(),
    preventionViewModel: PreventionViewModel = viewModel()
) {
    var expandedSections by remember { mutableStateOf(setOf(0)) }

    // Quiz/Certificate state
    val certificate by quizViewModel.certificate.collectAsState()
    val quizHistory by quizViewModel.quizHistory.collectAsState()
    val quizLoading by quizViewModel.isLoading.collectAsState()

    // Prevention content state
    val categories by preventionViewModel.categories.collectAsState()
    val preventionLoading by preventionViewModel.isLoading.collectAsState()
    val hasLoadedFromApi by preventionViewModel.hasLoadedFromApi.collectAsState()

    // Find best completed attempt
    val bestCompletedAttempt = quizHistory
        .filter { it.status == "Completed" && it.score >= 80.0 }
        .maxByOrNull { it.score }

    LaunchedEffect(userId) {
        if (userId > 0) {
            quizViewModel.loadUserCertificates(userId)
            quizViewModel.loadQuizHistory(userId)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = preventionLoading,
            onRefresh = { preventionViewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item(key = "header") {
                    ModernHeaderCard()
                }

                // Loading indicator for API content
                if (preventionLoading && categories.isEmpty()) {
                    item(key = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DengueRed)
                    }
                }
            }

            // Certificate Card
            when {
                certificate != null -> {
                    item(key = "certificate_available") {
                        CertificateAvailableCard(
                            certificate = certificate!!,
                            onViewCertificate = onNavigateToCertificate
                        )
                    }
                }
                bestCompletedAttempt != null -> {
                    val attempt = bestCompletedAttempt
                    item(key = "generate_certificate_${attempt.attemptId}") {
                        GenerateCertificateCard(
                            attempt = attempt,
                            onGenerateCertificate = { quizViewModel.generateCertificate() },
                            onNavigateToCertificate = onNavigateToCertificate,
                            isLoading = quizLoading
                        )
                    }
                }
            }

            // Dynamic sections from API/fallback
            itemsIndexed(
                items = categories,
                key = { index, category -> "section_${category.ID_CATEGORIA_PREVENCION}" }
            ) { index, category ->
                ExpandableSectionFromApi(
                    category = category,
                    isExpanded = expandedSections.contains(index),
                    onToggle = {
                        expandedSections = if (expandedSections.contains(index)) {
                            expandedSections - index
                        } else {
                            expandedSections + index
                        }
                    }
                )
            }

            // Quiz Button
            item(key = "quiz_button") {
                QuizCallToActionCard(
                    onNavigateToQuiz = onNavigateToQuiz,
                    hasCertificate = certificate != null || bestCompletedAttempt != null
                )
            }

            // Footer
            item(key = "footer") {
                ModernFooterCard()
            }
        }
        }
    }
}

@Composable
private fun ExpandableSectionFromApi(
    category: PreventionCategory,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val sectionColor = getColorFromKey(category.COLOR)
    // El icono ahora es un emoji directo desde la BD
    val sectionEmoji = category.ICONO ?: "📋"

    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 4.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(sectionColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Mostrar emoji directo como texto
                    Text(
                        text = sectionEmoji,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = category.NOMBRE_CATEGORIA,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = sectionColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessLow)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Images carousel (if any)
                    if (category.IMAGENES.isNotEmpty()) {
                        ImageCarousel(images = category.IMAGENES)
                    }

                    // Items
                    category.ITEMS.forEach { item ->
                        ModernInfoItemCard(item = item, accentColor = sectionColor)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ImageCarousel(images: List<PreventionImage>) {
    if (images.size == 1) {
        // Una sola imagen - mostrar completa
        SingleImageView(image = images.first())
    } else {
        // Múltiples imágenes - carrusel con indicadores
        MultiImageCarousel(images = images)
    }
}

@Composable
private fun SingleImageView(image: PreventionImage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            AsyncImage(
                model = "$BASE_IMAGE_URL${image.ID_IMAGEN_MONGO}",
                contentDescription = image.TITULO_IMAGEN ?: "Imagen de prevención",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Título de la imagen si existe
            image.TITULO_IMAGEN?.let { titulo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiImageCarousel(images: List<PreventionImage>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Pager de imágenes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val image = images[page]
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = "$BASE_IMAGE_URL${image.ID_IMAGEN_MONGO}",
                            contentDescription = image.TITULO_IMAGEN ?: "Imagen ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Título de la imagen si existe
                        image.TITULO_IMAGEN?.let { titulo ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = titulo,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Contador de página (ej: 1/3)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${images.size}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Indicadores de página (puntos)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) DengueRed else Color.Gray.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernInfoItemCard(item: PreventionItem, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.ES_ADVERTENCIA) {
                DengueRed.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.EMOJI_ITEM ?: "💡",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.TITULO_ITEM,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.ES_ADVERTENCIA) DengueRed else accentColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.DESCRIPCION_ITEM,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                        lineHeight = 20.sp
                    )
                }
            }

            // Images carousel for item (if any)
            if (item.IMAGENES.isNotEmpty()) {
                ItemImageCarousel(
                    images = item.IMAGENES,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ItemImageCarousel(
    images: List<PreventionItemImage>,
    modifier: Modifier = Modifier
) {
    if (images.size == 1) {
        // Single image - show full
        SingleItemImageView(image = images.first(), modifier = modifier)
    } else {
        // Multiple images - carousel with indicators
        MultiItemImageCarousel(images = images, modifier = modifier)
    }
}

@Composable
private fun SingleItemImageView(
    image: PreventionItemImage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box {
            AsyncImage(
                model = "$BASE_IMAGE_URL${image.ID_IMAGEN_MONGO}",
                contentDescription = image.TITULO_IMAGEN ?: "Imagen del item",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            // Image title if exists
            image.TITULO_IMAGEN?.let { titulo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiItemImageCarousel(
    images: List<PreventionItemImage>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Image pager
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val image = images[page]
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = "$BASE_IMAGE_URL${image.ID_IMAGEN_MONGO}",
                            contentDescription = image.TITULO_IMAGEN ?: "Imagen ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Image title if exists
                        image.TITULO_IMAGEN?.let { titulo ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                        )
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = titulo,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Page counter (e.g., 1/3)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${images.size}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Page indicators (dots)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) DengueRed else Color.Gray.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

// Helper functions
private fun getColorFromKey(colorKey: String?): Color {
    return when (colorKey?.lowercase()) {
        "primary" -> DengueBlue
        "danger" -> DengueRed
        "warning" -> DengueOrange
        "success" -> DengueGreen
        "info" -> DengueCyan
        "purple" -> DenguePurple
        else -> DengueBlue
    }
}

// getIconFromKey ya no es necesario - ahora usamos emojis directos desde la BD

// ==================== Certificate Cards ====================

@Composable
private fun GenerateCertificateCard(
    attempt: com.Tom.uceva_dengue.Data.Model.QuizHistoryModel,
    onGenerateCertificate: () -> Unit,
    onNavigateToCertificate: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E8449), Color(0xFF27AE60))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFFFD700)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Aprobaste la Evaluación!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Has obtenido ${attempt.score.toInt()}% de puntuación",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Genera tu certificado digital ahora",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onGenerateCertificate()
                        onNavigateToCertificate()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E8449)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF1E8449)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Generando...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Generar Mi Certificado", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificateAvailableCard(
    certificate: com.Tom.uceva_dengue.Data.Model.CertificateModel,
    onViewCertificate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Tienes un Certificado!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Has aprobado la evaluación con ${certificate.score.toInt()}%",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Código: ${certificate.verificationCode}",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onViewCertificate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFFA000)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Ver y Descargar Certificado", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== Quiz Call To Action ====================

@Composable
private fun QuizCallToActionCard(
    onNavigateToQuiz: () -> Unit,
    hasCertificate: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E8449), Color(0xFF27AE60))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (hasCertificate) "Mejora tu Puntaje" else "Evalúa tus Conocimientos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (hasCertificate)
                        "Vuelve a realizar la evaluación para mejorar tu puntaje y obtener un nuevo certificado."
                    else
                        "Pon a prueba lo que has aprendido sobre prevención del dengue. Responde 10 preguntas y obtén tu certificado.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuizFeature(icon = Icons.Default.QuestionAnswer, text = "10 Preguntas")
                    QuizFeature(icon = Icons.Default.Timer, text = "15 Minutos")
                    QuizFeature(icon = Icons.Default.CardMembership, text = "Certificado")
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onNavigateToQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E8449)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (hasCertificate) "Repetir Evaluación" else "Iniciar Evaluación",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizFeature(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==================== Header & Footer ====================

@Composable
private fun ModernHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DengueRed, DengueOrange)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛡️", fontSize = 56.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Prevención del Dengue",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Guía Completa para Protegerte y Proteger a tu Comunidad",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun ModernFooterCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(DengueRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚠️", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "La Prevención es Responsabilidad de Todos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DengueRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Eliminar los criaderos del mosquito Aedes aegypti es la forma más efectiva de combatir el dengue. La participación comunitaria es fundamental para prevenir la propagación de la enfermedad.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Información basada en:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Organización Mundial de la Salud (OMS)\nOrganización Panamericana de la Salud (OPS)\nMinisterio de Salud y Protección Social de Colombia",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
