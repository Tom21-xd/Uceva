package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.NotificationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            Log.d("NotificationVM", "Solicitando notificaciones al servidor...")
            val response = RetrofitClient.notificationService.getNotifications()
            Log.d("NotificationVM", "Respuesta recibida - Código: ${response.code()}, Exitoso: ${response.isSuccessful}")

            if (response.isSuccessful && response.body() != null) {
                val notificationsList = response.body()!!
                _notifications.value = notificationsList
                Log.d("NotificationVM", "Notificaciones cargadas: ${notificationsList.size} items")
                notificationsList.forEachIndexed { index, notif ->
                    Log.d("NotificationVM", "[$index] ID: ${notif.ID_NOTIFICACION}, Contenido: ${notif.CONTENIDO_NOTIFICACION}")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("NotificationVM", "Error al cargar notificaciones - Mensaje: ${response.message()}, Body: $errorBody")
                _error.value = "Error al cargar notificaciones: ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Excepción al obtener notificaciones: ${e.message}", e)
            _error.value = "Error de red: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    fun refreshData() = viewModelScope.launch {
        _isRefreshing.value = true
        _error.value = null
        try {
            Log.d("NotificationVM", "Refrescando notificaciones...")
            val response = RetrofitClient.notificationService.getNotifications()

            if (response.isSuccessful && response.body() != null) {
                val notificationsList = response.body()!!
                _notifications.value = notificationsList
                Log.d("NotificationVM", "Notificaciones refrescadas: ${notificationsList.size} items")
            } else {
                Log.e("NotificationVM", "Error al refrescar notificaciones: ${response.message()}")
                _error.value = "Error al actualizar: ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Error al refrescar notificaciones", e)
            _error.value = "Error de red: ${e.localizedMessage}"
        } finally {
            _isRefreshing.value = false
        }
    }

    fun fetchUnreadNotifications() = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val response = RetrofitClient.notificationService.getUnreadNotifications()
            if (response.isSuccessful && response.body() != null) {
                _notifications.value = response.body()!!
            } else {
                _error.value = "Error al cargar notificaciones no leídas: ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Error al obtener notificaciones no leídas", e)
            _error.value = "Error de red: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    fun markAsRead(notificationId: Int) = viewModelScope.launch {
        try {
            val response = RetrofitClient.notificationService.markAsRead(notificationId)
            if (response.isSuccessful) {
                _successMessage.value = "Notificación marcada como leída"
                fetchNotifications()
            } else {
                _error.value = "Error al marcar como leída: ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Error al marcar notificación como leída", e)
            _error.value = "Error de red: ${e.localizedMessage}"
        }
    }

    fun markAllAsRead() = viewModelScope.launch {
        _isLoading.value = true
        try {
            val response = RetrofitClient.notificationService.markAllAsRead()
            if (response.isSuccessful) {
                _successMessage.value = "Todas las notificaciones marcadas como leídas"
                fetchNotifications()
            } else {
                _error.value = "Error al marcar todas como leídas: ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Error al marcar todas las notificaciones como leídas", e)
            _error.value = "Error de red: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}
