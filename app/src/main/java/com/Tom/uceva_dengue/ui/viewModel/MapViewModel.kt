package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Service.SignalRService
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MapViewModel : ViewModel() {

    // SignalR service for real-time updates
    private val signalRService = SignalRService.getInstance()

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

    // Ubicación de búsqueda para filtrar (cuando usuario busca una dirección)
    private val _searchLocation = MutableStateFlow<LatLng?>(null)
    val searchLocation: StateFlow<LatLng?> = _searchLocation

    // Casos filtrados por proximidad
    private val _filteredCases = MutableStateFlow<List<CaseModel>>(emptyList())
    val filteredCases: StateFlow<List<CaseModel>> = _filteredCases

    // Filtro de tipo de dengue (null = todos los tipos)
    private val _selectedDengueTypeId = MutableStateFlow<Int?>(null)
    val selectedDengueTypeId: StateFlow<Int?> = _selectedDengueTypeId

    // Lista de tipos de dengue disponibles
    private val _dengueTypes = MutableStateFlow<List<TypeOfDengueModel>>(emptyList())
    val dengueTypes: StateFlow<List<TypeOfDengueModel>> = _dengueTypes

    // Filtro de grupo etario (null = todas las edades, 1-5 según clasificación OMS)
    // 1: 0-4 años, 2: 5-14 años, 3: 15-49 años, 4: 50-64 años, 5: 65+ años
    private val _selectedAgeGroup = MutableStateFlow<Int?>(null)
    val selectedAgeGroup: StateFlow<Int?> = _selectedAgeGroup

    // Filtro de año (null = todos los años, por defecto año actual)
    private val _selectedYear = MutableStateFlow<Int?>(2025)
    val selectedYear: StateFlow<Int?> = _selectedYear

    // Años disponibles para filtrar
    private val _availableYears = MutableStateFlow<List<Int>>(emptyList())
    val availableYears: StateFlow<List<Int>> = _availableYears

    // Estadísticas epidemiológicas para el dashboard
    data class EpidemicStats(
        val totalCases: Int = 0,
        val activeCases: Int = 0,
        val recoveredCases: Int = 0,
        val deceasedCases: Int = 0,
        val casesByType: Map<String, Int> = emptyMap(),
        val casesByAgeGroup: Map<String, Int> = emptyMap(),
        val weeklyTrend: List<Int> = emptyList(),
        val mostAffectedAreas: List<Pair<String, Int>> = emptyList(),
        val riskLevel: String = "BAJO",
        val lastUpdate: String = ""
    )

    private val _epidemicStats = MutableStateFlow(EpidemicStats())
    val epidemicStats: StateFlow<EpidemicStats> = _epidemicStats

    private var isCasesFetched = false
    private var isHospitalsFetched = false
    private var isDengueTypesFetched = false
    private var isYearsFetched = false

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
        connectToSignalR()
        observeSignalREvents()
    }

    /**
     * Connect to SignalR hub for real-time updates
     */
    private fun connectToSignalR() {
        viewModelScope.launch {
            try {
                signalRService.connect()
                Log.d("MapViewModel", "SignalR connection initiated")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error connecting to SignalR", e)
            }
        }
    }

    /**
     * Observe SignalR events and update UI accordingly
     */
    private fun observeSignalREvents() {
        // Observe new case events
        viewModelScope.launch {
            signalRService.newCaseEvent.collect { event ->
                event?.let { (caseId, message) ->
                    Log.d("MapViewModel", "New case received via SignalR: $caseId - $message")
                    // Reload data to get the new case
                    refreshData()
                    signalRService.clearNewCaseEvent()
                }
            }
        }

        // Observe case update events
        viewModelScope.launch {
            signalRService.caseUpdateEvent.collect { event ->
                event?.let { (caseId, message) ->
                    Log.d("MapViewModel", "Case update received via SignalR: $caseId - $message")
                    // Reload data to get the updated case
                    refreshData()
                    signalRService.clearCaseUpdateEvent()
                }
            }
        }

        // Observe case deleted events
        viewModelScope.launch {
            signalRService.caseDeletedEvent.collect { caseId ->
                caseId?.let {
                    Log.d("MapViewModel", "Case deleted received via SignalR: $caseId")
                    // Remove case from local list
                    _cases.value = _cases.value.filter { case -> case.ID_CASOREPORTADO != caseId }
                    filterCasesByProximity()
                    signalRService.clearCaseDeletedEvent()
                }
            }
        }
    }

    /**
     * Disconnect from SignalR when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        signalRService.disconnect()
        Log.d("MapViewModel", "SignalR disconnected")
    }

    /**
     * Carga casos, hospitales y tipos de dengue en paralelo para evitar flasheo de UI
     */
    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingError.value = null

            try {
                // Cargar años disponibles si aún no se han cargado
                if (!isYearsFetched) {
                    val yearsResponse = RetrofitClient.statisticsService.getAvailableYears()
                    if (yearsResponse.isSuccessful && yearsResponse.body() != null) {
                        _availableYears.value = yearsResponse.body()!!
                        isYearsFetched = true
                    }
                }

                // Lanzar todas las peticiones en paralelo
                // Usar endpoint mapCases con filtro de año en lugar de getCases
                val casesDeferred = async { RetrofitClient.statisticsService.getMapCases(_selectedYear.value) }
                val hospitalsDeferred = async { RetrofitClient.hospitalService.getHospitals() }
                val dengueTypesDeferred = async { RetrofitClient.dengueService.getTypesOfDengue() }

                // Esperar a que todas completen
                val casesResponse = casesDeferred.await()
                if (casesResponse.body() != null) {
                    // Convertir MapCase a CaseModel (temporal, se necesitaría un mapper o usar MapCase directamente)
                    // Por ahora mantenemos la estructura existente
                    _cases.value = casesResponse.body()!!.map { mapCase ->
                        CaseModel(
                            ID_CASOREPORTADO = mapCase.ID_CASOREPORTADO,
                            DESCRIPCION_CASOREPORTADO = mapCase.DESCRIPCION_CASO ?: "",
                            DIRECCION_CASOREPORTADO = "${mapCase.LATITUD}:${mapCase.LONGITUD}",
                            FECHA_CASOREPORTADO = mapCase.FECHA_REGISTRO,
                            FK_ID_TIPODENGUE = mapCase.FK_ID_TIPODENGUE,
                            FK_ID_ESTADOCASO = mapCase.FK_ID_ESTADO,
                            FK_ID_HOSPITAL = null, // MapCase no expone el ID del hospital
                            FK_ID_PACIENTE = null, // MapCase no expone el ID del paciente
                            FK_ID_PERSONALMEDICO = null,
                            ANIO_REPORTE = mapCase.ANIO_REPORTE,
                            EDAD_PACIENTE = mapCase.EDAD_PACIENTE,
                            NOMBRE_TEMPORAL = mapCase.NOMBRE_PACIENTE,
                            BARRIO_VEREDA = mapCase.BARRIO_VEREDA,
                            LATITUD = mapCase.LATITUD,
                            LONGITUD = mapCase.LONGITUD
                        )
                    }
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

                // Calcular estadísticas epidemiológicas
                calculateEpidemicStats()
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al cargar datos del mapa", e)
                _loadingError.value = "Error al cargar datos: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Calcula las estadísticas epidemiológicas basadas en los casos actuales
     * Optimizado para ejecutarse en hilo de fondo
     */
    private fun calculateEpidemicStats() {
        viewModelScope.launch(Dispatchers.Default) {
            val allCases = _cases.value

            if (allCases.isEmpty()) {
                withContext(Dispatchers.Main) {
                    _epidemicStats.value = EpidemicStats()
                }
                return@launch
            }

            // Total de casos
            val totalCases = allCases.size

            // Casos por estado (asumiendo FK_ID_ESTADOCASO: 1=Activo, 2=Recuperado, 3=Fallecido)
            val activeCases = allCases.count { it.FK_ID_ESTADOCASO == 1 }
            val recoveredCases = allCases.count { it.FK_ID_ESTADOCASO == 2 }
            val deceasedCases = allCases.count { it.FK_ID_ESTADOCASO == 3 }

            // Casos por tipo de dengue
            val casesByType = mutableMapOf<String, Int>()
            _dengueTypes.value.forEach { dengueType ->
                val count = allCases.count { it.FK_ID_TIPODENGUE == dengueType.ID_TIPODENGUE }
                if (count > 0) {
                    casesByType[dengueType.NOMBRE_TIPODENGUE ?: "Desconocido"] = count
                }
            }

            // Casos por grupo etario
            val casesByAgeGroup = mutableMapOf<String, Int>()
            val ageGroups = listOf(
                "0-4 años" to 1,
                "5-14 años" to 2,
                "15-49 años" to 3,
                "50-64 años" to 4,
                "65+ años" to 5
            )

            ageGroups.forEach { (label, groupId) ->
                val count = allCases.count { case ->
                    case.PACIENTE?.getAgeGroup() == groupId
                }
                if (count > 0) {
                    casesByAgeGroup[label] = count
                }
            }

            // Áreas más afectadas (por barrio/vereda)
            val mostAffectedAreas = allCases
                .mapNotNull { it.BARRIO_VEREDA }
                .filter { it.isNotBlank() }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(5)

            // Nivel de riesgo basado en tendencia
            val riskLevel = when {
                activeCases > totalCases * 0.7 -> "CRÍTICO"
                activeCases > totalCases * 0.5 -> "ALTO"
                activeCases > totalCases * 0.3 -> "MODERADO"
                else -> "BAJO"
            }

            // Tendencia semanal simulada (últimos 7 días)
            // En producción, esto vendría del backend con datos reales por fecha
            val weeklyTrend = listOf(
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-01-") == true },
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-02-") == true },
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-03-") == true },
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-04-") == true },
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-05-") == true },
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-06-") == true },
                allCases.count { it.FECHA_CASOREPORTADO?.contains("-07-") == true }
            ).takeLast(7)

            // Fecha de última actualización
            val lastUpdate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())

            // Actualizar en el hilo principal
            withContext(Dispatchers.Main) {
                _epidemicStats.value = EpidemicStats(
                    totalCases = totalCases,
                    activeCases = activeCases,
                    recoveredCases = recoveredCases,
                    deceasedCases = deceasedCases,
                    casesByType = casesByType,
                    casesByAgeGroup = casesByAgeGroup,
                    weeklyTrend = weeklyTrend,
                    mostAffectedAreas = mostAffectedAreas,
                    riskLevel = riskLevel,
                    lastUpdate = lastUpdate
                )

                Log.d("MapViewModel", "Estadísticas epidemiológicas calculadas: ${_epidemicStats.value}")
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
                // Recargar años disponibles
                val yearsResponse = RetrofitClient.statisticsService.getAvailableYears()
                if (yearsResponse.isSuccessful && yearsResponse.body() != null) {
                    _availableYears.value = yearsResponse.body()!!
                    isYearsFetched = true
                }

                // Lanzar todas las peticiones en paralelo
                val casesDeferred = async { RetrofitClient.statisticsService.getMapCases(_selectedYear.value) }
                val hospitalsDeferred = async { RetrofitClient.hospitalService.getHospitals() }
                val dengueTypesDeferred = async { RetrofitClient.dengueService.getTypesOfDengue() }

                // Esperar a que todas completen
                val casesResponse = casesDeferred.await()
                if (casesResponse.body() != null) {
                    _cases.value = casesResponse.body()!!.map { mapCase ->
                        CaseModel(
                            ID_CASOREPORTADO = mapCase.ID_CASOREPORTADO,
                            DESCRIPCION_CASOREPORTADO = mapCase.DESCRIPCION_CASO ?: "",
                            DIRECCION_CASOREPORTADO = "${mapCase.LATITUD}:${mapCase.LONGITUD}",
                            FECHA_CASOREPORTADO = mapCase.FECHA_REGISTRO,
                            FK_ID_TIPODENGUE = mapCase.FK_ID_TIPODENGUE,
                            FK_ID_ESTADOCASO = mapCase.FK_ID_ESTADO,
                            FK_ID_HOSPITAL = null,
                            FK_ID_PACIENTE = null,
                            FK_ID_PERSONALMEDICO = null,
                            ANIO_REPORTE = mapCase.ANIO_REPORTE,
                            EDAD_PACIENTE = mapCase.EDAD_PACIENTE,
                            NOMBRE_TEMPORAL = mapCase.NOMBRE_PACIENTE,
                            BARRIO_VEREDA = mapCase.BARRIO_VEREDA,
                            LATITUD = mapCase.LATITUD,
                            LONGITUD = mapCase.LONGITUD
                        )
                    }
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

                // Recalcular estadísticas epidemiológicas
                calculateEpidemicStats()

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
     * Actualiza la ubicación de búsqueda y filtra casos
     * Esta ubicación se usa para el radio cuando el usuario busca una dirección
     */
    fun updateSearchLocation(location: LatLng?) {
        _searchLocation.value = location
        filterCasesByProximity()
    }

    /**
     * Limpia la ubicación de búsqueda y vuelve a usar la ubicación del usuario
     */
    fun clearSearchLocation() {
        _searchLocation.value = null
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
     * Actualiza el grupo etario seleccionado (null = todas las edades)
     */
    fun updateSelectedAgeGroup(ageGroup: Int?) {
        _selectedAgeGroup.value = ageGroup
        filterCasesByProximity()
    }

    /**
     * Actualiza el año seleccionado (null = todos los años)
     */
    fun updateSelectedYear(year: Int?) {
        _selectedYear.value = year
        loadAllData() // Recargar datos con el nuevo filtro de año
    }

    /**
     * Filtra los casos según la ubicación del usuario, el radio configurado, el tipo de dengue y el grupo etario
     * Prioridad: si hay ubicación de búsqueda, usa esa; si no, usa ubicación del usuario; si no, muestra todos
     * Optimizado para ejecutarse en hilo de fondo
     */
    private fun filterCasesByProximity() {
        viewModelScope.launch(Dispatchers.Default) {
            // Priorizar ubicación de búsqueda sobre ubicación del usuario
            val searchLoc = _searchLocation.value
            val userLoc = _userLocation.value
            val centerLocation = searchLoc ?: userLoc

            val radius = _filterRadiusKm.value
            val selectedType = _selectedDengueTypeId.value
            val selectedAge = _selectedAgeGroup.value

            var casesToFilter = _cases.value

            Log.d("MapViewModel", "=== INICIANDO FILTRADO ===")
            Log.d("MapViewModel", "Total casos originales: ${casesToFilter.size}")
            Log.d("MapViewModel", "Ubicación búsqueda: $searchLoc")
            Log.d("MapViewModel", "Ubicación usuario: $userLoc")
            Log.d("MapViewModel", "Ubicación centro (usada): $centerLocation")
            Log.d("MapViewModel", "Filtro tipo dengue: $selectedType")
            Log.d("MapViewModel", "Filtro grupo edad: $selectedAge")

            // Filtrar por tipo de dengue si hay uno seleccionado
            if (selectedType != null) {
                casesToFilter = casesToFilter.filter { case ->
                    case.FK_ID_TIPODENGUE == selectedType
                }
                Log.d("MapViewModel", "Casos después filtro dengue: ${casesToFilter.size}")
            }

            // Filtrar por grupo etario si hay uno seleccionado
            if (selectedAge != null) {
                Log.d("MapViewModel", "--- Aplicando filtro de edad ---")

                // Contar casos con fecha de nacimiento
                val casesWithBirthDate = casesToFilter.count { it.PACIENTE?.FECHA_NACIMIENTO_USUARIO != null }
                Log.d("MapViewModel", "Casos con fecha nacimiento: $casesWithBirthDate de ${casesToFilter.size}")

                // Filtrar y loggear cada caso
                casesToFilter = casesToFilter.filter { case ->
                    val paciente = case.PACIENTE
                    val fechaNacimiento = paciente?.FECHA_NACIMIENTO_USUARIO
                    val edad = paciente?.calculateAge()
                    val grupo = paciente?.getAgeGroup()

                    if (paciente != null) {
                        Log.d("MapViewModel", "Paciente: ${paciente.NOMBRE_USUARIO}, FechaNac: $fechaNacimiento, Edad: $edad, Grupo: $grupo, ¿Coincide con $selectedAge?: ${grupo == selectedAge}")
                    }

                    grupo == selectedAge
                }
                Log.d("MapViewModel", "Casos después filtro edad: ${casesToFilter.size}")
            }

            // Filtrar por proximidad si hay una ubicación de referencia (búsqueda o usuario)
            if (centerLocation == null) {
                withContext(Dispatchers.Main) {
                    _filteredCases.value = casesToFilter
                }
                Log.d("MapViewModel", "Sin ubicación de referencia, mostrando todos los casos filtrados: ${casesToFilter.size}")
                return@launch
            }

            val filtered = casesToFilter.filter { case ->
                // Priorizar campos LATITUD y LONGITUD separados (nuevo formato)
                val lat = case.LATITUD
                val lng = case.LONGITUD

                val caseLocation = if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else {
                    // Fallback: parsear DIRECCION_CASOREPORTADO para compatibilidad con datos antiguos
                    case.DIRECCION_CASOREPORTADO?.let { address ->
                        parseLatLngFromString(address)
                    }
                }

                if (caseLocation != null) {
                    val distance = calculateDistance(centerLocation, caseLocation)
                    distance <= radius
                } else {
                    false
                }
            }

            withContext(Dispatchers.Main) {
                _filteredCases.value = filtered
            }

            val locationInfo = if (searchLoc != null) {
                " desde ubicación buscada"
            } else {
                " desde mi ubicación"
            }

            val typeInfo = if (selectedType != null) {
                val typeName = _dengueTypes.value.find { it.ID_TIPODENGUE == selectedType }?.NOMBRE_TIPODENGUE ?: "Desconocido"
                " (tipo: $typeName)"
            } else {
                ""
            }

            val ageInfo = if (selectedAge != null) {
                val ageRange = when(selectedAge) {
                    1 -> "0-4 años"
                    2 -> "5-14 años"
                    3 -> "15-49 años"
                    4 -> "50-64 años"
                    5 -> "65+ años"
                    else -> "Desconocido"
                }
                " (edad: $ageRange)"
            } else {
                ""
            }

            Log.d("MapViewModel", "=== RESULTADO FINAL ===")
            Log.d("MapViewModel", "Casos filtrados: ${filtered.size} de ${_cases.value.size} dentro de ${radius}km$locationInfo$typeInfo$ageInfo")
        }
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
