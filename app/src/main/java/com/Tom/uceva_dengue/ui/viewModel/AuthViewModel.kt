package com.Tom.uceva_dengue.ui.viewModel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.Data.Model.DepartmentModel
import com.Tom.uceva_dengue.Data.Model.FCMTokenRequest
import com.Tom.uceva_dengue.Data.Model.GenreModel
import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RegisterUserModel
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.Data.Service.FCMTokenManager
import com.Tom.uceva_dengue.ui.Navigation.Rout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _correo = MutableLiveData<String>()
    val correo: MutableLiveData<String>
        get() = _correo

    private val _contra = MutableLiveData<String>()
    val contra: MutableLiveData<String>
        get() = _contra

    private val _log_regis = MutableLiveData<Boolean>()
    val log_regis: MutableLiveData<Boolean>
        get() = _log_regis

    private val _contravisible = MutableLiveData<Boolean>()
    val contravisible: MutableLiveData<Boolean>
        get() = _contravisible

    private val _loginEnabled = MutableLiveData<Boolean>()
    val loginEnabled: MutableLiveData<Boolean>
        get() = _loginEnabled

    fun onContraVisibilityChange(visible: Boolean) {
        _contravisible.value = visible
    }
    private val _loginError = MutableLiveData<String?>(null)
    val loginError: LiveData<String?> get() = _loginError


    private fun contravalida(contra: String): Boolean = contra.length >= 8

    private fun correovalido(correo: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    fun onLoginChange(correo: String, contra: String) {
        _correo.value = correo
        _contra.value = contra
        _loginEnabled.value = correovalido(correo)
    }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _registerMessage = MutableLiveData<String?>(null)
    val registerMessage: LiveData<String?> get() = _registerMessage

    private val _registerError = MutableLiveData<String?>(null)
    val registerError: LiveData<String?> get() = _registerError

    fun iniciosesioncorreo(email: String, password: String, HomeScreen: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitClient.authService.login(LoginModel(email, password))
                _loading.value = false
                Log.d("NavigationCon", "Inicio de sesión exitoso. Respuesta: ${response.body()}")
                if (response.isSuccessful) {
                    _loginError.value = null
                    val userId = response.body()?.ID_USUARIO
                    val user = userId.toString()
                    val role = response.body()?.FK_ID_ROL


                    val authRepository = AuthRepository(context)

                    user?.let {
                        authRepository.saveUserAndRole(it, role ?: 2)
                    }
                    Log.d("NavigationCon", "Inicio de sesión exitoso. Usuario: $user, Rol: $role")

                    // Send FCM token to backend after successful login
                    userId?.let {
                        sendFCMTokenToServer(it)
                    }

                    HomeScreen()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        JSONObject(errorBody ?: "").optString("message", "Error desconocido")
                    } catch (e: Exception) {
                        "Error inesperado en el servidor"
                    }
                    _loginError.value = message
                }
            } catch (e: Exception) {
                _loading.value = false
                Log.e("LoginError", "Excepción en el login", e)
                _loginError.value = "Error de conexión. Revisa tu internet o inténtalo más tarde."
            }
        }
    }
    fun clearLoginError() {
        _loginError.value = null
    }

    // Function to send FCM token to backend
    private suspend fun sendFCMTokenToServer(userId: Int) {
        try {
            val fcmToken = FCMTokenManager.getFCMToken()
            if (fcmToken != null) {
                val request = FCMTokenRequest(userId = userId, fcmToken = fcmToken)
                val response = RetrofitClient.fcmService.saveToken(request)
                if (response.isSuccessful) {
                    Log.d("FCM", "Token FCM guardado exitosamente para usuario $userId")
                } else {
                    Log.e("FCM", "Error al guardar token FCM: ${response.errorBody()?.string()}")
                }
            } else {
                Log.w("FCM", "No se pudo obtener el token FCM")
            }
        } catch (e: Exception) {
            Log.e("FCM", "Excepción al enviar token FCM: ${e.message}")
        }
    }

    //---------------------------Registro----------------------------------------


    private val _departamentos = MutableStateFlow<List<DepartmentModel>>(emptyList())
    val departamentos = _departamentos.asStateFlow()

    private val _municipios = MutableStateFlow<List<CityModel>>(emptyList())
    val municipios = _municipios.asStateFlow()

    private val _generos = MutableStateFlow<List<GenreModel>>(emptyList())
    val generos = _generos.asStateFlow()

    private val _tiposSangre = MutableStateFlow<List<BloodTypeModel>>(emptyList())
    val tiposSangre = _tiposSangre.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isValidatingRethus = MutableLiveData<Boolean>()
    val isValidatingRethus: LiveData<Boolean> get() = _isValidatingRethus

    private val _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private val _firstName = MutableLiveData("")
    val firstName: LiveData<String> get() = _firstName

    private val _lastName = MutableLiveData("")
    val lastName: LiveData<String> get() = _lastName

    private val _genderId = MutableLiveData(0)
    val genderId: LiveData<Int> get() = _genderId

    private val _genderName = MutableLiveData("")
    val genderName: LiveData<String> get() = _genderName

    private val _bloodTypeId = MutableLiveData(0)
    val bloodTypeId: LiveData<Int> get() = _bloodTypeId

    private val _bloodTypeName = MutableLiveData("")
    val bloodTypeName: LiveData<String> get() = _bloodTypeName

    private val _department = MutableLiveData("")
    val department: LiveData<String> get() = _department

    private val _cityId = MutableLiveData(0)
    val cityId: LiveData<Int> get() = _cityId

    private val _cityName = MutableLiveData("")
    val cityName: LiveData<String> get() = _cityName

    private val _address = MutableLiveData("")
    val address: LiveData<String> get() = _address

    init {
        fetchDepartamentos()
        fetchGeneros()
        fetchTiposSangre()
    }

    fun fetchDepartamentos() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.departmentService.getDepartments()
                _departamentos.value = result
                Log.d("AuthViewModel", "Departamentos cargados: ${result.size}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar departamentos: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun fetchGeneros() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.genreService.getGenres()
                if (response.isSuccessful) {
                    val result = response.body() ?: emptyList()
                    _generos.value = result
                    Log.d("AuthViewModel", "Géneros cargados: ${result.size}")
                } else {
                    Log.e("AuthViewModel", "Error en respuesta de géneros: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar géneros: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun fetchTiposSangre() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.bloodTypeService.getBloodTypes()
                _tiposSangre.value = result
                Log.d("AuthViewModel", "Tipos de sangre cargados: ${result.size}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar tipos de sangre: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun fetchMunicipios(departamentoId: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.cityService.getCities(departamentoId)
                _municipios.value = result
                Log.d("AuthViewModel", "Municipios cargados: ${result.size}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar municipios: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun onRegisterChange(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        bloodTypeId: Int,
        cityId: Int,
        address: String,
        genderId: Int = 0,
    ) {
        _email.value = email
        _password.value = password
        _firstName.value = firstName
        _lastName.value = lastName
        _cityId.value = cityId
        _address.value = address
        _genderId.value = genderId
        _bloodTypeId.value = bloodTypeId
    }

    fun registrarUsuario(
        navController: NavController,
        esPersonalMedico: Boolean,
        tipoIdentificacion: String = "",
        numeroDocumento: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                _registerError.value = null
                _registerMessage.value = null
                var rolId = 1  // Por defecto, rol usuario normal

                if (esPersonalMedico) {
                    // Dividir nombres y apellidos
                    val nombresDivididos = (_firstName.value ?: "").split(" ")
                    val apellidosDivididos = (_lastName.value ?: "").split(" ")

                    // Usar solo el primer nombre y primer apellido
                    val primerNombre = nombresDivididos.firstOrNull() ?: ""
                    val primerApellido = apellidosDivididos.firstOrNull() ?: ""

                    if (primerNombre.isNotBlank() && primerApellido.isNotBlank()) {
                        try {
                            _isValidatingRethus.value = true // Activar loader de RETHUS

                            val rethusBody = mapOf(
                                "PrimerNombre" to primerNombre,
                                "PrimerApellido" to primerApellido,
                                "TipoIdentificacion" to tipoIdentificacion,
                                "Cedula" to numeroDocumento
                            )
                            Log.d("Rethus", "Consultando RETHUS con: $rethusBody")

                            val response = RetrofitClient.authService.consultarRethus(rethusBody)

                            _isValidatingRethus.value = false // Desactivar loader de RETHUS

                            Log.d("Rethus", "Respuesta RETHUS - Code: ${response.code()}, Success: ${response.isSuccessful}")
                            Log.d("Rethus", "Respuesta RETHUS - Body: ${response.body()}")

                            val isValidated = response.body()?.isSuccess() ?: false

                            if (response.isSuccessful && isValidated) {
                                rolId = 3  // RETHUS validado exitosamente, rol personal médico
                                Log.d("Rethus", "✓ Validación RETHUS EXITOSA. Usuario SÍ está en RETHUS. Asignando rol 3 (Personal Médico)")
                            } else {
                                // RETHUS falló o no está registrado, se registra como usuario normal (rol 1)
                                rolId = 1
                                val message = response.body()?.message ?: "Sin respuesta"
                                Log.w("Rethus", "✗ Validación RETHUS FALLIDA. Usuario NO está en RETHUS. Message: '$message'. Asignando rol 1 (Usuario Normal)")
                            }
                        } catch (rethusException: Exception) {
                            _isValidatingRethus.value = false // Asegurar que se desactive el loader

                            // Si RETHUS falla por timeout u otro error, registrar como usuario normal
                            rolId = 1
                            Log.e("Rethus", "⚠️ Error al consultar RETHUS: ${rethusException.message}. Registrando como usuario normal (rol 1)", rethusException)

                            // Opcional: informar al usuario que RETHUS no está disponible
                            // pero continuar con el registro como usuario normal
                        }
                    } else {
                        // Nombres incompletos, se registra como usuario normal
                        rolId = 1
                        Log.w("Rethus", "Nombres o apellidos incompletos (primer nombre: '$primerNombre', primer apellido: '$primerApellido'). Registrando como usuario normal (rol 1)")
                    }
                }

                val usuario = RegisterUserModel(
                    NOMBRE_USUARIO = "${_firstName.value} ${_lastName.value}",
                    CORREO_USUARIO = _email.value ?: "",
                    CONTRASENIA_USUARIO = _password.value ?: "",
                    DIRECCION_USUARIO = _address.value ?: "",
                    FK_ID_ROL = rolId,
                    FK_ID_TIPOSANGRE = _bloodTypeId.value ?: 0,
                    FK_ID_GENERO = _genderId.value ?: 0,
                    FK_ID_MUNICIPIO = _cityId.value ?: 0
                )

                Log.d("Registro", "Enviando registro: $usuario")
                val response = RetrofitClient.authService.register(usuario)
                Log.d("Registro", "Respuesta recibida: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val message = body?.message ?: "Registro completado"
                    val usuario = body?.usuario

                    Log.d("Registro", "Usuario registrado en MySQL: $body")

                    // Iniciar sesión automáticamente con el usuario creado
                    if (usuario != null) {
                        val authRepository = AuthRepository(getApplication<Application>().applicationContext)
                        authRepository.saveUserAndRole(
                            usuario.ID_USUARIO.toString(),
                            usuario.FK_ID_ROL
                        )
                        Log.d("Registro", "Sesión iniciada automáticamente. Usuario: ${usuario.ID_USUARIO}, Rol: ${usuario.FK_ID_ROL}")

                        // Send FCM token to backend after successful registration
                        sendFCMTokenToServer(usuario.ID_USUARIO)

                        _registerMessage.value = "$message. Redirigiendo..."
                        // Navegar automáticamente al HomeScreen después de un pequeño delay
                        kotlinx.coroutines.delay(1000)
                        navController.navigate(Rout.HomeScreen.name) {
                            popUpTo(Rout.LoginScreen.name) { inclusive = true }
                        }
                    } else {
                        _registerMessage.value = message
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        JSONObject(errorBody ?: "").optString("message", "No fue posible registrar al usuario")
                    } catch (e: Exception) {
                        "No fue posible registrar al usuario"
                    }
                    Log.e("Registro", "Error de API al registrar usuario: $errorBody")
                    _registerError.value = message
                }

            } catch (e: Exception) {
                Log.e("Registro", "Error al registrar usuario: ${e.message}", e)
                e.printStackTrace()
                _registerError.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRegisterFeedback() {
        _registerMessage.value = null
        _registerError.value = null
    }

    fun setBloodTypeName(name: String) {
        _bloodTypeName.value = name
    }
    fun setDepartment(name: String) {
        _department.value = name
    }
    fun setCityName(name: String) {
        _cityName.value = name
    }
    fun setGenderName(name: String) {
        _genderName.value = name
    }


}
