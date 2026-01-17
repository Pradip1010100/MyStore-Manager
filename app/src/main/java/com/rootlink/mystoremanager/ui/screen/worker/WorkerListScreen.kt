package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.enums.SalaryType
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerListScreen(
    navController: NavController
) {
    val viewModel: WorkerViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWorkers()
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
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Attendance"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.WORKER_ADD)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Worker")
            }
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.workers.isEmpty() -> {
                EmptyWorkerState(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(uiState.workers) { worker ->
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

@Composable
private fun WorkerRow(
    worker: WorkerEntity,
    onRowClick: () -> Unit,
    onPayClick: () -> Unit,
    onLedgerClick: () -> Unit
) {
    val statusColor =
        if (worker.status.name == "ACTIVE")
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onRowClick() },
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

            /* ---------- ACTIONS (EVENLY SPACED) ---------- */
            Row(
                modifier = Modifier.width(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onPayClick) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Pay"
                    )
                }

                IconButton(onClick = onLedgerClick) {
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
            text = "No workers added yet",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
