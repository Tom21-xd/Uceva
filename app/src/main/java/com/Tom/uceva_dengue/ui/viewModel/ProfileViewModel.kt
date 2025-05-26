package com.Tom.uceva_dengue.ui.viewModel

import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(role: Int) : ViewModel() {
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchUserProfile(role)
    }

    private fun fetchUserProfile(role : Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = RetrofitClient.userService.getUser(role.toString())
                Log.d("ProfileViewModel", "Response: $response")
                if (response.isSuccessful && response.body() != null) {
                    _user.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Error al cargar el perfil del usuario"
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error al obtener el perfil del usuario", e)
                _error.value = "Error de conexión o servidor no disponible"
            } finally {
                _loading.value = false
            }
        }
    }

}