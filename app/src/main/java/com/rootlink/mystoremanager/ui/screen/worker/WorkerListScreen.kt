package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.enums.WorkerStatus
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel

private enum class WorkerFilter { ALL, ACTIVE, INACTIVE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerListScreen(
    navController: NavController
) {
    val viewModel: WorkerViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var filter by remember { mutableStateOf(WorkerFilter.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadWorkers()
    }

    val filteredWorkers = remember(uiState.workers, filter) {
        when (filter) {
            WorkerFilter.ALL -> uiState.workers
            WorkerFilter.ACTIVE ->
                uiState.workers.filter { it.status == WorkerStatus.ACTIVE }
            WorkerFilter.INACTIVE ->
                uiState.workers.filter { it.status != WorkerStatus.ACTIVE }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workers") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.WORKER_ATTENDANCE)
                        }
                    ) {
                        Icon(Icons.Default.Event, contentDescription = "Attendance")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.WORKER_ADD) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Worker")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            /* ================= FILTER CHIPS ================= */

            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == WorkerFilter.ALL,
                    onClick = { filter = WorkerFilter.ALL },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = filter == WorkerFilter.ACTIVE,
                    onClick = { filter = WorkerFilter.ACTIVE },
                    label = { Text("Active") }
                )
                FilterChip(
                    selected = filter == WorkerFilter.INACTIVE,
                    onClick = { filter = WorkerFilter.INACTIVE },
                    label = { Text("Inactive") }
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                filteredWorkers.isEmpty() -> {
                    EmptyWorkerState(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredWorkers) { worker ->
                            WorkerRow(
                                worker = worker,
                                onRowClick = {
                                    navController.navigate(
                                        "worker_profile/${worker.workerId}"
                                    )
                                },
                                onPayClick = {
                                    navController.navigate(
                                        "worker_payment/${worker.workerId}"
                                    )
                                },
                                onLedgerClick = {
                                    navController.navigate(
                                        "worker_ledger/${worker.workerId}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerRow(
    worker: WorkerEntity,
    onRowClick: () -> Unit,
    onPayClick: () -> Unit,
    onLedgerClick: () -> Unit
) {
    val isActive = worker.status == WorkerStatus.ACTIVE

    val statusColor =
        if (isActive)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .alpha(if (isActive) 1f else 0.6f)
            .let {
                 it.clickable { onRowClick() }
            },
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* ---------- AVATAR ---------- */
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = worker.name.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.width(12.dp))

            /* ---------- NAME + PHONE ---------- */
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusColor, CircleShape)
                    )
                }

                worker.phone?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* ---------- ACTIONS ---------- */
            Row(
                modifier = Modifier.width(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    enabled = isActive,
                    onClick = onPayClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Pay"
                    )
                }

                IconButton(
                    enabled = isActive,
                    onClick = onLedgerClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Ledger"
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyWorkerState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No workers found",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
