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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val productId =
        backStackEntry?.arguments?.getLong("productId") ?: return

    var adjustmentType by remember { mutableStateOf(StockAdjustmentType.IN) }
    var quantity by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stock Adjustment") })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Adjustment Type")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdjustmentTypeChip("IN", adjustmentType == StockAdjustmentType.IN) {
                    adjustmentType = StockAdjustmentType.IN
                }
                AdjustmentTypeChip("OUT", adjustmentType == StockAdjustmentType.OUT) {
                    adjustmentType = StockAdjustmentType.OUT
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                enabled = quantity.isNotBlank() && reason.isNotBlank(),
                onClick = {
                    viewModel.adjustStock(
                        productId = productId,
                        type = adjustmentType,
                        quantity = quantity.toInt(),
                        reason = reason
                    )
                    navController.popBackStack()
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
