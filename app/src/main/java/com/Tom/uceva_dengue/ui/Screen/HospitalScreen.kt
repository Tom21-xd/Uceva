package com.Tom.uceva_dengue.ui.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Tom.uceva_dengue.Data.Model.HospitalModel
import com.Tom.uceva_dengue.ui.Components.HospitalCard
import com.Tom.uceva_dengue.ui.Navigation.Rout
import com.Tom.uceva_dengue.ui.viewModel.HospitalViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalScreen(
    navController: NavController,
    viewModel: HospitalViewModel = viewModel(),
    role: Int = 0
) {
    val hospitals by viewModel.hospitals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var hospitalToDelete by remember { mutableStateOf<HospitalModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
            // Buscador
            OutlinedTextField(
                value = searchText,
                onValueChange = { query ->
                    searchText = query
                    if (query.isNotBlank()) viewModel.filterHospitals(query)
                    else viewModel.fetchHospitals()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Buscar hospital...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },

                shape = RoundedCornerShape(24.dp)
            )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(hospitals) { hospital ->
                        HospitalCard(
                            hospital = hospital,
                            role = role,
                            onEdit = {
                                navController.navigate("${Rout.UpdateHospitalScreen.name}/${it.ID_HOSPITAL}")
                            },
                            onDelete = {
                                hospitalToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Botón FAB para crear hospital (solo Administrador y Personal Médico)
        if (role == 2 || role == 3) {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Rout.CreateHospitalScreen.name)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear hospital")
            }
        }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && hospitalToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el hospital '${hospitalToDelete?.NOMBRE_HOSPITAL}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        hospitalToDelete?.let { hospital ->
                            viewModel.deleteHospital(
                                id = hospital.ID_HOSPITAL,
                                onSuccess = {
                                    Toast.makeText(context, "Hospital eliminado con éxito", Toast.LENGTH_SHORT).show()
                                    showDeleteDialog = false
                                    hospitalToDelete = null
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    showDeleteDialog = false
                                }
                            )
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
