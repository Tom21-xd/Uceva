package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tom.uceva_dengue.Data.Model.QuizAnswerModel
import com.Tom.uceva_dengue.Data.Model.QuizCategoryModel
import com.Tom.uceva_dengue.Data.Model.QuizQuestionModel
import com.Tom.uceva_dengue.ui.viewModel.QuizViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizStartScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onQuizStarted: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentAttempt by viewModel.currentAttempt.collectAsState()

    LaunchedEffect(currentAttempt) {
        if (currentAttempt != null) {
            onQuizStarted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Evaluación de Conocimientos",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E8449),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF1E8449)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Pon a Prueba tus Conocimientos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E8449),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Responde 10 preguntas sobre prevención del dengue y obtén tu certificado al aprobar con 80% o más",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF424242)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quiz Info Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.QuestionAnswer,
                        title = "10",
                        subtitle = "Preguntas"
                    )
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        title = "80%",
                        subtitle = "Aprobación"
                    )
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.EmojiEvents,
                        title = "PDF",
                        subtitle = "Certificado"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Categories Section
                if (categories.isNotEmpty()) {
                    Text(
                        "Categorías Incluidas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E8449)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    categories.filter { it.isActive }.forEach { category ->
                        CategoryCard(category)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Start Quiz Button
                Button(
                    onClick = { viewModel.startQuiz(userId, 10) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E8449)
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Evaluación", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Error Message
            errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono con círculo de fondo
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E8449)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF616161),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryCard(category: QuizCategoryModel) {
    // Seleccionar icono basado en el nombre o icono de la categoría
    val categoryIcon = when {
        category.name.contains("General", ignoreCase = true) || category.icon == "info_circle" -> Icons.Default.Info
        category.name.contains("Síntomas", ignoreCase = true) || category.icon == "medical_alert" -> Icons.Default.LocalHospital
        category.name.contains("Prevención", ignoreCase = true) || category.icon == "water_drop" -> Icons.Default.WaterDrop
        category.name.contains("Protección", ignoreCase = true) || category.icon == "shield" -> Icons.Default.Shield
        category.name.contains("Mitos", ignoreCase = true) || category.icon == "psychology" -> Icons.Default.Psychology
        category.name.contains("Qué Hacer", ignoreCase = true) || category.icon == "medical_services" -> Icons.Default.MedicalServices
        else -> Icons.Default.Quiz
    }

    val categoryColor = when {
        category.name.contains("General", ignoreCase = true) -> Color(0xFF1976D2)
        category.name.contains("Síntomas", ignoreCase = true) -> Color(0xFFD32F2F)
        category.name.contains("Prevención", ignoreCase = true) -> Color(0xFF1E8449)
        category.name.contains("Protección", ignoreCase = true) -> Color(0xFFFF6F00)
        category.name.contains("Mitos", ignoreCase = true) -> Color(0xFF7B1FA2)
        category.name.contains("Qué Hacer", ignoreCase = true) -> Color(0xFF0288D1)
        else -> Color(0xFF1E8449)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = categoryColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${category.totalQuestions} preguntas",
                        style = MaterialTheme.typography.bodySmall,
                        color = categoryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestionsScreen(
    onNavigateBack: () -> Unit,
    onQuizFinished: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val currentAttempt by viewModel.currentAttempt.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val answerResult by viewModel.answerResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var elapsedSeconds by remember { mutableStateOf(0) }

    // Timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds++
            viewModel.updateElapsedTime(elapsedSeconds)
        }
    }

    val currentQuestion = currentAttempt?.questions?.getOrNull(currentQuestionIndex)
    val totalQuestions = currentAttempt?.questions?.size ?: 0
    val isLastQuestion = currentQuestionIndex == totalQuestions - 1

    Scaffold(
        topBar = {
            // TopAppBar compacto personalizado
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = Color(0xFF1E8449),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Salir",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Pregunta ${currentQuestionIndex + 1} de $totalQuestions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                formatTime(elapsedSeconds),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Progress Bar
                    LinearProgressIndicator(
                        progress = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF1E8449),
                        trackColor = Color(0xFFE8F5E9)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Category Badge
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            currentQuestion.categoryName ?: "Categoría",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1E8449),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Question Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E8449)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "?",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        currentQuestion.questionText,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        DifficultyChip(currentQuestion.difficulty)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = Color(0xFFFFF3E0),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "${currentQuestion.points} pts",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFFF6F00),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answers
                    Text(
                        "Selecciona tu respuesta:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    currentQuestion.answers.sortedBy { it.displayOrder }.forEach { answer ->
                        AnswerCard(
                            answer = answer,
                            isSelected = selectedAnswer == answer.id,
                            isAnswered = answerResult != null,
                            isCorrect = answerResult?.correctAnswerId == answer.id,
                            onSelect = {
                                if (answerResult == null) {
                                    viewModel.selectAnswer(answer.id)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Answer Feedback
                    AnimatedVisibility(
                        visible = answerResult != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        answerResult?.let { result ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (result.isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (result.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (result.isCorrect) Color(0xFF1E8449) else Color(0xFFD32F2F),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            if (result.isCorrect) "¡Correcto!" else "Incorrecto",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (result.isCorrect) Color(0xFF1E8449) else Color(0xFFD32F2F)
                                        )
                                        result.explanation?.let { explanation ->
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF424242)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    if (answerResult == null) {
                        // Submit Answer Button
                        Button(
                            onClick = { viewModel.submitAnswer() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = selectedAnswer != null && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E8449)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Confirmar Respuesta", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    } else {
                        // Next/Finish Button
                        Button(
                            onClick = {
                                if (isLastQuestion) {
                                    viewModel.finishQuiz()
                                    onQuizFinished()
                                } else {
                                    viewModel.nextQuestion()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E8449)
                            )
                        ) {
                            Text(
                                if (isLastQuestion) "Finalizar Evaluación" else "Siguiente Pregunta",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                if (isLastQuestion) Icons.Default.Check else Icons.Default.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Error Message
            errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(
    answer: QuizAnswerModel,
    isSelected: Boolean,
    isAnswered: Boolean,
    isCorrect: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = when {
        isAnswered && isCorrect -> Color(0xFFE8F5E9)
        isAnswered && isSelected && !isCorrect -> Color(0xFFFFEBEE)
        isSelected -> Color(0xFFE3F2FD)
        else -> Color.White
    }

    val borderColor = when {
        isAnswered && isCorrect -> Color(0xFF1E8449)
        isAnswered && isSelected && !isCorrect -> Color(0xFFD32F2F)
        isSelected -> Color(0xFF1976D2)
        else -> Color(0xFFE0E0E0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                enabled = !isAnswered
            )
            .border(
                width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                enabled = !isAnswered,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isAnswered && isCorrect) Color(0xFF1E8449) else Color(0xFF1976D2)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                answer.answerText,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            if (isAnswered && isCorrect) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF1E8449)
                )
            } else if (isAnswered && isSelected && !isCorrect) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
private fun DifficultyChip(difficulty: Int) {
    val (color, text) = when (difficulty) {
        1 -> Color(0xFF4CAF50) to "Fácil"
        2 -> Color(0xFFFF9800) to "Media"
        3 -> Color(0xFFF44336) to "Difícil"
        else -> Color(0xFF9E9E9E) to "Normal"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
