package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierListScreen(
    navController: NavController,
    suppliers: List<SupplierEntity> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suppliers") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: navigate to AddSupplierScreen
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Supplier")
            }
        }
    ) { paddingValues ->

        if (suppliers.isEmpty()) {
            EmptySupplierState(
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
                items(suppliers) { supplier ->
                    SupplierRow(
                        supplier = supplier,
                        onPurchaseClick = {
                            navController.navigate(
                                "purchase_entry/${supplier.supplierId}"
                            )
                        },
                        onLedgerClick = {
                            navController.navigate(
                                "supplier_ledger/${supplier.supplierId}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SupplierRow(
    supplier: SupplierEntity,
    onPurchaseClick: () -> Unit,
    onLedgerClick: () -> Unit
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
                text = supplier.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Phone: ${supplier.phone}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = onPurchaseClick) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "New Purchase"
                    )
                }

                IconButton(onClick = onLedgerClick) {
                    Icon(
                        Icons.Default.ListAlt,
                        contentDescription = "Supplier Ledger"
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySupplierState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No suppliers added yet",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


