package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.WorkerEntity

@Composable
fun WorkerListScreen(
    navController: NavController,
    workers: List<WorkerEntity> = emptyList() // temporary
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: navigate to AddWorkerScreen
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Worker")
            }
        }
    ) { paddingValues ->

        if (workers.isEmpty()) {
            EmptyWorkerState(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(workers) { worker ->
                    WorkerRow(
                        worker = worker,
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

@Composable
private fun WorkerRow(
    worker: WorkerEntity,
    onPayClick: () -> Unit,
    onLedgerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = worker.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Phone: ${worker.phone}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onPayClick) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = "Pay Worker"
                    )
                }

                IconButton(onClick = onLedgerClick) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = "Worker Ledger"
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
