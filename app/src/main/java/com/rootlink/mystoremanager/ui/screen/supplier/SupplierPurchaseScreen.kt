package com.rootlink.mystoremanager.ui.screen.supplier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.ui.screen.model.PurchaseItemUi
import com.rootlink.mystoremanager.ui.viewmodel.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierPurchaseScreen(
    navController: NavController
) {
    val viewModel: SupplierViewModel = hiltViewModel()

    val supplierId =
        navController.currentBackStackEntry
            ?.arguments
            ?.getLong("supplierId") ?: return

    val items by viewModel.purchaseItems.collectAsState()
    val total by viewModel.totalAmount.collectAsState()

    var paidAmount by remember { mutableStateOf("") }
    var showAddProduct by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Purchase") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text("Products", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            if (items.isEmpty()) {
                Text("No products added")
            } else {
                items.forEachIndexed { index, item ->
                    PurchaseItemCard(
                        item = item,
                        onRemove = {
                            viewModel.removePurchaseItem(index)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showAddProduct = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Product")
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Total Amount: ₹${"%,.0f".format(total)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = paidAmount,
                onValueChange = { paidAmount = it },
                label = { Text("Paid Now (optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = items.isNotEmpty(),
                onClick = {
                    // 🔒 This will call repository later
                    val paid = paidAmount.toDoubleOrNull() ?: 0.0

                    viewModel.savePurchase(
                        supplierId = supplierId,
                        paidAmount = paid
                    )

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Purchase")
            }
        }
    }

    if (showAddProduct) {
        AddProductBottomSheet(
            onAdd = {
                viewModel.addPurchaseItem(it)
                showAddProduct = false
            },
            onDismiss = { showAddProduct = false },
            onAddNewProduct = {
                navController.navigate("product_add")
            }
        )
    }
}
@Composable
private fun PurchaseItemCard(
    item: PurchaseItemUi,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {

            Text(item.productName, style = MaterialTheme.typography.titleMedium)

            Text(
                "Qty: ${item.quantity} × ₹${"%,.0f".format(item.price)}"
            )

            Text(
                "Line Total: ₹${"%,.0f".format(item.lineTotal)}",
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    onAdd: (PurchaseItemUi) -> Unit,
    onDismiss: () -> Unit,
    viewModel: SupplierViewModel = hiltViewModel(),
    onAddNewProduct: () -> Unit
) {
    val products by viewModel.products.collectAsState()

    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {

        Column(Modifier.padding(16.dp)) {

            Text("Add Product", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            ProductDropdown(
                products = products,
                onSelected = {
                    selectedProduct = it
                    price = it.purchasePrice.toString()
                },
                onAddNew = onAddNewProduct
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Purchase Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = selectedProduct != null &&
                        quantity.isNotBlank() &&
                        price.isNotBlank(),
                onClick = {
                    onAdd(
                        PurchaseItemUi(
                            productId = selectedProduct!!.productId,
                            productName = selectedProduct!!.name,
                            quantity = quantity.toInt(),
                            price = price.toDouble()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Item")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDropdown(
    products: List<ProductEntity>,
    onSelected: (ProductEntity) -> Unit,
    onAddNew: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Select Product") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Product") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            products.forEach { product ->
                DropdownMenuItem(
                    text = { Text(product.name) },
                    onClick = {
                        selectedText = product.name
                        expanded = false
                        onSelected(product)
                    }
                )
            }

            Divider()

            DropdownMenuItem(
                text = {
                    Text(
                        text = "➕ Add New Product",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    expanded = false
                    onAddNew()
                }
            )
        }
    }
}
