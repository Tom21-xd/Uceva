package com.Tom.uceva_dengue.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _userToken = MutableStateFlow<String?>(null)
    val userToken: StateFlow<String?> = _userToken

    init {
        viewModelScope.launch {
            _userRole.value = userPreferences.userRole.first()
            _userToken.value = userPreferences.userToken.first()
        }
    }

    fun saveUserSession(role: String, token: String) {
        viewModelScope.launch {
            userPreferences.saveUserSession(role, token)
            _userRole.value = role
            _userToken.value = token
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            userPreferences.clearUserSession()
            _userRole.value = null
            _userToken.value = null
        }
    }
}
