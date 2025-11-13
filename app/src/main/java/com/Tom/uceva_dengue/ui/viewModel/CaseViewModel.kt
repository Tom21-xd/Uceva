package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.CaseStateModel
import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CaseViewModel : ViewModel() {
    private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
    val cases: StateFlow<List<CaseModel>> = _cases

    private val _filteredCases = MutableStateFlow<List<CaseModel>>(emptyList())
    val filteredCases: StateFlow<List<CaseModel>> = _filteredCases

    // Paginación: casos visibles actualmente
    private val _displayedCases = MutableStateFlow<List<CaseModel>>(emptyList())
    val displayedCases: StateFlow<List<CaseModel>> = _displayedCases

    private val _caseStates = MutableStateFlow<List<CaseStateModel>>(emptyList())
    val caseStates: StateFlow<List<CaseStateModel>> = _caseStates

    private val _typeDengue = MutableStateFlow<List<TypeOfDengueModel>>(emptyList())
    val typeDengue: StateFlow<List<TypeOfDengueModel>> = _typeDengue

    // Estado de carga consolidado
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadingError = MutableStateFlow<String?>(null)
    val loadingError: StateFlow<String?> = _loadingError

    // Estado de refresh para pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Estados de paginación
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages

    private val pageSize = 20 // Cargar 20 casos por página
    private var currentPage = 0

    private var isCasesFetched = false
    private var isCaseStatesFetched = false
    private var isTypeDengueFetched = false

    init {
        loadAllData()
    }

    /**
     * Carga datos desde la API en paralelo
     * @param isRefreshing indica si es una recarga (para pull-to-refresh) o carga inicial
     */
    private suspend fun loadData(isRefreshing: Boolean) = coroutineScope {
        try {
            // Lanzar todas las peticiones en paralelo
            val casesDeferred = async { RetrofitClient.caseService.getCases() }
            val statesDeferred = async { RetrofitClient.caseService.getCaseStates() }
            val dengueTypesDeferred = async { RetrofitClient.dengueService.getTypesOfDengue() }

            // Esperar a que todas completen
            val casesResponse = casesDeferred.await()
            if (casesResponse.isSuccessful && casesResponse.body() != null) {
                val cases = casesResponse.body()!!
                _cases.value = cases
                _filteredCases.value = cases
                isCasesFetched = true

                // Inicializar paginación
                currentPage = 0
                _hasMorePages.value = cases.size > pageSize
                _displayedCases.value = cases.take(pageSize)
            }

            val statesResponse = statesDeferred.await()
            if (statesResponse.isSuccessful && statesResponse.body() != null) {
                _caseStates.value = statesResponse.body()!!
                isCaseStatesFetched = true
            }

            val dengueResponse = dengueTypesDeferred.await()
            if (dengueResponse.isSuccessful && dengueResponse.body() != null) {
                _typeDengue.value = dengueResponse.body()!!
                isTypeDengueFetched = true
            }

            if (isRefreshing) {
                Log.d("CaseViewModel", "Datos actualizados correctamente")
            }
        } catch (e: Exception) {
            Log.e("CaseViewModel", "Error al ${if (isRefreshing) "actualizar" else "cargar"} datos", e)
            _loadingError.value = "Error al ${if (isRefreshing) "actualizar" else "cargar"} datos: ${e.localizedMessage}"
            throw e
        }
    }

    /**
     * Carga todos los datos en paralelo para evitar flasheo de UI
     */
    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingError.value = null
            try {
                loadData(isRefreshing = false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Recarga todos los datos (para pull-to-refresh)
     */
    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _loadingError.value = null
            try {
                loadData(isRefreshing = true)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun fetchCases() {
        if (!isCasesFetched) {
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.caseService.getCases()
                    if (response.isSuccessful && response.body() != null) {
                        val cases = response.body()!!
                        _cases.value = cases
                        _filteredCases.value = cases
                        isCasesFetched = true
                    }
                } catch (e: Exception) {
                    Log.e("CaseViewModel", "Error al obtener los casos", e)
                }
            }
        }
    }

    fun fetchCaseStates() {
        if (!isCaseStatesFetched) {
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.caseService.getCaseStates()
                    if (response.isSuccessful && response.body() != null) {
                        _caseStates.value = response.body()!!
                        isCaseStatesFetched = true
                    }
                } catch (e: Exception) {
                    Log.e("CaseViewModel", "Error al obtener los estados de caso", e)
                }
            }
        }
    }

    fun fetchTypeDengue() {
        if(!isTypeDengueFetched) {
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.dengueService.getTypesOfDengue()
                    if (response.isSuccessful && response.body() != null) {
                        _typeDengue.value = response.body()!!
                        isTypeDengueFetched = true
                    }
                }catch (e:Exception){
                    Log.e("CaseViewModel", "Error al obtener el tipo de dengue", e)
                }
            }
        }
    }

    fun filterCasesByState(estado: String) {
        _filteredCases.value = if (estado == "Todos") {
            _cases.value
        } else {
            _cases.value.filter { case -> case.NOMBRE_ESTADOCASO == estado }
        }

        // Resetear paginación al filtrar
        currentPage = 0
        _hasMorePages.value = _filteredCases.value.size > pageSize
        _displayedCases.value = _filteredCases.value.take(pageSize)
    }

    fun filterCasesByTypeOfDengue(TypeOfDengue: String) {
        _filteredCases.value = if (TypeOfDengue == "Todos") {
            _cases.value
        } else {
            _cases.value.filter { case -> case.NOMBRE_TIPODENGUE == TypeOfDengue }
        }

        // Resetear paginación al filtrar
        currentPage = 0
        _hasMorePages.value = _filteredCases.value.size > pageSize
        _displayedCases.value = _filteredCases.value.take(pageSize)
    }

    /**
     * Cargar más casos (paginación infinita)
     */
    fun loadMoreCases() {
        if (_isLoadingMore.value || !_hasMorePages.value) return

        viewModelScope.launch {
            try {
                _isLoadingMore.value = true

                // Simular carga corta (paginación cliente-side)
                kotlinx.coroutines.delay(300)

                currentPage++
                val startIndex = currentPage * pageSize
                val endIndex = ((currentPage + 1) * pageSize).coerceAtMost(_filteredCases.value.size)

                if (startIndex < _filteredCases.value.size) {
                    val newCases = _filteredCases.value.subList(startIndex, endIndex)
                    _displayedCases.value = _displayedCases.value + newCases
                    _hasMorePages.value = endIndex < _filteredCases.value.size
                } else {
                    _hasMorePages.value = false
                }
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    // HU-006: Eliminar caso
    fun deleteCase(caseId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.caseService.deleteCase(caseId)
                if (response.isSuccessful) {
                    // Actualizar lista local
                    _cases.value = _cases.value.filter { it.ID_CASOREPORTADO != caseId }
                    _filteredCases.value = _filteredCases.value.filter { it.ID_CASOREPORTADO != caseId }
                    isCasesFetched = false
                    onSuccess()
                } else {
                    onError("Error al eliminar el caso: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CaseViewModel", "Error al eliminar el caso", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // HU-012: Obtener historial de casos de un paciente
    fun getCaseHistory(userId: Int, onSuccess: (List<CaseModel>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.caseService.getCaseHistory(userId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener el historial: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CaseViewModel", "Error al obtener el historial de casos", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // Obtener casos por hospital
    fun getCasesByHospital(hospitalId: Int, onSuccess: (List<CaseModel>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.caseService.getCasesByHospital(hospitalId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener casos del hospital: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CaseViewModel", "Error al obtener casos por hospital", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }
}
