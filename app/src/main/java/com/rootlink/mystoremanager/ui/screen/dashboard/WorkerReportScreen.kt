package com.rootlink.mystoremanager.ui.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.WorkerReportItemUi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerReportScreen(
    navController: NavController,
    report: List<WorkerReportItemUi> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Payments Report") }
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
                    WorkerReportRow(item)
                }
            }
        }
    }
}

@Composable
private fun WorkerReportRow(
    item: WorkerReportItemUi
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
                text = item.workerName,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Salary Paid: ₹${item.totalSalaryPaid}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Advance Paid: ₹${item.totalAdvancePaid}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
