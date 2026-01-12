package com.rootlink.mystoremanager.ui.screen.accounting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.TransactionEntity
import com.rootlink.mystoremanager.data.entity.enums.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    transactions: List<TransactionEntity> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") }
            )
        }
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
    transaction: TransactionEntity
) {
    val color = when (transaction.transactionType) {
        TransactionType.IN -> MaterialTheme.colorScheme.primary
        TransactionType.OUT -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "₹${transaction.amount}",
                    color = color,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = transaction.paymentMode.name,
                style = MaterialTheme.typography.bodySmall
            )

            transaction.notes?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
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

