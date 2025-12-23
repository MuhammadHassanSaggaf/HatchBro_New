package com.hatchbro.app.ui.screens.incubator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hatchbro.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorListScreen(
    navController: NavController,
    viewModel: IncubatorListViewModel = hiltViewModel()
) {
    val incubators by viewModel.incubators.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Incubators", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Incubator") },
                containerColor = MaterialTheme.colorScheme.secondary
            )
        }
    ) { padding ->
        if (incubators.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No incubators yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add your first incubator to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(incubators, key = { it.id }) { incubator ->
                    IncubatorCard(
                        incubator = incubator,
                        onClick = { navController.navigate(Screen.IncubatorDetail.createRoute(incubator.id)) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddIncubatorDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, model, location, trays, temp, humidity ->
                viewModel.addIncubator(name, model, location, trays, temp, humidity)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorCard(incubator: com.hatchbro.app.data.local.entity.IncubatorEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incubator.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = incubator.model,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = incubator.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "${incubator.trayCount} trays",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            HorizontalDivider()
            
            // Environment Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌡️", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${incubator.currentTemp}°C",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Temperature",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                VerticalDivider(modifier = Modifier.height(50.dp))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💧", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${incubator.currentHumidity}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Humidity",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddIncubatorDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var trays by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("37.5") }
    var humidity by remember { mutableStateOf("55") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Incubator") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = trays,
                    onValueChange = { trays = it },
                    label = { Text("Tray Count") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Environment Settings", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Temp (°C)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = humidity,
                        onValueChange = { humidity = it },
                        label = { Text("Humidity (%)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && model.isNotBlank()) {
                        onConfirm(
                            name,
                            model,
                            location.ifBlank { "Default Location" },
                            trays.toIntOrNull() ?: 0,
                            temp.toFloatOrNull() ?: 37.5f,
                            humidity.toFloatOrNull() ?: 55f
                        )
                    }
                },
                enabled = name.isNotBlank() && model.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
