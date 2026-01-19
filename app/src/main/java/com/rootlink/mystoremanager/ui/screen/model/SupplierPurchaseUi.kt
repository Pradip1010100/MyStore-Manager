package com.rootlink.mystoremanager.ui.screen.model

data class SupplierPurchaseUi(
    val purchaseId: Long,
    val date: String,
    val items: List<PurchaseItemUi>,
    val totalAmount: Double,
    val dueAmount: Double
)