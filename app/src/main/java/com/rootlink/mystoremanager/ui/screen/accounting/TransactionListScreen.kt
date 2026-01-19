package com.rootlink.mystoremanager.ui.screen.accounting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.time.LocalDate
import java.time.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: AccountingViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()

    val today = LocalDate.now()
    var selectedYear by remember { mutableStateOf(today.year) }
    var selectedMonth by remember { mutableStateOf(today.monthValue) }

    val canGoNext =
        !(selectedYear == today.year && selectedMonth == today.monthValue)

    LaunchedEffect(selectedYear, selectedMonth) {
        viewModel.loadTransactionsForMonth(
            selectedYear,
            selectedMonth
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            MonthSelector(
                year = selectedYear,
                month = selectedMonth,
                onPrevious = {
                    if (selectedMonth == 1) {
                        selectedMonth = 12
                        selectedYear--
                    } else {
                        selectedMonth--
                    }
                },
                onNext = {
                    if (selectedMonth == 12) {
                        selectedMonth = 1
                        selectedYear++
                    } else {
                        selectedMonth++
                    }
                },
                canGoNext = canGoNext
            )

            Divider()

            if (transactions.isEmpty()) {
                EmptyTransactionState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions) { transaction ->
                        TransactionRow(transaction)
                    }
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

            transaction.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text =
                    "${transaction.paymentMode.name} • " +
                            transaction.date.toReadableDateTime(),
                style = MaterialTheme.typography.bodySmall
            )

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

@Composable
private fun MonthSelector(
    year: Int,
    month: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoNext: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onPrevious) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Previous Month"
            )
        }

        Text(
            text = "${Month.of(month).name} $year",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onNext,
            enabled = canGoNext
        ) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Next Month"
            )
        }
    }
}
