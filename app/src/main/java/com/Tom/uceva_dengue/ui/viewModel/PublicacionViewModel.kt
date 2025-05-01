package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PublicacionViewModel : ViewModel() {

//    private val getPublicacionesUseCase = GetPublicacionesUseCase()
//
//    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
//    val publicaciones = _publicaciones.asStateFlow()
//
//    init {
//        obtenerPublicaciones()
//    }
//
//    private fun obtenerPublicaciones() {
//        viewModelScope.launch {
//            getPublicacionesUseCase.execute().collect {
//                _publicaciones.value = it
//            }
//        }
//    }
private val _publicaciones = MutableStateFlow(
    listOf(
        PublicationModel(
            ID_PUBLICACION = 1,
            TITULO_PUBLICACION = "Prevención del Dengue",
            DESCRIPCION_PUBLICACION = "Evita la proliferación del mosquito eliminando criaderos de agua estancada.",
            FECHA_PUBLICACION = "2024-02-13",
            IMAGEN_PUBLICACION = "",
            FK_ID_USUARIO = 1,
            NOMBRE_USUARIO = "Ministerio de Salud"
        )
    )
)

    val publicaciones = _publicaciones.asStateFlow()
}
