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

data class SectionData(
    val title: String,
    val icon: ImageVector,
    val colorKey: String, // "primary", "danger", "warning", "success", "info"
    val items: List<InfoItem>
)

data class InfoItem(
    val emoji: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreventionGuideScreen() {
    var expandedSections by remember { mutableStateOf(setOf<Int>()) }

    val sections = listOf(
        SectionData(
            title = "El Virus del Dengue",
            icon = Icons.Default.Info,
            colorKey = "info",
            items = listOf(
                InfoItem(
                    emoji = "🦟",
                    title = "Transmisión",
                    description = "Se transmite por la picadura del mosquito Aedes aegypti infectado. NO se contagia de persona a persona, ni por agua o alimentos."
                ),
                InfoItem(
                    emoji = "🧬",
                    title = "Serotipos",
                    description = "Existen 4 serotipos del virus (DENV-1, 2, 3 y 4). La infección con uno no protege contra los otros y puede aumentar el riesgo de dengue grave."
                ),
                InfoItem(
                    emoji = "📊",
                    title = "Situación 2025",
                    description = "Más de 4 millones de casos reportados globalmente. El dengue se está extendiendo a nuevas regiones como Europa y Mediterráneo Oriental."
                )
            )
        ),
        SectionData(
            title = "Criaderos del Mosquito",
            icon = Icons.Default.WaterDrop,
            colorKey = "primary",
            items = listOf(
                InfoItem(
                    emoji = "💧",
                    title = "Dónde se cría",
                    description = "En agua limpia y quieta acumulada en recipientes artificiales: baldes, llantas, macetas, botellas, canaletas tapadas."
                ),
                InfoItem(
                    emoji = "🏠",
                    title = "Dentro de casa",
                    description = "Floreros, bebederos de mascotas, porta cepillos, detrás de la heladera, platos bajo macetas. Cambiar el agua cada 2-3 días."
                ),
                InfoItem(
                    emoji = "🌳",
                    title = "Fuera de casa",
                    description = "Latas, botellas, neumáticos, juguetes, baldes. Dar vuelta o eliminar todo objeto que acumule agua de lluvia."
                ),
                InfoItem(
                    emoji = "⚠️",
                    title = "Todo el año",
                    description = "Los huevos resisten bajas temperaturas y sequía. La prevención debe ser continua durante los 12 meses del año."
                )
            )
        ),
        SectionData(
            title = "Síntomas y Signos de Alarma",
            icon = Icons.Default.Warning,
            colorKey = "warning",
            items = listOf(
                InfoItem(
                    emoji = "🌡️",
                    title = "Síntomas comunes",
                    description = "Fiebre alta (40°C), dolor de cabeza intenso, dolor detrás de los ojos, dolores musculares y articulares, náuseas, vómitos, sarpullido."
                ),
                InfoItem(
                    emoji = "⏱️",
                    title = "Período de incubación",
                    description = "Los síntomas aparecen entre 4-10 días después de la picadura infectada y duran de 2 a 7 días."
                ),
                InfoItem(
                    emoji = "🚨",
                    title = "¡SIGNOS DE ALARMA!",
                    description = "Dolor abdominal intenso, vómitos persistentes, sangrado de encías o nariz, sangre en vómito, somnolencia extrema, respiración acelerada, acumulación de líquidos."
                ),
                InfoItem(
                    emoji = "🏥",
                    title = "Cuándo consultar URGENTE",
                    description = "Si aparecen signos de alarma (días 3-7 tras inicio), acudir INMEDIATAMENTE al hospital. El dengue grave puede ser mortal sin atención médica."
                )
            )
        ),
        SectionData(
            title = "Recomendaciones Si Tienes Dengue",
            icon = Icons.Default.MedicalServices,
            colorKey = "danger",
            items = listOf(
                InfoItem(
                    emoji = "💊",
                    title = "Tratamiento",
                    description = "NO existe medicamento específico. El tratamiento es sintomático: reposo, hidratación abundante (3-4 litros/día) y paracetamol para la fiebre."
                ),
                InfoItem(
                    emoji = "❌",
                    title = "QUÉ EVITAR",
                    description = "NUNCA tomar aspirina, ibuprofeno ni antiinflamatorios (AINEs). Pueden causar hemorragias graves. Solo paracetamol/acetaminofén."
                ),
                InfoItem(
                    emoji = "💧",
                    title = "Hidratación",
                    description = "Beber mucho líquido: agua, suero oral, jugos naturales, caldos. La deshidratación es peligrosa en dengue."
                ),
                InfoItem(
                    emoji = "🛡️",
                    title = "Proteger a otros",
                    description = "Usar repelente y mosquitero para evitar que te piquen más mosquitos durante la enfermedad y no transmitir el virus."
                )
            )
        ),
        SectionData(
            title = "Prevención Comunitaria",
            icon = Icons.Default.Groups,
            colorKey = "success",
            items = listOf(
                InfoItem(
                    emoji = "🏘️",
                    title = "Acción colectiva",
                    description = "La eliminación de criaderos debe ser tarea de toda la comunidad. Organizar jornadas de limpieza en el barrio."
                ),
                InfoItem(
                    emoji = "🧴",
                    title = "Protección personal",
                    description = "Usar repelente (cada 3-4 horas), mosquiteros en ventanas, ropa clara de manga larga, tules en cochecitos de bebés."
                ),
                InfoItem(
                    emoji = "🔍",
                    title = "Revisión semanal",
                    description = "Inspeccionar patios, jardines y espacios comunes cada semana. Eliminar agua estancada inmediatamente."
                ),
                InfoItem(
                    emoji = "📢",
                    title = "Educar y compartir",
                    description = "Informar a vecinos, familia y amigos. Compartir esta información puede salvar vidas. La prevención es responsabilidad de todos."
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
            // Header
            item {
                HeaderCard()
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

            // Footer
            item {
                FooterCard()
            }
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header con gradiente
            Box {
                // Fondo con gradiente usando colores del tema
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono principal
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 48.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Guía de Prevención",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Todo lo que necesitas saber para protegerte del dengue",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
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
        "primary" -> MaterialTheme.colorScheme.primary
        "danger" -> MaterialTheme.colorScheme.error
        "warning" -> Color(0xFFFFA726) // Orange para warnings
        "success" -> MaterialTheme.colorScheme.tertiary
        "info" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header de la sección
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(sectionColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = sectionColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = section.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Contenido expandible
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    section.items.forEach { item ->
                        InfoItemCard(item = item, accentColor = sectionColor)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoItemCard(item: InfoItem, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Emoji
            Text(
                text = item.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun FooterCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Recuerda",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "La prevención del dengue es responsabilidad de todos. Eliminar los criaderos del mosquito es la forma más efectiva de combatir la enfermedad.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Información basada en fuentes oficiales: OMS, OPS, CDC",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
