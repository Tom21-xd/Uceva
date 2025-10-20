    package com.Tom.uceva_dengue.ui.Screen

    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material.icons.filled.Edit
    import androidx.compose.material.icons.filled.Error
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.material3.*
    import androidx.compose.material3.pulltorefresh.PullToRefreshBox
    import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavHostController
    import com.Tom.uceva_dengue.Data.Model.CaseModel
    import com.Tom.uceva_dengue.ui.Navigation.Rout
    import com.Tom.uceva_dengue.ui.viewModel.CaseViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CaseScreen(caseViewModel: CaseViewModel, role: Int, navController: NavHostController) {
        val cases by caseViewModel.filteredCases.collectAsState()
        val caseStates by caseViewModel.caseStates.collectAsState()
        val TypeOfDengue by caseViewModel.typeDengue.collectAsState()
        val isLoading by caseViewModel.isLoading.collectAsState()
        val loadingError by caseViewModel.loadingError.collectAsState()
        val isRefreshing by caseViewModel.isRefreshing.collectAsState()

        val estados = listOf("Todos") + caseStates.map { it.NOMBRE_ESTADOCASO }
        val tiposDengue = listOf("Todos")+TypeOfDengue.map { it.NOMBRE_TIPODENGUE }
        var selectedEstadoIndex by remember { mutableStateOf(0) }
        var selectedTipoDengueIndex by remember { mutableStateOf(0) }
        var searchQuery by remember { mutableStateOf("") }

        // Mostrar loader mientras carga
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando casos...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            return
        }

        // Mostrar error si lo hay
        if (loadingError != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = loadingError ?: "Error desconocido",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { caseViewModel.loadAllData() }) {
                        Text("Reintentar")
                    }
                }
            }
            return
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { caseViewModel.refreshData() },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            caseViewModel.filterCasesByState(estados[selectedEstadoIndex])
                        },
                        placeholder = { Text("Buscar por nombre o ID") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedEstadoIndex,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = Color(0xFF00796B),
                        indicator = { tabPositions ->
                            if (tabPositions.isNotEmpty() && selectedEstadoIndex < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[selectedEstadoIndex])
                                        .background(Color(0xFF00796B))
                                        .height(3.dp)
                                )
                            }
                        }
                    ) {
                        /*tiposDengue.forEachIndexed { index, typeDengue ->
                            Tab(
                                selected = selectedTipoDengueIndex == index,
                                onClick = {
                                    selectedTipoDengueIndex = index
                                    caseViewModel.filterCasesByTypeOfDengue(typeDengue)
                                },
                                text = {
                                    Text(
                                        text = typeDengue,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTipoDengueIndex == index) Color(0xFF00796B) else Color.Gray
                                    )
                                }
                            )
                        }*/

                        estados.forEachIndexed { index, estado ->
                            Tab(
                                selected = selectedEstadoIndex == index,
                                onClick = {
                                    selectedEstadoIndex = index
                                    caseViewModel.filterCasesByState(estado)
                                },
                                text = {
                                    Text(
                                        text = estado,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedEstadoIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cases) { case ->
                            CasoDengueCard(case, role, navController)
                        }
                    }
                }

            // Solo Administrador (2) y Personal Médico (3) pueden crear casos
            if (role == 2 || role == 3) {
                FloatingActionButton(
                    onClick = { navController.navigate(Rout.CreateCaseScreen.name) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Crear caso",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            }
        }


    }
    @Composable
    fun CasoDengueCard(case: CaseModel, role: Int, navController: NavHostController) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clip(MaterialTheme.shapes.medium),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color(0xFF00796B), Color(0xFF004D40))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = case.NOMBRE_PACIENTE.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = case.NOMBRE_PACIENTE,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Estado: ${case.NOMBRE_ESTADOCASO}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Fecha: ${case.FECHA_CASOREPORTADO}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Dirección: ${case.DIRECCION_CASOREPORTADO}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Solo Administrador (2) y Personal Médico (3) pueden ver/editar casos
                if (role == 2 || role == 3) {
                    IconButton(
                        onClick = {
                            navController.navigate("${Rout.CaseDetailsScreen.name}/${case.ID_CASOREPORTADO}")
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFF00796B).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Ver/Editar caso",
                            tint = Color(0xFF00796B),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

