package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.HospitalModel
import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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

    // Estado de refresh para pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Filtro de distancia en kilómetros
    private val _filterRadiusKm = MutableStateFlow(10f) // Por defecto 10km
    val filterRadiusKm: StateFlow<Float> = _filterRadiusKm

    // Ubicación del usuario para filtrar
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    // Casos filtrados por proximidad
    private val _filteredCases = MutableStateFlow<List<CaseModel>>(emptyList())
    val filteredCases: StateFlow<List<CaseModel>> = _filteredCases

    // Filtro de tipo de dengue (null = todos los tipos)
    private val _selectedDengueTypeId = MutableStateFlow<Int?>(null)
    val selectedDengueTypeId: StateFlow<Int?> = _selectedDengueTypeId

    // Lista de tipos de dengue disponibles
    private val _dengueTypes = MutableStateFlow<List<TypeOfDengueModel>>(emptyList())
    val dengueTypes: StateFlow<List<TypeOfDengueModel>> = _dengueTypes

    private var isCasesFetched = false
    private var isHospitalsFetched = false
    private var isDengueTypesFetched = false

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
     * Carga casos, hospitales y tipos de dengue en paralelo para evitar flasheo de UI
     */
    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingError.value = null

            try {
                // Lanzar todas las peticiones en paralelo
                val casesDeferred = async { RetrofitClient.caseService.getCases() }
                val hospitalsDeferred = async { RetrofitClient.hospitalService.getHospitals() }
                val dengueTypesDeferred = async { RetrofitClient.dengueService.getTypesOfDengue() }

                // Esperar a que todas completen
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

                val dengueTypesResponse = dengueTypesDeferred.await()
                if (dengueTypesResponse.isSuccessful && dengueTypesResponse.body() != null) {
                    _dengueTypes.value = dengueTypesResponse.body()!!
                    isDengueTypesFetched = true
                }

                _isLoading.value = false

                // Aplicar filtro después de cargar
                filterCasesByProximity()
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al cargar datos del mapa", e)
                _loadingError.value = "Error al cargar datos: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Recarga todos los datos del mapa (para pull-to-refresh)
     * Fuerza la recarga incluso si ya se habían cargado antes
     */
    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _loadingError.value = null

            try {
                // Lanzar todas las peticiones en paralelo
                val casesDeferred = async { RetrofitClient.caseService.getCases() }
                val hospitalsDeferred = async { RetrofitClient.hospitalService.getHospitals() }
                val dengueTypesDeferred = async { RetrofitClient.dengueService.getTypesOfDengue() }

                // Esperar a que todas completen
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

                val dengueTypesResponse = dengueTypesDeferred.await()
                if (dengueTypesResponse.isSuccessful && dengueTypesResponse.body() != null) {
                    _dengueTypes.value = dengueTypesResponse.body()!!
                    isDengueTypesFetched = true
                }

                // Aplicar filtro después de cargar
                filterCasesByProximity()

                _isRefreshing.value = false
                Log.d("MapViewModel", "Datos del mapa actualizados correctamente")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al actualizar datos del mapa", e)
                _loadingError.value = "Error al actualizar: ${e.localizedMessage}"
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Actualiza la ubicación del usuario y filtra casos
     */
    fun updateUserLocation(location: LatLng) {
        _userLocation.value = location
        filterCasesByProximity()
    }

    /**
     * Actualiza el radio de filtro y vuelve a filtrar casos
     */
    fun updateFilterRadius(radiusKm: Float) {
        _filterRadiusKm.value = radiusKm
        filterCasesByProximity()
    }

    /**
     * Actualiza el tipo de dengue seleccionado (null = todos)
     */
    fun updateSelectedDengueType(dengueTypeId: Int?) {
        _selectedDengueTypeId.value = dengueTypeId
        filterCasesByProximity()
    }

    /**
     * Filtra los casos según la ubicación del usuario, el radio configurado y el tipo de dengue
     */
    private fun filterCasesByProximity() {
        val userLoc = _userLocation.value
        val radius = _filterRadiusKm.value
        val selectedType = _selectedDengueTypeId.value

        var casesToFilter = _cases.value

        // Filtrar por tipo de dengue si hay uno seleccionado
        if (selectedType != null) {
            casesToFilter = casesToFilter.filter { case ->
                case.FK_ID_TIPODENGUE == selectedType
            }
        }

        // Filtrar por proximidad si hay ubicación del usuario
        if (userLoc == null) {
            _filteredCases.value = casesToFilter
            return
        }

        _filteredCases.value = casesToFilter.filter { case ->
            case.DIRECCION_CASOREPORTADO?.let { address ->
                parseLatLngFromString(address)?.let { caseLocation ->
                    val distance = calculateDistance(userLoc, caseLocation)
                    distance <= radius
                } ?: false
            } ?: false
        }

        val typeInfo = if (selectedType != null) {
            val typeName = _dengueTypes.value.find { it.ID_TIPODENGUE == selectedType }?.NOMBRE_TIPODENGUE ?: "Desconocido"
            " (tipo: $typeName)"
        } else {
            ""
        }
        Log.d("MapViewModel", "Casos filtrados: ${_filteredCases.value.size} de ${_cases.value.size} dentro de ${radius}km$typeInfo")
    }

    /**
     * Calcula la distancia en kilómetros entre dos puntos usando la fórmula de Haversine
     */
    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    /**
     * Parsea una cadena de coordenadas en formato "lat:lng" a LatLng
     */
    private fun parseLatLngFromString(coordinates: String): LatLng? {
        return try {
            val parts = coordinates.split(":")
            if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lng = parts[1].toDoubleOrNull()
                if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else null
            } else null
        } catch (e: Exception) {
            Log.e("MapViewModel", "Error parseando coordenadas: $coordinates", e)
            null
        }
    }
}
