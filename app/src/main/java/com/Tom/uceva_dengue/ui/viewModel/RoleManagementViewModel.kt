package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoleManagementViewModel : ViewModel() {

    private val roleService = RetrofitClient.roleService
    private val permissionService = RetrofitClient.permissionService

    // Estado de roles
    private val _roles = MutableStateFlow<List<RoleModel>>(emptyList())
    val roles: StateFlow<List<RoleModel>> = _roles

    // Estado de todos los permisos
    private val _allPermissions = MutableStateFlow<List<PermissionCategory>>(emptyList())
    val allPermissions: StateFlow<List<PermissionCategory>> = _allPermissions

    // Estado de permisos de un rol específico
    private val _rolePermissions = MutableStateFlow<RolePermissionsResponse?>(null)
    val rolePermissions: StateFlow<RolePermissionsResponse?> = _rolePermissions

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado de éxito
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // Rol seleccionado para edición
    private val _selectedRole = MutableStateFlow<RoleModel?>(null)
    val selectedRole: StateFlow<RoleModel?> = _selectedRole

    init {
        loadRoles()
        loadAllPermissions()
    }

    /**
     * Carga todos los roles
     */
    fun loadRoles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = roleService.getRoles()
                if (response.isSuccessful && response.body() != null) {
                    _roles.value = response.body()!!
                    Log.d("RoleManagement", "Roles cargados: ${_roles.value.size}")
                } else {
                    _errorMessage.value = "Error al cargar roles: ${response.message()}"
                    Log.e("RoleManagement", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al cargar roles", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga todos los permisos disponibles agrupados por categoría
     */
    fun loadAllPermissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("RoleManagement", "=== LOADING ALL PERMISSIONS ===")
                val response = permissionService.getAllPermissions()
                Log.d("RoleManagement", "Response code: ${response.code()}")
                Log.d("RoleManagement", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    _allPermissions.value = response.body()!!.data
                    Log.d("RoleManagement", "Permisos cargados: ${_allPermissions.value.size} categorías")
                    _allPermissions.value.forEach { category ->
                        Log.d("RoleManagement", "  Categoría: ${category.Category}, Permisos: ${category.TotalPermissions}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error al cargar permisos: ${response.message()}"
                    Log.e("RoleManagement", "Error response code: ${response.code()}")
                    Log.e("RoleManagement", "Error message: ${response.message()}")
                    Log.e("RoleManagement", "Error body: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al cargar permisos", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga los permisos de un rol específico
     */
    fun loadRolePermissions(roleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("RoleManagement", "=== LOADING ROLE PERMISSIONS FOR ROLE ID: $roleId ===")
                val response = permissionService.getRolePermissions(roleId)
                Log.d("RoleManagement", "Response code: ${response.code()}")
                Log.d("RoleManagement", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    _rolePermissions.value = response.body()!!
                    Log.d("RoleManagement", "Rol: ${_rolePermissions.value?.roleName}")
                    Log.d("RoleManagement", "Permisos del rol $roleId: ${_rolePermissions.value?.totalPermissions}")
                    _rolePermissions.value?.permissions?.forEach { perm ->
                        Log.d("RoleManagement", "  - ${perm.name} (${perm.code})")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Error al cargar permisos del rol: ${response.message()}"
                    Log.e("RoleManagement", "Error response code: ${response.code()}")
                    Log.e("RoleManagement", "Error message: ${response.message()}")
                    Log.e("RoleManagement", "Error body: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al cargar permisos del rol", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza los permisos de un rol
     */
    fun updateRolePermissions(roleId: Int, permissionIds: List<Int>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val request = UpdateRolePermissionsRequest(permissionIds = permissionIds)
                val response = permissionService.updateRolePermissions(roleId, request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _successMessage.value = body.message
                    Log.d("RoleManagement", "Permisos actualizados: ${body.totalPermissions}")

                    // Recargar permisos del rol
                    loadRolePermissions(roleId)
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al actualizar permisos: ${response.message()}"
                    Log.e("RoleManagement", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al actualizar permisos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Crea un nuevo rol
     */
    fun createRole(role: RoleModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = roleService.createRole(role)
                if (response.isSuccessful && response.body() != null) {
                    _successMessage.value = "Rol creado exitosamente"
                    Log.d("RoleManagement", "Rol creado: ${response.body()}")

                    // Recargar roles
                    loadRoles()
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al crear rol: ${response.message()}"
                    Log.e("RoleManagement", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al crear rol", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza un rol existente
     */
    fun updateRole(roleId: Int, role: RoleModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = roleService.updateRole(roleId, role)
                if (response.isSuccessful) {
                    _successMessage.value = "Rol actualizado exitosamente"
                    Log.d("RoleManagement", "Rol actualizado: $roleId")

                    // Recargar roles
                    loadRoles()
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al actualizar rol: ${response.message()}"
                    Log.e("RoleManagement", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al actualizar rol", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un rol
     */
    fun deleteRole(roleId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = roleService.deleteRole(roleId)
                if (response.isSuccessful) {
                    _successMessage.value = "Rol eliminado exitosamente"
                    Log.d("RoleManagement", "Rol eliminado: $roleId")

                    // Recargar roles
                    loadRoles()
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al eliminar rol: ${response.message()}"
                    Log.e("RoleManagement", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
                Log.e("RoleManagement", "Exception al eliminar rol", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Selecciona un rol para edición
     */
    fun selectRole(role: RoleModel?) {
        _selectedRole.value = role
        role?.let {
            loadRolePermissions(it.ID_ROL)
        }
    }

    /**
     * Limpia mensajes de error y éxito
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
