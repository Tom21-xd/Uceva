package com.Tom.uceva_dengue.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Manages user permissions in the application
 * Stores, retrieves, and validates user permissions from DataStore
 */
class UserPermissionsManager(private val context: Context) {

    companion object {
        private val USER_PERMISSIONS = stringPreferencesKey("user_permissions")
        private val USER_ID = stringPreferencesKey("user_id")
        private val ROLE_ID = stringPreferencesKey("role_id")
        private val ROLE_NAME = stringPreferencesKey("role_name")

        @Volatile
        private var INSTANCE: UserPermissionsManager? = null

        fun getInstance(context: Context): UserPermissionsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPermissionsManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val gson = Gson()

    /**
     * Saves user permissions to DataStore
     */
    suspend fun saveUserPermissions(
        userId: Int,
        roleId: Int,
        roleName: String,
        permissions: List<String>
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId.toString()
            preferences[ROLE_ID] = roleId.toString()
            preferences[ROLE_NAME] = roleName
            preferences[USER_PERMISSIONS] = gson.toJson(permissions)
        }
    }

    /**
     * Gets all user permissions as Flow
     */
    fun getUserPermissionsFlow(): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            val permissionsJson = preferences[USER_PERMISSIONS] ?: "[]"
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(permissionsJson, type) ?: emptyList()
        }
    }

    /**
     * Gets user permissions as a one-time read
     */
    suspend fun getUserPermissions(): List<String> {
        return getUserPermissionsFlow().first()
    }

    /**
     * Gets user ID
     */
    suspend fun getUserId(): Int? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID]?.toIntOrNull()
    }

    /**
     * Gets role ID
     */
    suspend fun getRoleId(): Int? {
        val preferences = context.dataStore.data.first()
        return preferences[ROLE_ID]?.toIntOrNull()
    }

    /**
     * Gets role name
     */
    suspend fun getRoleName(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[ROLE_NAME]
    }

    /**
     * Checks if user has a specific permission
     */
    suspend fun hasPermission(permission: String): Boolean {
        val permissions = getUserPermissions()
        return permissions.contains(permission)
    }

    /**
     * Checks if user has ANY of the specified permissions
     */
    suspend fun hasAnyPermission(vararg permissions: String): Boolean {
        val userPermissions = getUserPermissions()
        return permissions.any { it in userPermissions }
    }

    /**
     * Checks if user has ALL of the specified permissions
     */
    suspend fun hasAllPermissions(vararg permissions: String): Boolean {
        val userPermissions = getUserPermissions()
        return permissions.all { it in userPermissions }
    }

    /**
     * Clears all stored permissions
     */
    suspend fun clearPermissions() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_PERMISSIONS)
            preferences.remove(USER_ID)
            preferences.remove(ROLE_ID)
            preferences.remove(ROLE_NAME)
        }
    }

    /**
     * Checks if permissions are loaded
     */
    suspend fun hasPermissionsLoaded(): Boolean {
        val permissions = getUserPermissions()
        return permissions.isNotEmpty()
    }
}
