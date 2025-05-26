package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}

