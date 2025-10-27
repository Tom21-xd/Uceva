package com.example.ucevadengue.Data.Service

import com.example.ucevadengue.data.model.Permission
import com.example.ucevadengue.data.model.RolePermissionsResponse
import com.example.ucevadengue.data.model.UpdateRolePermissionsRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio Retrofit para gestión de permisos
 * Endpoints del PermissionControllerEF
 */
interface PermissionService {

    /**
     * Obtiene todos los permisos del sistema agrupados por categoría
     * GET /api/Permission/all
     */
    @GET("api/Permission/all")
    suspend fun getAllPermissions(): Response<AllPermissionsResponse>

    /**
     * Obtiene los permisos asignados a un rol específico
     * GET /api/Permission/role/{roleId}
     */
    @GET("api/Permission/role/{roleId}")
    suspend fun getRolePermissions(
        @Path("roleId") roleId: Int
    ): Response<RolePermissionsResponse>

    /**
     * Actualiza los permisos de un rol (asignar/revocar)
     * PUT /api/Permission/role/{roleId}
     */
    @PUT("api/Permission/role/{roleId}")
    suspend fun updateRolePermissions(
        @Path("roleId") roleId: Int,
        @Body request: UpdateRolePermissionsRequest
    ): Response<UpdatePermissionsResponse>

    /**
     * Obtiene la matriz de permisos (todos los roles vs todos los permisos)
     * GET /api/Permission/matrix
     */
    @GET("api/Permission/matrix")
    suspend fun getPermissionsMatrix(): Response<PermissionsMatrixResponse>

    /**
     * Verifica si el usuario actual tiene un permiso específico
     * GET /api/Permission/check/{permissionCode}
     */
    @GET("api/Permission/check/{permissionCode}")
    suspend fun checkUserPermission(
        @Path("permissionCode") permissionCode: String
    ): Response<CheckPermissionResponse>
}

/**
 * Response para getAllPermissions
 */
data class AllPermissionsResponse(
    val data: List<PermissionCategory>
)

data class PermissionCategory(
    val Category: String,
    val TotalPermissions: Int,
    val Permissions: List<Permission>
)

/**
 * Response para updateRolePermissions
 */
data class UpdatePermissionsResponse(
    val message: String,
    val roleId: Int,
    val totalPermissions: Int
)

/**
 * Response para getPermissionsMatrix
 */
data class PermissionsMatrixResponse(
    val data: List<PermissionMatrixRow>
)

data class PermissionMatrixRow(
    val permissionId: Int,
    val permissionName: String,
    val category: String?,
    val roles: List<RolePermissionStatus>
)

data class RolePermissionStatus(
    val roleId: Int,
    val roleName: String,
    val hasPermission: Boolean
)

/**
 * Response para checkUserPermission
 */
data class CheckPermissionResponse(
    val userId: Int,
    val permissionCode: String,
    val hasPermission: Boolean
)
