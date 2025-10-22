package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.PublicationModel
import com.Tom.uceva_dengue.Data.Model.PublicationCommentModel
import com.Tom.uceva_dengue.Data.Model.CreateCommentRequest
import com.Tom.uceva_dengue.Data.Model.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicacionViewModel : ViewModel() {
    private val _publicaciones = MutableStateFlow<List<PublicationModel>>(emptyList())
    val publicaciones = _publicaciones.asStateFlow()

    // Estado de refresh para pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        obtenerPublicaciones()
    }

    fun obtenerPublicaciones(userId: Int? = null) {
        viewModelScope.launch {
            try {
                _publicaciones.value = RetrofitClient.publicationService.getPublications(userId)
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones", e)
            }
        }
    }

    /**
     * Obtener feed inteligente y actualizar StateFlow
     * Ordena por: Fijadas > Prioridad (Urgente > Alta > Normal > Baja) > Fecha
     */
    fun obtenerFeedInteligente(
        userId: Int? = null,
        ciudadId: Int? = null,
        categoriaId: Int? = null,
        limit: Int = 50
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getFeed(
                    ciudadId = ciudadId,
                    categoriaId = categoriaId,
                    userId = userId,
                    limit = limit,
                    offset = 0
                )
                if (response.isSuccessful && response.body() != null) {
                    _publicaciones.value = response.body()!!
                } else {
                    Log.e("PublicacionViewModel", "Error al obtener feed: ${response.message()}")
                    // Fallback al método básico
                    obtenerPublicaciones(userId)
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener feed inteligente", e)
                // Fallback al método básico
                obtenerPublicaciones(userId)
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

    /**
     * Recarga las publicaciones (para pull-to-refresh)
     */
    fun refreshData(userId: Int? = null) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                _publicaciones.value = RetrofitClient.publicationService.getPublications(userId)
                Log.d("PublicacionViewModel", "Publicaciones actualizadas correctamente")
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al actualizar publicaciones", e)
            } finally {
                _isRefreshing.value = false
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
        userName: String? = null,
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
                        // Create comment object with user info
                        val userInfo = if (userName != null) {
                            UserInfo(
                                ID_USUARIO = userId,
                                NOMBRE_USUARIO = userName,
                                CORREO_USUARIO = "",
                                NOMBRE_ROL = null
                            )
                        } else null

                        val newComment = PublicationCommentModel(
                            ID_COMENTARIO = (commentMap["ID_COMENTARIO"] as? Double)?.toInt() ?: 0,
                            FK_ID_PUBLICACION = publicationId,
                            FK_ID_USUARIO = userId,
                            CONTENIDO_COMENTARIO = content,
                            FK_ID_COMENTARIO_PADRE = parentCommentId,
                            FECHA_COMENTARIO = "",
                            ESTADO_COMENTARIO = true,
                            USUARIO = userInfo,
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

    /**
     * Actualiza el estado local de una publicación sin recargar toda la lista
     * SOLO actualiza el estado de interacción del usuario.
     * Los contadores se actualizarán en la próxima recarga.
     */
    fun updatePublicationState(
        publicationId: Int,
        updateReaction: Boolean = false,
        hasReacted: Boolean = false,
        updateSave: Boolean = false,
        hasSaved: Boolean = false,
        userId: Int? = null
    ) {
        _publicaciones.value = _publicaciones.value.map { publication ->
            if (publication.ID_PUBLICACION == publicationId) {
                publication.copy(
                    USUARIO_HA_REACCIONADO = if (updateReaction) hasReacted else publication.USUARIO_HA_REACCIONADO,
                    USUARIO_HA_GUARDADO = if (updateSave) hasSaved else publication.USUARIO_HA_GUARDADO
                )
            } else {
                publication
            }
        }

        // Recargar la publicación específica del backend para obtener contadores actualizados
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getPublicationById(publicationId, userId)
                if (response.isSuccessful && response.body() != null) {
                    val updatedPublication = response.body()!!
                    // Actualizar solo esta publicación en la lista
                    _publicaciones.value = _publicaciones.value.map { publication ->
                        if (publication.ID_PUBLICACION == publicationId) {
                            updatedPublication
                        } else {
                            publication
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al actualizar publicación", e)
            }
        }
    }

    // ==================== NUEVAS FUNCIONALIDADES ====================

    /**
     * Obtener feed inteligente ordenado por prioridad
     */
    fun getFeed(
        userId: Int? = null,
        ciudadId: Int? = null,
        categoriaId: Int? = null,
        limit: Int = 20,
        offset: Int = 0,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getFeed(
                    ciudadId = ciudadId,
                    categoriaId = categoriaId,
                    userId = userId,
                    limit = limit,
                    offset = offset
                )
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener el feed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener feed", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Obtener publicaciones por categoría
     */
    fun getPublicationsByCategory(
        categoryId: Int,
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getPublicationsByCategory(categoryId, userId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener publicaciones por categoría: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones por categoría", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Obtener publicaciones urgentes
     */
    fun getUrgentPublications(
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getUrgentPublications(userId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener publicaciones urgentes: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones urgentes", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Obtener publicaciones fijadas
     */
    fun getPinnedPublications(
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getPinnedPublications(userId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener publicaciones fijadas: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones fijadas", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Obtener publicaciones cercanas a una ubicación
     */
    fun getNearbyPublications(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getNearbyPublications(
                    latitude = latitude,
                    longitude = longitude,
                    radiusKm = radiusKm,
                    userId = userId
                )
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener publicaciones cercanas: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones cercanas", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Obtener publicaciones por etiqueta
     */
    fun getPublicationsByTag(
        tagId: Int,
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getPublicationsByTag(tagId, userId)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener publicaciones por etiqueta: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener publicaciones por etiqueta", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Buscar publicaciones avanzado
     */
    fun searchPublicationsAdvanced(
        query: String,
        categoriaId: Int? = null,
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.searchPublications(
                    query = query,
                    categoriaId = categoriaId,
                    userId = userId
                )
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al buscar publicaciones: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al buscar publicaciones", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Registrar vista en una publicación
     */
    fun registerView(
        publicationId: Int,
        userId: Int,
        readTimeSeconds: Int? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val viewData = com.Tom.uceva_dengue.Data.Model.RegisterViewRequest(
                    UserId = userId,
                    ReadTimeSeconds = readTimeSeconds
                )
                val response = RetrofitClient.publicationService.registerView(publicationId, viewData)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al registrar vista: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al registrar vista", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Obtener publicaciones trending (más populares)
     */
    fun getTrendingPublications(
        limit: Int = 10,
        days: Int = 7,
        userId: Int? = null,
        onSuccess: (List<PublicationModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.publicationService.getTrendingPublications(
                    limit = limit,
                    days = days,
                    userId = userId
                )
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error al obtener trending: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PublicacionViewModel", "Error al obtener trending", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }
}

