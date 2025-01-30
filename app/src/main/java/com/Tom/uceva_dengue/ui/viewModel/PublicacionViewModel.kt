package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import com.Tom.uceva_dengue.Domain.UseCases.Publicacion.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicacionViewModel : ViewModel() {

    private val getPublicacionesUseCase = GetPublicacionesUseCase()

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones = _publicaciones.asStateFlow()

    init {
        obtenerPublicaciones()
    }

    private fun obtenerPublicaciones() {
        viewModelScope.launch {
            getPublicacionesUseCase.execute().collect {
                _publicaciones.value = it
            }
        }
    }
}
