package com.Tom.uceva_dengue.ui.viewModel

import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CaseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MapViewModel : ViewModel() {

    private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
    val cases: StateFlow<List<CaseModel>> = _cases

    private var isCasesFetched = false

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

    init {
        fetchCases()
    }
}
