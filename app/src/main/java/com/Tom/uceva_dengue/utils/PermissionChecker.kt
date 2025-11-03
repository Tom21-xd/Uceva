package com.Tom.uceva_dengue.utils

/**
 * Constants for all permission codes in the system
 * Must match the CODIGO_PERMISO values in the database
 */
object PermissionCode {
    // Casos (IDs 1-7)
    const val CASE_VIEW = "CASE_VIEW"
    const val CASE_VIEW_ALL = "CASE_VIEW_ALL"
    const val CASE_CREATE = "CASE_CREATE"
    const val CASE_UPDATE = "CASE_UPDATE"
    const val CASE_DELETE = "CASE_DELETE"
    const val CASE_IMPORT_CSV = "CASE_IMPORT_CSV"
    const val CASE_EXPORT = "CASE_EXPORT"

    // Usuarios (IDs 8-12)
    const val USER_VIEW = "USER_VIEW"
    const val USER_VIEW_ALL = "USER_VIEW_ALL"
    const val USER_CREATE = "USER_CREATE"
    const val USER_UPDATE = "USER_UPDATE"
    const val USER_DELETE = "USER_DELETE"

    // Hospitales (IDs 13-16)
    const val HOSPITAL_VIEW = "HOSPITAL_VIEW"
    const val HOSPITAL_CREATE = "HOSPITAL_CREATE"
    const val HOSPITAL_UPDATE = "HOSPITAL_UPDATE"
    const val HOSPITAL_DELETE = "HOSPITAL_DELETE"

    // Publicaciones (IDs 17-20, 32)
    const val PUBLICATION_VIEW = "PUBLICATION_VIEW"
    const val PUBLICATION_CREATE = "PUBLICATION_CREATE"
    const val PUBLICATION_UPDATE = "PUBLICATION_UPDATE"
    const val PUBLICATION_DELETE = "PUBLICATION_DELETE"
    const val PUBLICATION_SAVE = "PUBLICATION_SAVE" // ID 32

    // Notificaciones (IDs 21-22, 31)
    const val NOTIFICATION_SEND = "NOTIFICATION_SEND"
    const val NOTIFICATION_VIEW_ALL = "NOTIFICATION_VIEW_ALL"
    const val NOTIFICATION_VIEW = "NOTIFICATION_VIEW" // ID 31

    // Estadísticas (IDs 23-24, 33)
    const val STATISTICS_VIEW = "STATISTICS_VIEW"
    const val REPORTS_GENERATE = "REPORTS_GENERATE"
    const val MAP_VIEW = "MAP_VIEW" // ID 33

    // Administración (IDs 25-27)
    const val ROLE_MANAGE = "ROLE_MANAGE"
    const val PERMISSION_MANAGE = "PERMISSION_MANAGE"
    const val SYSTEM_CONFIG = "SYSTEM_CONFIG"

    // Aprobación de Usuarios
    const val USER_APPROVAL_VIEW = "USER_APPROVAL_VIEW"
    const val USER_APPROVAL_APPROVE = "USER_APPROVAL_APPROVE"
    const val USER_APPROVAL_REJECT = "USER_APPROVAL_REJECT"
    const val USER_APPROVAL_HISTORY = "USER_APPROVAL_HISTORY"

    // Educación (IDs 28-30)
    const val QUIZ_VIEW = "QUIZ_VIEW"
    const val QUIZ_MANAGE = "QUIZ_MANAGE"
    const val CERTIFICATE_VIEW = "CERTIFICATE_VIEW"
}

/**
 * Helper class to check user permissions for different features
 */
object PermissionChecker {

