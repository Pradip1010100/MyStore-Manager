package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.entity.PurchaseEntity
import com.rootlink.mystoremanager.data.enums.PaymentStatus
import com.rootlink.mystoremanager.data.enums.SupplierLedgerType
import com.rootlink.mystoremanager.ui.screen.model.SupplierLedgerUiItem
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel
import com.rootlink.mystoremanager.ui.viewmodel.state.SupplierLedgerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierLedgerScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()

    val supplierId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("supplierId")
            ?: return

    val ledger by viewModel.ledgerUi.collectAsState()
    val due by viewModel.due.collectAsState()

    LaunchedEffect(supplierId) {
        viewModel.loadLedger(supplierId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplier Ledger") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(
                                "supplier_payment/$supplierId"
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Pay Supplier"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
        ) {

            // ---------- Due Summary ----------
            Text(
                text = "Current Due: ₹${"%,.0f".format(due)}",
                style = MaterialTheme.typography.titleMedium,
                color = if (due > 0)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            // ---------- Ledger Header ----------
            LedgerHeaderRow()

            Divider()

            // ---------- Ledger Rows ----------
            LazyColumn {
                items(ledger) { item ->
                    LedgerRow(item)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun LedgerHeaderRow() {
    Row(Modifier.fillMaxWidth()) {
        Text("Date", Modifier.weight(1f))
        Text("Type", Modifier.weight(1f))
        Text("Amount", Modifier.weight(1f), textAlign = TextAlign.End)
        Text("Due", Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
private fun LedgerRow(item: SupplierLedgerUiItem) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Text(item.date, Modifier.weight(1f))
        Text(item.label, Modifier.weight(1f))

        Text(
            text = if (item.amount >= 0)
                "+₹${"%,.0f".format(item.amount)}"
            else
                "-₹${"%,.0f".format(-item.amount)}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            color = if (item.amount < 0)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )

        Text(
            "₹${"%,.0f".format(item.dueAfter)}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}



