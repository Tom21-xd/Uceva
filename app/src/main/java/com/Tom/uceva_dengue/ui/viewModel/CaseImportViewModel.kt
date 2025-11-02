package com.Tom.uceva_dengue.ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.CaseImportResultDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.CellType
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream

class CaseImportViewModel : ViewModel() {

    private val caseImportService = RetrofitClient.caseImportService

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado de resultado de importación
    private val _importResult = MutableStateFlow<CaseImportResultDto?>(null)
    val importResult: StateFlow<CaseImportResultDto?> = _importResult

    // Columnas detectadas del archivo
    private val _detectedColumns = MutableStateFlow<List<String>>(emptyList())
    val detectedColumns: StateFlow<List<String>> = _detectedColumns

    // Mapeo de columnas: campo del sistema -> columna del archivo
    private val _columnMapping = MutableStateFlow<Map<String, String>>(emptyMap())
    val columnMapping: StateFlow<Map<String, String>> = _columnMapping

    // Muestra la pantalla de mapeo
    private val _showMappingScreen = MutableStateFlow(false)
    val showMappingScreen: StateFlow<Boolean> = _showMappingScreen

    /**
     * Importa casos desde un archivo CSV
     */
    fun importCsvFile(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _importResult.value = null

            try {
                Log.d("CaseImport", "Importando CSV: ${file.name}, size: ${file.length()} bytes")

                val requestFile = file.asRequestBody("text/csv".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // Convertir mapeo a JSON si existe
                val mappingJson = if (_columnMapping.value.isNotEmpty()) {
                    val json = Gson().toJson(_columnMapping.value)
                    Log.d("CaseImport", "Enviando mapeo: $json")
                    json.toRequestBody("text/plain".toMediaTypeOrNull())
                } else {
                    null
                }

                val response = caseImportService.importCsv(body, mappingJson)

                if (response.isSuccessful && response.body() != null) {
                    val importResponse = response.body()!!
                    _importResult.value = importResponse.data
                    Log.d("CaseImport", "Importación exitosa: ${importResponse.data.successfulImports} casos")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error al importar: ${response.message()}"
                    Log.e("CaseImport", "Error: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("CaseImport", "Exception al importar CSV", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Importa casos desde un archivo Excel
     */
    fun importExcelFile(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _importResult.value = null

            try {
                Log.d("CaseImport", "Importando Excel: ${file.name}, size: ${file.length()} bytes")

                val requestFile = file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // Convertir mapeo a JSON si existe
                val mappingJson = if (_columnMapping.value.isNotEmpty()) {
                    val json = Gson().toJson(_columnMapping.value)
                    Log.d("CaseImport", "Enviando mapeo: $json")
                    json.toRequestBody("text/plain".toMediaTypeOrNull())
                } else {
                    null
                }

                val response = caseImportService.importExcel(body, mappingJson)

                if (response.isSuccessful && response.body() != null) {
                    val importResponse = response.body()!!
                    _importResult.value = importResponse.data
                    Log.d("CaseImport", "Importación exitosa: ${importResponse.data.successfulImports} casos")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error al importar: ${response.message()}"
                    Log.e("CaseImport", "Error: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("CaseImport", "Exception al importar Excel", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Descarga la plantilla CSV
     */
    fun downloadCsvTemplate(onSuccess: (ByteArray) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = caseImportService.downloadCsvTemplate()

                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    onSuccess(bytes)
                    Log.d("CaseImport", "Plantilla CSV descargada: ${bytes.size} bytes")
                } else {
                    _errorMessage.value = "Error al descargar plantilla"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("CaseImport", "Exception al descargar plantilla CSV", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Descarga la plantilla Excel
     */
    fun downloadExcelTemplate(onSuccess: (ByteArray) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = caseImportService.downloadExcelTemplate()

                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    onSuccess(bytes)
                    Log.d("CaseImport", "Plantilla Excel descargada: ${bytes.size} bytes")
                } else {
                    _errorMessage.value = "Error al descargar plantilla"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("CaseImport", "Exception al descargar plantilla Excel", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Extrae las columnas de un archivo CSV
     * Detecta automáticamente el delimitador (coma o punto y coma)
     */
    fun extractCsvColumns(file: File) {
        try {
            val firstLine = file.bufferedReader().use { it.readLine() }
            if (firstLine != null) {
                // Detectar el delimitador: si tiene más punto y comas que comas, usar punto y coma
                val delimiter = if (firstLine.count { it == ';' } > firstLine.count { it == ',' }) {
                    ";"
                } else {
                    ","
                }

                val columns = firstLine.split(delimiter).map { it.trim().replace("\"", "") }
                _detectedColumns.value = columns
                _showMappingScreen.value = true

                // Mapeo automático inteligente
                autoMapColumns(columns)

                Log.d("CaseImport", "Columnas CSV detectadas con delimitador '$delimiter': $columns")
            } else {
                _errorMessage.value = "El archivo CSV está vacío"
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error al leer el archivo: ${e.localizedMessage}"
            Log.e("CaseImport", "Error extrayendo columnas CSV", e)
        }
    }

    /**
     * Extrae las columnas de un archivo Excel (.xls o .xlsx)
     */
    fun extractExcelColumns(file: File) {
        try {
            FileInputStream(file).use { fis ->
                val workbook = WorkbookFactory.create(fis)
                val sheet = workbook.getSheetAt(0) // Primera hoja

                if (sheet.physicalNumberOfRows > 0) {
                    val headerRow = sheet.getRow(0)
                    if (headerRow != null) {
                        val columns = mutableListOf<String>()

                        // Iterar sobre todas las celdas de la primera fila
                        for (cell in headerRow) {
                            val cellValue = when (cell.cellType) {
                                CellType.STRING -> cell.stringCellValue
                                CellType.NUMERIC -> cell.numericCellValue.toString()
                                else -> cell.toString()
                            }
                            val trimmedValue = cellValue.trim()
                            if (trimmedValue.isNotEmpty()) {
                                columns.add(trimmedValue)
                            }
                        }

                        if (columns.isNotEmpty()) {
                            _detectedColumns.value = columns
                            _showMappingScreen.value = true

                            // Mapeo automático inteligente
                            autoMapColumns(columns)

                            Log.d("CaseImport", "Columnas Excel detectadas: $columns")
                        } else {
                            _errorMessage.value = "El archivo Excel no tiene encabezados"
                        }
                    } else {
                        _errorMessage.value = "El archivo Excel no tiene encabezados"
                    }
                } else {
                    _errorMessage.value = "El archivo Excel está vacío"
                }

                workbook.close()
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error al leer el archivo Excel: ${e.localizedMessage}"
            Log.e("CaseImport", "Error extrayendo columnas Excel", e)
            e.printStackTrace()
        }
    }

    /**
     * Mapeo automático inteligente basado en nombres de columnas
     */
    private fun autoMapColumns(columns: List<String>) {
        val mapping = mutableMapOf<String, String>()

        columns.forEach { col ->
            val colLower = col.lowercase()
            when {
                colLower.contains("año") || colLower.contains("anio") -> mapping["año"] = col
                colLower.contains("edad") -> mapping["edad"] = col
                colLower.contains("clasificacion") || colLower.contains("tipo") || colLower.contains("dengue") -> mapping["clasificacion"] = col
                colLower.contains("sexo") || colLower.contains("genero") -> mapping["sexo"] = col
                colLower.contains("barrio") || colLower.contains("vereda") || colLower.contains("bar_ver") -> mapping["barrio"] = col
                colLower.contains("latitud") || colLower.contains("lat") -> mapping["latitud"] = col
                colLower.contains("longitud") || colLower.contains("long") || colLower.contains("lon") -> mapping["longitud"] = col
                colLower.contains("comuna") -> mapping["comuna"] = col
                colLower.contains("descripcion") || colLower.contains("desc") -> mapping["descripcion"] = col
            }
        }

        _columnMapping.value = mapping
        Log.d("CaseImport", "Mapeo automático: $mapping")
    }

    /**
     * Actualiza el mapeo de una columna específica
     */
    fun updateColumnMapping(systemField: String, fileColumn: String) {
        val newMapping = _columnMapping.value.toMutableMap()
        newMapping[systemField] = fileColumn
        _columnMapping.value = newMapping
    }

    /**
     * Limpia el mapeo de una columna específica
     */
    fun clearColumnMapping(systemField: String) {
        val newMapping = _columnMapping.value.toMutableMap()
        newMapping.remove(systemField)
        _columnMapping.value = newMapping
    }

    /**
     * Cierra la pantalla de mapeo
     */
    fun closeMappingScreen() {
        _showMappingScreen.value = false
        _detectedColumns.value = emptyList()
        _columnMapping.value = emptyMap()
    }

    /**
     * Limpia los mensajes y resultados
     */
    fun clearMessages() {
        _errorMessage.value = null
        _importResult.value = null
    }
}
