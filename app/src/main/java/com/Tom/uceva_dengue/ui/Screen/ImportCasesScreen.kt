package com.Tom.uceva_dengue.ui.Screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.viewModel.CaseImportViewModel
import com.Tom.uceva_dengue.utils.rememberAppDimensions
import java.io.File
import java.io.FileOutputStream

// Colores
private val PrimaryBlue = Color(0xFF5E81F4)
private val SuccessGreen = Color(0xFF26DE81)
private val DangerRed = Color(0xFFFF4757)
private val WarningOrange = Color(0xFFFFA502)

@Composable
fun ImportCasesScreen(
    navController: NavController,
    viewModel: CaseImportViewModel = viewModel()
) {
    val context = LocalContext.current
    val dimensions = rememberAppDimensions()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val importResult by viewModel.importResult.collectAsState()
    val showMappingScreen by viewModel.showMappingScreen.collectAsState()
    val detectedColumns by viewModel.detectedColumns.collectAsState()
    val columnMapping by viewModel.columnMapping.collectAsState()

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var fileType by remember { mutableStateOf<String?>(null) }

    // Launcher para seleccionar archivo
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = getFileName(context, it)
            fileType = context.contentResolver.getType(it)

            // Copiar archivo y extraer columnas automáticamente
            val file = copyUriToFile(context, it, selectedFileName ?: "temp_file")
            selectedFile = file
            file?.let { f ->
                when {
                    selectedFileName?.endsWith(".csv", ignoreCase = true) == true -> {
                        viewModel.extractCsvColumns(f)
                    }
                    selectedFileName?.endsWith(".xls", ignoreCase = true) == true ||
                    selectedFileName?.endsWith(".xlsx", ignoreCase = true) == true -> {
                        viewModel.extractExcelColumns(f)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.paddingMedium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
        ) {
            // Instrucciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(dimensions.paddingMedium)
            ) {
                Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                        Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                        Text(
                            "Instrucciones de Importación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                    Text(
                        "1. Descarga la plantilla CSV o Excel\n" +
                        "2. Completa los datos de los casos\n" +
                        "3. Selecciona el archivo completado\n" +
                        "4. Haz clic en 'Importar' para cargar los casos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }

            // Descargar plantillas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.paddingMedium)
            ) {
                Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
                    Text(
                        "Descargar Plantillas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                    ) {
                        // Botón CSV
                        OutlinedButton(
                            onClick = {
                                viewModel.downloadCsvTemplate { bytes ->
                                    saveFile(context, bytes, "plantilla_casos.csv", "text/csv")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Icon(Icons.Default.DownloadForOffline, contentDescription = null)
                            Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                            Text("CSV")
                        }

                        // Botón Excel
                        OutlinedButton(
                            onClick = {
                                viewModel.downloadExcelTemplate { bytes ->
                                    saveFile(context, bytes, "plantilla_casos.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Icon(Icons.Default.DownloadForOffline, contentDescription = null)
                            Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                            Text("Excel")
                        }
                    }
                }
            }

            // Seleccionar archivo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.paddingMedium)
            ) {
                Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
                    Text(
                        "Seleccionar Archivo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                    Button(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                        Text("Seleccionar Archivo CSV/Excel")
                    }

                    selectedFileName?.let { filename ->
                        Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensions.paddingSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                                Text(
                                    filename,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    selectedFileUri = null
                                    selectedFileName = null
                                    fileType = null
                                }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar",
                                        tint = DangerRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Pantalla de Mapeo de Columnas
            if (showMappingScreen && detectedColumns.isNotEmpty()) {
                ColumnMappingSection(
                    detectedColumns = detectedColumns,
                    columnMapping = columnMapping,
                    onMappingUpdate = { field, column -> viewModel.updateColumnMapping(field, column) },
                    onClearMapping = { field -> viewModel.clearColumnMapping(field) },
                    dimensions = dimensions
                )
            }

            // Botón importar
            Button(
                onClick = {
                    selectedFileUri?.let { uri ->
                        val file = copyUriToFile(context, uri, selectedFileName ?: "temp_file")
                        file?.let {
                            when {
                                selectedFileName?.endsWith(".csv", ignoreCase = true) == true -> {
                                    viewModel.importCsvFile(it)
                                }
                                selectedFileName?.endsWith(".xlsx", ignoreCase = true) == true ||
                                selectedFileName?.endsWith(".xls", ignoreCase = true) == true -> {
                                    viewModel.importExcelFile(it)
                                }
                                else -> {
                                    viewModel.clearMessages()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedFileUri != null && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(dimensions.paddingSmall)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                    Text("Importando...")
                } else {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                    Text("Importar Casos")
                }
            }

            // Resultados
            importResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.failedImports == 0) SuccessGreen.copy(alpha = 0.1f)
                        else WarningOrange.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(dimensions.paddingMedium)
                ) {
                    Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (result.failedImports == 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (result.failedImports == 0) SuccessGreen else WarningOrange,
                                modifier = Modifier.size(dimensions.iconMedium)
                            )
                            Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                            Text(
                                "Resultado de Importación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                        ResultRow("Total de filas:", result.totalRows.toString())
                        ResultRow("Casos importados:", result.successfulImports.toString(), SuccessGreen)
                        if (result.failedImports > 0) {
                            ResultRow("Casos fallidos:", result.failedImports.toString(), DangerRed)
                        }

                        if (result.errors.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(dimensions.paddingMedium))
                            Text(
                                "Errores:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                            Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                            result.errors.take(5).forEach { error ->
                                Text(
                                    "• Fila ${error.row}: ${error.error}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )
                            }
                            if (result.errors.size > 5) {
                                Text(
                                    "... y ${result.errors.size - 5} errores más",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Mensajes de error
            errorMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(dimensions.paddingMedium)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.paddingMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = DangerRed
                        )
                        Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                        Text(
                            it,
                            color = DangerRed,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearMessages() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = DangerRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, color: Color = Color.DarkGray) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnMappingSection(
    detectedColumns: List<String>,
    columnMapping: Map<String, String>,
    onMappingUpdate: (String, String) -> Unit,
    onClearMapping: (String) -> Unit,
    dimensions: com.Tom.uceva_dengue.utils.AppDimensions
) {
    // Campos del sistema que necesitan mapeo
    val systemFields = listOf(
        "año" to "Año del Caso",
        "edad" to "Edad del Paciente",
        "tipoDengue" to "Tipo de Dengue",
        "sexo" to "Sexo/Género",
        "barrio" to "Barrio o Vereda",
        "latitud" to "Latitud",
        "longitud" to "Longitud",
        "descripcion" to "Descripción (Opcional)"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(dimensions.paddingMedium)
    ) {
        Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(dimensions.iconMedium)
                )
                Spacer(modifier = Modifier.width(dimensions.paddingSmall))
                Text(
                    "Mapeo de Columnas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            Spacer(modifier = Modifier.height(dimensions.paddingSmall))
            Text(
                "Relaciona los campos del sistema con las columnas de tu archivo.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(dimensions.paddingMedium))

            // Lista de mapeos
            systemFields.forEach { (fieldKey, fieldLabel) ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        fieldLabel,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    var expanded by remember { mutableStateOf(false) }
                    val selectedColumn = columnMapping[fieldKey]

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedColumn ?: "Seleccionar columna...",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            detectedColumns.forEach { column ->
                                DropdownMenuItem(
                                    text = { Text(column) },
                                    onClick = {
                                        onMappingUpdate(fieldKey, column)
                                        expanded = false
                                    }
                                )
                            }
                            if (selectedColumn != null) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Limpiar",
                                            color = DangerRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    onClick = {
                                        onClearMapping(fieldKey)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(dimensions.paddingMedium))
            }
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    result = it.getString(displayNameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "archivo_desconocido"
}

private fun copyUriToFile(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun saveFile(context: Context, bytes: ByteArray, fileName: String, mimeType: String) {
    try {
        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeBytes(bytes)
        // Aquí podrías agregar lógica para compartir o abrir el archivo
        android.widget.Toast.makeText(context, "Plantilla guardada: ${file.absolutePath}", android.widget.Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        android.widget.Toast.makeText(context, "Error al guardar plantilla", android.widget.Toast.LENGTH_SHORT).show()
    }
}
