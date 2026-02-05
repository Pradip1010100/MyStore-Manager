package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rootlink.mystoremanager.data.entity.ProductCategoryEntity
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.enums.ProductStatus
import com.rootlink.mystoremanager.data.enums.StockAdjustmentType
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInventoryScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val productId = backStackEntry?.arguments?.getLong("productId") ?: return

    val stockOverview by viewModel.stockOverview.collectAsState()
    val history by viewModel.stockHistory.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }


    LaunchedEffect(productId) {
        viewModel.loadStockOverview()
        viewModel.loadStockHistory(productId)
    }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    val stockItem = stockOverview.firstOrNull {
        it.product.productId == productId
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product Details") })
        }
    ) { padding ->

        if (stockItem == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            /* ================= PRODUCT HEADER ================= */
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = stockItem.product.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (stockItem.product.brand.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stockItem.product.brand,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }


            /* ================= PRODUCT INFO ================= */
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        InfoRow("Unit", stockItem.product.unit.ifBlank { "-" })

                        Divider()

                        InfoRow(
                            "Purchase Price",
                            "₹${stockItem.product.purchasePrice}"
                        )

                        InfoRow(
                            "Selling Price",
                            "₹${stockItem.product.sellingPrice}"
                        )

                        InfoRow(
                            "Warranty",
                            if (stockItem.product.warrantyMonths > 0)
                                "${stockItem.product.warrantyMonths} months"
                            else
                                "No warranty"
                        )
                    }
                }
            }


            /* ================= STOCK SUMMARY ================= */
            item {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (stockItem.stock.quantityOnHand <= 5)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Current Stock",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = stockItem.stock.quantityOnHand.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color =
                                if (stockItem.stock.quantityOnHand <= 5)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                        )

                        if (stockItem.stock.quantityOnHand <= 5) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "⚠ Low Stock",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            /* ================= ACTIONS ================= */
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            enabled = stockItem.product.status == ProductStatus.ACTIVE,
                            onClick = {
                                navController.navigate(
                                    "stock_adjustment/${productId}?type=IN"
                                )
                            }
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Stock IN")
                        }

                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            enabled = stockItem.product.status == ProductStatus.ACTIVE,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = {
                                navController.navigate(
                                    "stock_adjustment/${productId}?type=OUT"
                                )
                            }
                        ) {
                            Icon(Icons.Default.Remove, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Stock OUT")
                        }
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = stockItem.product.status == ProductStatus.ACTIVE,
                        onClick = { showEditDialog = true }
                    ) {
                        Text("Edit Product")
                    }


                    /* -------- ACTIVATE / DEACTIVATE -------- */

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            if (stockItem.product.status == ProductStatus.ACTIVE)
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            else
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                        onClick = { showConfirm = true }
                    ) {
                        Text(
                            if (stockItem.product.status == ProductStatus.ACTIVE)
                                "Deactivate Product"
                            else
                                "Activate Product"
                        )
                    }
                }
            }


            /* ================= HISTORY ================= */
            item {
                Text(
                    text = "Stock History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        text = "No stock movements yet",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(history) { item ->
                    StockHistoryRow(item)
                }
            }
        }
    }
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = {
                Text(
                    if (stockItem?.product?.status == ProductStatus.ACTIVE)
                        "Deactivate Product"
                    else
                        "Activate Product"
                )
            },
            text = {
                Text(
                    if (stockItem?.product?.status == ProductStatus.ACTIVE)
                        "This product will no longer be available for sale. Continue?"
                    else
                        "This product will become available for sale again. Continue?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirm = false
                        if (stockItem?.product?.status == ProductStatus.ACTIVE) {
                            viewModel.deactivateProduct(productId)
                        } else {
                            viewModel.activateProduct(productId)
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        EditProductDialog(
            product = stockItem?.product,
            categories = viewModel.categories.collectAsState().value,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateProduct(
                    productId = productId,
                    name = updated.name,
                    categoryId = updated.categoryId,
                    brand = updated.brand,
                    unit = updated.unit,
                    purchasePrice = updated.purchasePrice,
                    sellingPrice = updated.sellingPrice,
                    warrantyMonths = updated.warrantyMonths
                )
                showEditDialog = false
            }
        )
    }


}

/* ---------------- SMALL COMPONENTS ---------------- */

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun StockHistoryRow(
    adjustment: com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity
) {
    val formatter = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    }

    val isIn = adjustment.adjustmentType == StockAdjustmentType.IN

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {

            Text(
                text = if (isIn)
                    "IN  +${adjustment.quantity}"
                else
                    "OUT  -${adjustment.quantity}",
                color =
                    if (isIn)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleSmall
            )

            if (adjustment.reason.isNotBlank()) {
                Text(
                    text = adjustment.reason,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = formatter.format(Date(adjustment.adjustmentDate)),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    product: ProductEntity?,
    categories: List<ProductCategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    if (product == null) return

    var name by remember(product.productId) { mutableStateOf(product.name) }
    var brand by remember(product.productId) { mutableStateOf(product.brand) }
    var unit by remember(product.productId) { mutableStateOf(product.unit) }
    var purchasePrice by remember(product.productId) { mutableStateOf(product.purchasePrice.toString()) }
    var sellingPrice by remember(product.productId) { mutableStateOf(product.sellingPrice.toString()) }
    var warranty by remember(product.productId) { mutableStateOf(product.warrantyMonths.toString()) }
    var categoryId by remember(product.productId) { mutableStateOf(product.categoryId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Product") },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (pcs, kg, etc.)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    label = { Text("Purchase Price") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = sellingPrice,
                    onValueChange = { sellingPrice = it },
                    label = { Text("Selling Price") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = warranty,
                    onValueChange = { warranty = it },
                    label = { Text("Warranty (months)") },
                    singleLine = true
                )

                // Category selector (simple & safe)
                var expanded by remember { mutableStateOf(false) }
                val selectedCategory =
                    categories.firstOrNull { it.categoryId == categoryId }?.name ?: "Select Category"

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    categoryId = category.categoryId
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                enabled = name.isNotBlank()
                        && purchasePrice.toDoubleOrNull() != null
                        && sellingPrice.toDoubleOrNull() != null
                        && warranty.toIntOrNull() != null,
                onClick = {
                    onSave(
                        product.copy(
                            name = name.trim(),
                            brand = brand.trim(),
                            unit = unit.trim(),
                            purchasePrice = purchasePrice.toDouble(),
                            sellingPrice = sellingPrice.toDouble(),
                            warrantyMonths = warranty.toInt(),
                            categoryId = categoryId
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