    /**
     * Permissions required for each menu item
     */
    object MenuPermissions {
        val HOME = emptyList<String>() // Everyone can access home
        val PROFILE = listOf(PermissionCode.USER_VIEW) // View own profile
        val OPTIONS = emptyList<String>() // Everyone can access options
        val INFO = emptyList<String>() // Everyone can access info
        val CASES = listOf(
            PermissionCode.CASE_VIEW_ALL,
            PermissionCode.CASE_CREATE,
            PermissionCode.CASE_UPDATE,
            PermissionCode.CASE_DELETE,
            PermissionCode.CASE_IMPORT_CSV,
            PermissionCode.CASE_EXPORT
        ) // View cases if user has ANY case-related permission
        val HOSPITALS = listOf(PermissionCode.HOSPITAL_VIEW)
        val USER_MANAGEMENT = listOf(PermissionCode.USER_VIEW_ALL) // View all users
        val PREVENTION_GUIDE = emptyList<String>() // Everyone can access guide
        val SAVED_PUBLICATIONS = listOf(PermissionCode.PUBLICATION_SAVE)
        val PERMISSIONS_MANAGEMENT = listOf(PermissionCode.PERMISSION_MANAGE)
        val ROLE_MANAGEMENT = listOf(PermissionCode.ROLE_MANAGE)
        val IMPORT_CASES = listOf(PermissionCode.CASE_IMPORT_CSV)
        val USER_APPROVAL = listOf(PermissionCode.USER_APPROVAL_VIEW)
    }

    /**
     * Permissions required for specific actions
     */
    object ActionPermissions {
        // Cases
        val CREATE_CASE = listOf(PermissionCode.CASE_CREATE)
        val EDIT_CASE = listOf(PermissionCode.CASE_UPDATE)
        val DELETE_CASE = listOf(PermissionCode.CASE_DELETE)
        val EXPORT_CASE = listOf(PermissionCode.CASE_EXPORT)
        val IMPORT_CASE = listOf(PermissionCode.CASE_IMPORT_CSV)

        // Hospitals
        val CREATE_HOSPITAL = listOf(PermissionCode.HOSPITAL_CREATE)
        val EDIT_HOSPITAL = listOf(PermissionCode.HOSPITAL_UPDATE)
        val DELETE_HOSPITAL = listOf(PermissionCode.HOSPITAL_DELETE)

        // Publications
        val CREATE_PUBLICATION = listOf(PermissionCode.PUBLICATION_CREATE)
        val EDIT_PUBLICATION = listOf(PermissionCode.PUBLICATION_UPDATE)
        val DELETE_PUBLICATION = listOf(PermissionCode.PUBLICATION_DELETE)
        val SAVE_PUBLICATION = listOf(PermissionCode.PUBLICATION_SAVE)

        // Users
        val CREATE_USER = listOf(PermissionCode.USER_CREATE)
        val EDIT_USER = listOf(PermissionCode.USER_UPDATE)
        val DELETE_USER = listOf(PermissionCode.USER_DELETE)

        // Notifications
        val SEND_NOTIFICATION = listOf(PermissionCode.NOTIFICATION_SEND)
        val VIEW_ALL_NOTIFICATIONS = listOf(PermissionCode.NOTIFICATION_VIEW_ALL)

        // Map
        val VIEW_MAP = listOf(PermissionCode.MAP_VIEW)

        // Statistics
        val VIEW_STATISTICS = listOf(PermissionCode.STATISTICS_VIEW)
        val GENERATE_REPORTS = listOf(PermissionCode.REPORTS_GENERATE)

        // Quiz
        val TAKE_QUIZ = listOf(PermissionCode.QUIZ_VIEW)
        val MANAGE_QUIZ = listOf(PermissionCode.QUIZ_MANAGE)
        val VIEW_CERTIFICATE = listOf(PermissionCode.CERTIFICATE_VIEW)

        // System
        val CONFIGURE_SYSTEM = listOf(PermissionCode.SYSTEM_CONFIG)
    }

    /**
     * Checks if the user has the required permissions for a menu item
     */
    suspend fun canAccessMenu(
        menuPermissions: List<String>,
        permissionsManager: UserPermissionsManager
    ): Boolean {
        if (menuPermissions.isEmpty()) return true
        return permissionsManager.hasAllPermissions(*menuPermissions.toTypedArray())
    }

    /**
     * Checks if the user can perform a specific action
     */
    suspend fun canPerformAction(
        actionPermissions: List<String>,
        permissionsManager: UserPermissionsManager
    ): Boolean {
        if (actionPermissions.isEmpty()) return true
        return permissionsManager.hasAllPermissions(*actionPermissions.toTypedArray())
    }
}
