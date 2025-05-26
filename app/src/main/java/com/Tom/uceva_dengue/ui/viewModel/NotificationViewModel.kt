package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val response = RetrofitClient.notificationService.getNotifications()
            if (response.isSuccessful && response.body() != null) {
                _notifications.value = response.body()!!
            } else {
                _error.value = "Error al cargar notificaciones: ${response.message()}"
            }
        } catch (e: Exception) {
            Log.e("NotificationVM", "Error al obtener notificaciones", e)
            _error.value = "Error de red: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }
}
