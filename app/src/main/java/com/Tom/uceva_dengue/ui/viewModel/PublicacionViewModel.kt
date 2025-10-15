package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
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

    // DELETE: Eliminar publicación
    fun deletePublication(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.deletePublication(id)
                if (response.isSuccessful) {
                    // Actualizar lista local
                    _publicaciones.value = _publicaciones.value.filter { it.ID_PUBLICACION != id }
                    Log.d("PublicacionViewModel", "Publicación eliminada con éxito")
                    onSuccess()
                } else {
                    val errorMsg = "Error al eliminar: ${response.message()}"
                    Log.e("PublicacionViewModel", errorMsg)
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de red: ${e.localizedMessage}"
                Log.e("PublicacionViewModel", "Error al eliminar publicación", e)
                onError(errorMsg)
            }
        }
    }

    // UPDATE: Actualizar publicación
    fun updatePublication(
        id: Int,
        titulo: String,
        descripcion: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val publicationData = mapOf(
                    "Titulo" to titulo,
                    "Descripcion" to descripcion
                )

                val response = RetrofitClient.publicationService.updatePublication(id, publicationData)
                if (response.isSuccessful) {
                    Log.d("PublicacionViewModel", "Publicación actualizada con éxito")
                    obtenerPublicaciones() // Recargar lista
                    onSuccess()
                } else {
                    val errorMsg = "Error al actualizar: ${response.message()}"
                    Log.e("PublicacionViewModel", errorMsg)
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error de red: ${e.localizedMessage}"
                Log.e("PublicacionViewModel", "Error al actualizar publicación", e)
                onError(errorMsg)
            }
        }
    }

    // Obtener una publicación por ID
    fun getPublicationById(id: Int, onSuccess: (com.Tom.uceva_dengue.Data.Model.PublicationModel) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getPublicationById(id)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener la publicación: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicación por ID", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }
}

