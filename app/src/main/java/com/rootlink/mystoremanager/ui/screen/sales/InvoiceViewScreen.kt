package com.rootlink.mystoremanager.ui.screen.sales

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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.entity.SaleItemEntity
import com.rootlink.mystoremanager.ui.viewmodel.state.InvoiceUi
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel


import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceViewScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()

    val saleId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("saleId") ?: return

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(saleId) {
        viewModel.loadInvoice(saleId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Invoice") }) }
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

            uiState.selectedSale == null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Invoice not available")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    item {
                        val sale = uiState.selectedSale!!
                        Text("Sale #${sale.saleId}")
                        Text("Total: ₹${sale.totalAmount}")
                        Text("Discount: ₹${sale.discount}")
                        Text("Final: ₹${sale.finalAmount}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                    }

                    items(uiState.invoiceItems) {
                        InvoiceItemRow(it)
                    }
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
