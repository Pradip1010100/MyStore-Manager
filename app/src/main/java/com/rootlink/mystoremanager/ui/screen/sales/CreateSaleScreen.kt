package com.rootlink.mystoremanager.ui.screen.sales

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.ui.screen.model.ProductForSaleUi
import com.rootlink.mystoremanager.ui.screen.model.SaleItemUi
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import kotlin.math.roundToInt

/* -------------------------------------------------------------------------- */
/*                                  SCREEN                                    */
/* -------------------------------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleScreen(
    navController: NavController
) {
    val viewModel: SalesViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.loadProductsForSale()
    }

    val products by viewModel.products.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    val saleItems = remember { mutableStateListOf<SaleItemUi>() }

    var discount by rememberSaveable { mutableStateOf("") }
    var hasOldBattery by rememberSaveable { mutableStateOf(false) }
    var oldBatteryAmount by rememberSaveable { mutableStateOf("") }

    val subtotal = saleItems.sumOf { it.quantity * it.price }
    val discountValue = discount.toDoubleOrNull() ?: 0.0
    val oldBatteryValue =
        if (hasOldBattery) oldBatteryAmount.toDoubleOrNull() ?: 0.0 else 0.0
    val finalAmount = subtotal - discountValue - oldBatteryValue

    Scaffold(
        topBar = { TopAppBar(title = { Text("Create Sale") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Button(
                onClick = { showAddItemDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Item")
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(saleItems, key = { it.productId!! }) { item ->
                    SaleItemRow(
                        item = item,
                        onRemove = { saleItems.remove(item) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(Modifier.padding(16.dp)) {

                    AmountRow("Subtotal", subtotal)

                    OutlinedTextField(
                        value = discount,
                        onValueChange = { discount = it },
                        label = { Text("Discount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = hasOldBattery,
                            onCheckedChange = { hasOldBattery = it }
                        )
                        Text("Old Battery Exchange")
                    }

                    AnimatedVisibility(hasOldBattery) {
                        OutlinedTextField(
                            value = oldBatteryAmount,
                            onValueChange = { oldBatteryAmount = it },
                            label = { Text("Old Battery Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Divider(Modifier.padding(vertical = 12.dp))

                    AmountRow("Final Amount", finalAmount, highlight = true)
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = saleItems.isNotEmpty(),
                onClick = {
                    viewModel.createSale(
                        items = saleItems,
                        discount = discountValue,
                        oldBatteryAmount =
                            if (hasOldBattery) oldBatteryValue else null
                    )
                    navController.popBackStack()
                }
            ) {
                Text("COMPLETE SALE")
            }
        }
    }

    if (showAddItemDialog) {
        AddSaleItemDialog(
            products = products,
            saleItems = saleItems,
            onAdd = { product, qty ->

                val existingItem = saleItems.find {
                    it.productId == product.productId
                }

                if (existingItem != null) {
                    val index = saleItems.indexOf(existingItem)
                    saleItems[index] = existingItem.copy(
                        quantity = existingItem.quantity + qty
                    )
                } else {
                    saleItems.add(
                        SaleItemUi(
                            productId = product.productId,
                            productName = product.name,
                            quantity = qty,
                            price = product.sellingPrice
                        )
                    )
                }
            },
            onDismiss = { showAddItemDialog = false }
        )
    }
}

/* -------------------------------------------------------------------------- */
/*                               ITEM ROW                                     */
/* -------------------------------------------------------------------------- */

@Composable
private fun SaleItemRow(
    item: SaleItemUi,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(item.productName, style = MaterialTheme.typography.bodyLarge)
            Text("Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("₹${item.quantity * item.price}")
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Remove, contentDescription = "Remove")
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                            ADD ITEM DIALOG                                  */
/* -------------------------------------------------------------------------- */

@Composable
fun AddSaleItemDialog(
    products: List<ProductForSaleUi>,
    saleItems: List<SaleItemUi>,
    onAdd: (ProductForSaleUi, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedProduct by remember { mutableStateOf<ProductForSaleUi?>(null) }
    var qtyText by remember { mutableStateOf("1") }
    var showPicker by remember { mutableStateOf(false) }

    val maxQty = selectedProduct?.availableStock?.roundToInt() ?: 0

    val alreadyAddedQty = saleItems
        .filter { it.productId == selectedProduct?.productId }
        .sumOf { it.quantity }

    val totalStock = selectedProduct?.availableStock?.roundToInt() ?: 0
    val remainingStock = (totalStock - alreadyAddedQty).coerceAtLeast(0)

    val enteredQty = qtyText.toIntOrNull() ?: 0
    val isQtyValid = enteredQty in 1..remainingStock
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedButton(
                    onClick = { showPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedProduct?.name ?: "Select Product")
                }

                selectedProduct?.let {

                    OutlinedTextField(
                        value = qtyText,
                        onValueChange = { text ->
                            qtyText = text

                            val value = text.toIntOrNull()
                            if (value != null && value > remainingStock) {
                                Toast.makeText(
                                    context,
                                    "Only $remainingStock items left in stock",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        label = { Text("Quantity (max $remainingStock)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedProduct != null && isQtyValid,
                onClick = {
                    onAdd(selectedProduct!!, enteredQty)
                    onDismiss()
                }
            ) {
                Text("ADD ITEM")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showPicker) {
        ProductPickerDialog(
            products = products,
            onSelect = {
                selectedProduct = it
                qtyText = "1"
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }
}

/* -------------------------------------------------------------------------- */
/*                              PRODUCT PICKER                                */
/* -------------------------------------------------------------------------- */

@Composable
private fun ProductPickerDialog(
    products: List<ProductForSaleUi>,
    onSelect: (ProductForSaleUi) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isBlank()) {
            products
        } else {
            products.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.brand.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Product") },
        text = {
            Column {

                /* ---------------- SEARCH FIELD ---------------- */
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search product or brand") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                /* ---------------- PRODUCT LIST ---------------- */
                LazyColumn {
                    items(filteredProducts) { product ->
                        val enabled = product.availableStock > 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = enabled) {
                                    onSelect(product)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(product.name)
                                Text(
                                    "${product.brand} • ₹${product.sellingPrice}/${product.unit}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                "Stock: ${product.availableStock.roundToInt()}",
                                color =
                                    if (enabled)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    if (filteredProducts.isEmpty()) {
                        item {
                            Text(
                                "No products found",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/* -------------------------------------------------------------------------- */
/*                                HELPERS                                     */
/* -------------------------------------------------------------------------- */

@Composable
private fun AmountRow(
    label: String,
    amount: Double,
    highlight: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            "₹%.2f".format(amount),
            style =
                if (highlight)
                    MaterialTheme.typography.titleLarge
                else
                    MaterialTheme.typography.bodyLarge,
            color =
                if (highlight)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
        )
    }
}
