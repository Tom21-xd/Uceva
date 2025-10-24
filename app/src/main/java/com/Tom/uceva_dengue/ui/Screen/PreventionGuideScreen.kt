package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores modernos del dengue
private val DengueRed = Color(0xFFE53935)
private val DengueOrange = Color(0xFFFF6F00)
private val DengueBlue = Color(0xFF1976D2)
private val DenguePurple = Color(0xFF7B1FA2)
private val DengueGreen = Color(0xFF388E3C)
private val DenguePink = Color(0xFFD81B60)

data class SectionData(
    val title: String,
    val icon: ImageVector,
    val colorKey: String,
    val items: List<InfoItem>
)

data class InfoItem(
    val emoji: String,
    val title: String,
    val description: String,
    val isWarning: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreventionGuideScreen(
    onNavigateToQuiz: () -> Unit = {}
) {
    var expandedSections by remember { mutableStateOf(setOf(0)) } // Primera sección expandida por defecto

    val sections = listOf(
        // ¿Qué es el Dengue?
        SectionData(
            title = "¿Qué es el Dengue?",
            icon = Icons.Default.Info,
            colorKey = "info",
            items = listOf(
                InfoItem(
                    emoji = "🦟",
                    title = "Vector: Mosquito Aedes aegypti",
                    description = "Enfermedad viral transmitida por la picadura del mosquito Aedes aegypti. Se caracteriza por rayas blancas en patas y cuerpo, y pica durante el día, especialmente en las primeras horas de la mañana y al atardecer."
                ),
                InfoItem(
                    emoji = "🦠",
                    title = "Agente: Virus del dengue",
                    description = "El virus se transmite cuando el mosquito adquiere el virus al picar a una persona infectada, y posteriormente puede transmitirlo a otras personas sanas."
                ),
                InfoItem(
                    emoji = "👤",
                    title = "Huésped: Ser humano",
                    description = "Este ciclo de transmisión convierte al mosquito en el vector principal de la enfermedad, siendo fundamental comprender su comportamiento para implementar medidas preventivas efectivas."
                )
            )
        ),

        // Síntomas Iniciales
        SectionData(
            title = "Síntomas Iniciales del Dengue",
            icon = Icons.Default.Thermostat,
            colorKey = "warning",
            items = listOf(
                InfoItem(
                    emoji = "⏱️",
                    title = "Período de incubación",
                    description = "Los síntomas iniciales del dengue suelen aparecer entre 4 y 10 días después de la picadura del mosquito infectado. Es fundamental reconocerlos tempranamente para buscar atención médica oportuna y evitar complicaciones."
                ),
                InfoItem(
                    emoji = "🌡️",
                    title = "Fiebre Alta",
                    description = "Temperatura corporal elevada de inicio súbito, generalmente superior a 38.5°C, que puede durar entre 2 y 7 días."
                ),
                InfoItem(
                    emoji = "🧠",
                    title = "Cefalea Intensa",
                    description = "Dolor de cabeza severo y persistente, frecuentemente acompañado de dolor retroocular que se intensifica con el movimiento de los ojos."
                ),
                InfoItem(
                    emoji = "💪",
                    title = "Dolor Muscular",
                    description = "Malestar general con dolores musculares y articulares intensos, razón por la cual el dengue también se conoce como 'fiebre rompehuesos'."
                ),
                InfoItem(
                    emoji = "🔴",
                    title = "Erupciones Cutáneas",
                    description = "Aparición de manchas rojizas en la piel que pueden presentarse en diferentes momentos de la enfermedad."
                )
            )
        ),

        // Signos de Alarma
        SectionData(
            title = "Signos de Alarma",
            icon = Icons.Default.Emergency,
            colorKey = "danger",
            items = listOf(
                InfoItem(
                    emoji = "⚠️",
                    title = "Atención Inmediata Requerida",
                    description = "Los signos de alarma del dengue aparecen típicamente entre el tercer y quinto día de la enfermedad, coincidiendo con la fase crítica. Ante la presencia de cualquiera de estos signos, se debe acudir inmediatamente al servicio de salud, ya que indican un posible dengue grave que requiere atención médica urgente y hospitalización.",
                    isWarning = true
                ),
                InfoItem(
                    emoji = "🤢",
                    title = "Dolor Abdominal Intenso",
                    description = "Dolor abdominal intenso y continuo, o dolor a la palpación del abdomen, que puede indicar complicaciones graves."
                ),
                InfoItem(
                    emoji = "🤮",
                    title = "Vómitos Persistentes",
                    description = "Vómitos frecuentes que impiden la hidratación oral adecuada y pueden llevar a deshidratación severa."
                ),
                InfoItem(
                    emoji = "🩸",
                    title = "Sangrado de Mucosas",
                    description = "Sangrado de encías, nariz, o presencia de sangre en vómito u orina, señal de alteraciones en la coagulación."
                ),
                InfoItem(
                    emoji = "😴",
                    title = "Alteración del Estado Mental",
                    description = "Somnolencia excesiva, irritabilidad, confusión o inquietud que pueden indicar compromiso neurológico."
                ),
                InfoItem(
                    emoji = "💧",
                    title = "Acumulación de Líquidos",
                    description = "Dificultad respiratoria por acumulación de líquidos en el pecho o abdomen distendido."
                )
            )
        ),

        // Prevención: Eliminación de Criaderos
        SectionData(
            title = "Prevención: Eliminación de Criaderos",
            icon = Icons.Default.WaterDrop,
            colorKey = "primary",
            items = listOf(
                InfoItem(
                    emoji = "💡",
                    title = "Estrategia Principal",
                    description = "La prevención del dengue se basa fundamentalmente en eliminar los criaderos de mosquitos y evitar las picaduras. Los criaderos se forman en depósitos de agua limpia y estancada donde el mosquito Aedes aegypti deposita sus huevos. Estos pueden encontrarse tanto dentro como fuera de las viviendas, en recipientes como floreros, llantas, tanques, platos de macetas y cualquier objeto que acumule agua."
                ),
                InfoItem(
                    emoji = "🚰",
                    title = "Tapar Depósitos",
                    description = "Mantener tapados todos los tanques y depósitos de agua para evitar que los mosquitos depositen sus huevos."
                ),
                InfoItem(
                    emoji = "🔄",
                    title = "Cambiar Agua",
                    description = "Cambiar el agua de los floreros cada dos días, lavando bien las paredes del recipiente con cepillo."
                ),
                InfoItem(
                    emoji = "🧹",
                    title = "Limpiar Espacios",
                    description = "Mantener patios, jardines y áreas comunes limpios, sin objetos que acumulen agua estancada."
                ),
                InfoItem(
                    emoji = "🚮",
                    title = "Eliminar Llantas",
                    description = "Eliminar, perforar o almacenar bajo techo las llantas y recipientes que puedan acumular agua de lluvia."
                )
            )
        ),

        // Protección Personal
        SectionData(
            title = "Protección Personal contra Picaduras",
            icon = Icons.Default.Shield,
            colorKey = "success",
            items = listOf(
                InfoItem(
                    emoji = "🕐",
                    title = "Horarios Críticos",
                    description = "Además de eliminar criaderos, es fundamental protegerse de las picaduras del mosquito, especialmente durante las horas de mayor actividad del Aedes aegypti: primeras horas de la mañana y al atardecer."
                ),
                InfoItem(
                    emoji = "👕",
                    title = "Usar ropa de manga larga y pantalones largos",
                    description = "Preferiblemente de colores claros."
                ),
                InfoItem(
                    emoji = "🧴",
                    title = "Aplicar repelente de mosquitos",
                    description = "En la piel expuesta, siguiendo las instrucciones del producto."
                ),
                InfoItem(
                    emoji = "🪟",
                    title = "Instalar mosquiteros",
                    description = "En puertas y ventanas de la vivienda."
                ),
                InfoItem(
                    emoji = "❄️",
                    title = "Utilizar mosquiteros sobre las camas",
                    description = "Especialmente para proteger a bebés y niños pequeños."
                ),
                InfoItem(
                    emoji = "💨",
                    title = "Usar ventiladores o aire acondicionado",
                    description = "Ya que el mosquito evita las corrientes de aire."
                ),
                InfoItem(
                    emoji = "🛑",
                    title = "Evitar la acumulación de agua",
                    description = "En platillos de macetas y otros recipientes dentro del hogar."
                ),
                InfoItem(
                    emoji = "🌿",
                    title = "Zonas de Riesgo",
                    description = "Patios, jardines y áreas con vegetación. Mantenlos limpios y sin objetos que acumulen agua."
                )
            )
        ),

        // Mitos y Realidades
        SectionData(
            title = "Mitos y Realidades sobre el Dengue",
            icon = Icons.Default.Psychology,
            colorKey = "purple",
            items = listOf(
                InfoItem(
                    emoji = "ℹ️",
                    title = "Información correcta",
                    description = "Existen numerosos conceptos erróneos sobre el dengue que pueden llevar a prácticas inadecuadas de prevención o tratamiento. Es fundamental conocer la información científicamente validada para tomar decisiones correctas en el cuidado de la salud."
                ),
                InfoItem(
                    emoji = "🏙️",
                    title = "Mito: Transmisión Geográfica",
                    description = "El dengue solo se transmite en zonas rurales.\n\nRealidad: El dengue también ocurre en áreas urbanas. De hecho, las ciudades con alta densidad poblacional presentan mayor riesgo de transmisión."
                ),
                InfoItem(
                    emoji = "💊",
                    title = "Mito: Tratamiento con Antibióticos",
                    description = "El dengue se cura con antibióticos.\n\nRealidad: No existe tratamiento específico para el dengue. Los antibióticos no son efectivos contra virus. Solo se realiza manejo sintomático con hidratación y control de la fiebre."
                ),
                InfoItem(
                    emoji = "🌡️",
                    title = "Mito: Presencia de Fiebre",
                    description = "Si no hay fiebre, no es dengue.\n\nRealidad: Algunos casos presentan fiebre leve o sin fiebre al inicio, especialmente en niños pequeños o personas con infecciones previas por dengue."
                )
            )
        ),

        // Qué Hacer ante la Sospecha
        SectionData(
            title = "Qué Hacer ante la Sospecha de Dengue",
            icon = Icons.Default.MedicalServices,
            colorKey = "danger",
            items = listOf(
                InfoItem(
                    emoji = "⚕️",
                    title = "Importancia de la atención temprana",
                    description = "Si se presentan síntomas compatibles con dengue, es fundamental actuar de manera adecuada para evitar complicaciones. La automedicación puede ser peligrosa, especialmente con ciertos medicamentos que están contraindicados en casos de dengue.",
                    isWarning = true
                ),
                InfoItem(
                    emoji = "🛌",
                    title = "01 - Descansar Adecuadamente",
                    description = "Guardar reposo en cama y evitar actividades físicas que puedan empeorar los síntomas o causar complicaciones."
                ),
                InfoItem(
                    emoji = "💧",
                    title = "02 - Hidratarse Constantemente",
                    description = "Beber abundantes líquidos: agua, suero oral, jugos naturales y caldos. La hidratación es fundamental para la recuperación."
                ),
                InfoItem(
                    emoji = "❌",
                    title = "03 - Evitar Automedicación",
                    description = "NO tomar aspirina (ácido acetilsalicílico) ni ibuprofeno, ya que pueden aumentar el riesgo de sangrado. Solo usar acetaminofén bajo supervisión médica."
                ),
                InfoItem(
                    emoji = "🏥",
                    title = "04 - Acudir al Centro de Salud",
                    description = "Buscar atención médica inmediata para valoración, diagnóstico y seguimiento apropiado del caso."
                ),
                InfoItem(
                    emoji = "⚠️",
                    title = "Importante sobre Antiinflamatorios",
                    description = "Los medicamentos antiinflamatorios no esteroideos (AINES) como la aspirina, el ibuprofeno y el naproxeno están contraindicados en casos de dengue porque aumentan el riesgo de hemorragias.",
                    isWarning = true
                )
            )
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header moderno
            item {
                ModernHeaderCard()
            }

            // Secciones expandibles
            items(sections.size) { index ->
                ExpandableSection(
                    section = sections[index],
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
            item {
                QuizCallToActionCard(onNavigateToQuiz)
            }

            // Footer
            item {
                ModernFooterCard()
            }
        }
    }
}

@Composable
private fun QuizCallToActionCard(onNavigateToQuiz: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E8449),
                            Color(0xFF27AE60)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
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
                    text = "Evalúa tus Conocimientos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pon a prueba lo que has aprendido sobre prevención del dengue. Responde 10 preguntas y obtén tu certificado.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Features
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuizFeature(icon = Icons.Default.QuestionAnswer, text = "10 Preguntas")
                    QuizFeature(icon = Icons.Default.Timer, text = "15 Minutos")
                    QuizFeature(icon = Icons.Default.CardMembership, text = "Certificado")
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Button
                Button(
                    onClick = onNavigateToQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E8449)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Iniciar Evaluación",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
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
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun ModernHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DengueRed,
                            DengueOrange
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono principal con animación
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 56.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Prevención del Dengue",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Guía Completa para Protegerte y Proteger a tu Comunidad",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableSection(
    section: SectionData,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val sectionColor = when (section.colorKey) {
        "primary" -> DengueBlue
        "danger" -> DengueRed
        "warning" -> DengueOrange
        "success" -> DengueGreen
        "info" -> MaterialTheme.colorScheme.secondary
        "purple" -> DenguePurple
        else -> MaterialTheme.colorScheme.primary
    }

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
            // Header de la sección
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
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = sectionColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = section.title,
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

            // Contenido expandible
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
                    section.items.forEach { item ->
                        ModernInfoItemCard(item = item, accentColor = sectionColor)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ModernInfoItemCard(item: InfoItem, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isWarning) {
                DengueRed.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Emoji con fondo
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isWarning) DengueRed else accentColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                    lineHeight = 20.sp
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                Text(
                    text = "⚠️",
                    fontSize = 36.sp
                )
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
