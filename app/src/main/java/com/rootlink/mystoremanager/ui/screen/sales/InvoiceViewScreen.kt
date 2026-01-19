package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import com.rootlink.mystoremanager.util.toReadableDateTime
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceViewScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()

    val saleId = navController
        .currentBackStackEntry
        ?.arguments
        ?.getLong("saleId") ?: return
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(saleId) {
        viewModel.loadInvoice(saleId)
    }

    val sale = uiState.selectedSale ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice") },
                actions = {
                    IconButton(
                        onClick = {
                            // 🔜 Hook for PDF generation + share
                            viewModel.shareInvoicePdf(
                                context,
                                sale,
                                uiState.invoiceItems
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Invoice"
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
                .fillMaxSize()
        ) {

            /* ---------------- SHOP HEADER ---------------- */
            Text(
                text = "S N Enterprises",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Battery & Inverter Store",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            /* ---------------- INVOICE META ---------------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Invoice No:")
                    Text(
                        "INV-${sale.saleId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Date:")
                    Text(
                        sale.saleDate.toReadableDateTime(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- ITEMS HEADER ---------------- */
            InvoiceHeaderRow()

            Divider()

            /* ---------------- ITEMS LIST ---------------- */
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.invoiceItems) { item ->
                    InvoiceItemRow(
                        item = item,
                        productName =
                            uiState.productNameMap[item.productId] ?: "Product"
                    )
                }
            }

            Divider()

            /* ---------------- TOTALS ---------------- */
            Spacer(Modifier.height(8.dp))

            AmountRow("Subtotal", sale.totalAmount)
            AmountRow("Discount", sale.discount)
            AmountRow("Final Amount", sale.finalAmount, highlight = true)

            Spacer(Modifier.height(16.dp))

            /* ---------------- FOOTER ---------------- */
            Text(
                text = "Thank you for your business!",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                               ITEM TABLE                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun InvoiceHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Item", modifier = Modifier.weight(2f))
        Text("Qty", modifier = Modifier.weight(1f))
        Text("Amount", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
private fun InvoiceItemRow(
    item: SaleItemEntity,
    productName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(productName, Modifier.weight(2f))
        Text(item.quantity.toString(), Modifier.weight(1f))
        Text(
            "₹${item.lineTotal}",
            Modifier.weight(1f)
        )
    }
}

/* -------------------------------------------------------------------------- */
/*                               HELPERS                                      */
/* -------------------------------------------------------------------------- */

@Composable
private fun AmountRow(
    label: String,
    amount: Double,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            "₹%.2f".format(amount),
            style =
                if (highlight)
                    MaterialTheme.typography.titleLarge
                else
                    MaterialTheme.typography.bodyLarge,
            color =
                if (highlight)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatDate(timestamp: Long?): String {
    if (timestamp == null) return "-"
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
