package com.rootlink.mystoremanager.ui.screen.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rootlink.mystoremanager.data.entity.OrderEntity
import com.rootlink.mystoremanager.data.entity.enums.OrderStatus
import com.rootlink.mystoremanager.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    orders: List<OrderEntity> = emptyList() // temporary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.CREATE_ORDER)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Order")
            }
        }
    ) { paddingValues ->

        if (orders.isEmpty()) {
            EmptyOrderState(
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
                items(orders) { order ->
                    OrderRow(
                        order = order,
                        onConvertClick = {
                            if (order.status == OrderStatus.OPEN) {
                                // TODO: navigate to ConvertOrderScreen or directly convert
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderRow(
    order: OrderEntity,
    onConvertClick: () -> Unit
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
                text = "Order #${order.orderId}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Advance: ₹${order.advanceAmount}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Status: ${order.status.name}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (order.status == OrderStatus.OPEN) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onConvertClick) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Convert to Sale"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyOrderState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No orders created yet",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
