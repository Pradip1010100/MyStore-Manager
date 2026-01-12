package com.rootlink.mystoremanager.ui.screen.model

import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.entity.StockEntity

data class StockUi(
    val product: ProductEntity,
    val stock: StockEntity
)