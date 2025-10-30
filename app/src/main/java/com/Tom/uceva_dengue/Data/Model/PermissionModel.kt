package com.Tom.uceva_dengue.Data.Model

/**
 * Modelo de permiso individual
 */
data class Permission(
    val id: Int,
    val code: String,
    val name: String,
    val description: String?,
    val category: String?,
    val isActive: Boolean
)

/**
 * Respuesta de permisos de un rol específico
 */
data class RolePermissionsResponse(
    val roleId: Int,
    val roleName: String,
    val permissions: List<Permission>,
    val totalPermissions: Int
)

/**
 * Request para actualizar permisos de un rol
 */
data class UpdateRolePermissionsRequest(
    @com.google.gson.annotations.SerializedName("PermissionIds")
    val permissionIds: List<Int>
)

/**
 * Respuesta al obtener todos los permisos
 */
data class AllPermissionsResponse(
    val data: List<PermissionCategory>
)

/**
 * Categoría de permisos
 */
data class PermissionCategory(
    val Category: String,
    val TotalPermissions: Int,
    val Permissions: List<Permission>
)

/**
 * Respuesta al actualizar permisos
 */
data class UpdatePermissionsResponse(
    val message: String,
    val roleId: Int,
    val totalPermissions: Int
)

/**
 * Matriz de permisos (roles vs permisos)
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
 * Respuesta al verificar permiso de usuario
 */
data class CheckPermissionResponse(
    val userId: Int,
    val permissionCode: String,
    val hasPermission: Boolean
)
