package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInventoryScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val productId = backStackEntry?.arguments?.getLong("productId") ?: return

    val stockOverview by viewModel.stockOverview.collectAsState()
    val history by viewModel.stockHistory.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadStockOverview()
        viewModel.loadStockHistory(productId)
    }

    val stockItem = stockOverview.firstOrNull {
        it.product.productId == productId
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product Details") })
        }
    ) { padding ->

        if (stockItem == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            /* ================= PRODUCT HEADER ================= */
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = stockItem.product.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (stockItem.product.brand.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stockItem.product.brand,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


            /* ================= PRODUCT INFO ================= */
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        InfoRow("Unit", stockItem.product.unit.ifBlank { "-" })

                        Divider()

                        InfoRow(
                            "Purchase Price",
                            "₹${stockItem.product.purchasePrice}"
                        )

                        InfoRow(
                            "Selling Price",
                            "₹${stockItem.product.sellingPrice}"
                        )

                        InfoRow(
                            "Warranty",
                            if (stockItem.product.warrantyMonths > 0)
                                "${stockItem.product.warrantyMonths} months"
                            else
                                "No warranty"
                        )
                    }
                }
            }


            /* ================= STOCK SUMMARY ================= */
            item {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (stockItem.stock.quantityOnHand <= 5)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Current Stock",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = stockItem.stock.quantityOnHand.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color =
                                if (stockItem.stock.quantityOnHand <= 5)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                        )

                        if (stockItem.stock.quantityOnHand <= 5) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "⚠ Low Stock",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            /* ================= ACTIONS ================= */
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(
                                "stock_adjustment/${productId}?type=IN"
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Stock IN")
                    }

                    FilledTonalButton(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        onClick = {
                            navController.navigate(
                                "stock_adjustment/${productId}?type=OUT"
                            )
                        }
                    ) {
                        Icon(Icons.Default.Remove, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Stock OUT")
                    }
                }
            }

            /* ================= HISTORY ================= */
            item {
                Text(
                    text = "Stock History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        text = "No stock movements yet",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(history) { item ->
                    StockHistoryRow(item)
                }
            }
        }
    }
}

/* ---------------- SMALL COMPONENTS ---------------- */

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun StockHistoryRow(
    adjustment: com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity
) {
    val formatter = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    }

    val isIn = adjustment.adjustmentType == StockAdjustmentType.IN

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {

            Text(
                text = if (isIn)
                    "IN  +${adjustment.quantity}"
                else
                    "OUT  -${adjustment.quantity}",
                color =
                    if (isIn)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall
            )

            if (adjustment.reason.isNotBlank()) {
                Text(
                    text = adjustment.reason,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = formatter.format(Date(adjustment.adjustmentDate)),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
