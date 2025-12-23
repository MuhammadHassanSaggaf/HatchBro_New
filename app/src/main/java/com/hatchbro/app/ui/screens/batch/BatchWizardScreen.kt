package com.hatchbro.app.ui.screens.batch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchWizardScreen(
    navController: NavController,
    viewModel: BatchWizardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.startDate.time
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateStartDate(it) }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Batch") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Steps Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Configuration", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    
                    // Incubator
                    WizardDropdown(
                        label = "Incubator",
                        items = uiState.incubators,
                        selectedItem = uiState.selectedIncubator,
                        itemLabel = { it.name },
                        onItemSelected = { viewModel.selectIncubator(it) }
                    )

                    // Tray
                    WizardDropdown(
                        label = "Tray",
                        items = uiState.availableTrays,
                        selectedItem = uiState.selectedTray,
                        itemLabel = { "Tray #${it.index} (Cap: ${it.capacity})" },
                        onItemSelected = { viewModel.selectTray(it) },
                        enabled = uiState.selectedIncubator != null
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Species & Breed", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    WizardDropdown(
                        label = "Species",
                        items = uiState.speciesList,
                        selectedItem = uiState.selectedSpecies,
                        itemLabel = { it.name },
                        onItemSelected = { viewModel.selectSpecies(it) }
                    )

                    WizardDropdown(
                        label = "Breed",
                        items = uiState.availableBreeds,
                        selectedItem = uiState.selectedBreed,
                        itemLabel = { it.name },
                        onItemSelected = { viewModel.selectBreed(it) },
                        enabled = uiState.selectedSpecies != null
                    )

                    if (uiState.selectedBreed != null) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Recommended Settings", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    IconText(Icons.Default.Thermostat, "${uiState.selectedBreed!!.minTemp}-${uiState.selectedBreed!!.maxTemp}°C")
                                    IconText(Icons.Default.WaterDrop, "${uiState.selectedBreed!!.minHumidity}-${uiState.selectedBreed!!.maxHumidity}%")
                                }
                            }
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Batch Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = uiState.eggsSet,
                        onValueChange = { viewModel.updateEggsSet(it) },
                        label = { Text("Number of Eggs") },
                        leadingIcon = { Icon(Icons.Default.Egg, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                        // keyboardType = Number
                    )

                    OutlinedTextField(
                        value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(uiState.startDate),
                        onValueChange = {},
                        label = { Text("Start Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                        enabled = false, // Handled by click or icon
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) 
                    )

                    // Show error message if exists
                    uiState.errorMessage?.let { errorMsg ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Show temp/humidity warning if exists
                    uiState.tempHumidityWarning?.let { warning ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚠️ Temperature/Humidity Notice",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = warning,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    if (uiState.expectedHatchDate != null) {
                        Text(
                            text = "Expected Hatch: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(uiState.expectedHatchDate!!)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    if (uiState.lockdownDate != null) {
                        Text(
                            text = "Lockdown Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(uiState.lockdownDate!!)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveBatch {
                         navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.selectedBreed != null && uiState.selectedTray != null && uiState.eggsSet.isNotEmpty() && uiState.errorMessage == null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Start Incubation")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun IconText(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> WizardDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (items.isEmpty()) {
                DropdownMenuItem(text = { Text("No items available") }, onClick = {})
            } else {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item)) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
