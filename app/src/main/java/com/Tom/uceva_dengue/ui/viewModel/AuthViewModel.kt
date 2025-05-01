package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.Data.Model.DepartmentModel
import com.Tom.uceva_dengue.Data.Model.GenreModel
import com.Tom.uceva_dengue.Data.Model.LoginModel
import com.Tom.uceva_dengue.Data.Model.RegisterUserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

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
            HomeScreen()
            _loading.value = true

            val response = RetrofitClient.authService.login(LoginModel(email, password))

            _loading.value = false

            response.onSuccess { usuarioEncontrado ->
                HomeScreen()
            }.onFailure { error ->
            }
        }
    }

    //---------------------------Registro----------------------------------------


    private val _departamentos = MutableStateFlow<List<DepartmentModel>>(emptyList())
    val departamentos = _departamentos.asStateFlow()

    private val _municipios = MutableStateFlow<List<CityModel>>(emptyList())
    val municipios = _municipios.asStateFlow()

    private val _generos = MutableStateFlow<List<GenreModel>>(emptyList())
    val generos = _generos.asStateFlow()

    init {
        fetchDepartamentos()
        fetchGeneros()
    }

    private fun fetchDepartamentos() {
        viewModelScope.launch {
            _departamentos.value = RetrofitClient.departmentService.getDepartments()

        }
    }

    fun fetchGeneros() {
        viewModelScope.launch {
            _generos.value = RetrofitClient.genreService.getGenres()
        }
    }

    fun fetchMunicipios(departamentoId: String) {
        viewModelScope.launch {
            try {
                _municipios.value = RetrofitClient.cityService.getCities(departamentoId)
            } catch (e: Exception) {
                // Manejo de error
            }

        }
    }

    private val _nombres = MutableLiveData<String>()
    val nombres: MutableLiveData<String>
        get() = _nombres

    private val _apellidos = MutableLiveData<String>()
    val apellidos: MutableLiveData<String>
        get() = _apellidos

    private val _correoR = MutableLiveData<String>()
    val CorreoR: MutableLiveData<String>
        get() = _correoR

    private val _generoId = MutableLiveData<Int>()
    val generoId: LiveData<Int> get() = _generoId

    private val _generoNombre = MutableLiveData<String>()
    val generoNombre: LiveData<String> get() = _generoNombre

    private val _departamento = MutableLiveData<String>()
    val departamento: MutableLiveData<String>
        get() = _departamento

    private val _ciudadId = MutableLiveData<Int>()
    val ciudadId: MutableLiveData<Int>
        get() = _ciudadId

    private val _ciudadNombre = MutableLiveData<String>()
    val ciudadNombre: MutableLiveData<String>
        get() = _ciudadNombre

    private val _direccion = MutableLiveData<String>()
    val direccion: MutableLiveData<String>
        get() = _direccion

    private val _personalMedico = MutableLiveData<Boolean>()
    val personalMedico: MutableLiveData<Boolean>
        get() = _personalMedico

    private val _confirmacionContra = MutableLiveData<String>()
    val confirmacionContra: MutableLiveData<String>
        get() = _confirmacionContra

    private val _profesion = MutableLiveData<String>()
    val profesion: MutableLiveData<String>
        get() = _profesion

    private val _tipoSangre = MutableLiveData<Int>()
    val tipoSangre: MutableLiveData<Int>
        get() = _tipoSangre

    private val _especialidadMedica = MutableLiveData<String>()
    val especialidadMedica: MutableLiveData<String>
        get() = _especialidadMedica

    private val _registroMedico = MutableLiveData<String>()
    val registroMedico: MutableLiveData<String>
        get() = _registroMedico

    private val _fechaNacimiento = MutableLiveData<String>()
    val fechaNacimiento: MutableLiveData<String>
        get() = _fechaNacimiento


    fun OnRegisterChange(
        correoR: String,
        contra: String,
        confirmacionContra: String,
        nombres: String,
        apellidos: String,
        departamento: String,
        ciudadId: Int,
        direccion: String,
        personalMedico: Boolean,
        profesion: String = "",
        especialidadMedica: String = "",
        registroMedico: String = "",
        generoId: Int = 0,
        fechaNacimiento: String="",
        tipoSangre: Int = 0
    ) {
        _correoR.value = correoR
        _contra.value = contra
        _confirmacionContra.value = confirmacionContra
        _nombres.value = nombres
        _apellidos.value = apellidos
        _departamento.value = departamento
        _ciudadId.value = ciudadId
        _direccion.value = direccion
        _personalMedico.value = personalMedico
        _profesion.value = profesion
        _especialidadMedica.value = especialidadMedica
        _registroMedico.value = registroMedico
        _generoId.value = generoId
        _fechaNacimiento.value = fechaNacimiento
        _tipoSangre.value = tipoSangre
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    fun registrarUsuario(navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true

            val email = _correo.value ?: ""
            val password = _contra.value ?: ""

            if (email.isEmpty() || password.isEmpty()) {
                Log.e("Registro", "Correo y contraseña no pueden estar vacíos")
                _isLoading.value = false
                return@launch
            }

            val usuario = RegisterUserModel(
                NOMBRE_USUARIO = "${_nombres.value} ${_apellidos.value}",
                CORREO_USUARIO = email,
                CONTRASENIA_USUARIO = password,
                DIRECCION_USUARIO = _direccion.value ?: "",
                FK_ID_ROL = 2,
                FK_ID_TIPOSANGRE = _tipoSangre.value ?: 0,
                FK_ID_GENERO = _generoId.value ?: 0,
                FK_ID_MUNICIPIO = _ciudadId.value ?: 0
            )

            try {
                val responseMessage = RetrofitClient.authService.register(usuario)

                Log.d("Registro", "Usuario registrado en MySQL: $responseMessage")
                navController.navigate("HomeScreen")

            } catch (e: Exception) {
                Log.e("Registro", "Error al registrar en MySQL: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }




}