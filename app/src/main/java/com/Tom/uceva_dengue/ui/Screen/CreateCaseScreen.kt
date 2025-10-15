package com.Tom.uceva_dengue.ui.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.Tom.uceva_dengue.Data.Model.UserModel
import com.Tom.uceva_dengue.ui.Components.ComboBox
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.CreateCaseViewModel
import com.Tom.uceva_dengue.ui.viewModel.MapViewModel
import com.Tom.uceva_dengue.utils.geocodeAddress
import com.Tom.uceva_dengue.utils.moveToUserLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun CreateCaseScreen(
    viewModel: CreateCaseViewModel,
    role: Int,
    user: String?,
    navController: NavHostController
) {
    var isPatientSectionExpanded by remember { mutableStateOf(true) }
    var isDengueSectionExpanded by remember { mutableStateOf(false) }
    var isLocationSectionExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader(
                    title = "Paciente",
                    expanded = isPatientSectionExpanded,
                    onExpandChanged = { isPatientSectionExpanded = !isPatientSectionExpanded }
                ) {
                    PatientSection(viewModel)
                }
            }

            item {
                SectionHeader(
                    title = "Tipo de Dengue",
                    expanded = isDengueSectionExpanded,
                    onExpandChanged = { isDengueSectionExpanded = !isDengueSectionExpanded }
                ) {
                    DengueSection(viewModel)
                }
            }

            item {
                SectionHeader(
                    title = "Ubicación",
                    expanded = isLocationSectionExpanded,
                    onExpandChanged = { isLocationSectionExpanded = !isLocationSectionExpanded }
                ) {
                    LocationSection(viewModel, MapViewModel())
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            viewModel.createCase(
                                idPersonalMedico = user?.toInt() ?: 0,
                                onSuccess = {
                                    Toast.makeText(context, "Caso Creado Exitosamente", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Rout.CaseScreen.name)
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5E81F4),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Reportar Caso",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF2FA))
                    .clickable { onExpandChanged() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E81F4)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF5E81F4)
                )
            }

            if (expanded) {
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}


@Composable
fun PatientSection(viewModel: CreateCaseViewModel) {
    val isExistingUser by viewModel.isExistingUser.collectAsState()
    val patientFirstName by viewModel.patientFirstName.collectAsState()
    val address by viewModel.address.collectAsState()
    val users by viewModel.users.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val genres by viewModel.genres.collectAsState()
    val typeofblood by viewModel.typeofblood.collectAsState()
    val genreSelected: String by viewModel.selectedGenre.observeAsState(initial = "")
    val bloodTypeSelected: String by viewModel.selectedBloodType.observeAsState(initial = "")

    var searchQuery by remember { mutableStateOf("") }
    var filteredUsers by remember { mutableStateOf(users) }
    var showDropdown by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Usuario Existente o Nuevo
        Text(
            text = "¿Usuario existente?",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RadioButton(
                    selected = isExistingUser,
                    onClick = {
                        viewModel.setExistingUser(true)
                        showDropdown = false
                        searchQuery = ""
                    }
                )
                Text("Sí", fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.width(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RadioButton(
                    selected = !isExistingUser,
                    onClick = {
                        viewModel.setExistingUser(false)
                        showDropdown = false
                    }
                )
                Text("No", fontSize = 15.sp)
            }
        }

        if (isExistingUser) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        filteredUsers = if (query.isNotBlank()) {
                            users.filter {
                                it.NOMBRE_USUARIO.toString().contains(query, ignoreCase = true)
                            }
                        } else {
                            emptyList()
                        }
                        showDropdown = filteredUsers.isNotEmpty()
                    },
                    label = { Text("Buscar Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                )

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF4F4F4))
                ) {
                    filteredUsers.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.NOMBRE_USUARIO.toString()) },
                            onClick = {
                                viewModel.selectUser(user)
                                searchQuery = user.NOMBRE_USUARIO.toString()
                                showDropdown = false
                            }
                        )
                    }
                }
            }

            selectedUser?.let {
                SelectedUserCard(it)
            }


        } else {
            OutlinedTextField(
                value = patientFirstName,
                onValueChange = viewModel::setPatientFirstName,
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            ComboBox(
                selectedValue = genreSelected,
                options = genres.map { it.NOMBRE_GENERO },
                label = "Género"
            ) { seleccion ->
                val selectedGenre = genres.firstOrNull { it.NOMBRE_GENERO == seleccion }
                selectedGenre?.let {
                    viewModel.setSelectedGenre(it.NOMBRE_GENERO, it.ID_GENERO)
                }
            }

            ComboBox(
                selectedValue = bloodTypeSelected,
                options = typeofblood.map { it.NOMBRE_TIPOSANGRE },
                label = "Tipo de sangre"
            ) { seleccion ->
                val selectedBlood = typeofblood.firstOrNull { it.NOMBRE_TIPOSANGRE == seleccion }
                selectedBlood?.let {
                    viewModel.setSelectedBloodType(it.NOMBRE_TIPOSANGRE, it.ID_TIPOSANGRE)
                }
            }

            OutlinedTextField(
                value = address,
                onValueChange = viewModel::setAddress,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

        }
    }
}

