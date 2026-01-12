package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHomeScreen(
    navController: NavController,
    sales: List<SaleEntity> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.CREATE_SALE)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Sale")
            }
        }
    ) { paddingValues ->

        if (sales.isEmpty()) {
            EmptySalesState(
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
                items(sales) { sale ->
                    SaleRow(
                        sale = sale,
                        onInvoiceClick = {
                            navController.navigate(
                                "invoice_view/${sale.saleId}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SaleRow(
    sale: SaleEntity,
    onInvoiceClick: () -> Unit
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
                text = "Sale #${sale.saleId}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ₹${sale.finalAmount}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onInvoiceClick) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = "View Invoice"
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySalesState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No sales recorded yet",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
