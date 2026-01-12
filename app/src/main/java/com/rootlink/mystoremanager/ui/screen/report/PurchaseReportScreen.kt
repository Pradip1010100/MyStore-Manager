package com.rootlink.mystoremanager.ui.screen.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.PurchaseReportItemUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseReportScreen(
    navController: NavController,
    report: List<PurchaseReportItemUi> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase Report") }
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
                    PurchaseReportRow(item)
                }
            }
        }
    }
}

@Composable
private fun PurchaseReportRow(
    item: PurchaseReportItemUi
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
                text = item.date,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Purchases: ${item.purchaseCount}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Total: ₹${item.totalPurchase}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Paid: ₹${item.totalPaid}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (item.totalDue > 0) {
                    Text(
                        text = "Due: ₹${item.totalDue}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
