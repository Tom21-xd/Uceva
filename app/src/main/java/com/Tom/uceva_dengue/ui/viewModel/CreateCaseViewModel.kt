package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.Data.Model.CreateCaseModel
import com.Tom.uceva_dengue.Data.Model.DepartmentModel
import com.Tom.uceva_dengue.Data.Model.UserModel
import com.Tom.uceva_dengue.Data.Model.GenreModel
import com.Tom.uceva_dengue.Data.Model.HospitalModel
import com.Tom.uceva_dengue.Data.Model.SymptomModel
import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateCaseViewModel : ViewModel() {

    private val _isExistingUser = MutableStateFlow(true)
    val isExistingUser: StateFlow<Boolean> = _isExistingUser

    private val _users = MutableStateFlow<List<UserModel>>(emptyList())
    val users: StateFlow<List<UserModel>> = _users

    private val _genres = MutableStateFlow<List<GenreModel>>(emptyList())
    val genres: StateFlow<List<GenreModel>> = _genres

    private val _typeofblood = MutableStateFlow<List<BloodTypeModel>>(emptyList())
    val typeofblood: StateFlow<List<BloodTypeModel>> = _typeofblood

    private val _patientFirstName = MutableStateFlow("")
    val patientFirstName: StateFlow<String> = _patientFirstName

    private val _gender = MutableStateFlow("Seleccionar Género")
    val gender: StateFlow<String> = _gender

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _selectedUser = MutableStateFlow<UserModel?>(null)
    val selectedUser: StateFlow<UserModel?> = _selectedUser

    private val _selectedGenre = MutableLiveData("Sin Género")
    val selectedGenre: MutableLiveData<String> = _selectedGenre

    private val _selectedBloodType = MutableLiveData("Sin Tipo")
    val selectedBloodType: MutableLiveData<String> = _selectedBloodType

    private val _symptoms = MutableStateFlow<List<SymptomModel>>(emptyList())
    val symptoms: StateFlow<List<SymptomModel>> = _symptoms

    private val _selectedSymptoms = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSymptoms: StateFlow<Set<Int>> = _selectedSymptoms

    private val _typesOfDengue = MutableStateFlow<List<TypeOfDengueModel>>(emptyList())
    val typesOfDengue: StateFlow<List<TypeOfDengueModel>> = _typesOfDengue.asStateFlow()

    private val _selectedDengueType = MutableStateFlow<String>("")
    val selectedDengueType: StateFlow<String> = _selectedDengueType.asStateFlow()

    private var _selectedDengueTypeID = MutableStateFlow<Int>(0)
    val selectedDengueTypeID: StateFlow<Int> = _selectedDengueTypeID.asStateFlow()


    private val _departamentos = MutableStateFlow<List<DepartmentModel>>(emptyList())
    val departamentos = _departamentos.asStateFlow()

    private val _municipios = MutableStateFlow<List<CityModel>>(emptyList())
    val municipios = _municipios.asStateFlow()

    private val _department = MutableLiveData("")
    val department: LiveData<String> get() = _department

    private val _cityId = MutableLiveData(0)
    val cityId: LiveData<Int> get() = _cityId

    private val _cityName = MutableLiveData("")
    val cityName: LiveData<String> get() = _cityName

    private val _hospitals = MutableStateFlow<List<HospitalModel>>(emptyList())
    val hospitals: MutableStateFlow<List<HospitalModel>> = _hospitals

    private val _selectedHospital = MutableLiveData<String>("")
    val selectedHospital: MutableLiveData<String> = _selectedHospital

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _idhospital = MutableLiveData<Int>(0)
    val idhospital: LiveData<Int> get() = _idhospital

    private val _locationCoordinates = MutableStateFlow<LatLng?>(null)
    val locationCoordinates: StateFlow<LatLng?> = _locationCoordinates.asStateFlow()

    fun setLocationCoordinates(latLng: LatLng) {
        _locationCoordinates.value = latLng
    }


    fun setDepartment(name: String) {
        _department.value = name
    }
    fun setCityName(name: String) {
        _cityName.value = name
    }


    fun setSelectedHospital(hospital: String, idHospital: Int) {
        _selectedHospital.value = hospital
        _idhospital.value = idHospital
    }

    fun setDescription(description: String) {
        _description.value = description
    }


    init {
        fetchUsers()
        fetchGenres()
        fetchBloodType()
        fetchSymptoms()
        fetchTypesOfDengue()
        fetchDepartamentos()
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


    fun fetchMunicipios(departamentoId: String) {
        viewModelScope.launch {
            try {
                _municipios.value = RetrofitClient.cityService.getCities(departamentoId)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cargar municipios: ${e.message}")
            }
        }
    }




    fun fetchHospitals(id:Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.hospitalService.getHospitalsByCity(id)
                if (response.isSuccessful) {
                    _hospitals.value = response.body() ?: emptyList()
                } else {
                    Log.e("CreateCaseViewModel", "Error al cargar hospitales: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCaseViewModel", "Error al cargar hospitales", e)
            }
        }
    }


    private fun fetchSymptoms() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.dengueService.getSymptoms()
                if (response.isSuccessful) {
                    _symptoms.value = response.body() ?: emptyList()
                } else {
                    Log.e("CreateCaseViewModel", "Error al cargar síntomas: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCaseViewModel", "Error al cargar síntomas", e)
            }
        }
    }

    private fun fetchTypesOfDengue() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.dengueService.getTypesOfDengue()
                if (response.isSuccessful) {
                    _typesOfDengue.value = response.body() ?: emptyList()
                } else {
                    Log.e("CreateCaseViewModel", "Error al cargar tipos de dengue: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCaseViewModel", "Error al cargar tipos de dengue", e)
            }
        }
    }

    fun toggleSymptom(symptomId: Int) {
        _selectedSymptoms.value = if (_selectedSymptoms.value.contains(symptomId)) {
            _selectedSymptoms.value - symptomId
        } else {
            _selectedSymptoms.value + symptomId
        }
    }

    fun setDengueType(typeName: String) {
        _selectedDengueType.value = typeName
        val selected = _typesOfDengue.value.firstOrNull { it.NOMBRE_TIPODENGUE == typeName }
        _selectedDengueTypeID.value = selected?.ID_TIPODENGUE ?: 0
    }


    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getUserLives()
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                } else {
                    Log.e("CreateCaseViewModel", "Error al cargar usuarios: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCaseViewModel", "Error al cargar usuarios", e)
            }
        }
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.genreService.getGenres()
                if (response.isSuccessful) {
                    _genres.value = response.body() ?: emptyList()
                } else {
                    Log.e("CreateCaseViewModel", "Error al cargar géneros: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCaseViewModel", "Error al cargar géneros", e)
            }
        }
    }

    private fun fetchBloodType() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.dengueService.getTypesOfBlood()
                if (response.isSuccessful) {
                    _typeofblood.value = (response.body() ?: emptyList())
                } else {
                    Log.e("CreateCaseViewModel", "Error al cargar el tipo de sangre: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateCaseViewModel", "Error al cargar el tipo de angre", e)
            }
        }
    }

    fun setExistingUser(isExisting: Boolean) {
        _isExistingUser.value = isExisting

        if (!isExisting) {
            _selectedUser.value = null
            _patientFirstName.value = ""
            _gender.value = "Seleccionar Género"
            _address.value = ""
        }
    }

    fun setPatientFirstName(firstName: String) {
        _patientFirstName.value = firstName
    }


    fun setAddress(address: String) {
        _address.value = address
    }

    fun setGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun setBloodType(bloodType: String) {
        _selectedBloodType.value = bloodType
    }

    fun selectUser(user: UserModel) {
        _selectedUser.value = user
        _patientFirstName.value = user.NOMBRE_USUARIO.toString()
        _address.value = user.DIRECCION_USUARIO ?: ""
        _gender.value = user.NOMBRE_GENERO ?: "Sin género"
    }

    fun createCase(idPersonalMedico: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val tipoDengueId = _selectedDengueTypeID.value

                val idPaciente = if (_isExistingUser.value) {
                    _selectedUser.value?.ID_USUARIO ?: 0
                } else {
                    0
                }
                val coordenadas = _locationCoordinates.value?.let { "${it.latitude}:${it.longitude}" } ?: ""

                val request = CreateCaseModel(
                    descripcion = _description.value,
                    id_hospital = _idhospital.value ?: 0,
                    id_tipoDengue = tipoDengueId,
                    id_paciente = idPaciente,
                    id_personalMedico = idPersonalMedico,
                    direccion = coordenadas
                )
                Log.d("CreateCaseViewModel", "Request: $request")

                val response = RetrofitClient.caseService.createCase(request)
                if (response.isSuccessful) {
                    onSuccess()

                } else {
                    onError("Error al crear el caso: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Excepción al crear el caso: ${e.message}")
            }
        }
    }

}

