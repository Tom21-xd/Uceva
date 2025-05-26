package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.CaseStateModel
import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
import com.Tom.uceva_dengue.Data.Model.UpdateCaseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CaseDetailsViewModel : ViewModel() {

    // --- UI state ---
    private val _case = MutableStateFlow<CaseModel?>(null)
    val case: StateFlow<CaseModel?> = _case.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // editable fields
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    // lookup lists
    private val _states = MutableStateFlow<List<CaseStateModel>>(emptyList())
    val states: StateFlow<List<CaseStateModel>> = _states.asStateFlow()

    private val _typesOfDengue = MutableStateFlow<List<TypeOfDengueModel>>(emptyList())
    val typesOfDengue: StateFlow<List<TypeOfDengueModel>> = _typesOfDengue.asStateFlow()

    // selected IDs
    private val _selectedStateId = MutableStateFlow(0)
    val selectedStateId: StateFlow<Int> = _selectedStateId.asStateFlow()

    private val _selectedDengueTypeId = MutableStateFlow(0)
    val selectedDengueTypeId: StateFlow<Int> = _selectedDengueTypeId.asStateFlow()

    /** Call to load case details, all possible states & dengue-types */
    fun fetchData(caseId: String) {
        fetchCaseById(caseId)
        fetchStates()
        fetchDengueTypes()
    }

    private fun fetchCaseById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = RetrofitClient.caseService.getCaseById(id)
                if (resp.isSuccessful && resp.body() != null) {
                    val c = resp.body()!!
                    _case.value = c
                    // seed editable fields
                    _description.value = c.DESCRIPCION_CASOREPORTADO ?: ""
                    _selectedStateId.value = c.FK_ID_ESTADOCASO ?: 0
                    _selectedDengueTypeId.value = c.FK_ID_TIPODENGUE ?: 0
                } else {
                    _errorMessage.value = "Error al cargar caso: ${resp.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
                Log.e("CaseDetailsVM", "fetchCaseById", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchStates() {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.caseService.getStateCase()
                if (resp.isSuccessful && resp.body() != null) {
                    _states.value = resp.body()!!
                } else {
                    Log.e("CaseDetailsVM", "fetchStates failed: ${resp.message()}")
                }
            } catch (e: Exception) {
                Log.e("CaseDetailsVM", "fetchStates", e)
            }
        }
    }

    private fun fetchDengueTypes() {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.dengueService.getTypesOfDengue()
                if (resp.isSuccessful && resp.body() != null) {
                    _typesOfDengue.value = resp.body()!!
                } else {
                    Log.e("CaseDetailsVM", "fetchDengueTypes failed: ${resp.message()}")
                }
            } catch (e: Exception) {
                Log.e("CaseDetailsVM", "fetchDengueTypes", e)
            }
        }
    }

    /** Called from the UI when the user edits the description */
    fun setDescription(text: String) {
        _description.value = text
    }

    /** Called when the user picks a new state from the ComboBox */
    fun setSelectedState(name: String) {
        val st = _states.value.firstOrNull { it.NOMBRE_ESTADOCASO == name }
        _selectedStateId.value = st?.ID_ESTADOCASO ?: 0
    }

    /** Called when the user picks a new dengue type from the ComboBox */
    fun setSelectedDengue(name: String) {
        val dt = _typesOfDengue.value.firstOrNull { it.NOMBRE_TIPODENGUE == name }
        _selectedDengueTypeId.value = dt?.ID_TIPODENGUE ?: 0
    }

    /**
     * Sends the update‐case request with the current
     * state, dengue type and description.
     */
    fun updateCase(
        caseId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = UpdateCaseModel(
                    idestadoCaso = _selectedStateId.value,
                    idTipoDengue = _selectedDengueTypeId.value,
                    description = _description.value
                )
                Log.d("CaseDetailsVM", "updateCase: $request")
                val resp = RetrofitClient.caseService.updateCase(caseId, request)
                Log.d("CaseDetailsVM", "updateCase: $resp")
                if (resp.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al actualizar: ${resp.message()}")
                }
            } catch (e: Exception) {
                onError("Excepción: ${e.localizedMessage}")
                Log.e("CaseDetailsVM", "updateCase", e)
            }
        }
    }
}
