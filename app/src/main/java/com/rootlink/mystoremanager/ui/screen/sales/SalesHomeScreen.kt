package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
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
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import com.rootlink.mystoremanager.util.toReadableDateTime
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHomeScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadSales() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.CREATE_SALE) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Sale")
            }
        }
    ) { padding ->

        if (uiState.sales.isEmpty()) {
            EmptySalesState(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.sales) { sale ->
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

/* -------------------------------------------------------------------------- */
/*                                SALE ROW                                    */
/* -------------------------------------------------------------------------- */

@Composable
private fun SaleRow(
    sale: SaleEntity,
    onInvoiceClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            /* ---- TOP ROW: SALE ID + DATE ---- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sale #${sale.saleId}",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = sale.saleDate.toReadableDateTime(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ---- TOTAL AMOUNT ---- */
            Text(
                text = "₹${sale.finalAmount}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            /* ---- ACTIONS ---- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onInvoiceClick) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = "Invoice"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Invoice")
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                              EMPTY STATE                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun EmptySalesState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No sales recorded yet",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tap + to create your first sale",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
