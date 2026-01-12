package com.rootlink.mystoremanager.ui.screen.supplier

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
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseEntryScreen(
    navController: NavController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val supplierId = backStackEntry
        ?.arguments
        ?.getLong("supplierId") ?: return

    var paidAmount by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<PurchaseItemUi>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Purchase") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    items.add(PurchaseItemUi())
                }
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
                text = "Purchase Items",
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
                        PurchaseItemRow(item)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = paidAmount,
                onValueChange = { paidAmount = it },
                label = { Text("Paid Amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: call PurchaseViewModel.recordPurchase(...)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Purchase")
            }
        }
    }
}

data class PurchaseItemUi(
    var productName: String = "",
    var quantity: String = "",
    var price: String = ""
)

@Composable
private fun PurchaseItemRow(
    item: PurchaseItemUi
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
