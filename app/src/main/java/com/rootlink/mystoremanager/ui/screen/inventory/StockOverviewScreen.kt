package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.entity.StockEntity
import com.rootlink.mystoremanager.ui.screen.model.StockUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockOverviewScreen(
    navController: NavController,
    stockList: List<StockUi> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Overview") }
            )
        }
    ) { paddingValues ->

        if (stockList.isEmpty()) {
            EmptyStockState(
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
                items(stockList) { item ->
                    StockRow(item)
                }
            }
        }
    }
}

@Composable
private fun StockRow(
    item: StockUi
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = item.product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Qty: ${item.stock.quantityOnHand}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyStockState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No stock data available",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
