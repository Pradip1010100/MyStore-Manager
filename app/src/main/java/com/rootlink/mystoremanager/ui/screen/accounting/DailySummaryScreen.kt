package com.rootlink.mystoremanager.ui.screen.accounting

import com.rootlink.mystoremanager.ui.screen.model.DailySummaryUi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryScreen(
    navController: NavController,
    summary: DailySummaryUi? = null // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Summary") }
            )
        }
    ) { paddingValues ->

        if (summary == null) {
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

                SummaryCard(
                    title = "Total In",
                    amount = summary.totalIn,
                    color = MaterialTheme.colorScheme.primary
                )

                SummaryCard(
                    title = "Total Out",
                    amount = summary.totalOut,
                    color = MaterialTheme.colorScheme.error
                )

                Divider()

                SummaryCard(
                    title = "Net Balance",
                    amount = summary.net,
                    color =
                        if (summary.net >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "₹$amount",
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

