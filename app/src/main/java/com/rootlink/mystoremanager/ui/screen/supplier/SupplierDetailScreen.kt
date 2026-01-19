package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.SupplierPurchaseUi
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDetailScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()

    val supplierId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("supplierId")
            ?: return

    val purchases by viewModel.purchaseHistory.collectAsState()
    val totalPurchased by viewModel.totalPurchased.collectAsState()
    val totalPaid by viewModel.totalPaid.collectAsState()
    val due by viewModel.due.collectAsState()

    LaunchedEffect(supplierId) {
        viewModel.loadSupplierDetail(supplierId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplier Overview") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("supplier_profile/$supplierId")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Supplier Profile"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            /* ---------------- FINANCIAL SUMMARY ---------------- */

            SupplierFinancialSummary(
                totalPurchased = totalPurchased,
                totalPaid = totalPaid,
                due = due
            )

            Spacer(Modifier.height(20.dp))

            /* ---------------- ACTION BAR ---------------- */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("purchase_entry/$supplierId")
                    }
                ) {
                    Text("New Purchase")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("supplier_payment/$supplierId")
                    }
                ) {
                    Text("Pay")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("supplier_ledger/$supplierId")
                    }
                ) {
                    Text("Ledger")
                }
            }

            Spacer(Modifier.height(28.dp))

            /* ---------------- PURCHASE HISTORY ---------------- */

            Text(
                text = "Purchase History",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            if (purchases.isEmpty()) {
                Text(
                    text = "No purchases yet",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                purchases.forEach { purchase ->
                    PurchaseHistoryCardAdvanced(purchase)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SupplierFinancialSummary(
    totalPurchased: Double,
    totalPaid: Double,
    due: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(
                text = "Financial Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            SummaryRow("Total Purchased", totalPurchased)
            SummaryRow(
                "Total Paid",
                totalPaid,
                MaterialTheme.colorScheme.primary
            )

            Divider(Modifier.padding(vertical = 8.dp))

            SummaryRow(
                "Outstanding Due",
                due,
                valueColor =
                    if (due > 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                bold = true
            )
        }
    }
}

@Composable
fun PurchaseHistoryCardAdvanced(
    purchase: SupplierPurchaseUi
) {
    val paid = purchase.totalAmount - purchase.dueAmount
    val isPaid = purchase.dueAmount == 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            /* ---------- HEADER ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = purchase.date,
                    style = MaterialTheme.typography.labelLarge
                )

                StatusChip(isPaid = isPaid)
            }

            Spacer(Modifier.height(10.dp))

            /* ---------- ITEMS ---------- */
            purchase.items.forEach { item ->
                Text(
                    text = "• ${item.productName} × ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            /* ---------- TOTALS ---------- */
            AmountRow(label = "Total", amount = purchase.totalAmount)

            AmountRow(
                label = "Paid",
                amount = paid,
                color = MaterialTheme.colorScheme.primary
            )

            if (!isPaid) {
                AmountRow(
                    label = "Due",
                    amount = purchase.dueAmount,
                    color = MaterialTheme.colorScheme.error,
                    bold = true
                )
            }
        }
    }
}

@Composable
private fun StatusChip(isPaid: Boolean) {
    val bg =
        if (isPaid)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.errorContainer

    val fg =
        if (isPaid)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.error

    Card(
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = if (isPaid) "PAID" else "DUE",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg
        )
    }
}

@Composable
private fun AmountRow(
    label: String,
    amount: Double,
    color: Color = MaterialTheme.colorScheme.onSurface,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style =
                if (bold)
                    MaterialTheme.typography.titleMedium
                else
                    MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "₹${"%,.0f".format(amount)}",
            style =
                if (bold)
                    MaterialTheme.typography.titleMedium
                else
                    MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: Double,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "₹${"%,.0f".format(value)}",
            style =
                if (bold)
                    MaterialTheme.typography.titleMedium
                else
                    MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}
