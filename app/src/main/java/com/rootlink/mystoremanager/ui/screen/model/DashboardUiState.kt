package com.rootlink.mystoremanager.ui.screen.model

data class DashboardUiState(
    val isLoading: Boolean = false,

    val todaySalesAmount: Double = 0.0,
    val todaySalesCount: Int = 0,
    val todayPurchaseAmount: Double = 0.0,

    val cashIn: Double = 0.0,
    val cashOut: Double = 0.0,

    val lowStockCount: Int = 0,
    val workersWithDue: Int = 0,

    val error: String? = null
)