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
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.material3.*
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

    @Composable
    fun CaseScreen(caseViewModel: CaseViewModel, role: Int, navController: NavHostController) {
        val cases by caseViewModel.filteredCases.collectAsState()
        val caseStates by caseViewModel.caseStates.collectAsState()
        val TypeOfDengue by caseViewModel.typeDengue.collectAsState()

        val estados = listOf("Todos") + caseStates.map { it.NOMBRE_ESTADOCASO }
        val tiposDengue = listOf("Todos")+TypeOfDengue.map { it.NOMBRE_TIPODENGUE }
        var selectedEstadoIndex by remember { mutableStateOf(0) }
        var selectedTipoDengueIndex by remember { mutableStateOf(0) }
        var searchQuery by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
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
                    containerColor = Color.White,
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
                                    color = if (selectedEstadoIndex == index) Color(0xFF00796B) else Color.Gray
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
                        CasoDengueCard(case, navController)
                    }
                }
            }

                FloatingActionButton(
                    onClick = { navController.navigate(Rout.CreateCaseScreen.name) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    containerColor = Color(0xFF92C5FC)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Crear publicación",
                        tint = Color.Black
                    )
                }

        }


    }
    @Composable
    fun CasoDengueCard(case: CaseModel, navController: NavHostController) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clip(MaterialTheme.shapes.medium),
            border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                            fontSize = 24.sp,
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
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "Estado: ${case.NOMBRE_ESTADOCASO}",
                            fontSize = 14.sp,
                            color = Color(0xFF00796B),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Fecha: ${case.FECHA_CASOREPORTADO}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Dirección: ${case.DIRECCION_CASOREPORTADO}",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        navController.navigate("${Rout.CaseDetailsScreen.name}/${case.ID_CASOREPORTADO}")
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar caso",
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

