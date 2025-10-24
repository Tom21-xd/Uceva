package com.Tom.uceva_dengue.ui.Screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Tom.uceva_dengue.ui.viewModel.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val certificate by viewModel.certificate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadUserCertificates(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Certificado",
                        style = MaterialTheme.typography.titleMedium
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
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (certificate != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Certificate Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF1E8449), Color(0xFF145A32))
                                    )
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp),
                                    tint = Color(0xFFFFD700)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "¡Felicitaciones!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Has completado exitosamente la evaluación",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Certificate Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Detalles del Certificado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E8449)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            CertificateDetailRow(
                                icon = Icons.Default.Person,
                                label = "Nombre",
                                value = certificate!!.userName
                            )

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            CertificateDetailRow(
                                icon = Icons.Default.Email,
                                label = "Correo",
                                value = certificate!!.userEmail
                            )

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            CertificateDetailRow(
                                icon = Icons.Default.Star,
                                label = "Puntaje Obtenido",
                                value = "${certificate!!.score.toInt()}%"
                            )

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            CertificateDetailRow(
                                icon = Icons.Default.DateRange,
                                label = "Fecha de Emisión",
                                value = formatDate(certificate!!.issuedAt)
                            )

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            CertificateDetailRow(
                                icon = Icons.Default.VerifiedUser,
                                label = "Código de Verificación",
                                value = certificate!!.verificationCode
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status Badge
                    Surface(
                        color = when (certificate!!.status) {
                            "Activo" -> Color(0xFFE8F5E9)
                            "Revocado" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFF5F5F5)
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when (certificate!!.status) {
                                    "Activo" -> Icons.Default.CheckCircle
                                    "Revocado" -> Icons.Default.Cancel
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = when (certificate!!.status) {
                                    "Activo" -> Color(0xFF1E8449)
                                    "Revocado" -> Color(0xFFD32F2F)
                                    else -> Color(0xFF757575)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Estado: ${certificate!!.status}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = when (certificate!!.status) {
                                    "Activo" -> Color(0xFF1E8449)
                                    "Revocado" -> Color(0xFFD32F2F)
                                    else -> Color(0xFF757575)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Download PDF Button
                    Button(
                        onClick = {
                            scope.launch {
                                isDownloading = true
                                downloadCertificatePdf(
                                    context = context,
                                    certificateId = certificate!!.id,
                                    fileName = "Certificado_${certificate!!.verificationCode}.pdf",
                                    viewModel = viewModel
                                )
                                isDownloading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E8449)
                        ),
                        enabled = !isDownloading && certificate!!.status == "Activo"
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Descargando...")
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Descargar Certificado PDF", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Share Button
                    OutlinedButton(
                        onClick = {
                            shareCertificateInfo(context, certificate!!)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF1E8449)
                        ),
                        enabled = certificate!!.status == "Activo"
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir Certificado", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Nota Importante",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Este certificado digital valida tus conocimientos en prevención del dengue. Puedes verificar su autenticidad usando el código de verificación.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF424242)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1E8449))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Cargando certificado...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF757575)
                        )
                    }
                }
            } else {
                // No Certificate State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFBDBDBD)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay certificado disponible",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Completa la evaluación con un puntaje del 80% o superior para obtener tu certificado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Center
                        )
                    }
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
private fun CertificateDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF1E8449),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }
    }
}

private suspend fun downloadCertificatePdf(
    context: Context,
    certificateId: Int,
    fileName: String,
    viewModel: QuizViewModel
) {
    try {
        val response = withContext(Dispatchers.IO) {
            com.Tom.uceva_dengue.Data.Api.RetrofitClient.quizService.downloadCertificate(certificateId)
        }

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            val file = savePdfToDownloads(context, body, fileName)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Certificado descargado correctamente", Toast.LENGTH_LONG).show()
                openPdf(context, file)
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al descargar el certificado", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun savePdfToDownloads(context: Context, body: ResponseBody, fileName: String): File {
    val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    FileOutputStream(file).use { outputStream ->
        outputStream.write(body.bytes())
    }

    return file
}

private fun openPdf(context: Context, file: File) {
    try {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se encontró una app para abrir PDFs", Toast.LENGTH_SHORT).show()
    }
}

private fun shareCertificateInfo(context: Context, certificate: com.Tom.uceva_dengue.Data.Model.CertificateModel) {
    val shareText = """
        🏆 Certificado de Prevención del Dengue

        He completado exitosamente la evaluación de conocimientos sobre prevención del dengue.

        📊 Puntaje: ${certificate.score.toInt()}%
        📅 Fecha: ${formatDate(certificate.issuedAt)}
        🔐 Código: ${certificate.verificationCode}

        #PrevenciónDengue #SaludPública #UCEVA
    """.trimIndent()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    context.startActivity(Intent.createChooser(shareIntent, "Compartir certificado"))
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}
