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

    init { fetchHospitals() }

    fun fetchHospitals() = viewModelScope.launch {
        _isLoading.value = true
        try {
            RetrofitClient.hospitalService.getHospitals().let { r ->
                if (r.isSuccessful) _hospitals.value = r.body() ?: emptyList()
            }
        } catch(_ : Exception) { }
        finally { _isLoading.value = false }
    }

    fun filterHospitals(name: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            RetrofitClient.hospitalService.filterHospitals(name).let { r ->
                if (r.isSuccessful) _hospitals.value = r.body() ?: emptyList()
            }
        } catch(_ : Exception) { }
        finally { _isLoading.value = false }
    }

    /**
     * Recarga los hospitales (para pull-to-refresh)
     */
    fun refreshData() = viewModelScope.launch {
        _isRefreshing.value = true
        try {
            RetrofitClient.hospitalService.getHospitals().let { r ->
                if (r.isSuccessful) _hospitals.value = r.body() ?: emptyList()
            }
        } catch(_ : Exception) { }
        finally { _isRefreshing.value = false }
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
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val hospitalData = mapOf(
                    "Nombre" to nombre,
                    "ImagenId" to null
                )

                val response = RetrofitClient.hospitalService.updateHospital(id, hospitalData)
                if (response.isSuccessful) {
                    fetchHospitals() // Recargar lista
                    onSuccess()
                } else {
                    onError("Error al actualizar: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error de red: ${e.localizedMessage}")
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

