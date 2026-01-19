package com.rootlink.mystoremanager.ui.screen.model

data class SaleItemUi(
    var productId: Long? = null,
    var productName: String = "",
    var availableStock: Double = 0.0,
    var quantity: Int = 1,
    var price: Double = 0.0
)
