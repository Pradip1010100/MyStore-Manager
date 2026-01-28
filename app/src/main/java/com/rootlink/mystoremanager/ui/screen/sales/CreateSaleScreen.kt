package com.rootlink.mystoremanager.ui.screen.sales

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Delete
import com.rootlink.mystoremanager.data.entity.OldBatteryEntity

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

    /* ---------------- STATE ---------------- */

    val saleItems = remember { mutableStateListOf<SaleItemUi>() }

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showCustomerPicker by remember { mutableStateOf(false) }

    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    var discount by rememberSaveable { mutableStateOf("") }

    val subtotal = saleItems.sumOf { it.quantity * it.price }
    val discountValue = discount.toDoubleOrNull() ?: 0.0

    var hasOldBattery by rememberSaveable { mutableStateOf(false) }
    var showOldBatteryDialog by remember { mutableStateOf(false) }

    var obName by rememberSaveable { mutableStateOf("") }
    var obBrand by rememberSaveable { mutableStateOf("") }
    var obType by rememberSaveable { mutableStateOf("") }
    var obQty by rememberSaveable { mutableStateOf("") }
    var obRate by rememberSaveable { mutableStateOf("") }
    var obWeight by rememberSaveable { mutableStateOf("") }
    var obNote by rememberSaveable { mutableStateOf("") }


    val oldBatteryAmount =
        if (hasOldBattery)
            (obQty.toIntOrNull() ?: 0) *
                    (obRate.toDoubleOrNull() ?: 0.0)
        else 0.0

    val finalAmount = subtotal - discountValue - oldBatteryAmount


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
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Final Amount", style = MaterialTheme.typography.labelMedium)
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
                                existingCustomer = selectedCustomer,
                                manualName = name,
                                manualPhone = phone,
                                manualAddress = address,

                                hasOldBattery = hasOldBattery,
                                obName = obName,
                                obBrand = obBrand,
                                obType = obType,
                                obQty = obQty.toIntOrNull() ?: 0,
                                obRate = obRate.toDoubleOrNull() ?: 0.0,
                                obWeight = obWeight.toDoubleOrNull(),
                                obNote = obNote.ifBlank { null }
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

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /* ---------------- CUSTOMER ---------------- */

            item {
                Card {
                    Column(Modifier.padding(12.dp)) {

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Customer", style = MaterialTheme.typography.titleSmall)
                            TextButton(onClick = { showCustomerPicker = true }) {
                                Text("Select Existing")
                            }
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                selectedCustomer = null
                            },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
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
                            modifier = Modifier.fillMaxWidth()
                        )

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
            }

            /* ---------------- ITEMS ---------------- */

            items(saleItems) { item ->
                SaleItemRow(
                    item = item,
                    onRemove = { saleItems.remove(item) }
                )
            }

            if (saleItems.isEmpty()) {
                item {
                    Text(
                        "No items added",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            /* ---------------- ADJUSTMENTS ---------------- */

            item {
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
                        onCheckedChange = {
                            hasOldBattery = it
                            if (it) showOldBatteryDialog = true
                        }
                    )
                    Text("Old Battery Exchange")
                }

                AnimatedVisibility(hasOldBattery) {
                    Text(
                        "Old Battery Amount: ₹%.2f".format(oldBatteryAmount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    /* ---------------- DIALOGS ---------------- */

    if (showAddItemDialog) {
        AddSaleItemDialog(
            products = products,
            saleItems = saleItems,
            onAdd = { product, qty ->
                val existing = saleItems.find { it.productId == product.productId }
                if (existing != null) {
                    val index = saleItems.indexOf(existing)
                    saleItems[index] =
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

    if (showOldBatteryDialog) {
        OldBatteryDialog(
            onSave = { name, brand, type, qty, rate, weight, note ->
                obName = name
                obBrand = brand
                obType = type
                obQty = qty.toString()
                obRate = rate.toString()
                obWeight = weight?.toString() ?: ""
                obNote = note ?: ""
                showOldBatteryDialog = false
            },
            onDismiss = {
                hasOldBattery = false
                showOldBatteryDialog = false
            }
        )
    }

}

@Composable
fun OldBatteryDialog(
    onSave: (
        name: String,
        brand: String,
        type: String,
        qty: Int,
        rate: Double,
        weight: Double?,
        note: String?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    var qty by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    var note by remember { mutableStateOf("") }

    val quantity = qty.toIntOrNull() ?: 0
    val rateValue = rate.toDoubleOrNull() ?: 0.0
    val weightValue = weight.toDoubleOrNull()

    val amount = quantity * rateValue

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Old Battery Details") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Battery Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Battery Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Rate (per battery)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Divider()

                Text(
                    text = "Amount: ₹%.2f".format(amount),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            Button(
                enabled =
                    name.isNotBlank() &&
                            brand.isNotBlank() &&
                            type.isNotBlank() &&
                            quantity > 0 &&
                            rateValue > 0,
                onClick = {
                    onSave(
                        name.trim(),
                        brand.trim(),
                        type.trim(),
                        quantity,
                        rateValue,
                        weightValue,
                        note.trim().ifBlank { null }
                    )
                }
            ) {
                Text("SAVE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


/* ---------------- ITEM ROW ---------------- */

@Composable
private fun SaleItemRow(
    item: SaleItemUi,
    onRemove: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
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
