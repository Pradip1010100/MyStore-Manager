package com.rootlink.mystoremanager.ui.screen.model

data class PurchaseItemUi(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
) {
    val lineTotal: Double
        get() = quantity * price
}
