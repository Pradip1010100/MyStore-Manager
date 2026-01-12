package com.rootlink.mystoremanager.ui.screen.model

data class StockReportItemUi(
    val productName: String,
    val quantityOnHand: Double,
    val unit: String,
    val purchaseValue: Double,
    val sellingValue: Double
)