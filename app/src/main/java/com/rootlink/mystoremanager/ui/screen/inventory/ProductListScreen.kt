package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.enums.ProductStatus
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel

enum class ProductFilter {
    ALL, ACTIVE, INACTIVE
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()

    var filter by remember { mutableStateOf(ProductFilter.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    val filteredProducts = remember(products, filter) {
        when (filter) {
            ProductFilter.ALL -> products
            ProductFilter.ACTIVE ->
                products.filter { it.status == ProductStatus.ACTIVE }
            ProductFilter.INACTIVE ->
                products.filter { it.status != ProductStatus.ACTIVE }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                actions = {
                    TextButton(
                        onClick = { navController.navigate(Routes.OLD_BATTERY_LIST) }
                    ) {
                        Text("Old Batteries")
                    }

                    TextButton(
                        onClick = { navController.navigate(Routes.LOW_STOCK) }
                    ) {
                        Text("Low Stock")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.PRODUCT_ADD) }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            /* ---------------- FILTER CHIPS ---------------- */

            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == ProductFilter.ALL,
                    onClick = { filter = ProductFilter.ALL },
                    label = { Text("All") }
                )

                FilterChip(
                    selected = filter == ProductFilter.ACTIVE,
                    onClick = { filter = ProductFilter.ACTIVE },
                    label = { Text("Active") }
                )

                FilterChip(
                    selected = filter == ProductFilter.INACTIVE,
                    onClick = { filter = ProductFilter.INACTIVE },
                    label = { Text("Inactive") }
                )
            }

            /* ---------------- LIST ---------------- */

            if (filteredProducts.isEmpty()) {
                EmptyProductState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredProducts) { product ->
                        InventoryProductRow(
                            product = product,
                            onOpenInventory = {
                                navController.navigate(
                                    "product_inventory/${product.productId}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun InventoryProductRow(
    product: ProductEntity,
    onOpenInventory: () -> Unit
) {
    val isInactive = product.status != ProductStatus.ACTIVE

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .then(
                if (isInactive)
                    Modifier.alpha(0.6f)
                else
                    Modifier
            ),
        border =
            if (isInactive)
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            else
                null,
        onClick = onOpenInventory
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (isInactive) {
                        Spacer(Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Inactive") }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = buildString {
                        if (product.brand.isNotBlank()) append(product.brand)
                        if (product.unit.isNotBlank()) append(" • ${product.unit}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "₹${product.sellingPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (isInactive)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



@Composable
private fun EmptyProductState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No products added yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
