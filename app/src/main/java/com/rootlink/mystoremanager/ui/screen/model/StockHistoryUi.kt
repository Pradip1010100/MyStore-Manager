package com.rootlink.mystoremanager.ui.screen.model

import com.rootlink.mystoremanager.data.entity.ProductEntity
import com.rootlink.mystoremanager.data.entity.StockAdjustmentEntity

data class StockHistoryUi(
    val product: ProductEntity,
    val adjustments: List<StockAdjustmentEntity>
)