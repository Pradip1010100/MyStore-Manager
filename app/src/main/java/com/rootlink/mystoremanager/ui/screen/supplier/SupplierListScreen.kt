package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierListScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()
    val suppliersWithDue by viewModel.suppliersWithDue.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSuppliersWithDue()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Suppliers") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("supplier_add") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Supplier")
            }
        }
    ) { padding ->

        if (suppliersWithDue.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No suppliers added")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                items(suppliersWithDue) { (supplier, due) ->
                    SupplierListItem(
                        supplierName = supplier.name,
                        due = due,
                        onClick = {
                            navController.navigate(
                                "supplier_detail/${supplier.supplierId}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SupplierListItem(
    supplierName: String,
    due: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Business icon / initial
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = supplierName.first().uppercase(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = supplierName,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Due: ₹${"%,.0f".format(due)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (due > 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
