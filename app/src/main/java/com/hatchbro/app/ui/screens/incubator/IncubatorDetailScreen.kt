package com.hatchbro.app.ui.screens.incubator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorDetailScreen(
    navController: NavController,
    incubatorId: Long,
    viewModel: IncubatorDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(incubatorId) {
        viewModel.loadIncubator(incubatorId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val incubator = uiState.incubator
    var showAddTrayDialog by remember { mutableStateOf(false) }
    var showTempHumidityDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(incubator?.name ?: "Incubator Details", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (incubator != null) {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Incubator Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model: ${incubator.model}", style = MaterialTheme.typography.bodyLarge)
                            Text("Location: ${incubator.location}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Trays (${uiState.trays.size})", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.weight(1f))
                        ExtendedFloatingActionButton(
                            onClick = { showAddTrayDialog = true },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            text = { Text("Add Tray") },
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                items(uiState.trays, key = { it.id }) { tray ->
                    TrayCard(
                        tray = tray,
                        onDelete = { viewModel.deleteTray(tray) }
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    if (showAddTrayDialog) {
        AddTrayDialog(
            onDismiss = { showAddTrayDialog = false },
            onConfirm = { capacity ->
                viewModel.addTray(incubator!!.id, capacity)
                showAddTrayDialog = false
            }
        )
    }

    if (showTempHumidityDialog && incubator != null) {
        TempHumidityDialog(
            currentTemp = incubator.currentTemp,
            currentHumidity = incubator.currentHumidity,
            onDismiss = { showTempHumidityDialog = false },
            onConfirm = { temp, humidity ->
                viewModel.updateTempHumidity(temp, humidity)
                showTempHumidityDialog = false
            }
        )
    }
}

@Composable
fun TrayCard(tray: com.hatchbro.app.data.local.entity.TrayEntity, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tray #${tray.index}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Capacity: ${tray.capacity} eggs", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Tray", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Tray?") },
            text = { Text("Are you sure you want to delete Tray #${tray.index}? Any batches using this tray will be affected.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AddTrayDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var capacity by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Tray") },
        text = {
            OutlinedTextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Egg Capacity") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                val cap = capacity.toIntOrNull() ?: 30
                if (cap > 0) onConfirm(cap)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TempHumidityDialog(
    currentTemp: Float,
    currentHumidity: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float) -> Unit
) {
    var temp by remember { mutableStateOf(currentTemp.toString()) }
    var humidity by remember { mutableStateOf(currentHumidity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Environment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = temp,
                    onValueChange = { temp = it },
                    label = { Text("Temperature (°C)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = humidity,
                    onValueChange = { humidity = it },
                    label = { Text("Humidity (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tempFloat = temp.toFloatOrNull() ?: currentTemp
                    val humidityFloat = humidity.toFloatOrNull() ?: currentHumidity
                    onConfirm(tempFloat, humidityFloat)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
