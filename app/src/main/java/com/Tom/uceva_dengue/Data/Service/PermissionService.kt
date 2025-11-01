package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.AllPermissionsResponse
import com.Tom.uceva_dengue.Data.Model.CheckPermissionResponse
import com.Tom.uceva_dengue.Data.Model.PermissionsMatrixResponse
import com.Tom.uceva_dengue.Data.Model.RolePermissionsResponse
import com.Tom.uceva_dengue.Data.Model.UpdatePermissionsResponse
import com.Tom.uceva_dengue.Data.Model.UpdateRolePermissionsRequest
import com.Tom.uceva_dengue.Data.Model.UserPermissionsResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio Retrofit para gestión de permisos
 * Conecta con PermissionControllerEF del backend
 */
interface PermissionService {

    /**
     * Obtiene todos los permisos del sistema agrupados por categoría
     * GET /api/PermissionEF/all
     */
    @GET("api/PermissionEF/all")
    suspend fun getAllPermissions(): Response<AllPermissionsResponse>

    /**
     * Obtiene los permisos del usuario autenticado
     * GET /api/PermissionEF/current-user
     */
    @GET("api/PermissionEF/current-user")
    suspend fun getCurrentUserPermissions(): Response<UserPermissionsResponse>

    /**
     * Obtiene los permisos asignados a un rol específico
     * GET /api/PermissionEF/role/{roleId}
     */
    @GET("api/PermissionEF/role/{roleId}")
    suspend fun getRolePermissions(
        @Path("roleId") roleId: Int
    ): Response<RolePermissionsResponse>

    /**
     * Actualiza los permisos de un rol (asignar/revocar)
     * PUT /api/PermissionEF/role/{roleId}
     */
    @PUT("api/PermissionEF/role/{roleId}")
    suspend fun updateRolePermissions(
        @Path("roleId") roleId: Int,
        @Body request: UpdateRolePermissionsRequest
    ): Response<UpdatePermissionsResponse>

    /**
     * Obtiene la matriz de permisos (todos los roles vs todos los permisos)
     * GET /api/PermissionEF/matrix
     */
    @GET("api/PermissionEF/matrix")
    suspend fun getPermissionsMatrix(): Response<PermissionsMatrixResponse>

    /**
     * Verifica si el usuario actual tiene un permiso específico
     * GET /api/PermissionEF/check/{permissionCode}
     */
    @GET("api/PermissionEF/check/{permissionCode}")
    suspend fun checkUserPermission(
        @Path("permissionCode") permissionCode: String
    ): Response<CheckPermissionResponse>
}
