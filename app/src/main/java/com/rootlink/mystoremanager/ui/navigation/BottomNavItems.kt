package com.rootlink.mystoremanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt

object BottomNavItems {

    val items = listOf(
        BottomNavItem(
            route = MainRoute.WORKERS.route,
            label = "Workers",
            icon = Icons.Default.People
        ),
        BottomNavItem(
            route = MainRoute.SUPPLIERS.route,
            label = "Suppliers",
            icon = Icons.Default.Business
        ),
        BottomNavItem(
            route = MainRoute.SALES.route,
            label = "Sales",
            icon = Icons.Default.Receipt
        ),
        BottomNavItem(
            route = MainRoute.INVENTORY.route,
            label = "Inventory",
            icon = Icons.Default.Inventory
        ),
        BottomNavItem(
            route = MainRoute.ACCOUNTING.route,
            label = "Accounting",
            icon = Icons.Default.AccountBalance
        ),
        BottomNavItem(
            route = MainRoute.REPORTS.route,
            label = "Reports",
            icon = Icons.Default.BarChart
        )
    )
}

