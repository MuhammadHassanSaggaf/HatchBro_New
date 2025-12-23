package com.hatchbro.app.ui.screens.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hatchbro.app.data.local.entity.BreedEntity
import com.hatchbro.app.data.local.entity.SpeciesEntity
import com.hatchbro.app.data.local.entity.SpeciesWithBreeds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val speciesList by viewModel.speciesWithBreeds.collectAsState()
    var showAddSpeciesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Species & Breeds", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSpeciesDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Species") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(speciesList, key = { it.species.id }) { speciesLinks ->
                SpeciesCard(
                    speciesData = speciesLinks,
                    onDeleteSpecies = { viewModel.deleteSpecies(it) },
                    onAddBreed = { name, incub, lock, discard ->
                        viewModel.addBreed(speciesLinks.species.id, name, incub, lock, discard)
                    },
                    onDeleteBreed = { viewModel.deleteBreed(it) }
                )
            }
        }
    }

    if (showAddSpeciesDialog) {
        SimpleInputDialog(
            title = "New Species",
            label = "Species Name (e.g. Chicken)",
            onConfirm = { 
                viewModel.addSpecies(it)
                showAddSpeciesDialog = false
            },
            onDismiss = { showAddSpeciesDialog = false }
        )
    }
}

@Composable
fun SpeciesCard(
    speciesData: SpeciesWithBreeds,
    onDeleteSpecies: (SpeciesEntity) -> Unit,
    onAddBreed: (String, Int, Int, Int) -> Unit,
    onDeleteBreed: (BreedEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAddBreed by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = speciesData.species.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Species", tint = MaterialTheme.colorScheme.error)
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Breeds",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (speciesData.breeds.isEmpty()) {
                        Text(
                            "No breeds added yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        speciesData.breeds.forEach { breed ->
                            BreedItem(breed = breed, onDelete = { onDeleteBreed(breed) })
                        }
                    }

                    Button(
                        onClick = { showAddBreed = true },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Breed")
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Species?") },
            text = { Text("Deleting ${speciesData.species.name} will also delete all associated breeds and batches. Use with caution.") },
            confirmButton = {
                Button(
                    onClick = { 
                        onDeleteSpecies(speciesData.species)
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

    if (showAddBreed) {
        AddBreedDialog(
            onDismiss = { showAddBreed = false },
            onConfirm = { name, incubation, lockdown, discard ->
                onAddBreed(name, incubation, lockdown, discard)
                showAddBreed = false
            }
        )
    }
}

@Composable
fun BreedItem(breed: BreedEntity, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(breed.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "${breed.defaultIncubationDays} days incubation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Breed", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun SimpleInputDialog(title: String, label: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text(label) }, singleLine = true) },
        confirmButton = { Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddBreedDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var incubationDays by remember { mutableStateOf("21") }
    // We can calculate logic here or just let user input everything. 
    // Usually lockdown is Incubation - 3.
    // Discard is usually Incubation + 2.
    
    // Auto-calculate logic helpers
    fun updateDefaults(incDays: String) {
        val days = incDays.toIntOrNull()
        if (days != null) {
            // No, can't easily update other fields if they are also stateful strings unless we track "touched".
            // For MVP, just defaults.
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Breed") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Breed Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = incubationDays,
                    onValueChange = { incubationDays = it },
                    label = { Text("Incubation Days") },
                    singleLine = true
                    // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Needs import
                )
            }
        },
        confirmButton = { 
            Button(
                onClick = { 
                    val days = incubationDays.toIntOrNull() ?: 21
                    val lockdown = days - 3
                    val discard = 2 // Grace period
                    if (name.isNotBlank()) onConfirm(name, days, lockdown, discard) 
                }
            ) { Text("Add Breed") } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
