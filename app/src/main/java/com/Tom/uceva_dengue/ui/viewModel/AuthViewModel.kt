package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Domain.Entities.Departamento
import com.Tom.uceva_dengue.Domain.Entities.Genero
import com.Tom.uceva_dengue.Domain.Entities.Municipio
import com.Tom.uceva_dengue.Domain.Entities.Usuario
import com.Tom.uceva_dengue.Domain.UseCases.Departamento.GetDepartamentosUseCase
import com.Tom.uceva_dengue.Domain.UseCases.Genero.GetGenerosUseCase
import com.Tom.uceva_dengue.Domain.UseCases.Municipio.GetMunicipiosUseCase
import com.Tom.uceva_dengue.Domain.UseCases.Usuario.CrearUsuarioUseCase
import com.Tom.uceva_dengue.Domain.UseCases.Auth.IniciarSesion
import com.Tom.uceva_dengue.Domain.UseCases.Auth.RegistrarUsuarioAuthUseCase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){


    private val  auth : FirebaseAuth = Firebase.auth

    private val _correo = MutableLiveData<String>() //Se usa para crear e iniciar sesion
    val correo: MutableLiveData<String>
        get() = _correo

    private val _contra = MutableLiveData<String>()//Se usa para crear e iniciar sesion
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

    private fun correovalido(correo: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    fun onLoginChange(correo: String, contra: String) {
        _correo.value = correo
        _contra.value = contra
        _loginEnabled.value = correovalido(correo) && contravalida(contra)
    }

    private val signInUseCase = IniciarSesion()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    fun iniciosesioncorreo(email: String, password: String, HomeScreen: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true

            val success = signInUseCase.execute(email, password)

            _loading.value = false

            if (success) {
                HomeScreen()
            } else {

            }
        }
    }

    //---------------------------Registro----------------------------------------
    private val getDepartmentsUseCase: GetDepartamentosUseCase = GetDepartamentosUseCase()
    private val getMunicipiosUseCase: GetMunicipiosUseCase = GetMunicipiosUseCase()
    private val getGenerosUseCase: GetGenerosUseCase = GetGenerosUseCase()
    private val CrearUsuarioUseCase: CrearUsuarioUseCase = CrearUsuarioUseCase()
    private val registrarUsuarioAuthUseCase: RegistrarUsuarioAuthUseCase = RegistrarUsuarioAuthUseCase()

    private val _departamentos = MutableStateFlow<List<Departamento>>(emptyList())
    val departamentos = _departamentos.asStateFlow()

    private val _municipios = MutableStateFlow<List<Municipio>>(emptyList())
    val municipios = _municipios.asStateFlow()

    private val _generos = MutableStateFlow<List<Genero>>(emptyList())
    val generos = _generos.asStateFlow()

    init {
        fetchDepartamentos()
        fetchGeneros()
    }

    private fun fetchDepartamentos() {
        viewModelScope.launch {
            getDepartmentsUseCase.execute().collect { listaDepartamentos ->
                _departamentos.value = listaDepartamentos
            }
        }
    }

    fun fetchGeneros() {
        viewModelScope.launch {
            getGenerosUseCase.execute().collect {
                _generos.value = it
            }
        }
    }

    fun fetchMunicipios(departamentoId: String) {
        viewModelScope.launch {
            getMunicipiosUseCase.execute(departamentoId).collect {
                _municipios.value = it
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
        get()= _correoR

    private val _genero = MutableLiveData<String>()
    val genero: MutableLiveData<String>
        get() = _genero

    private val _departamento = MutableLiveData<String>()
    val departamento: MutableLiveData<String>
        get() = _departamento

    private val _ciudad = MutableLiveData<String>()
    val ciudad: MutableLiveData<String>
        get() = _ciudad


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
        ciudad: String,
        direccion: String,
        personalMedico: Boolean,
        profesion: String = "",
        especialidadMedica: String = "",
        registroMedico: String = "",
        genero: String = "",
        fechaNacimiento: String=""

    ) {
        _correoR.value = correoR
        _contra.value = contra
        _confirmacionContra.value = confirmacionContra
        _nombres.value = nombres
        _apellidos.value = apellidos
        _departamento.value = departamento
        _ciudad.value = ciudad
        _direccion.value = direccion
        _personalMedico.value = personalMedico
        _profesion.value = profesion
        _especialidadMedica.value = especialidadMedica
        _registroMedico.value = registroMedico
        _genero.value = genero
        _fechaNacimiento.value = fechaNacimiento
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    fun RegistrarUsuario(navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true

            val email = _correoR.value ?: ""
            val password = _contra.value ?: ""

            if (email.isEmpty() || password.isEmpty()) {
                Log.e("Registro", "Correo y contraseña no pueden estar vacíos")
                _isLoading.value = false
                return@launch
            }

            val resultadoAuth = registrarUsuarioAuthUseCase.execute(email, password)
            resultadoAuth.onSuccess { userId ->

                val usuario = Usuario(
                    Id = userId,
                    Nombre = "${_nombres.value} ${_apellidos.value}",
                    Ciudad = _ciudad.value ?: "",
                    Departamento = _departamento.value ?: "",
                    Correo = email,
                    Direccion = _direccion.value ?: "",
                    FechaNacimiento = _fechaNacimiento.value ?: "",
                    Genero = _genero.value ?: "",
                    personalMedico = _personalMedico.value ?: false,
                    profesion = _profesion.value ?: "",
                    especialidadMedica = _especialidadMedica.value ?: "",
                    registroMedico = _registroMedico.value ?: ""
                )

                val resultadoFirestore = CrearUsuarioUseCase.execute(usuario)
                resultadoFirestore.onSuccess {
                    Log.d("Registro", "Usuario registrado en Firestore")
                    _isLoading.value = false
                    navController.navigate("HomeScreen")
                }.onFailure { error ->
                    Log.e("Registro", "Error al guardar en Firestore: ${error.message}")
                    _isLoading.value = false
                }
            }.onFailure { error ->
                Log.e("Registro", "Error al registrar en FirebaseAuth: ${error.message}")
                _isLoading.value = false
            }
        }
    }}