package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.UpdateUserRequest
import com.Tom.uceva_dengue.Data.Model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditUserState(
    val user: UserModel? = null,
    val nombre: String = "",
    val correo: String = "",
    val direccion: String = "",
    val selectedRol: Int = 1,
    val selectedGenero: Int = 1,
    val selectedMunicipio: Int = 1,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val nombreError: String? = null,
    val correoError: String? = null,
    val direccionError: String? = null
)

class EditUserViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditUserState())
    val state: StateFlow<EditUserState> = _state.asStateFlow()

    private val userService = RetrofitClient.userService

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = userService.getUser(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        Log.d("EditUserVM", "User loaded successfully: ${user.ID_USUARIO}, Municipio: ${user.FK_ID_MUNICIPIO}")
                        _state.value = _state.value.copy(
                            user = user,
                            nombre = user.NOMBRE_USUARIO ?: "",
                            correo = user.CORREO_USUARIO ?: "",
                            direccion = user.DIRECCION_USUARIO ?: "",
                            selectedRol = user.FK_ID_ROL,
                            selectedGenero = user.FK_ID_GENERO,
                            selectedMunicipio = user.FK_ID_MUNICIPIO ?: 0,
                            isLoading = false
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Usuario no encontrado"
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar usuario: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("EditUserVM", "Error loading user", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun updateNombre(nombre: String) {
        _state.value = _state.value.copy(nombre = nombre, nombreError = null)
    }

    fun updateCorreo(correo: String) {
        _state.value = _state.value.copy(correo = correo, correoError = null)
    }

    fun updateDireccion(direccion: String) {
        _state.value = _state.value.copy(direccion = direccion, direccionError = null)
    }

    fun updateRol(rol: Int) {
        _state.value = _state.value.copy(selectedRol = rol)
    }

    fun updateGenero(genero: Int) {
        _state.value = _state.value.copy(selectedGenero = genero)
    }

    fun updateMunicipio(municipio: Int) {
        _state.value = _state.value.copy(selectedMunicipio = municipio)
    }

    fun validateAndSave(userId: Int, onSuccess: () -> Unit) {
        val currentState = _state.value
        var isValid = true

        // Validar nombre
        if (currentState.nombre.isBlank()) {
            _state.value = _state.value.copy(nombreError = "El nombre es requerido")
            isValid = false
        }

        // Validar correo
        if (currentState.correo.isBlank()) {
            _state.value = _state.value.copy(correoError = "El correo es requerido")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.correo).matches()) {
            _state.value = _state.value.copy(correoError = "Correo inválido")
            isValid = false
        }

        // Validar dirección
        if (currentState.direccion.isBlank()) {
            _state.value = _state.value.copy(direccionError = "La dirección es requerida")
            isValid = false
        }

        if (!isValid) return

        saveUser(userId, onSuccess)
    }

    private fun saveUser(userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            try {
                val userData = UpdateUserRequest(
                    nombre = _state.value.nombre,
                    correo = _state.value.correo,
                    direccion = _state.value.direccion,
                    id_rol = _state.value.selectedRol,
                    id_genero = _state.value.selectedGenero,
                    id_municipio = if (_state.value.selectedMunicipio > 0) _state.value.selectedMunicipio else null
                )

                Log.d("EditUserVM", "Sending update request: userId=$userId, data=$userData")

                val response = userService.updateUser(userId, userData)
                if (response.isSuccessful) {
                    Log.d("EditUserVM", "Update successful")
                    _state.value = _state.value.copy(
                        isSaving = false,
                        successMessage = "Usuario actualizado con éxito"
                    )
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditUserVM", "Update failed: ${response.code()}, body: $errorBody")
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Error al actualizar usuario: ${response.code()} - ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("EditUserVM", "Error saving user", e)
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveUser(
        userId: Int,
        nombre: String,
        correo: String,
        direccion: String,
        idRol: Int,
        idGenero: Int,
        idMunicipio: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            try {
                val userData = UpdateUserRequest(
                    nombre = nombre,
                    correo = correo,
                    direccion = direccion,
                    id_rol = idRol,
                    id_genero = idGenero,
                    id_municipio = if (idMunicipio > 0) idMunicipio else null
                )

                Log.d("EditUserVM", "Saving user: userId=$userId, data=$userData")

                val response = userService.updateUser(userId, userData)
                if (response.isSuccessful) {
                    Log.d("EditUserVM", "User saved successfully")
                    _state.value = _state.value.copy(
                        isSaving = false,
                        successMessage = "Usuario guardado correctamente"
                    )
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditUserVM", "Save failed: ${response.code()}, body: $errorBody")
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Error: ${response.code()} - ${response.message()}"
                    )
                    onError("Error al guardar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("EditUserVM", "Exception saving user", e)
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
                onError("Error de conexión: ${e.message}")
            }
        }
    }
}
