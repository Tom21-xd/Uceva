package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.UpdateUserRequest
import com.Tom.uceva_dengue.Data.Model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun loadUserProfile(userId: String?) {
        if (userId.isNullOrBlank()) {
            Log.e("ProfileViewModel", "User ID is null or blank")
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                Log.d("ProfileViewModel", "Loading user profile for ID: $userId")

                val response = RetrofitClient.userService.getUser(userId)

                if (response.isSuccessful && response.body() != null) {
                    _user.value = response.body()
                    Log.d("ProfileViewModel", "User loaded successfully: ${response.body()?.NOMBRE_USUARIO}")
                } else {
                    Log.e("ProfileViewModel", "Error loading user: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception loading user profile", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveProfile(
        nombre: String,
        correo: String,
        direccion: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = _user.value
        if (currentUser == null) {
            onError("No hay usuario cargado")
            return
        }

        viewModelScope.launch {
            try {
                _isSaving.value = true
                Log.d("ProfileViewModel", "Saving profile for user ${currentUser.ID_USUARIO}")

                val updateData = UpdateUserRequest(
                    nombre = nombre,
                    correo = correo,
                    direccion = direccion,
                    id_rol = currentUser.FK_ID_ROL,
                    id_genero = currentUser.FK_ID_GENERO,
                    id_municipio = currentUser.FK_ID_MUNICIPIO
                )

                val response = RetrofitClient.userService.updateProfile(
                    currentUser.ID_USUARIO,
                    updateData
                )

                if (response.isSuccessful) {
                    Log.d("ProfileViewModel", "Profile updated successfully")
                    // Reload user data
                    loadUserProfile(currentUser.ID_USUARIO.toString())
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    Log.e("ProfileViewModel", "Error updating profile: $errorMsg")
                    onError("Error al actualizar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception saving profile", e)
                onError("Error de conexión: ${e.localizedMessage}")
            } finally {
                _isSaving.value = false
            }
        }
    }
}
