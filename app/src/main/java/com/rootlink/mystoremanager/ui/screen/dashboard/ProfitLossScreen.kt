package com.rootlink.mystoremanager.ui.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.ProfitLossUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitLossScreen(
    navController: NavController,
    report: ProfitLossUi? = null // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profit & Loss") }
            )
        }
    ) { paddingValues ->

        if (report == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No data available")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                ProfitLossRow(
                    label = "Total Sales",
                    amount = report.totalSales,
                    positive = true
                )

                ProfitLossRow(
                    label = "Purchases",
                    amount = report.totalPurchases,
                    positive = false
                )

                ProfitLossRow(
                    label = "Worker Payments",
                    amount = report.totalWorkerPayments,
                    positive = false
                )

                ProfitLossRow(
                    label = "Other Expenses",
                    amount = report.otherExpenses,
                    positive = false
                )

                Divider()

                ProfitLossRow(
                    label = "Net Profit",
                    amount = report.profit,
                    positive = report.profit >= 0
                )
            }
        }
    }
}

@Composable
private fun ProfitLossRow(
    label: String,
    amount: Double,
    positive: Boolean
) {
    val color =
        if (positive)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.error

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "₹$amount",
            color = color,
            style = MaterialTheme.typography.titleSmall
        )
    }
}
