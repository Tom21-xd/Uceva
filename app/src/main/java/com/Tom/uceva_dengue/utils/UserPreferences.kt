package com.Tom.uceva_dengue.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Configuración del DataStore como delegado de propiedad
val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "user_session")

object UserPreferencesKeys {
    val USER_ROLE = stringPreferencesKey("user_role")
    val USER_TOKEN = stringPreferencesKey("user_token")
}

class UserPreferences(private val context: Context) {

    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.USER_ROLE]
    }

    val userToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.USER_TOKEN]
    }

    suspend fun saveUserSession(role: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_ROLE] = role
            preferences[UserPreferencesKeys.USER_TOKEN] = token
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
