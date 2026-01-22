package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.SupplierEntity
import com.rootlink.mystoremanager.data.enums.SupplierStatus
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel

private enum class SupplierFilter {
    ALL, ACTIVE, INACTIVE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierListScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()
    val suppliersWithDue by viewModel.suppliersWithDue.collectAsState()

    var filter by remember { mutableStateOf(SupplierFilter.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadSuppliersWithDue()
    }

    val filteredSuppliers = remember(suppliersWithDue, filter) {
        when (filter) {
            SupplierFilter.ALL -> suppliersWithDue
            SupplierFilter.ACTIVE ->
                suppliersWithDue.filter { it.first.status == SupplierStatus.ACTIVE }
            SupplierFilter.INACTIVE ->
                suppliersWithDue.filter { it.first.status != SupplierStatus.ACTIVE }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Suppliers") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("supplier_add") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Supplier")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            /* ---------- FILTER CHIPS ---------- */
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == SupplierFilter.ALL,
                    onClick = { filter = SupplierFilter.ALL },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = filter == SupplierFilter.ACTIVE,
                    onClick = { filter = SupplierFilter.ACTIVE },
                    label = { Text("Active") }
                )
                FilterChip(
                    selected = filter == SupplierFilter.INACTIVE,
                    onClick = { filter = SupplierFilter.INACTIVE },
                    label = { Text("Inactive") }
                )
            }

            /* ---------- LIST ---------- */

            if (filteredSuppliers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No suppliers",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn {
                    items(filteredSuppliers) { (supplier, due) ->
                        SupplierListItem(
                            supplier = supplier,
                            due = due,
                            onClick = {
                                navController.navigate(
                                    "supplier_detail/${supplier.supplierId}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SupplierListItem(
    supplier: SupplierEntity,
    due: Double,
    onClick: () -> Unit
) {
    val isInactive = supplier.status != SupplierStatus.ACTIVE

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .then(
                if (isInactive)
                    Modifier.alpha(0.6f)
                else
                    Modifier
            ),
        border =
            if (isInactive)
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            else
                null,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* ---------- AVATAR ---------- */
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = supplier.name.first().uppercase(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            /* ---------- INFO ---------- */
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = supplier.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (isInactive) {
                        Spacer(Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Inactive") }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Due: ₹${"%,.0f".format(due)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (due > 0)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
