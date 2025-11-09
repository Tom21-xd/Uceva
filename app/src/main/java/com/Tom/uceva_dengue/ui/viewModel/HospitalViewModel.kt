package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.HospitalModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HospitalViewModel : ViewModel() {
    private val _hospitals = MutableStateFlow<List<HospitalModel>>(emptyList())
    val hospitals: StateFlow<List<HospitalModel>> = _hospitals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de refresh para pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init { fetchHospitals() }

    fun fetchHospitals() = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null
        try {
            val response = RetrofitClient.hospitalService.getHospitals()
            if (response.isSuccessful) {
                _hospitals.value = response.body() ?: emptyList()
                android.util.Log.d("HospitalViewModel", "Hospitales cargados: ${_hospitals.value.size}")
            } else {
                _errorMessage.value = "Error al cargar hospitales: ${response.code()}"
                android.util.Log.e("HospitalViewModel", "Error response: ${response.code()}")
            }
        } catch(e: Exception) {
            _errorMessage.value = "Error de red: ${e.localizedMessage}"
            android.util.Log.e("HospitalViewModel", "Exception: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    fun filterHospitals(name: String) = viewModelScope.launch {
        if (name.isBlank()) {
            fetchHospitals()
            return@launch
        }

        _isLoading.value = true
        _errorMessage.value = null
        try {
            val response = RetrofitClient.hospitalService.filterHospitals(name)
            if (response.isSuccessful) {
                _hospitals.value = response.body() ?: emptyList()
                android.util.Log.d("HospitalViewModel", "Filtrados: ${_hospitals.value.size} resultados para '$name'")
            } else {
                _errorMessage.value = "Error al buscar: ${response.code()}"
            }
        } catch(e: Exception) {
            _errorMessage.value = "Error de búsqueda: ${e.localizedMessage}"
            android.util.Log.e("HospitalViewModel", "Filter exception: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Recarga los hospitales (para pull-to-refresh)
     */
    fun refreshData() = viewModelScope.launch {
        _isRefreshing.value = true
        _errorMessage.value = null
        try {
            val response = RetrofitClient.hospitalService.getHospitals()
            if (response.isSuccessful) {
                _hospitals.value = response.body() ?: emptyList()
                android.util.Log.d("HospitalViewModel", "Datos refrescados: ${_hospitals.value.size} hospitales")
            } else {
                _errorMessage.value = "Error al refrescar: ${response.code()}"
            }
        } catch(e: Exception) {
            _errorMessage.value = "Error de conexión"
            android.util.Log.e("HospitalViewModel", "Refresh exception: ${e.message}", e)
        } finally {
            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // DELETE: Eliminar hospital
    fun deleteHospital(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.hospitalService.deleteHospital(id)
                if (response.isSuccessful) {
                    _hospitals.value = _hospitals.value.filter { it.ID_HOSPITAL != id }
                    onSuccess()
                } else {
                    onError("Error al eliminar: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // UPDATE: Actualizar hospital
    fun updateHospital(
        id: Int,
        nombre: String,
        direccion: String? = null,
        latitud: String? = null,
        longitud: String? = null,
        idMunicipio: Int? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Usar DTO tipado en lugar de Map genérico
                val hospitalData = com.Tom.uceva_dengue.Data.Model.UpdateHospitalDto(
                    nombre = nombre,
                    direccion = if (direccion.isNullOrBlank()) null else direccion,
                    latitud = if (latitud.isNullOrBlank()) null else latitud,
                    longitud = if (longitud.isNullOrBlank()) null else longitud,
                    idMunicipio = if (idMunicipio != null && idMunicipio > 0) idMunicipio else null
                )

                android.util.Log.d("HospitalViewModel", "Updating hospital $id with data: $hospitalData")

                val response = RetrofitClient.hospitalService.updateHospital(id, hospitalData)
                if (response.isSuccessful) {
                    android.util.Log.d("HospitalViewModel", "Hospital updated successfully")
                    fetchHospitals() // Recargar lista
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    android.util.Log.e("HospitalViewModel", "Update failed: ${response.code()} - $errorBody")
                    onError("Error al actualizar: $errorBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("HospitalViewModel", "Update exception: ${e.message}", e)
                onError("Error de red: ${e.localizedMessage ?: e.message}")
            }
        }
    }

    // Obtener hospital por ID
    fun getHospitalById(id: Int, onSuccess: (HospitalModel) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.hospitalService.getHospitalById(id)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener el hospital: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }
}

