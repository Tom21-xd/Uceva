package com.Tom.uceva_dengue.ui.Components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Componente que convierte texto con URLs en enlaces clicables
 * Soporta:
 * - URLs web (https://..., http://...)
 * - Videos de YouTube (detecta y abre en la app de YouTube)
 * - Enlaces de correo (mailto:...)
 */
@Composable
fun ClickableTextWithLinks(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    fontSize: TextUnit = TextUnit.Unspecified,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current

    // Regex mejorado para detectar URLs
    val urlPattern = Regex(
        """(https?://[^\s]+|www\.[^\s]+|youtube\.com/watch\?v=[^\s]+|youtu\.be/[^\s]+|mailto:[^\s]+)""",
        RegexOption.IGNORE_CASE
    )

    // Construir texto anotado con enlaces clicables
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = textColor,
                fontSize = fontSize
            )
        ) {
            var lastIndex = 0

            urlPattern.findAll(text).forEach { matchResult ->
                val startIndex = matchResult.range.first
                val endIndex = matchResult.range.last + 1
                val url = matchResult.value

                // Agregar texto antes del enlace
                append(text.substring(lastIndex, startIndex))

                // Agregar enlace con estilo
                pushStringAnnotation(
                    tag = "URL",
                    annotation = url
                )
                withStyle(
                    style = SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(url)
                }
                pop()

                lastIndex = endIndex
            }

            // Agregar texto restante después del último enlace
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }

    var layoutResult: TextLayoutResult? = null

    BasicText(
        text = annotatedString,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                layoutResult?.let { layout ->
                    val position = layout.getOffsetForPosition(offset)
                    // Encontrar la anotación clicada
                    annotatedString.getStringAnnotations(
                        tag = "URL",
                        start = position,
                        end = position
                    ).firstOrNull()?.let { annotation ->
                        val url = annotation.item
                        val intent = when {
                            // YouTube videos - abrir en la app de YouTube si está instalada
                            url.contains("youtube.com/watch?v=") || url.contains("youtu.be/") -> {
                                val videoId = extractYouTubeVideoId(url)
                                if (videoId != null) {
                                    // Intentar abrir en la app de YouTube
                                    try {
                                        Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
                                            setPackage("com.google.android.youtube")
                                        }.also {
                                            context.startActivity(it)
                                            return@detectTapGestures
                                        }
                                    } catch (e: Exception) {
                                        // YouTube app no instalada, usar navegador
                                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    }
                                } else {
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                }
                            }
                            // Mailto
                            url.startsWith("mailto:") -> {
                                Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                            }
                            // URLs normales
                            url.startsWith("http://") || url.startsWith("https://") -> {
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            }
                            // URLs sin protocolo (www.ejemplo.com)
                            url.startsWith("www.") -> {
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://$url"))
                            }
                            else -> {
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://$url"))
                            }
                        }

                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Si no se puede abrir, mostrar error silenciosamente
                            android.util.Log.e("ClickableTextWithLinks", "Error opening URL: $url", e)
                        }
                    }
                }
            }
        },
        style = TextStyle(
            fontSize = fontSize,
            color = textColor
        ),
        maxLines = maxLines,
        overflow = overflow,
        onTextLayout = { layoutResult = it }
    )
}

/**
 * Extrae el ID de video de una URL de YouTube
 * Soporta formatos:
 * - https://www.youtube.com/watch?v=VIDEO_ID
 * - https://youtu.be/VIDEO_ID
 * - youtube.com/watch?v=VIDEO_ID&other=params
 */
private fun extractYouTubeVideoId(url: String): String? {
    return try {
        when {
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            }
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("v=").substringBefore("&").substringBefore("#")
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}
