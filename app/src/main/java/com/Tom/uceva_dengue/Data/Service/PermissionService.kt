package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.AllPermissionsResponse
import com.Tom.uceva_dengue.Data.Model.CheckPermissionResponse
import com.Tom.uceva_dengue.Data.Model.PermissionsMatrixResponse
import com.Tom.uceva_dengue.Data.Model.RolePermissionsResponse
import com.Tom.uceva_dengue.Data.Model.UpdatePermissionsResponse
import com.Tom.uceva_dengue.Data.Model.UpdateRolePermissionsRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio Retrofit para gestión de permisos
 * Conecta con PermissionControllerEF del backend
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
