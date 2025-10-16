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


class MapViewModel : ViewModel() {

    private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
    val cases: StateFlow<List<CaseModel>> = _cases

    private val _hospitals = MutableStateFlow<List<HospitalModel>>(emptyList())
    val hospitals: StateFlow<List<HospitalModel>> = _hospitals

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
        fetchCases()
        fetchHospitals()
    }
}
