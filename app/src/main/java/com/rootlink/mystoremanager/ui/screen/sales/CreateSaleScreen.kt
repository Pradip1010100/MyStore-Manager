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
import com.rootlink.mystoremanager.data.entity.CustomerEntity
import com.rootlink.mystoremanager.ui.screen.model.ProductForSaleUi
import com.rootlink.mystoremanager.ui.screen.model.SaleItemUi
import com.rootlink.mystoremanager.ui.viewmodel.SalesViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleScreen(navController: NavController) {

    val viewModel: SalesViewModel = hiltViewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProductsForSale()
        viewModel.loadCustomers()
    }

    val products by viewModel.products.collectAsState()
    val customers by viewModel.customers.collectAsState()

    val saleItems = remember { mutableStateListOf<SaleItemUi>() }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showCustomerPicker by remember { mutableStateOf(false) }

    /* ---------------- CUSTOMER STATE ---------------- */

    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    /* ---------------- AMOUNT STATE ---------------- */

    var discount by rememberSaveable { mutableStateOf("") }
    var hasOldBattery by rememberSaveable { mutableStateOf(false) }
    var oldBatteryAmount by rememberSaveable { mutableStateOf("") }

    val subtotal = saleItems.sumOf { it.quantity * it.price }
    val discountValue = discount.toDoubleOrNull() ?: 0.0
    val oldBatteryValue =
        if (hasOldBattery) oldBatteryAmount.toDoubleOrNull() ?: 0.0 else 0.0
    val finalAmount = subtotal - discountValue - oldBatteryValue

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Sale") },
                actions = {
                    IconButton(onClick = { showAddItemDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 6.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "₹%.2f".format(finalAmount),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Button(
                        enabled = saleItems.isNotEmpty(),
                        onClick = {
                            viewModel.createSale(
                                items = saleItems,
                                discount = discountValue,
                                oldBatteryAmount =
                                    if (hasOldBattery) oldBatteryValue else null,
                                existingCustomer = selectedCustomer,
                                manualName = name,
                                manualPhone = phone,
                                manualAddress = address
                            )
                            navController.popBackStack()
                        }
                    ) {
                        Text("COMPLETE")
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize()
        ) {

            /* ================= CUSTOMER ================= */

            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Customer", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { showCustomerPicker = true }) {
                            Text("Select Existing")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                selectedCustomer = null
                            },
                            label = { Text("Name") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                selectedCustomer = null
                            },
                            label = { Text("Phone") },
                            keyboardOptions =
                                KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            selectedCustomer = null
                        },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ================= ITEMS ================= */

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(saleItems, key = { it.productId!! }) { item ->
                    DenseItemRow(item) {
                        saleItems.remove(item)
                    }
                }

                if (saleItems.isEmpty()) {
                    item {
                        Text(
                            "No items added",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            /* ================= ADJUSTMENTS ================= */

            OutlinedTextField(
                value = discount,
                onValueChange = { discount = it },
                label = { Text("Discount") },
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    keyboardOptions =
                        KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    /* ================= DIALOGS ================= */

    if (showCustomerPicker) {
        CustomerPickerDialog(
            customers = customers,
            onSelect = {
                selectedCustomer = it
                name = it.name
                phone = it.phone
                address = it.address ?: ""
                showCustomerPicker = false
            },
            onDismiss = { showCustomerPicker = false }
        )
    }

    if (showAddItemDialog) {
        AddSaleItemDialog(
            products = products,
            saleItems = saleItems,
            onAdd = { product, qty ->
                val existing = saleItems.find { it.productId == product.productId }
                if (existing != null) {
                    val i = saleItems.indexOf(existing)
                    saleItems[i] =
                        existing.copy(quantity = existing.quantity + qty)
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

/* ================= DENSE ITEM ROW ================= */

@Composable
private fun DenseItemRow(
    item: SaleItemUi,
    onRemove: () -> Unit
) {
    Surface(tonalElevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.productName)
                Text(
                    "Qty ${item.quantity} × ₹${item.price}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text("₹${item.quantity * item.price}")
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Remove, contentDescription = "Remove")
            }
        }
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


//Customer Picker Dialog

@Composable
fun CustomerPickerDialog(
    customers: List<CustomerEntity>,
    onSelect: (CustomerEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Customer") },
        text = {
            LazyColumn {
                items(customers) { customer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(customer)
                            }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(customer.name)
                            Text(customer.phone, style = MaterialTheme.typography.bodySmall)
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
