package com.rootlink.mystoremanager.ui.screen.inventory

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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.ui.navigation.Routes
import com.rootlink.mystoremanager.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                actions = {
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

        if (products.isEmpty()) {
            EmptyProductState(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(products) { product ->
                    InventoryProductRow(
                        product = product,
                        onOpenInventory = {
                            navController.navigate(
                                "product_inventory/${product.productId}"
                            )
                        },
                        onDeactivate = {
                            viewModel.deactivateProduct(product.productId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryProductRow(
    product: ProductEntity,
    onOpenInventory: () -> Unit,
    onDeactivate: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        onClick = onOpenInventory
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* -------- LEFT INFO -------- */
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(2.dp))

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
                    color = MaterialTheme.colorScheme.primary
                )
            }

            /* -------- ACTION -------- */
            IconButton(
                onClick = onDeactivate
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Deactivate Product",
                    tint = MaterialTheme.colorScheme.error
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
