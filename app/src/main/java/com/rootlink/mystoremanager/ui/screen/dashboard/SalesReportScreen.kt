package com.rootlink.mystoremanager.ui.screen.dashboard

import com.rootlink.mystoremanager.ui.screen.model.SalesReportItemUi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    navController: NavController,
    report: List<SalesReportItemUi> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Report") }
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
                    SalesReportRow(item)
                }
            }
        }
    }
}

@Composable
private fun SalesReportRow(
    item: SalesReportItemUi
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Transactions: ${item.totalTransactions}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "₹${item.totalSales}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun EmptyReportState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No report data available",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
