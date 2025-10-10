package com.Tom.uceva_dengue.ui.viewModel

import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.CaseStateModel
import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CaseViewModel : ViewModel() {
    private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
    val cases: StateFlow<List<CaseModel>> = _cases

    private val _filteredCases = MutableStateFlow<List<CaseModel>>(emptyList())
    val filteredCases: StateFlow<List<CaseModel>> = _filteredCases

    private val _caseStates = MutableStateFlow<List<CaseStateModel>>(emptyList())
    val caseStates: StateFlow<List<CaseStateModel>> = _caseStates

    private val _typeDengue = MutableStateFlow<List<TypeOfDengueModel>>(emptyList())
    val typeDengue: StateFlow<List<TypeOfDengueModel>> = _typeDengue

    private var isCasesFetched = false
    private var isCaseStatesFetched = false
    private var isTypeDengueFetched = false

    init {
        fetchCases()
        fetchCaseStates()
        fetchTypeDengue()
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
                    val response = RetrofitClient.caseService.getStateCase()
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
    }

    fun filterCasesByTypeOfDengue(TypeOfDengue: String) {
        _filteredCases.value = if (TypeOfDengue == "Todos") {
            _cases.value
        } else {
            _cases.value.filter { case -> case.NOMBRE_TIPODENGUE == TypeOfDengue }
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
