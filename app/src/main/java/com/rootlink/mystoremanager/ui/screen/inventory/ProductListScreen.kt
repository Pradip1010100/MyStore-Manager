package com.rootlink.mystoremanager.ui.screen.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    products: List<ProductEntity> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: navigate to AddProductScreen
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->

        if (products.isEmpty()) {
            EmptyProductState(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(products) { product ->
                    ProductRow(
                        product = product,
                        onStockClick = {
                            navController.navigate(Routes.STOCK_OVERVIEW)
                        },
                        onAdjustClick = {
                            navController.navigate(
                                "stock_adjustment/${product.productId}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    product: ProductEntity,
    onStockClick: () -> Unit,
    onAdjustClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Brand: ${product.brand}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Price: ₹${product.sellingPrice}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = onStockClick) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = "View Stock"
                    )
                }

                IconButton(onClick = onAdjustClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Adjust Stock"
                    )
                }
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
