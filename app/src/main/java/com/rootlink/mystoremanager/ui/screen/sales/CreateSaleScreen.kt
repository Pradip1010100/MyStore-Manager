package com.rootlink.mystoremanager.ui.screen.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.SaleItemUi
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()

    val items = remember { mutableStateListOf<SaleItemUi>() }
    var discount by remember { mutableStateOf("") }
    var oldBatteryAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Sale") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { items.add(SaleItemUi()) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { item ->
                    SaleItemRow(item)
                }
            }

            OutlinedTextField(
                value = discount,
                onValueChange = { discount = it },
                label = { Text("Discount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = oldBatteryAmount,
                onValueChange = { oldBatteryAmount = it },
                label = { Text("Old Battery Amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.createSale(
                        items = items.toList(),
                        discount = discount.toDoubleOrNull() ?: 0.0,
                        oldBatteryAmount =
                            oldBatteryAmount.toDoubleOrNull()
                    )
                    navController.popBackStack()
                }
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
                    value = item.quantity.toString(),
                    onValueChange = { item.quantity = it.toInt() },
                    label = { Text("Qty") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = item.price.toString(),
                    onValueChange = { item.price = it.toDouble() },
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
