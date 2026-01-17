package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.enums.PaymentType
import com.rootlink.mystoremanager.ui.viewmodel.WorkerViewModel
import com.rootlink.mystoremanager.util.toReadableDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerLedgerScreen(
    navController: NavController,
    viewModel: WorkerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val workerId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("workerId") ?: return

    LaunchedEffect(Unit) {
        viewModel.loadWorker(workerId)
        viewModel.loadWorkerLedger(workerId)
    }

    val worker = uiState.selectedWorker
    val payments = uiState.payments

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Worker Ledger") })
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

            worker == null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Worker not found")
                }
            }

            payments.isEmpty() -> {
                EmptyLedgerState(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    /* ================= WORKER HEADER ================= */
                    item {
                        WorkerLedgerHeader(worker)
                    }

                    /* ================= SUMMARY ================= */
                    item {
                        LedgerSummary(payments)
                    }

                    /* ================= PAYMENTS ================= */
                    items(payments) { payment ->
                        WorkerLedgerRowAdvanced(payment)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerLedgerHeader(worker: WorkerEntity) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = worker.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Salary Type: ${worker.salaryType.name}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LedgerSummary(
    payments: List<WorkerPaymentEntity>
) {
    val totalSalary = payments
        .filter { it.paymentType == PaymentType.SALARY }
        .sumOf { it.amount }

    val totalAdvance = payments
        .filter { it.paymentType == PaymentType.ADVANCE }
        .sumOf { it.amount }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                "Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            SummaryRow("Total Salary Paid", "₹ $totalSalary")
            SummaryRow("Total Advance Paid", "₹ $totalAdvance")
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value)
    }
}

@Composable
private fun WorkerLedgerRowAdvanced(
    payment: WorkerPaymentEntity
) {
    val isSalary = payment.paymentType == PaymentType.SALARY

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isSalary)
                    MaterialTheme.colorScheme.surface
                else
                    MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = payment.paymentType.name,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "₹ ${payment.amount}",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = payment.paymentDate.toReadableDate(),
                style = MaterialTheme.typography.bodySmall
            )

            payment.notes?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun EmptyLedgerState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No payments recorded",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
