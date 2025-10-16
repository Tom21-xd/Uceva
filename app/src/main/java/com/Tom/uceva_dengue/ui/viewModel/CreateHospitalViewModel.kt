package com.Tom.uceva_dengue.ui.viewModel

import android.content.Context
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Model.CityModel
import com.Tom.uceva_dengue.Data.Model.DepartmentModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class CreateHospitalViewModel : ViewModel() {

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

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()

    private val _latitud = MutableStateFlow("")
    val latitud: StateFlow<String> = _latitud.asStateFlow()

    private val _longitud = MutableStateFlow("")
    val longitud: StateFlow<String> = _longitud.asStateFlow()

    init {
        fetchDepartamentos()
    }

    fun setNombre(nombre: String) {
        _nombre.value = nombre
    }

    fun setDireccion(direccion: String) {
        _direccion.value = direccion
    }

    fun setLatitud(lat: String) {
        _latitud.value = lat
    }

    fun setLongitud(lon: String) {
        _longitud.value = lon
    }

    fun setDepartment(name: String) {
        _department.value = name
    }

    fun setCityName(name: String) {
        _cityName.value = name
    }

    fun setCityId(id: Int) {
        _cityId.value = id
    }

    private fun fetchDepartamentos() {
        viewModelScope.launch {
            try {
                _departamentos.value = RetrofitClient.departmentService.getDepartments()
            } catch (e: Exception) {
                Log.e("CreateHospitalVM", "Error al cargar departamentos: ${e.message}")
            }
        }
    }

    fun fetchMunicipios(departamentoId: String) {
        viewModelScope.launch {
            try {
                _municipios.value = RetrofitClient.cityService.getCities(departamentoId)
            } catch (e: Exception) {
                Log.e("CreateHospitalVM", "Error al cargar municipios: ${e.message}")
            }
        }
    }

    fun createHospital(
        context: Context,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (_nombre.value.isBlank()) {
                    onError("El nombre es requerido")
                    return@launch
                }

                if (_direccion.value.isBlank()) {
                    onError("La dirección es requerida")
                    return@launch
                }

                if (_latitud.value.isBlank() || _longitud.value.isBlank()) {
                    onError("Las coordenadas son requeridas")
                    return@launch
                }

                if (_cityId.value == 0) {
                    onError("Debe seleccionar un municipio")
                    return@launch
                }

                if (imageUri == null) {
                    onError("La imagen del hospital es requerida")
                    return@launch
                }

                val nombrePart = RequestBody.create("text/plain".toMediaTypeOrNull(), _nombre.value)
                val direccionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), _direccion.value)
                val latitudPart = RequestBody.create("text/plain".toMediaTypeOrNull(), _latitud.value)
                val longitudPart = RequestBody.create("text/plain".toMediaTypeOrNull(), _longitud.value)
                val municipioPart = RequestBody.create("text/plain".toMediaTypeOrNull(), _cityId.value.toString())

                val imagePart = imageUri?.let { uri ->
                    try {
                        val file = copyUriToTempFile(context, uri)
                        MultipartBody.Part.createFormData(
                            "imagen",
                            file.name,
                            RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                val response = RetrofitClient.hospitalService.createHospital(
                    nombre = nombrePart,
                    direccion = direccionPart,
                    latitud = latitudPart,
                    longitud = longitudPart,
                    idMunicipio = municipioPart,
                    imagen = imagePart
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al crear hospital: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CreateHospitalVM", "Error al crear hospital", e)
                onError("Error de red: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun copyUriToTempFile(context: Context, uri: Uri): File {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("hospital_", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }
            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            tempFile
        }
    }
}
