package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.SaleItemUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleScreen(
    navController: NavController
) {
    val items = remember { mutableStateListOf<SaleItemUi>() }
    var discount by remember { mutableStateOf("") }
    var oldBatteryAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Sale") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { items.add(SaleItemUi()) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Sale Items",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (items.isEmpty()) {
                Text(
                    text = "No items added",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(items) { item ->
                        SaleItemRow(item)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = discount,
                onValueChange = { discount = it },
                label = { Text("Discount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = oldBatteryAmount,
                onValueChange = { oldBatteryAmount = it },
                label = { Text("Old Battery Amount (Optional)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: call SalesViewModel.createSale(...)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete Sale")
            }
        }
    }
}

@Composable
private fun SaleItemRow(
    item: SaleItemUi
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            OutlinedTextField(
                value = item.productName,
                onValueChange = { item.productName = it },
                label = { Text("Product") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = item.quantity,
                    onValueChange = { item.quantity = it },
                    label = { Text("Qty") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = item.price,
                    onValueChange = { item.price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
