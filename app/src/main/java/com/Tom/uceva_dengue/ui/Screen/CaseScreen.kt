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

        val cases by caseViewModel.displayedCases.collectAsState()
        val caseStates by caseViewModel.caseStates.collectAsState()
        val TypeOfDengue by caseViewModel.typeDengue.collectAsState()
        val isLoading by caseViewModel.isLoading.collectAsState()
        val loadingError by caseViewModel.loadingError.collectAsState()
        val isRefreshing by caseViewModel.isRefreshing.collectAsState()
        val isLoadingMore by caseViewModel.isLoadingMore.collectAsState()
        val hasMorePages by caseViewModel.hasMorePages.collectAsState()

        // Check if user has permission to view all cases
        var hasViewPermission by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            hasViewPermission = permissionsManager.hasPermission(PermissionCode.CASE_VIEW_ALL)
        }

        val tiposDengue = listOf("Todos") + TypeOfDengue.map { it.NOMBRE_TIPODENGUE }
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
                            caseViewModel.filterCasesByTypeOfDengue(tiposDengue[selectedTipoDengueIndex])
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
                        selectedTabIndex = selectedTipoDengueIndex,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            if (tabPositions.isNotEmpty() && selectedTipoDengueIndex < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTipoDengueIndex])
                                        .background(MaterialTheme.colorScheme.primary)
                                        .height(3.dp)
                                )
                            }
                        }
                    ) {
                        tiposDengue.forEachIndexed { index, tipoDengue ->
                            Tab(
                                selected = selectedTipoDengueIndex == index,
                                onClick = {
                                    selectedTipoDengueIndex = index
                                    caseViewModel.filterCasesByTypeOfDengue(tipoDengue)
                                },
                                text = {
                                    Text(
                                        text = tipoDengue,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTipoDengueIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
                            items(cases, key = { it.ID_CASOREPORTADO }) { case ->
                                CasoDengueCard(case, role, navController, dimensions)
                            }

                            // Indicador de carga al final de la lista
                            if (hasMorePages && cases.isNotEmpty()) {
                                item {
                                    LaunchedEffect(Unit) {
                                        caseViewModel.loadMoreCases()
                                    }

                                    if (isLoadingMore) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(dimensions.paddingMedium),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            // Mensaje cuando no hay más casos
                            if (!hasMorePages && cases.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimensions.paddingMedium),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No hay más casos",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
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
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = case.NOMBRE_PACIENTE.take(2).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(dimensions.spacingMedium))

                    Column {
                        // Nombre del paciente con badge si es anónimo
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = case.NOMBRE_PACIENTE,
                                fontWeight = FontWeight.Bold,
                                fontSize = dimensions.textSizeLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Badge de caso anónimo
                            if (case.FK_ID_PACIENTE == null) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = "Anónimo",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Mostrar edad y año para casos epidemiológicos
                        if (case.EDAD_PACIENTE != null || case.ANIO_REPORTE != null) {
                            val infoText = buildString {
                                case.EDAD_PACIENTE?.let { append("Edad: $it años") }
                                if (case.EDAD_PACIENTE != null && case.ANIO_REPORTE != null) append(" • ")
                                case.ANIO_REPORTE?.let { append("Año: $it") }
                            }
                            Text(
                                text = infoText,
                                fontSize = dimensions.textSizeSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

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

                        // Mostrar barrio si existe
                        case.BARRIO_VEREDA?.let { barrio ->
                            if (barrio.isNotBlank()) {
                                Text(
                                    text = "Barrio: $barrio",
                                    fontSize = dimensions.textSizeSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

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
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Ver/Editar caso",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(dimensions.iconLarge)
                        )
                    }
                }
            }
        }
    }