@Composable
fun DengueSection(viewModel: CreateCaseViewModel) {
    val symptoms by viewModel.symptoms.collectAsState()
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsState()
    val typesOfDengue by viewModel.typesOfDengue.collectAsState()
    val selectedDengueType by viewModel.selectedDengueType.collectAsState()
    val  selectedDengueTypeID by viewModel.selectedDengueTypeID.collectAsState()
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Síntomas",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )

        val columnCount = 2
        val rows = symptoms.chunked(columnCount)

        rows.forEach { rowSymptoms ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowSymptoms.forEach { symptom ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = selectedSymptoms.contains(symptom.ID_SINTOMA),
                            onCheckedChange = {
                                viewModel.toggleSymptom(symptom.ID_SINTOMA)
                            }
                        )
                        Text(
                            text = symptom.NOMBRE_SINTOMA ?: "Sin Nombre",
                            modifier = Modifier.padding(start = 4.dp),
                            maxLines = 1
                        )
                    }
                }

                if (rowSymptoms.size < columnCount) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tipo de Dengue",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )

        ComboBox(
            selectedValue = selectedDengueType,
            options = typesOfDengue.map { it.NOMBRE_TIPODENGUE },
            label = "Seleccione tipo de dengue"
        ) { seleccion ->
            viewModel.setDengueType(seleccion)
        }


    }
}

@Composable
fun LocationSection(viewModel: CreateCaseViewModel, mapViewModel: MapViewModel) {
    val hospitals by viewModel.hospitals.collectAsState()
    val description by viewModel.description.collectAsState()
    val departamentos by viewModel.departamentos.collectAsState()
    val ciudades by viewModel.municipios.collectAsState()

    val cityId: Int by viewModel.cityId.observeAsState(initial = 0)
    val cityName: String by viewModel.cityName.observeAsState(initial = "")
    val selectedHospital by viewModel.selectedHospital.observeAsState(initial = "")
    val department: String by viewModel.department.observeAsState(initial = "")

    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(1.61438, -75.60623), 12f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                    userLocation = it
                }
            }
        }
    )

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        ComboBox(
            selectedValue = department,
            options = departamentos.map { it.NOMBRE_DEPARTAMENTO },
            label = "Departamento",
            enabled = true
        ) { nuevoDepartamento ->
            val departamentoSeleccionado = departamentos.firstOrNull { it.NOMBRE_DEPARTAMENTO == nuevoDepartamento }
            departamentoSeleccionado?.ID_DEPARTAMENTO?.let {
                viewModel.fetchMunicipios(it.toString())
                viewModel.setDepartment(nuevoDepartamento)
                viewModel.setCityName("")
            }
        }

        ComboBox(
            selectedValue = cityName,
            options = ciudades.map { it.NOMBRE_MUNICIPIO ?: "" },
            label = "Municipio",
            enabled = department.isNotBlank()
        ) { nuevaCiudad ->
            val ciudadSeleccionada = ciudades.firstOrNull { it.NOMBRE_MUNICIPIO == nuevaCiudad }
            ciudadSeleccionada?.let {
                val idCiudad = it.ID_MUNICIPIO
                viewModel.setCityName(nuevaCiudad)
                viewModel.setSelectedHospital("", 0)
                viewModel.fetchHospitals(idCiudad)
            }
        }

        ComboBox(
            selectedValue = selectedHospital,
            options = hospitals.map { it.NOMBRE_HOSPITAL ?: "" },
            label = "Hospital",
            enabled = cityName.isNotBlank()
        ) { hospital ->
            val hospitalSeleccionado = hospitals.firstOrNull { it.NOMBRE_HOSPITAL == hospital }
            val idHospital = hospitalSeleccionado?.ID_HOSPITAL ?: 0
            if (hospitalSeleccionado != null) {
                viewModel.setSelectedHospital(hospitalSeleccionado.NOMBRE_HOSPITAL, idHospital)
            }
        }


        OutlinedTextField(
            value = description,
            onValueChange = viewModel::setDescription,
            label = { Text("Descripción del Caso") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar dirección") },
                placeholder = { Text("Ej: Calle 10 #15-20") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    val location = geocodeAddress(context, searchText)
                    if (location != null) {
                        searchLocation = location
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                        viewModel.setLocationCoordinates(location)
                    }
                }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Buscar dirección")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    tiltGesturesEnabled = true,
                    zoomGesturesEnabled = true
                )
            ) {
                searchLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Ubicación buscada"
                    )
                }

                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Mi ubicación",
                        snippet = "Ubicación actual"
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        moveToUserLocation(context, fusedLocationClient, cameraPositionState) {
                            userLocation = it
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                containerColor = Color(0xFF00796B),
                contentColor = Color.White,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.LocationSearching, contentDescription = "Mi ubicación")
            }


        }
    }
}

@Composable
fun SelectedUserCard(user: UserModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.NOMBRE_USUARIO.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF4A4A4A)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Género:",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )
                Text(
                    text = user.NOMBRE_GENERO ?: "Sin género",
                    color = Color(0xFF333333)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Dirección:",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )
                Text(
                    text = user.DIRECCION_USUARIO ?: "Sin dirección",
                    color = Color(0xFF333333)
                )
            }
        }
    }
}
