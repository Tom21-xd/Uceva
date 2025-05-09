package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicacionViewModel : ViewModel() {
    private val _publicaciones = MutableStateFlow<List<PublicationModel>>(emptyList())
    val publicaciones = _publicaciones.asStateFlow()

    init {
        obtenerPublicaciones()
    }

    fun obtenerPublicaciones() {
        viewModelScope.launch {
            try {
                _publicaciones.value = RetrofitClient.publicationService.getPublications()
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones", e)
            }
        }
    }

    fun buscarPublicacion(nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isNotBlank()) {
                    _publicaciones.value = RetrofitClient.publicationService.getPublication(nombre)
                } else {
                    obtenerPublicaciones() // Si el campo está vacío, obtenemos todas las publicaciones
                }
                Log.d("PublicacionViewModel", "Publicaciones: ${_publicaciones.value}")
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al buscar publicaciones", e)
            }
        }
    }
}

