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
import java.io.File

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

                val response = caseImportService.importCsv(body)

                if (response.isSuccessful && response.body() != null) {
                    _importResult.value = response.body()
                    Log.d("CaseImport", "Importación exitosa: ${response.body()?.successfulImports} casos")
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

                val response = caseImportService.importExcel(body)

                if (response.isSuccessful && response.body() != null) {
                    _importResult.value = response.body()
                    Log.d("CaseImport", "Importación exitosa: ${response.body()?.successfulImports} casos")
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
     * Limpia los mensajes y resultados
     */
    fun clearMessages() {
        _errorMessage.value = null
        _importResult.value = null
    }
}
