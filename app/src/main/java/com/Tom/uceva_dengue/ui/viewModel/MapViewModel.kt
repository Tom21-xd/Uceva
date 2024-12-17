package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.utils.CasoReportadoRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val casoReportadoRepository: CasoReportadoRepository) : ViewModel() {

    private val _reportedCases = MutableStateFlow<List<LatLng>>(emptyList())
    val reportedCases: StateFlow<List<LatLng>> = _reportedCases

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchCases()
    }

    private fun fetchCases() {
        viewModelScope.launch {
            _isLoading.value = true // Indicar que la carga está en proceso
            try {
                val cases = casoReportadoRepository.fetchReportedCases()
                _reportedCases.value = cases // Actualizar los casos reportados
            } catch (e: Exception) {
                _errorMessage.value = e.message // Capturar el mensaje de error
            } finally {
                _isLoading.value = false // Indicar que la carga ha terminado
            }
        }
    }
}
