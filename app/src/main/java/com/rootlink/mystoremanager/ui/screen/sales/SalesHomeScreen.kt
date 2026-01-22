package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.CustomerEntity
import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import com.rootlink.mystoremanager.util.toReadableDateTime
import java.util.*

/* -------------------------------------------------------------------------- */
/*                                PRESETS                                     */
/* -------------------------------------------------------------------------- */

private enum class DatePreset {
    TODAY, LAST_7_DAYS, THIS_MONTH, PREVIOUS_MONTH, THIS_YEAR, PREVIOUS_YEAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHomeScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf<DatePreset?>(null) }
    var fromDate by remember { mutableStateOf<Long?>(null) }
    var toDate by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { viewModel.loadSales() }

    /* ---------------- FILTER LOGIC ---------------- */

    val filteredSales by remember(
        uiState.sales,
        uiState.customerMap,
        searchQuery,
        fromDate,
        toDate
    ) {
        derivedStateOf {

            val query = searchQuery.trim().lowercase()
            val toDateEnd = toDate?.let { endOfDay(it) }

            uiState.sales.filter { sale ->

                val customerName =
                    if (sale.customerId == null)
                        "walk-in customer"
                    else
                        uiState.customerMap[sale.customerId]?.name
                            ?.lowercase()
                            ?: ""

                val saleIdText = sale.saleId.toString()

                val matchesSearch =
                    query.isBlank() ||
                            saleIdText.contains(query) ||
                            customerName.contains(query)

                val matchesDate =
                    (fromDate == null || sale.saleDate >= fromDate!!) &&
                            (toDateEnd == null || sale.saleDate <= toDateEnd)

                matchesSearch && matchesDate
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sales") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.CREATE_SALE) }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        ) {

            /* ---------------- SEARCH ---------------- */

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("Search Sale ID or Customer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            /* ---------------- PRESET CHIPS ---------------- */

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                item {
                    presetChip("Today", selectedPreset == DatePreset.TODAY) {
                        selectedPreset = DatePreset.TODAY
                        fromDate = startOfToday()
                        toDate = endOfToday()
                    }
                }
                item {
                    presetChip("Last 7 Days", selectedPreset == DatePreset.LAST_7_DAYS) {
                        selectedPreset = DatePreset.LAST_7_DAYS
                        val (f, t) = last7DaysRange()
                        fromDate = f
                        toDate = t
                    }
                }
                item {
                    presetChip("This Month", selectedPreset == DatePreset.THIS_MONTH) {
                        selectedPreset = DatePreset.THIS_MONTH
                        val (f, t) = thisMonthRange()
                        fromDate = f
                        toDate = t
                    }
                }
                item {
                    presetChip("Previous Month", selectedPreset == DatePreset.PREVIOUS_MONTH) {
                        selectedPreset = DatePreset.PREVIOUS_MONTH
                        val (f, t) = previousMonthRange()
                        fromDate = f
                        toDate = t
                    }
                }
                item {
                    presetChip("This Year", selectedPreset == DatePreset.THIS_YEAR) {
                        selectedPreset = DatePreset.THIS_YEAR
                        val (f, t) = thisYearRange()
                        fromDate = f
                        toDate = t
                    }
                }
                item {
                    presetChip("Previous Year", selectedPreset == DatePreset.PREVIOUS_YEAR) {
                        selectedPreset = DatePreset.PREVIOUS_YEAR
                        val (f, t) = previousYearRange()
                        fromDate = f
                        toDate = t
                    }
                }
                item {
                    presetChip("Clear", false) {
                        selectedPreset = null
                        fromDate = null
                        toDate = null
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------------- LIST ---------------- */

            if (filteredSales.isEmpty()) {
                EmptySalesState(Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSales) { sale ->
                        SaleCard(
                            sale = sale,
                            customer = uiState.customerMap[sale.customerId],
                            onInvoiceClick = {
                                navController.navigate("invoice_view/${sale.saleId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                CHIP HELPER                                 */
/* -------------------------------------------------------------------------- */

@Composable
private fun presetChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}

/* -------------------------------------------------------------------------- */
/*                                SALE CARD                                   */
/* -------------------------------------------------------------------------- */

@Composable
private fun SaleCard(
    sale: SaleEntity,
    customer: CustomerEntity?,
    onInvoiceClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Sale #${sale.saleId}", fontWeight = FontWeight.SemiBold)
                    Text(
                        customer?.name ?: "Walk-in Customer",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        sale.saleDate.toReadableDateTime(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    "₹%.2f".format(sale.finalAmount),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text("Subtotal ₹%.2f".format(sale.totalAmount)) }
                )

                if (sale.discount > 0) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Discount ₹%.2f".format(sale.discount)) }
                    )
                }
            }

            Divider(Modifier.padding(top = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onInvoiceClick) {
                    Icon(Icons.Default.Receipt, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Invoice")
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                EMPTY STATE                                 */
/* -------------------------------------------------------------------------- */

@Composable
private fun EmptySalesState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Receipt, null, Modifier.size(52.dp))
            Spacer(Modifier.height(8.dp))
            Text("No sales found")
            Text("Try adjusting search or filters", style = MaterialTheme.typography.bodySmall)
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                                DATE UTILS                                  */
/* -------------------------------------------------------------------------- */

private fun startOfToday(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun endOfToday(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.timeInMillis

private fun last7DaysRange(): Pair<Long, Long> {
    val end = endOfToday()
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -6)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    return cal.timeInMillis to end
}

private fun thisMonthRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    return cal.timeInMillis to endOfToday()
}

private fun previousMonthRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, -1)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.MONTH, 1)
    cal.add(Calendar.MILLISECOND, -1)
    return start to cal.timeInMillis
}

private fun thisYearRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    return cal.timeInMillis to endOfToday()
}

private fun previousYearRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.add(Calendar.YEAR, -1)
    cal.set(Calendar.MONTH, Calendar.JANUARY)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.YEAR, 1)
    cal.add(Calendar.MILLISECOND, -1)
    return start to cal.timeInMillis
}

private fun endOfDay(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.timeInMillis
