package com.rootlink.mystoremanager.ui.screen.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.entity.WorkerPaymentEntity
import com.rootlink.mystoremanager.data.entity.enums.PaymentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerLedgerScreen(
    navController: NavController,
    payments: List<WorkerPaymentEntity> = emptyList() // temporary
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val workerId = backStackEntry
        ?.arguments
        ?.getLong("workerId") ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Ledger") }
            )
        }
    ) { paddingValues ->

        if (payments.isEmpty()) {
            EmptyLedgerState(
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
                items(payments) { payment ->
                    WorkerLedgerRow(payment)
                }
            }
        }
    }
}

@Composable
private fun WorkerLedgerRow(
    payment: WorkerPaymentEntity
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when (payment.paymentType) {
                    PaymentType.SALARY -> "Salary payment"
                    PaymentType.ADVANCE -> "Advance payment"
                },
                style = MaterialTheme.typography.bodySmall
            )

            payment.notes?.let {
                Spacer(modifier = Modifier.height(4.dp))
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
