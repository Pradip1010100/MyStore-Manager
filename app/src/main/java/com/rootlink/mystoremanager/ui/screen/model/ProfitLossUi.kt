package com.rootlink.mystoremanager.ui.screen.model

data class ProfitLossUi(
    val totalSales: Double,
    val totalPurchases: Double,
    val totalWorkerPayments: Double,
    val otherExpenses: Double
) {
    val profit: Double
        get() = totalSales -
                (totalPurchases + totalWorkerPayments + otherExpenses)
}