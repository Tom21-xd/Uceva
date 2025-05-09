package com.Tom.uceva_dengue.ui.viewModel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.Data.Model.DepartmentModel
import com.Tom.uceva_dengue.Data.Model.GenreModel
import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RegisterUserModel
import com.Tom.uceva_dengue.Data.Service.AuthRepository
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

    fun iniciosesioncorreo(email: String, password: String, HomeScreen: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitClient.authService.login(LoginModel(email, password))
                _loading.value = false
                Log.d("NavigationCon", "Inicio de sesión exitoso. Respuesta: ${response.body()}")
                if (response.isSuccessful) {
                    _loginError.value = null
                    val user = response.body()?.ID_USUARIO.toString()
                    val role = response.body()?.FK_ID_ROL


                    val authRepository = AuthRepository(context)

                    user?.let {
                        authRepository.saveUserAndRole(it, role ?: 2)
                    }
                    Log.d("NavigationCon", "Inicio de sesión exitoso. Usuario: $user, Rol: $role")
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
                _departamentos.value = RetrofitClient.departmentService.getDepartments()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar departamentos: ${e.message}")
            }
        }
    }

    fun fetchGeneros() {
        viewModelScope.launch {
            try {
                _generos.value = RetrofitClient.genreService.getGenres()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar géneros: ${e.message}")
            }
        }
    }

    fun fetchTiposSangre() {
        viewModelScope.launch {
            try {
                _tiposSangre.value = RetrofitClient.bloodTypeService.getBloodTypes()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar tipos de sangre: ${e.message}")
            }
        }
    }

    fun fetchMunicipios(departamentoId: String) {
        viewModelScope.launch {
            try {
                _municipios.value = RetrofitClient.cityService.getCities(departamentoId)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar municipios: ${e.message}")
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
                var rolId = 2  // Por defecto, rol regular

                if (esPersonalMedico) {
                    // Dividir nombres y apellidos
                    val nombresDivididos = (_firstName.value ?: "").split(" ")
                    val apellidosDivididos = (_lastName.value ?: "").split(" ")

                    // Usar solo el primer nombre y primer apellido
                    val primerNombre = nombresDivididos.firstOrNull() ?: ""
                    val primerApellido = apellidosDivididos.firstOrNull() ?: ""

                    if (primerNombre.isNotBlank() && primerApellido.isNotBlank()) {
                        val response = RetrofitClient.authService.consultarRethus(
                            primerNombre = primerNombre,
                            primerApellido = primerApellido,
                            tipoIdentificacion = tipoIdentificacion,
                            cedula = numeroDocumento
                        )
                        val a = response.body()?.isSuccess()
                        if (response.isSuccessful && a == true) {
                            rolId = 3
                        } else {
                            Log.e("Rethus", "Validación fallida: ${response.errorBody()?.string()}")
                            _isLoading.value = false
                            return@launch
                        }
                    } else {
                        Log.e("Rethus", "Nombres o apellidos incompletos para validación")
                        _isLoading.value = false
                        return@launch
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

                val responseMessage = RetrofitClient.authService.register(usuario)

                Log.d("Registro", "Usuario registrado en MySQL: $responseMessage")
                navController.navigate("HomeScreen")

            } catch (e: Exception) {
                Log.e("Registro", "Error al registrar usuario: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
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