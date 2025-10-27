package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.Permission
import com.Tom.uceva_dengue.Data.Model.RolePermissionsResponse
import com.Tom.uceva_dengue.Data.Model.UpdateRolePermissionsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de permisos por rol
 * Conectado con PermissionService vía RetrofitClient
 */
class PermissionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PermissionsUiState>(PermissionsUiState.Loading)
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    private val _selectedRole = MutableStateFlow(1) // Admin por defecto
    val selectedRole: StateFlow<Int> = _selectedRole.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Cache de permisos actuales para toggle rápido
    private val _currentPermissions = MutableStateFlow<List<Int>>(emptyList())

    init {
        loadPermissions()
    }

    /**
     * Carga los permisos del rol seleccionado desde la API
     */
    fun loadPermissions(roleId: Int = _selectedRole.value) {
        viewModelScope.launch {
            _uiState.value = PermissionsUiState.Loading
            try {
                val response = RetrofitClient.permissionService.getRolePermissions(roleId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _uiState.value = PermissionsUiState.Success(data)
                    // Guardar IDs de permisos actuales
                    _currentPermissions.value = data.permissions.map { it.id }
                } else {
                    _uiState.value = PermissionsUiState.Error(
                        "Error al cargar permisos: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = PermissionsUiState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Cambia el rol seleccionado
     */
    fun selectRole(roleId: Int) {
        _selectedRole.value = roleId
        loadPermissions(roleId)
    }

    /**
     * Actualiza la búsqueda
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Activa/desactiva un permiso para el rol actual
     */
    fun togglePermission(permissionId: Int, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val updatedPermissions = if (enabled) {
                    _currentPermissions.value + permissionId
                } else {
                    _currentPermissions.value.filter { it != permissionId }
                }

                val request = UpdateRolePermissionsRequest(updatedPermissions)
                val response = RetrofitClient.permissionService.updateRolePermissions(
                    roleId = _selectedRole.value,
                    request = request
                )

                if (response.isSuccessful) {
                    // Recargar permisos después de actualizar
                    loadPermissions()
                } else {
                    _uiState.value = PermissionsUiState.Error(
                        "Error al actualizar permiso: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = PermissionsUiState.Error(
                    e.message ?: "Error al actualizar"
                )
            }
        }
    }

    /**
     * Agrupa permisos por categoría con filtro de búsqueda
     */
    fun getPermissionsByCategory(): Map<String, List<Permission>> {
        return when (val state = _uiState.value) {
            is PermissionsUiState.Success -> {
                val query = _searchQuery.value.lowercase()
                state.data.permissions
                    .filter {
                        if (query.isEmpty()) true
                        else it.name.lowercase().contains(query) ||
                             it.description?.lowercase()?.contains(query) == true ||
                             it.category?.lowercase()?.contains(query) == true
                    }
                    .groupBy { it.category ?: "Sin categoría" }
            }
            else -> emptyMap()
        }
    }
}

/**
 * Estados de la UI
 */
sealed class PermissionsUiState {
    object Loading : PermissionsUiState()
    data class Success(val data: RolePermissionsResponse) : PermissionsUiState()
    data class Error(val message: String) : PermissionsUiState()
}
