package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CaseEvolutionViewModel : ViewModel() {

    // --- UI State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // --- Patient States ---
    private val _patientStates = MutableStateFlow<List<PatientStateModel>>(emptyList())
    val patientStates: StateFlow<List<PatientStateModel>> = _patientStates.asStateFlow()

    // --- Evolutions ---
    private val _evolutions = MutableStateFlow<List<CaseEvolutionModel>>(emptyList())
    val evolutions: StateFlow<List<CaseEvolutionModel>> = _evolutions.asStateFlow()

    private val _latestEvolution = MutableStateFlow<CaseEvolutionModel?>(null)
    val latestEvolution: StateFlow<CaseEvolutionModel?> = _latestEvolution.asStateFlow()

    private val _evolutionSummary = MutableStateFlow<CaseEvolutionSummaryModel?>(null)
    val evolutionSummary: StateFlow<CaseEvolutionSummaryModel?> = _evolutionSummary.asStateFlow()

    // --- Form Fields for Creating Evolution ---
    private val _selectedPatientStateId = MutableStateFlow(0)
    val selectedPatientStateId: StateFlow<Int> = _selectedPatientStateId.asStateFlow()

    private val _dayOfIllness = MutableStateFlow("")
    val dayOfIllness: StateFlow<String> = _dayOfIllness.asStateFlow()

    // Signos vitales
    private val _temperature = MutableStateFlow("")
    val temperature: StateFlow<String> = _temperature.asStateFlow()

    private val _systolicBP = MutableStateFlow("")
    val systolicBP: StateFlow<String> = _systolicBP.asStateFlow()

    private val _diastolicBP = MutableStateFlow("")
    val diastolicBP: StateFlow<String> = _diastolicBP.asStateFlow()

    private val _heartRate = MutableStateFlow("")
    val heartRate: StateFlow<String> = _heartRate.asStateFlow()

    private val _respiratoryRate = MutableStateFlow("")
    val respiratoryRate: StateFlow<String> = _respiratoryRate.asStateFlow()

    private val _oxygenSaturation = MutableStateFlow("")
    val oxygenSaturation: StateFlow<String> = _oxygenSaturation.asStateFlow()

    // Laboratorios
    private val _platelets = MutableStateFlow("")
    val platelets: StateFlow<String> = _platelets.asStateFlow()

    private val _hematocrit = MutableStateFlow("")
    val hematocrit: StateFlow<String> = _hematocrit.asStateFlow()

    private val _hemoglobin = MutableStateFlow("")
    val hemoglobin: StateFlow<String> = _hemoglobin.asStateFlow()

    private val _whiteBloodCells = MutableStateFlow("")
    val whiteBloodCells: StateFlow<String> = _whiteBloodCells.asStateFlow()

    // Observaciones
    private val _clinicalObservations = MutableStateFlow("")
    val clinicalObservations: StateFlow<String> = _clinicalObservations.asStateFlow()

    private val _prescribedTreatment = MutableStateFlow("")
    val prescribedTreatment: StateFlow<String> = _prescribedTreatment.asStateFlow()

    // Signos de alarma
    private val _abdominalPain = MutableStateFlow(false)
    val abdominalPain: StateFlow<Boolean> = _abdominalPain.asStateFlow()

    private val _persistentVomiting = MutableStateFlow(false)
    val persistentVomiting: StateFlow<Boolean> = _persistentVomiting.asStateFlow()

    private val _mucosal_bleeding = MutableStateFlow(false)
    val mucosalBleeding: StateFlow<Boolean> = _mucosal_bleeding.asStateFlow()

    private val _lethargy = MutableStateFlow(false)
    val lethargy: StateFlow<Boolean> = _lethargy.asStateFlow()

    // --- Data Loading Functions ---

    /**
     * Load patient states from the API
     */
    fun fetchPatientStates() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.caseEvolutionService.getAllPatientStates()
                if (response.isSuccessful && response.body() != null) {
                    _patientStates.value = response.body()!!
                    Log.d("CaseEvolutionVM", "Loaded ${_patientStates.value.size} patient states")
                } else {
                    Log.e("CaseEvolutionVM", "Failed to load patient states: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CaseEvolutionVM", "Error loading patient states", e)
            }
        }
    }

    /**
     * Load all evolutions for a case
     */
    fun fetchCaseEvolutions(caseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.caseEvolutionService.getCaseEvolutions(caseId)
                if (response.isSuccessful && response.body() != null) {
                    _evolutions.value = response.body()!!
                    Log.d("CaseEvolutionVM", "Loaded ${_evolutions.value.size} evolutions")
                } else {
                    _errorMessage.value = "Error al cargar evoluciones: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
                Log.e("CaseEvolutionVM", "Error loading evolutions", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load latest evolution for a case
     */
    fun fetchLatestEvolution(caseId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.caseEvolutionService.getLatestEvolution(caseId)
                if (response.isSuccessful && response.body() != null) {
                    _latestEvolution.value = response.body()!!
                    Log.d("CaseEvolutionVM", "Loaded latest evolution")
                } else {
                    Log.d("CaseEvolutionVM", "No evolutions found for case")
                }
            } catch (e: Exception) {
                Log.e("CaseEvolutionVM", "Error loading latest evolution", e)
            }
        }
    }

    /**
     * Load evolution summary with trends
     */
    fun fetchEvolutionSummary(caseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.caseEvolutionService.getEvolutionSummary(caseId)
                if (response.isSuccessful && response.body() != null) {
                    _evolutionSummary.value = response.body()!!
                    Log.d("CaseEvolutionVM", "Loaded evolution summary")
                } else {
                    _errorMessage.value = "Error al cargar resumen: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
                Log.e("CaseEvolutionVM", "Error loading summary", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Form Field Setters ---

    fun setSelectedPatientState(stateName: String) {
        val state = _patientStates.value.firstOrNull { it.NOMBRE_ESTADO_PACIENTE == stateName }
        _selectedPatientStateId.value = state?.ID_ESTADO_PACIENTE ?: 0
    }

    fun setDayOfIllness(value: String) { _dayOfIllness.value = value }
    fun setTemperature(value: String) { _temperature.value = value }
    fun setSystolicBP(value: String) { _systolicBP.value = value }
    fun setDiastolicBP(value: String) { _diastolicBP.value = value }
    fun setHeartRate(value: String) { _heartRate.value = value }
    fun setRespiratoryRate(value: String) { _respiratoryRate.value = value }
    fun setOxygenSaturation(value: String) { _oxygenSaturation.value = value }
    fun setPlatelets(value: String) { _platelets.value = value }
    fun setHematocrit(value: String) { _hematocrit.value = value }
    fun setHemoglobin(value: String) { _hemoglobin.value = value }
    fun setWhiteBloodCells(value: String) { _whiteBloodCells.value = value }
    fun setClinicalObservations(value: String) { _clinicalObservations.value = value }
    fun setPrescribedTreatment(value: String) { _prescribedTreatment.value = value }

    fun setAbdominalPain(value: Boolean) { _abdominalPain.value = value }
    fun setPersistentVomiting(value: Boolean) { _persistentVomiting.value = value }
    fun setMucosalBleeding(value: Boolean) { _mucosal_bleeding.value = value }
    fun setLethargy(value: Boolean) { _lethargy.value = value }

    /**
     * Create a new evolution for a case
     */
    fun createEvolution(
        caseId: Int,
        typeOfDengueId: Int,
        doctorId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val request = CreateCaseEvolutionRequest(
                    FK_ID_ESTADO_PACIENTE = _selectedPatientStateId.value,
                    FK_ID_TIPODENGUE = typeOfDengueId,
                    FK_ID_MEDICO = doctorId,
                    DIA_ENFERMEDAD = _dayOfIllness.value.toIntOrNull(),
                    SINTOMAS_REPORTADOS = "[]", // TODO: Implement symptoms selection
                    TEMPERATURA = _temperature.value.toDoubleOrNull(),
                    PRESION_ARTERIAL_SISTOLICA = _systolicBP.value.toIntOrNull(),
                    PRESION_ARTERIAL_DIASTOLICA = _diastolicBP.value.toIntOrNull(),
                    FRECUENCIA_CARDIACA = _heartRate.value.toIntOrNull(),
                    FRECUENCIA_RESPIRATORIA = _respiratoryRate.value.toIntOrNull(),
                    SATURACION_OXIGENO = _oxygenSaturation.value.toDoubleOrNull(),
                    PLAQUETAS = _platelets.value.toIntOrNull(),
                    HEMATOCRITO = _hematocrit.value.toDoubleOrNull(),
                    HEMOGLOBINA = _hemoglobin.value.toDoubleOrNull(),
                    LEUCOCITOS = _whiteBloodCells.value.toIntOrNull(),
                    OBSERVACIONES_CLINICAS = _clinicalObservations.value.ifEmpty { null },
                    TRATAMIENTO_INDICADO = _prescribedTreatment.value.ifEmpty { null }
                )

                Log.d("CaseEvolutionVM", "Creating evolution: $request")
                val response = RetrofitClient.caseEvolutionService.createEvolution(caseId, request)

                if (response.isSuccessful) {
                    _successMessage.value = "Evolución registrada exitosamente"
                    clearForm()
                    onSuccess()
                } else {
                    val errorMsg = "Error al crear evolución: ${response.message()}"
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de red: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                onError(errorMsg)
                Log.e("CaseEvolutionVM", "Error creating evolution", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear form fields
     */
    fun clearForm() {
        _selectedPatientStateId.value = 0
        _dayOfIllness.value = ""
        _temperature.value = ""
        _systolicBP.value = ""
        _diastolicBP.value = ""
        _heartRate.value = ""
        _respiratoryRate.value = ""
        _oxygenSaturation.value = ""
        _platelets.value = ""
        _hematocrit.value = ""
        _hemoglobin.value = ""
        _whiteBloodCells.value = ""
        _clinicalObservations.value = ""
        _prescribedTreatment.value = ""
        _abdominalPain.value = false
        _persistentVomiting.value = false
        _mucosal_bleeding.value = false
        _lethargy.value = false
    }

    /**
     * Clear messages
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
