package com.rootlink.mystoremanager.ui.screen.supplier

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
import com.rootlink.mystoremanager.data.entity.PurchaseEntity
import com.rootlink.mystoremanager.data.entity.enums.PaymentStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierLedgerScreen(
    navController: NavController,
    purchases: List<PurchaseEntity> = emptyList() // temporary
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val supplierId = backStackEntry
        ?.arguments
        ?.getLong("supplierId") ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplier Ledger") }
            )
        }
    ) { paddingValues ->

        if (purchases.isEmpty()) {
            EmptySupplierLedgerState(
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
                items(purchases) { purchase ->
                    SupplierLedgerRow(purchase)
                }
            }
        }
    }
}

@Composable
private fun SupplierLedgerRow(
    purchase: PurchaseEntity
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
                    text = "Purchase #${purchase.purchaseId}",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = when (purchase.status) {
                        PaymentStatus.PAID -> "PAID"
                        PaymentStatus.PARTIALLY_PAID -> "PARTIAL"
                        PaymentStatus.CREATED -> "CREDIT"
                        PaymentStatus.CANCELLED -> "CANCELLED"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ₹${purchase.totalAmount}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Paid: ₹${purchase.paidAmount}",
                style = MaterialTheme.typography.bodySmall
            )

            if (purchase.dueAmount > 0) {
                Text(
                    text = "Due: ₹${purchase.dueAmount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptySupplierLedgerState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No purchases recorded",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


