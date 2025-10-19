package com.Tom.uceva_dengue.ui.Components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Tipos de categoría de publicaciones con configuración visual
 */
enum class PublicationCategory(
    val id: Int,
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val lightColor: Color
) {
    ALERTA(1, "Alerta", Icons.Default.Warning, Color(0xFFD32F2F), Color(0xFFFFCDD2)),
    NOTICIA(2, "Noticia", Icons.Default.Newspaper, Color(0xFF1976D2), Color(0xFFBBDEFB)),
    CONSEJO(3, "Consejo", Icons.Default.Lightbulb, Color(0xFFF57C00), Color(0xFFFFE0B2)),
    INFORMACION(4, "Información", Icons.Default.Info, Color(0xFF388E3C), Color(0xFFC8E6C9)),
    PREVENCION(5, "Prevención", Icons.Default.HealthAndSafety, Color(0xFF7B1FA2), Color(0xFFE1BEE7)),
    GENERAL(0, "General", Icons.Default.Article, Color(0xFF455A64), Color(0xFFCFD8DC));

    companion object {
        fun fromId(id: Int?): PublicationCategory {
            return values().find { it.id == id } ?: GENERAL
        }
    }
}

/**
 * Niveles de prioridad con configuración visual
 */
enum class PriorityLevel(
    val id: Int,
    val displayName: String,
    val color: Color,
    val gradientStart: Color,
    val gradientEnd: Color
) {
    URGENTE(4, "URGENTE", Color(0xFFD32F2F), Color(0xFFFF5252), Color(0xFFD32F2F)),
    ALTA(3, "Alta", Color(0xFFFF6F00), Color(0xFFFF9800), Color(0xFFFF6F00)),
    NORMAL(2, "Normal", Color(0xFF1976D2), Color(0xFF42A5F5), Color(0xFF1976D2)),
    BAJA(1, "Baja", Color(0xFF388E3C), Color(0xFF66BB6A), Color(0xFF388E3C));

    companion object {
        fun fromId(id: Int?): PriorityLevel {
            return when (id) {
                4 -> URGENTE
                3 -> ALTA
                2 -> NORMAL
                1 -> BAJA
                else -> NORMAL
            }
        }

        fun fromString(nivel: String?): PriorityLevel {
            return when (nivel?.lowercase()) {
                "urgente" -> URGENTE
                "alta" -> ALTA
                "normal" -> NORMAL
                "baja" -> BAJA
                else -> NORMAL
            }
        }
    }
}

/**
 * Tipos de filtro para publicaciones
 */
enum class FilterType(
    val displayName: String,
    val icon: ImageVector
) {
    TODAS("Todas", Icons.Default.GridView),
    ALERTAS("Alertas", Icons.Default.Warning),
    NOTICIAS("Noticias", Icons.Default.Newspaper),
    CONSEJOS("Consejos", Icons.Default.Lightbulb),
    GUARDADAS("Guardadas", Icons.Default.Bookmark);
}

/**
 * Modo de visualización
 */
enum class ViewMode {
    LIST,
    GRID
}
