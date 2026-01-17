package com.rootlink.mystoremanager.ui.screen.accounting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.enums.TransactionType
import com.rootlink.mystoremanager.ui.viewmodel.AccountingViewModel
import com.rootlink.mystoremanager.ui.viewmodel.state.TransactionUiItem
import com.rootlink.mystoremanager.util.toReadableDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: AccountingViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodayTransactions()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transactions") }) }
    ) { paddingValues ->

        if (transactions.isEmpty()) {
            EmptyTransactionState(
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
                items(transactions) { transaction ->
                    TransactionRow(transaction)
                }
            }
        }
    }
}



@Composable
private fun TransactionRow(
    transaction: TransactionUiItem
) {
    val isOut = transaction.type == TransactionType.OUT

    val bgColor =
        if (isOut)
            MaterialTheme.colorScheme.errorContainer
        else
            MaterialTheme.colorScheme.primaryContainer

    val amountColor =
        if (isOut)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            /* ---------- TOP ROW ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector =
                            if (isOut)
                                Icons.Default.ArrowUpward
                            else
                                Icons.Default.ArrowDownward,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "₹${transaction.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(6.dp))

            /* ---------- SUBTITLE ---------- */
            transaction.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            /* ---------- META ---------- */
            Text(
                text =
                    "${transaction.paymentMode.name} • " +
                            transaction.date.toReadableDateTime(),
                style = MaterialTheme.typography.bodySmall
            )

            /* ---------- NOTES ---------- */
            transaction.notes?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun EmptyTransactionState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No transactions found",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

