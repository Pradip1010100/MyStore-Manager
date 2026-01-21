package com.rootlink.mystoremanager.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
import com.rootlink.mystoremanager.data.enums.DashboardPeriod
import com.rootlink.mystoremanager.ui.navigation.MainRoute
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.screen.model.DashboardUiState
import com.rootlink.mystoremanager.ui.viewmodel.DashboardViewModel

/* ---------------------------------------------------
   DASHBOARD SCREEN (ADVANCED)
--------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    val workers by viewModel.workers.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    var showSupplierSheet by remember { mutableStateOf(false) }
    var showWorkerSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTodayDashboard()
        viewModel.loadSuppliers()
        viewModel.loadWorkers()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                DashboardPeriodToggle(
                    selectedPeriod = selectedPeriod,
                    onToday = { viewModel.selectToday() },
                    onMonthly = { viewModel.selectMonth() }
                )
            }
            item {
                BalanceSummaryCard(
                    cashIn = uiState.cashIn,
                    cashOut = uiState.cashOut
                )
            }

            item {
                AdvancedDashboardKpis(
                    uiState = uiState,
                    navController = navController
                )
            }

            item {
                InsightSection(title = "Business Insights") {
                    Text(
                        text =
                            if (uiState.lowStockCount == 0)
                                "All stock levels are healthy 👍"
                            else
                                "${uiState.lowStockCount} products need restocking ⚠",
                        fontSize = 14.sp
                    )
                }
            }
            item {
                DashboardActionRow(
                    onSale = { navController.navigate(Routes.CREATE_SALE) },
                    onPurchase = { showSupplierSheet = true },
                    onPayWorker = { showWorkerSheet = true },
                    onPersonal = { navController.navigate(Routes.PERSONAL_TRANSACTION) }
                )
            }

        }
    }

    /* ---------------- SUPPLIER BOTTOM SHEET ---------------- */

    if (showSupplierSheet) {
        SelectorBottomSheet(
            title = "Select Supplier",
            items = suppliers,
            label = { it.name },
            onSelect = {
                navController.navigate("purchase_entry/${it.supplierId}")
            },
            onDismiss = { showSupplierSheet = false }
        )
    }

    /* ---------------- WORKER BOTTOM SHEET ---------------- */

    if (showWorkerSheet) {
        SelectorBottomSheet(
            title = "Select Worker",
            items = workers,
            label = { it.name },
            onSelect = {
                navController.navigate("worker_payment/${it.workerId}")
            },
            onDismiss = { showWorkerSheet = false }
        )
    }
}

/* ---------------------------------------------------
   KPI SECTION
--------------------------------------------------- */

@Composable
fun AdvancedDashboardKpis(
    uiState: DashboardUiState,
    navController: NavHostController
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(Modifier.fillMaxWidth()) {
            KpiCard(
                title = "Sales",
                value = "${uiState.todaySalesCount} Bills",
                delta = +6.8,
                positive = true,
                onClick = {
                    navController.navigate(MainRoute.SALES.route)
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            KpiCard(
                title = "Low Stock",
                value = "${uiState.lowStockCount}",
                delta = null,
                positive = false,
                onClick = {
                    navController.navigate(Routes.LOW_STOCK)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    delta: Double?,
    positive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            delta?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (positive) Icons.Default.ArrowUpward
                        else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint =
                            if (positive) Color(0xFF2E7D32)
                            else Color(0xFFC62828),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${if (positive) "+" else ""}$it%",
                        fontSize = 12.sp,
                        color =
                            if (positive) Color(0xFF2E7D32)
                            else Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

/* ---------------------------------------------------
   INSIGHT SECTION
--------------------------------------------------- */
@Composable
fun BalanceSummaryCard(
    cashIn: Double,
    cashOut: Double
) {
    val balance = cashIn - cashOut
    val positive = balance >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (positive) Color(0xFFE8F5E9)
                else Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                "Available Balance",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "₹ ${String.format("%.2f", balance)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color =
                    if (positive) Color(0xFF2E7D32)
                    else Color(0xFFC62828)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "In: ₹${cashIn}",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    "Out: ₹${cashOut}",
                    fontSize = 12.sp,
                    color = Color(0xFFC62828)
                )
            }
        }
    }
}

@Composable
fun InsightSection(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(shape = RoundedCornerShape(14.dp)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.ExpandLess
                    else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            if (expanded) {
                Divider()
                Box(Modifier.padding(12.dp)) {
                    content()
                }
            }
        }
    }
}

/* ---------------------------------------------------
   QUICK ACTIONS
--------------------------------------------------- */

@Composable
fun DashboardActionRow(
    onSale: () -> Unit,
    onPurchase: () -> Unit,
    onPayWorker: () -> Unit,
    onPersonal: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        DashboardActionItem(
            icon = Icons.Default.Receipt,
            label = "Sale",
            onClick = onSale
        )

        DashboardActionItem(
            icon = Icons.Default.Inventory,
            label = "Purchase",
            onClick = onPurchase
        )

        DashboardActionItem(
            icon = Icons.Default.People,
            label = "Pay Worker",
            onClick = onPayWorker
        )

        DashboardActionItem(
            icon = Icons.Default.AccountBalanceWallet,
            label = "Personal",
            onClick = onPersonal
        )
    }
}

@Composable
fun DashboardActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/* ---------------------------------------------------
   PERIOD TOGGLE
--------------------------------------------------- */

@Composable
fun DashboardPeriodToggle(
    selectedPeriod: DashboardPeriod,
    onToday: () -> Unit,
    onMonthly: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.Center) {
        FilterChip(
            selected = selectedPeriod == DashboardPeriod.TODAY,
            onClick = onToday,
            label = { Text("Today") }
        )
        Spacer(Modifier.width(8.dp))
        FilterChip(
            selected = selectedPeriod == DashboardPeriod.MONTH,
            onClick = onMonthly,
            label = { Text("This Month") }
        )
    }
}


/* ---------------------------------------------------
   GENERIC BOTTOM SHEET SELECTOR
--------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorBottomSheet(
    title: String,
    items: List<T>,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            title,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        LazyColumn {
            items(items) { item ->
                ListItem(
                    headlineContent = { Text(label(item)) },
                    modifier = Modifier.clickable {
                        onSelect(item)
                        onDismiss()
                    }
                )
            }
        }
    }
}
