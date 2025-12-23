package com.hatchbro.app.ui.screens.batch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hatchbro.app.data.local.entity.BatchStatus
import com.hatchbro.app.data.local.entity.EventType
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(
    navController: NavController,
    batchId: Long,
    viewModel: BatchDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(batchId) {
        viewModel.loadBatch(batchId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val batch = uiState.batch
    var showNoteDialog by remember { mutableStateOf(false) }
    var hatchedCount by remember { mutableIntStateOf(0) }
    var discardedCount by remember { mutableIntStateOf(0) }

    // Update counters when batch loads
    LaunchedEffect(batch) {
        if (batch != null) {
            hatchedCount = batch.hatchedCount
            discardedCount = batch.discardedCount
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Batch #${batchId}", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (batch != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (batch.status) {
                            BatchStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            batch.status.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = when (batch.status) {
                                BatchStatus.LOCKDOWN -> MaterialTheme.colorScheme.error
                                BatchStatus.HATCHING -> MaterialTheme.colorScheme.tertiary
                                BatchStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        HorizontalDivider()
                        Text("Eggs Set: ${batch.eggsSet}", style = MaterialTheme.typography.bodyLarge)
                        Text("Start: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(batch.startDate)}")
                        Text("Lockdown: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(batch.lockdownDate)}", color = MaterialTheme.colorScheme.error)
                        Text("Expected Hatch: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(batch.expectedHatchDate)}", color = MaterialTheme.colorScheme.tertiary)
                        
                        if (batch.status == BatchStatus.COMPLETED && batch.hatchedCount > 0) {
                            HorizontalDivider()
                            val efficiency = (batch.hatchedCount.toFloat() / batch.eggsSet.toFloat() * 100).toInt()
                            Text(
                                "Hatch Efficiency: $efficiency%",
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (efficiency >= 80) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Hatching Tracker (only for HATCHING or INCUBATING status)
                if (batch.status == BatchStatus.HATCHING || batch.status == BatchStatus.INCUBATING) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Hatch Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            
                            // Hatched Counter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hatched", style = MaterialTheme.typography.bodyLarge)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (hatchedCount > 0) hatchedCount-- },
                                        enabled = hatchedCount > 0
                                    ) {
                                        Icon(Icons.Default.Remove, "Decrease")
                                    }
                                    Text(
                                        "$hatchedCount",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.widthIn(min = 50.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = { if (hatchedCount < batch.eggsSet) hatchedCount++ },
                                        enabled = hatchedCount < batch.eggsSet
                                    ) {
                                        Icon(Icons.Default.Add, "Increase")
                                    }
                                }
                            }

                            // Discarded Counter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Discarded", style = MaterialTheme.typography.bodyLarge)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (discardedCount > 0) discardedCount-- },
                                        enabled = discardedCount > 0
                                    ) {
                                        Icon(Icons.Default.Remove, "Decrease")
                                    }
                                    Text(
                                        "$discardedCount",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.widthIn(min = 50.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = { if (discardedCount < batch.eggsSet) discardedCount++ },
                                        enabled = discardedCount < batch.eggsSet
                                    ) {
                                        Icon(Icons.Default.Add, "Increase")
                                    }
                                }
                            }

                            HorizontalDivider()
                            
                            // Save and Complete Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateBatchCounts(hatchedCount, discardedCount)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Save Progress")
                                }
                                Button(
                                    onClick = {
                                        viewModel.completeBatch(hatchedCount, discardedCount)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Complete")
                                }
                            }
                        }
                    }
                }

                // Notes Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { showNoteDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Note")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.events.isEmpty()) {
                            Text(
                                "No notes yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            uiState.events.forEach { event ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(event.timestamp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (event.notes != null) {
                                            Text(event.notes, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    if (showNoteDialog) {
        AddNoteDialog(
            onDismiss = { showNoteDialog = false },
            onConfirm = { note ->
                viewModel.addNote(batchId, note)
                showNoteDialog = false
            }
        )
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var note by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
        },
        confirmButton = {
            Button(
                onClick = { if (note.isNotBlank()) onConfirm(note) },
                enabled = note.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
