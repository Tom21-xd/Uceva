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
    import androidx.compose.material.icons.filled.Info
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.material3.*
    import androidx.compose.material3.pulltorefresh.PullToRefreshBox
    import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavHostController
    import com.Tom.uceva_dengue.Data.Model.CaseModel
    import com.Tom.uceva_dengue.ui.Components.RequirePermission
    import com.Tom.uceva_dengue.ui.Navigation.Rout
    import com.Tom.uceva_dengue.ui.viewModel.CaseViewModel
    import com.Tom.uceva_dengue.utils.PermissionCode
    import com.Tom.uceva_dengue.utils.UserPermissionsManager
    import com.Tom.uceva_dengue.utils.rememberAppDimensions
    import com.Tom.uceva_dengue.utils.rememberWindowSize
    import kotlinx.coroutines.launch

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CaseScreen(caseViewModel: CaseViewModel, role: Int, navController: NavHostController) {
        val dimensions = rememberAppDimensions()
        val windowSize = rememberWindowSize()
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val permissionsManager = remember { UserPermissionsManager.getInstance(context) }

        val cases by caseViewModel.filteredCases.collectAsState()
        val caseStates by caseViewModel.caseStates.collectAsState()
        val TypeOfDengue by caseViewModel.typeDengue.collectAsState()
        val isLoading by caseViewModel.isLoading.collectAsState()
        val loadingError by caseViewModel.loadingError.collectAsState()
        val isRefreshing by caseViewModel.isRefreshing.collectAsState()

        // Check if user has permission to view all cases
        var hasViewPermission by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            hasViewPermission = permissionsManager.hasPermission(PermissionCode.CASE_VIEW_ALL)
        }

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
                        modifier = Modifier.size(dimensions.iconExtraLarge),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
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
                    .padding(dimensions.paddingMedium),
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
                        modifier = Modifier.size(dimensions.iconExtraLarge)
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                    Text(
                        text = loadingError ?: "Error desconocido",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
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
                    .padding(bottom = dimensions.paddingSmall)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(dimensions.paddingMedium)) {
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

                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

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

                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                    // Show list only if user has VIEW permission
                    if (hasViewPermission) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(cases) { case ->
                                CasoDengueCard(case, role, navController, dimensions)
                            }
                        }
                    } else {
                        // Show informative message when user doesn't have VIEW permission
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensions.paddingMedium),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(dimensions.paddingMedium)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(dimensions.paddingLarge)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Información",
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                                    Text(
                                        text = "No tienes permiso para ver la lista de casos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                                    Text(
                                        text = "Puedes reportar un nuevo caso de dengue usando el botón de abajo",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

            // Solo usuarios con permiso CASE_CREATE pueden crear casos
            RequirePermission(permission = PermissionCode.CASE_CREATE) {
                FloatingActionButton(
                    onClick = { navController.navigate(Rout.CreateCaseScreen.name) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(dimensions.paddingSmall),
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
    fun CasoDengueCard(case: CaseModel, role: Int, navController: NavHostController, dimensions: com.Tom.uceva_dengue.utils.AppDimensions) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingSmall, vertical = dimensions.paddingSmall)
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
                    .padding(dimensions.paddingMedium)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(dimensions.iconExtraLarge)
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

                    Spacer(modifier = Modifier.width(dimensions.spacingMedium))

                    Column {
                        Text(
                            text = case.NOMBRE_PACIENTE,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimensions.textSizeLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Estado: ${case.NOMBRE_ESTADOCASO}",
                            fontSize = dimensions.textSizeMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Fecha: ${case.FECHA_CASOREPORTADO}",
                            fontSize = dimensions.textSizeSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Dirección: ${case.DIRECCION_CASOREPORTADO}",
                            fontSize = dimensions.textSizeSmall,
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
                            .size(dimensions.iconExtraLarge)
                            .background(
                                color = Color(0xFF00796B).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Ver/Editar caso",
                            tint = Color(0xFF00796B),
                            modifier = Modifier.size(dimensions.iconLarge)
                        )
                    }
                }
            }
        }
    }

