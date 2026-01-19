package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockScreen(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val lowStock by viewModel.lowStock.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLowStock()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Low Stock Alerts") }) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(lowStock) { item ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.product.name)
                        Text("Qty left: ${item.stock.quantityOnHand}")
                    }
                }
            }
        }
    }
}


@Composable
private fun LowStockRow(product: com.rootlink.mystoremanager.data.entity.ProductEntity) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text("⚠ Low Stock Alert")
        }
    }
}
