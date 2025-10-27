package com.example.ucevadengue.Data.Repository

import com.example.ucevadengue.Data.Service.*
import com.example.ucevadengue.data.model.RolePermissionsResponse
import com.example.ucevadengue.data.model.UpdateRolePermissionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository para gestión de permisos
 * Maneja la lógica de negocio entre el ViewModel y la API
 */
class PermissionRepository(private val service: PermissionService) {

    /**
     * Obtiene todos los permisos del sistema
     */
    suspend fun getAllPermissions(): Result<AllPermissionsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = service.getAllPermissions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener permisos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los permisos de un rol específico
     */
    suspend fun getRolePermissions(roleId: Int): Result<RolePermissionsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = service.getRolePermissions(roleId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener permisos del rol: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los permisos de un rol
     */
    suspend fun updateRolePermissions(
        roleId: Int,
        permissionIds: List<Int>
    ): Result<UpdatePermissionsResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateRolePermissionsRequest(permissionIds)
            val response = service.updateRolePermissions(roleId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar permisos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene la matriz completa de permisos
     */
    suspend fun getPermissionsMatrix(): Result<PermissionsMatrixResponse> = withContext(Dispatchers.IO) {
        try {
            val response = service.getPermissionsMatrix()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener matriz de permisos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica si el usuario tiene un permiso específico
     */
    suspend fun checkUserPermission(permissionCode: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = service.checkUserPermission(permissionCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.hasPermission)
            } else {
                Result.failure(Exception("Error al verificar permiso: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Activa/desactiva un permiso específico para un rol
     */
    suspend fun togglePermission(
        roleId: Int,
        permissionId: Int,
        enabled: Boolean,
        currentPermissions: List<Int>
    ): Result<UpdatePermissionsResponse> = withContext(Dispatchers.IO) {
        try {
            val updatedPermissions = if (enabled) {
                currentPermissions + permissionId
            } else {
                currentPermissions.filter { it != permissionId }
            }

            updateRolePermissions(roleId, updatedPermissions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
