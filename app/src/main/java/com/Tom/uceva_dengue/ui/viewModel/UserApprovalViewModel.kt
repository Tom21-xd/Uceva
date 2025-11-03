package com.Tom.uceva_dengue.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.RoleModel
import com.Tom.uceva_dengue.Data.Model.UserApproval.ApprovalRequestDto
import com.Tom.uceva_dengue.Data.Model.UserApproval.ApproveUserRequest
import com.Tom.uceva_dengue.Data.Model.UserApproval.RejectUserRequest
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * ViewModel para gestión de solicitudes de aprobación de usuarios
 * Maneja la lógica de negocio para aprobar/rechazar usuarios y cambiar roles
 */
class UserApprovalViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val authRepository = AuthRepository(context)

    // Lista de solicitudes pendientes
    private val _pendingRequests = MutableLiveData<List<ApprovalRequestDto>>()
    val pendingRequests: LiveData<List<ApprovalRequestDto>> get() = _pendingRequests

    // Lista de todas las solicitudes (historial)
    private val _allRequests = MutableLiveData<List<ApprovalRequestDto>>()
    val allRequests: LiveData<List<ApprovalRequestDto>> get() = _allRequests

    // Lista de roles disponibles
    private val _availableRoles = MutableLiveData<List<RoleModel>>()
    val availableRoles: LiveData<List<RoleModel>> get() = _availableRoles

    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Mensajes de éxito
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    // Mensajes de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Estado del diálogo de aprobación
    private val _showApproveDialog = MutableLiveData(false)
    val showApproveDialog: LiveData<Boolean> get() = _showApproveDialog

    // Estado del diálogo de rechazo
    private val _showRejectDialog = MutableLiveData(false)
    val showRejectDialog: LiveData<Boolean> get() = _showRejectDialog

    // Solicitud seleccionada para acción
    private val _selectedRequest = MutableLiveData<ApprovalRequestDto?>()
    val selectedRequest: LiveData<ApprovalRequestDto?> get() = _selectedRequest

    /**
     * Cargar solicitudes pendientes
     */
    fun loadPendingRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.userApprovalService.getPendingApprovals()

                if (response.isSuccessful) {
                    _pendingRequests.value = response.body() ?: emptyList()
                    Log.d("UserApprovalVM", "Solicitudes pendientes cargadas: ${response.body()?.size}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "{}").optString("message", "Error al cargar solicitudes")
                    } catch (e: Exception) {
                        "Error al cargar solicitudes: ${response.code()}"
                    }
                    _errorMessage.value = errorMessage
                    Log.e("UserApprovalVM", "Error al cargar pendientes: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                Log.e("UserApprovalVM", "Excepción al cargar pendientes", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar todas las solicitudes (historial)
     */
    fun loadAllRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.userApprovalService.getAllApprovals()

                if (response.isSuccessful) {
                    _allRequests.value = response.body() ?: emptyList()
                    Log.d("UserApprovalVM", "Historial cargado: ${response.body()?.size}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "{}").optString("message", "Error al cargar historial")
                    } catch (e: Exception) {
                        "Error al cargar historial: ${response.code()}"
                    }
                    _errorMessage.value = errorMessage
                    Log.e("UserApprovalVM", "Error al cargar historial: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                Log.e("UserApprovalVM", "Excepción al cargar historial", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar roles disponibles
     */
    fun loadAvailableRoles() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.roleService.getRoles()

                if (response.isSuccessful) {
                    // Filtrar solo roles activos
                    val roles = response.body()?.filter { it.ESTADO_ROL } ?: emptyList()
                    _availableRoles.value = roles
                    Log.d("UserApprovalVM", "Roles cargados: ${roles.size}")
                } else {
                    Log.e("UserApprovalVM", "Error al cargar roles: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserApprovalVM", "Excepción al cargar roles", e)
            }
        }
    }

    /**
     * Aprobar usuario y cambiar su rol
     */
    fun approveUser(userId: Int, newRoleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener ID del admin actual
                val adminId = authRepository.getPermissionsManager().getUserId() ?: run {
                    _errorMessage.value = "Error: No se pudo obtener ID del administrador"
                    _isLoading.value = false
                    return@launch
                }

                val request = ApproveUserRequest(
                    userId = userId,
                    newRoleId = newRoleId
                )

                val response = RetrofitClient.userApprovalService.approveUser(adminId, request)

                if (response.isSuccessful) {
                    val result = response.body()
                    _successMessage.value = result?.message ?: "Usuario aprobado exitosamente"
                    Log.d("UserApprovalVM", "Usuario aprobado: ${result?.usuario?.nombre}")

                    // Recargar lista de pendientes
                    loadPendingRequests()

                    // Cerrar diálogo
                    _showApproveDialog.value = false
                    _selectedRequest.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "{}").optString("message", "Error al aprobar usuario")
                    } catch (e: Exception) {
                        "Error al aprobar usuario: ${response.code()}"
                    }
                    _errorMessage.value = errorMessage
                    Log.e("UserApprovalVM", "Error al aprobar: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                Log.e("UserApprovalVM", "Excepción al aprobar", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Rechazar usuario con motivo
     */
    fun rejectUser(userId: Int, rejectionReason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener ID del admin actual
                val adminId = authRepository.getPermissionsManager().getUserId() ?: run {
                    _errorMessage.value = "Error: No se pudo obtener ID del administrador"
                    _isLoading.value = false
                    return@launch
                }

                val request = RejectUserRequest(
                    userId = userId,
                    rejectionReason = rejectionReason
                )

                val response = RetrofitClient.userApprovalService.rejectUser(adminId, request)

                if (response.isSuccessful) {
                    val result = response.body()
                    _successMessage.value = result?.message ?: "Solicitud rechazada exitosamente"
                    Log.d("UserApprovalVM", "Usuario rechazado: ${result?.usuario?.nombre}")

                    // Recargar lista de pendientes
                    loadPendingRequests()

                    // Cerrar diálogo
                    _showRejectDialog.value = false
                    _selectedRequest.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "{}").optString("message", "Error al rechazar usuario")
                    } catch (e: Exception) {
                        "Error al rechazar usuario: ${response.code()}"
                    }
                    _errorMessage.value = errorMessage
                    Log.e("UserApprovalVM", "Error al rechazar: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                Log.e("UserApprovalVM", "Excepción al rechazar", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mostrar diálogo de aprobación para una solicitud
     */
    fun showApproveDialog(request: ApprovalRequestDto) {
        _selectedRequest.value = request
        _showApproveDialog.value = true
    }

    /**
     * Mostrar diálogo de rechazo para una solicitud
     */
    fun showRejectDialog(request: ApprovalRequestDto) {
        _selectedRequest.value = request
        _showRejectDialog.value = true
    }

    /**
     * Cerrar diálogos
     */
    fun dismissDialogs() {
        _showApproveDialog.value = false
        _showRejectDialog.value = false
        _selectedRequest.value = null
    }

    /**
     * Limpiar mensajes
     */
    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}
