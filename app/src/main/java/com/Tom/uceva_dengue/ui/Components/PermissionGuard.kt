package com.Tom.uceva_dengue.ui.Components

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.Tom.uceva_dengue.utils.UserPermissionsManager
import kotlinx.coroutines.flow.first

/**
 * Composable that conditionally shows content based on user permissions
 *
 * @param permissions List of permission codes required to view the content
 * @param requireAll If true, user must have ALL permissions. If false, user needs ANY permission
 * @param fallback Composable to show when user doesn't have required permissions (optional)
 * @param content The content to show if user has required permissions
 */
@Composable
fun PermissionGuard(
    permissions: List<String>,
    requireAll: Boolean = true,
    fallback: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permissionsManager = remember { UserPermissionsManager.getInstance(context) }
    var hasAccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(permissions, requireAll) {
        // If no permissions required, grant access
        if (permissions.isEmpty()) {
            hasAccess = true
            isLoading = false
            return@LaunchedEffect
        }

        // Check permissions
        val userPermissions = permissionsManager.getUserPermissionsFlow().first()

        hasAccess = if (requireAll) {
            permissions.all { it in userPermissions }
        } else {
            permissions.any { it in userPermissions }
        }

        isLoading = false
    }

    if (!isLoading) {
        if (hasAccess) {
            content()
        } else {
            fallback?.invoke()
        }
    }
}

/**
 * Composable that conditionally shows content based on a single permission
 *
 * @param permission Permission code required to view the content
 * @param fallback Composable to show when user doesn't have required permission (optional)
 * @param content The content to show if user has required permission
 */
@Composable
fun RequirePermission(
    permission: String,
    fallback: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    PermissionGuard(
        permissions = listOf(permission),
        requireAll = true,
        fallback = fallback,
        content = content
    )
}

/**
 * Hook to check if user has specific permissions
 * Returns a State<Boolean> that can be used in composables
 *
 * @param permissions List of permission codes to check
 * @param requireAll If true, checks if user has ALL permissions. If false, checks for ANY
 */
@Composable
fun rememberHasPermissions(
    permissions: List<String>,
    requireAll: Boolean = true
): State<Boolean> {
    val context = LocalContext.current
    val permissionsManager = remember { UserPermissionsManager.getInstance(context) }

    return produceState(initialValue = false, permissions, requireAll) {
        if (permissions.isEmpty()) {
            value = true
            return@produceState
        }

        val userPermissions = permissionsManager.getUserPermissionsFlow().first()

        value = if (requireAll) {
            permissions.all { it in userPermissions }
        } else {
            permissions.any { it in userPermissions }
        }
    }
}

/**
 * Hook to check if user has a single permission
 * Returns a State<Boolean> that can be used in composables
 *
 * @param permission Permission code to check
 */
@Composable
fun rememberHasPermission(permission: String): State<Boolean> {
    return rememberHasPermissions(listOf(permission), requireAll = true)
}
