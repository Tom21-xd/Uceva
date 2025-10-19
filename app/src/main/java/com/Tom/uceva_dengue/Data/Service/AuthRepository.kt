package com.Tom.uceva_dengue.Data.Service

import android.content.Context


class AuthRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    fun saveUserAndRole(username: String, roleId: Int, displayName: String? = null) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putInt("user_role", roleId)
        if (displayName != null) {
            editor.putString("user_display_name", displayName)
        }
        editor.apply()
    }

    fun getUser(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getUserDisplayName(): String? {
        return sharedPreferences.getString("user_display_name", null)
    }

    fun getRole(): Int {
        return sharedPreferences.getInt("user_role", 0) // 2 por defecto (rol regular)
    }

    fun clearUserSession() {
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("user_role")
        editor.remove("user_display_name")
        editor.apply()
    }
}
