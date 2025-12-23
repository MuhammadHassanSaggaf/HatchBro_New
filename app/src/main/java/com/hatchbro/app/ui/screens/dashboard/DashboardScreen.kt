package com.hatchbro.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hatchbro.app.data.local.entity.BatchEntity
import com.hatchbro.app.data.local.entity.BatchStatus
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text("HatchBro", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = true,
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Incubators") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.IncubatorList.route)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Species & Breeds") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Catalog.route)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Settings.route)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("HatchBro", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                if (uiState.incubators.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.BatchWizard.route) },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("New Batch") },
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Incubators Section
                item {
                    Text(
                        text = "Incubators",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.incubators.isEmpty()) {
                    item {
                        EmptyStateCard(
                            message = "No incubators found. Start by adding your first incubator!",
                            icon = Icons.Default.Home,
                            actionText = "Add Incubator",
                            onAction = { navController.navigate(Screen.IncubatorList.route) }
                        )
                    }
                } else {
                    items(uiState.incubators) { incubator ->
                        IncubatorCard(
                            incubator = incubator,
                            onClick = { navController.navigate(Screen.IncubatorDetail.createRoute(incubator.id)) }
                        )
                    }
                }

                // Batches Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Active Batches",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.activeBatches.isEmpty()) {
                    item {
                        EmptyStateCard(
                            message = "No active batches. Create a new batch to start tracking!",
                            icon = Icons.Default.Egg,
                            actionText = if (uiState.incubators.isNotEmpty()) "Add Batch" else null,
                            onAction = if (uiState.incubators.isNotEmpty()) {
                                { navController.navigate(Screen.BatchWizard.route) }
                            } else null
                        )
                    }
                } else {
                    items(uiState.activeBatches) { batch ->
                        BatchCard(
                            batch = batch,
                            onClick = { navController.navigate(Screen.BatchDetail.createRoute(batch.id)) }
                        )
                    }
                }
                
                // Bottom spacing for FAB
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorCard(incubator: IncubatorEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = incubator.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${incubator.model} • ${incubator.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchCard(batch: BatchEntity, onClick: () -> Unit) {
    val daysPassed = remember(batch.startDate) {
        val diff = Date().time - batch.startDate.time
        TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt().coerceAtLeast(0)
    }
    
    val progress = if (batch.incubationDays > 0) {
        (daysPassed.toFloat() / batch.incubationDays.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val daysRemaining = (batch.incubationDays - daysPassed).coerceAtLeast(0)

    val statusColor = when(batch.status) {
         BatchStatus.LOCKDOWN -> MaterialTheme.colorScheme.error
         BatchStatus.HATCHING -> MaterialTheme.colorScheme.tertiary
         BatchStatus.COMPLETED -> MaterialTheme.colorScheme.primary
         else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (batch.status == BatchStatus.COMPLETED) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Batch #${batch.id}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${batch.eggsSet} eggs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = batch.status.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            HorizontalDivider()
            
            // Progress section
            if (batch.status != BatchStatus.COMPLETED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Day $daysPassed of ${batch.incubationDays}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$daysRemaining days left",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.2f)
                )
            } else {
                // Show efficiency for completed batches
                val efficiency = if (batch.eggsSet > 0) {
                    (batch.hatchedCount.toFloat() / batch.eggsSet.toFloat() * 100).toInt()
                } else 0
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Hatched: ${batch.hatchedCount}", style = MaterialTheme.typography.bodyMedium)
                        if (batch.discardedCount > 0) {
                            Text("Discarded: ${batch.discardedCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        "Efficiency: $efficiency%",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (efficiency >= 80) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}
