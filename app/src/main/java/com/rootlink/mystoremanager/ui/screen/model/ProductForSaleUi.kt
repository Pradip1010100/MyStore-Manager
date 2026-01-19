package com.rootlink.mystoremanager.ui.screen.model

/* ---------- UI MODEL FOR SALES PRODUCT PICKER ---------- */
data class ProductForSaleUi(
    val productId: Long,
    val name: String,
    val brand: String,
    val unit: String,
    val sellingPrice: Double,
    val warrantyMonths: Int,
    val availableStock: Double
)
