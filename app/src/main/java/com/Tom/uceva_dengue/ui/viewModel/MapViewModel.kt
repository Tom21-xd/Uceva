package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.HospitalModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async


class MapViewModel : ViewModel() {

    private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
    val cases: StateFlow<List<CaseModel>> = _cases

    private val _hospitals = MutableStateFlow<List<HospitalModel>>(emptyList())
    val hospitals: StateFlow<List<HospitalModel>> = _hospitals

    // Estado de carga consolidado
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadingError = MutableStateFlow<String?>(null)
    val loadingError: StateFlow<String?> = _loadingError

    private var isCasesFetched = false
    private var isHospitalsFetched = false

    fun fetchCases() {

        if (!isCasesFetched) {
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.caseService.getCases()
                    Log.d("Mapa", "Response: $response")

                    if (response.body() != _cases.value) {
                        _cases.value = response.body()!!
                    }

                    isCasesFetched = true
                } catch (e: Exception) {
                    Log.e("Mapa", "Error fetching cases", e)
                }
            }
        }
    }

    fun fetchHospitals() {
        if (!isHospitalsFetched) {
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.hospitalService.getHospitals()
                    Log.d("Mapa", "Hospitals Response: $response")

                    if (response.body() != _hospitals.value) {
                        _hospitals.value = response.body()!!
                    }

                    isHospitalsFetched = true
                } catch (e: Exception) {
                    Log.e("Mapa", "Error fetching hospitals", e)
                }
            }
        }
    }

    init {
        loadAllData()
    }

    /**
     * Carga casos y hospitales en paralelo para evitar flasheo de UI
     */
    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingError.value = null

            try {
                // Lanzar ambas peticiones en paralelo
                val casesDeferred = async { RetrofitClient.caseService.getCases() }
                val hospitalsDeferred = async { RetrofitClient.hospitalService.getHospitals() }

                // Esperar a que ambas completen
                val casesResponse = casesDeferred.await()
                if (casesResponse.body() != null) {
                    _cases.value = casesResponse.body()!!
                    isCasesFetched = true
                }

                val hospitalsResponse = hospitalsDeferred.await()
                if (hospitalsResponse.body() != null) {
                    _hospitals.value = hospitalsResponse.body()!!
                    isHospitalsFetched = true
                }

                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al cargar datos del mapa", e)
                _loadingError.value = "Error al cargar datos: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }
}
