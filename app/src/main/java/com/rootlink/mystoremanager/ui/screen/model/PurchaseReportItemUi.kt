package com.rootlink.mystoremanager.ui.screen.model

data class PurchaseReportItemUi(
    val date: String,
    val totalPurchase: Double,
    val totalPaid: Double,
    val totalDue: Double,
    val purchaseCount: Int
)