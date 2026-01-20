package com.rootlink.mystoremanager.ui.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.StockReportItemUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockReportScreen(
    navController: NavController,
    report: List<StockReportItemUi> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Report") }
            )
        }
    ) { paddingValues ->

        if (report.isEmpty()) {
            EmptyReportState(
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
                items(report) { item ->
                    StockReportRow(item)
                }
            }
        }
    }
}

@Composable
private fun StockReportRow(
    item: StockReportItemUi
) {
    val isLowStock = item.quantityOnHand <= 0

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
                text = item.productName,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Qty: ${item.quantityOnHand} ${item.unit}",
                color = if (isLowStock)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Purchase Value: ₹${item.purchaseValue}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Selling Value: ₹${item.sellingValue}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
