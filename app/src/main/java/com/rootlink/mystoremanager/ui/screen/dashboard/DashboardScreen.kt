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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.entity.WorkerEntity
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
                    onToday = { viewModel.loadTodayDashboard() },
                    onMonthly = { viewModel.loadMonthlyDashboard() }
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
                SmartQuickActions(
                    hasLowStock = uiState.lowStockCount > 0,
                    onSale = {
                        navController.navigate(Routes.CREATE_SALE)
                    },
                    onPurchase = {
                        showSupplierSheet = true
                    },
                    onPayWorker = {
                        showWorkerSheet = true
                    }
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
                title = "Cash In",
                value = "₹ ${uiState.cashIn}",
                delta = +12.5,
                positive = true,
                onClick = {
                    navController.navigate(Routes.TRANSACTION_LIST)
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            KpiCard(
                title = "Cash Out",
                value = "₹ ${uiState.cashOut}",
                delta = -4.2,
                positive = false,
                onClick = {
                    navController.navigate(Routes.TRANSACTION_LIST)
                },
                modifier = Modifier.weight(1f)
            )
        }

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
fun SmartQuickActions(
    hasLowStock: Boolean,
    onSale: () -> Unit,
    onPurchase: () -> Unit,
    onPayWorker: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Button(onClick = onSale, modifier = Modifier.fillMaxWidth()) {
            Text("New Sale")
        }

        Button(
            onClick = onPurchase,
            modifier = Modifier.fillMaxWidth(),
            colors =
                if (hasLowStock)
                    ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE082))
                else ButtonDefaults.buttonColors()
        ) {
            Text(if (hasLowStock) "Restock Now ⚠" else "New Purchase")
        }

        OutlinedButton(
            onClick = onPayWorker,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay Worker")
        }
    }
}

/* ---------------------------------------------------
   PERIOD TOGGLE
--------------------------------------------------- */

@Composable
fun DashboardPeriodToggle(
    onToday: () -> Unit,
    onMonthly: () -> Unit
) {
    var selected by remember { mutableStateOf(0) }

    Row(horizontalArrangement = Arrangement.Center) {
        FilterChip(
            selected = selected == 0,
            onClick = {
                selected = 0
                onToday()
            },
            label = { Text("Today") }
        )
        Spacer(Modifier.width(8.dp))
        FilterChip(
            selected = selected == 1,
            onClick = {
                selected = 1
                onMonthly()
            },
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
