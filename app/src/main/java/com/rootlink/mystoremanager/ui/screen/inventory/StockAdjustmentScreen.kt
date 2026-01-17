package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentScreen(
    navController: NavController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val productId = backStackEntry
        ?.arguments
        ?.getLong("productId") ?: return

    var adjustmentType by remember { mutableStateOf(StockAdjustmentType.IN) }
    var quantity by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Adjustment") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Adjustment Type", style = MaterialTheme.typography.titleSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdjustmentTypeChip(
                    text = "IN",
                    selected = adjustmentType == StockAdjustmentType.IN
                ) {
                    adjustmentType = StockAdjustmentType.IN
                }

                AdjustmentTypeChip(
                    text = "OUT",
                    selected = adjustmentType == StockAdjustmentType.OUT
                ) {
                    adjustmentType = StockAdjustmentType.OUT
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (quantity.isNotBlank() && reason.isNotBlank()) {
                        // TODO: call InventoryViewModel.adjustStock(...)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Adjustment")
            }
        }
    }
}

@Composable
private fun AdjustmentTypeChip(
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
