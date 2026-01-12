package com.rootlink.mystoremanager.ui.screen.sales

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
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.ui.screen.model.InvoiceUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceViewScreen(
    navController: NavController,
    invoice: InvoiceUi? = null // temporary
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val saleId = backStackEntry
        ?.arguments
        ?.getLong("saleId") ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice") }
            )
        }
    ) { paddingValues ->

        if (invoice == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Invoice not available")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                item {
                    Text(
                        text = "Sale #${invoice.sale.saleId}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total Amount: ₹${invoice.sale.totalAmount}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Discount: ₹${invoice.sale.discount}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Final Amount: ₹${invoice.sale.finalAmount}",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                items(invoice.items) { item ->
                    InvoiceItemRow(item)
                }
            }
        }
    }
}

@Composable
private fun InvoiceItemRow(
    item: SaleItemEntity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Product #${item.productId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Qty: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "₹${item.lineTotal}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
