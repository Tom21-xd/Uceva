package com.Tom.uceva_dengue.ui.Components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tom.uceva_dengue.ui.Screen.TipoDocumento
import com.Tom.uceva_dengue.ui.Screen.tipoIdentificaciones
import com.Tom.uceva_dengue.ui.viewModel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrationForm(
    viewModel: AuthViewModel,
    showMedicalPersonnelOption: Boolean = true,
    showTitle: Boolean = true,
    showPasswordField: Boolean = true,
    medicalPersonnelData: MutableState<MedicalPersonnelData>? = null
) {
    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()
    val generos by viewModel.generos.collectAsState()
    val tiposSangre by viewModel.tiposSangre.collectAsState()

    val firstName: String by viewModel.firstName.observeAsState(initial = "")
    val lastName: String by viewModel.lastName.observeAsState(initial = "")
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val address: String by viewModel.address.observeAsState(initial = "")
    val genderId: Int by viewModel.genderId.observeAsState(initial = 0)
    val genderName: String by viewModel.genderName.observeAsState(initial = "")
    val bloodTypeId: Int by viewModel.bloodTypeId.observeAsState(initial = 0)
    val bloodTypeName: String by viewModel.bloodTypeName.observeAsState(initial = "")
    val cityId: Int by viewModel.cityId.observeAsState(initial = 0)
    val cityName: String by viewModel.cityName.observeAsState(initial = "")
    val department: String by viewModel.department.observeAsState(initial = "")
    val registerError: String? by viewModel.registerError.observeAsState()

    var tipoDocumentoSeleccionado by remember { mutableStateOf(tipoIdentificaciones.first().codigo) }
    var numeroDocumento by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var esPersonalMedico by remember { mutableStateOf(false) }

    // Actualizar el estado externo cuando cambien los datos
    LaunchedEffect(esPersonalMedico, tipoDocumentoSeleccionado, numeroDocumento) {
        medicalPersonnelData?.value = MedicalPersonnelData(
            isMedicalPersonnel = esPersonalMedico,
            documentType = tipoDocumentoSeleccionado,
            documentNumber = numeroDocumento
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showTitle) {
            Text("Registro", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Campo(firstName, "Nombres") {
            viewModel.onRegisterChange(email, password, it, lastName, bloodTypeId, cityId, address, genderId)
        }
        Campo(lastName, "Apellidos") {
            viewModel.onRegisterChange(email, password, firstName, it, bloodTypeId, cityId, address, genderId)
        }
        Campo(email, "Correo") {
            viewModel.onRegisterChange(it, password, firstName, lastName, bloodTypeId, cityId, address, genderId)
        }
        Campo(address, "Direccion") {
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, it, genderId)
        }

        // Solo mostrar campo de contraseña si showPasswordField es true
        if (showPasswordField) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.onRegisterChange(email, it, firstName, lastName, bloodTypeId, cityId, address, genderId)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                        Icon(
                            imageVector = if (contrasenaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                }
            )
        }

        ComboBox(
            selectedValue = bloodTypeName,
            options = tiposSangre.map { it.NOMBRE_TIPOSANGRE },
            label = "Tipo de Sangre"
        ) { seleccion ->
            val selectedBloodType = tiposSangre.firstOrNull { it.NOMBRE_TIPOSANGRE == seleccion }
            val id = selectedBloodType?.ID_TIPOSANGRE ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, id, cityId, address, genderId)
            viewModel.setBloodTypeName(seleccion)
        }

        ComboBox(
            selectedValue = department,
            options = departamentos.map { it.NOMBRE_DEPARTAMENTO },
            label = "Departamento"
        ) { nuevoDepartamento ->
            val departamentoSeleccionado = departamentos.firstOrNull { it.NOMBRE_DEPARTAMENTO == nuevoDepartamento }
            departamentoSeleccionado?.ID_DEPARTAMENTO?.let {
                viewModel.fetchMunicipios(it.toString())
                viewModel.setDepartment(nuevoDepartamento)
            }
        }
        ComboBox(
            selectedValue = cityName,
            options = ciudades.map { it.NOMBRE_MUNICIPIO ?: "" },
            label = "Municipio"
        ) { nuevaCiudad ->
            val ciudadSeleccionada = ciudades.firstOrNull { it.NOMBRE_MUNICIPIO == nuevaCiudad }
            val idCiudad = ciudadSeleccionada?.ID_MUNICIPIO ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, idCiudad, address, genderId)
            viewModel.setCityName(nuevaCiudad)
        }
        ComboBox(
            selectedValue = genderName,
            options = generos.map { it.NOMBRE_GENERO },
            label = "Género"
        ) { seleccion ->
            val selectedGenero = generos.firstOrNull { it.NOMBRE_GENERO == seleccion }
            val id = selectedGenero?.ID_GENERO ?: 0
            viewModel.onRegisterChange(email, password, firstName, lastName, bloodTypeId, cityId, address, id)
            viewModel.setGenderName(seleccion)
        }

        // Personal Médico (solo si se permite mostrar esta opción)
        if (showMedicalPersonnelOption) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = esPersonalMedico,
                    onCheckedChange = { esPersonalMedico = it }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Personal Médico", fontSize = 14.sp, color = Color.Black)
            }

            if (esPersonalMedico) {
                ComboBox(
                    selectedValue = tipoDocumentoSeleccionado,
                    options = tipoIdentificaciones.map { "${it.nombre} (${it.codigo})" },
                    label = "Tipo de Documento"
                ) { seleccion ->
                    tipoDocumentoSeleccionado = tipoIdentificaciones.firstOrNull {
                        "${it.nombre} (${it.codigo})" == seleccion
                    }?.codigo ?: ""
                }

                Campo(numeroDocumento, "Número de Documento") {
                    numeroDocumento = it
                }
            }
        }

        registerError?.let { message ->
            Text(
                text = message,
                color = Color(0xFFD32F2F),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

// Exponer las variables del personal médico para que puedan ser accedidas desde fuera
data class MedicalPersonnelData(
    val isMedicalPersonnel: Boolean,
    val documentType: String,
    val documentNumber: String
)
