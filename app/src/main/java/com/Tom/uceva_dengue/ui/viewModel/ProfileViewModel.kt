package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUserProfile(userId: String?) {
        if (userId.isNullOrBlank()) {
            _error.value = "ID de usuario no disponible"
            _loading.value = false
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                Log.d("ProfileViewModel", "Fetching user with ID: $userId")
                val response = RetrofitClient.userService.getUser(userId)
                Log.d("ProfileViewModel", "Response: ${response.code()} - ${response.message()}")

                if (response.isSuccessful && response.body() != null) {
                    _user.value = response.body()
                    Log.d("ProfileViewModel", "User loaded: ${response.body()?.NOMBRE_USUARIO}")
                } else {
                    _error.value = "Error al cargar el perfil (${response.code()})"
                    Log.e("ProfileViewModel", "Error response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error al obtener el perfil del usuario", e)
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}