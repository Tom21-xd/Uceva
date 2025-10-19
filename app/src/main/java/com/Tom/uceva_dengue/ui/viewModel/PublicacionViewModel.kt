package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import com.Tom.uceva_dengue.Data.Model.PublicationCommentModel
import com.Tom.uceva_dengue.Data.Model.CreateCommentRequest
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

    // ==================== REACCIONES ====================

    fun toggleReaction(
        publicationId: Int,
        userId: Int,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.toggleReaction(publicationId, userId)
                if (response.isSuccessful) {
                    val hasReaction = response.body()?.get("hasReaction") as? Boolean ?: false
                    onSuccess(hasReaction)
                } else {
                    onError("Error al procesar reacción: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al toggle reaction", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // ==================== COMENTARIOS ====================

    fun loadComments(
        publicationId: Int,
        onSuccess: (List<PublicationCommentModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getComments(publicationId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al cargar comentarios: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al cargar comentarios", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun createComment(
        publicationId: Int,
        userId: Int,
        content: String,
        parentCommentId: Int?,
        onSuccess: (PublicationCommentModel) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val commentRequest = CreateCommentRequest(
                    Content = content,
                    ParentCommentId = parentCommentId
                )
                val response = RetrofitClient.publicationService.createComment(publicationId, userId, commentRequest)
                if (response.isSuccessful) {
                    val commentMap = response.body()?.get("comment") as? Map<*, *>
                    if (commentMap != null) {
                        // Parse the comment from the response
                        // For now, create a simple comment object
                        val newComment = PublicationCommentModel(
                            ID_COMENTARIO = (commentMap["ID_COMENTARIO"] as? Double)?.toInt() ?: 0,
                            FK_ID_PUBLICACION = publicationId,
                            FK_ID_USUARIO = userId,
                            CONTENIDO_COMENTARIO = content,
                            FK_ID_COMENTARIO_PADRE = parentCommentId,
                            FECHA_COMENTARIO = "",
                            ESTADO_COMENTARIO = true,
                            USUARIO = null,
                            RESPUESTAS = null
                        )
                        onSuccess(newComment)
                    } else {
                        onError("Error al procesar respuesta")
                    }
                } else {
                    onError("Error al crear comentario: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al crear comentario", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun deleteComment(
        commentId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.deleteComment(commentId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al eliminar comentario: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al eliminar comentario", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // ==================== GUARDADOS ====================

    fun toggleSave(
        publicationId: Int,
        userId: Int,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.toggleSave(publicationId, userId)
                if (response.isSuccessful) {
                    val isSaved = response.body()?.get("isSaved") as? Boolean ?: false
                    onSuccess(isSaved)
                } else {
                    onError("Error al guardar publicación: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al toggle save", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    // ==================== INTERACCIONES ====================

    fun loadUserInteractions(
        publicationId: Int,
        userId: Int,
        onSuccess: (Boolean, Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getUserInteractions(publicationId, userId)
                if (response.isSuccessful && response.body() != null) {
                    val hasReacted = response.body()?.get("hasReacted") ?: false
                    val hasSaved = response.body()?.get("hasSaved") ?: false
                    onSuccess(hasReacted, hasSaved)
                } else {
                    onError("Error al cargar interacciones: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al cargar interacciones", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun loadSavedPublications(
        userId: Int,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getSavedPublications(userId)
                if (response.isSuccessful && response.body() != null) {
                    // Extract publications from SavedPublicationModel
                    val publications = response.body()!!.mapNotNull { it.PUBLICACION }
                    onSuccess(publications)
                } else {
                    onError("Error al cargar guardados: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al cargar publicaciones guardadas", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }
}

