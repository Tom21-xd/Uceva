package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserManagementState(
    val users: List<UserModel> = emptyList(),
    val filteredUsers: List<UserModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedRoleFilter: Int? = null
)

class UserManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(UserManagementState())
    val state: StateFlow<UserManagementState> = _state.asStateFlow()

    private val userService = RetrofitClient.userService

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = userService.getUsers()
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    _state.value = _state.value.copy(
                        users = users,
                        filteredUsers = users,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar usuarios: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("UserManagementVM", "Error loading users", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun searchUsers(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        filterUsers()
    }

    fun filterByRole(roleId: Int?) {
        _state.value = _state.value.copy(selectedRoleFilter = roleId)
        filterUsers()
    }

    private fun filterUsers() {
        val currentState = _state.value
        val query = currentState.searchQuery.lowercase()
        val roleFilter = currentState.selectedRoleFilter

        val filtered = currentState.users.filter { user ->
            val matchesQuery = query.isEmpty() ||
                (user.NOMBRE_USUARIO?.lowercase()?.contains(query) ?: false) ||
                (user.CORREO_USUARIO?.lowercase()?.contains(query) ?: false) ||
                user.ID_USUARIO.toString().contains(query)

            val matchesRole = roleFilter == null || user.FK_ID_ROL == roleFilter

            matchesQuery && matchesRole
        }

        _state.value = currentState.copy(filteredUsers = filtered)
    }

    fun deleteUser(userId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userService.deleteUser(userId)
                if (response.isSuccessful) {
                    // Reload users after deletion
                    loadUsers()
                    onSuccess()
                } else {
                    onError("Error al eliminar usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserManagementVM", "Error deleting user", e)
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
