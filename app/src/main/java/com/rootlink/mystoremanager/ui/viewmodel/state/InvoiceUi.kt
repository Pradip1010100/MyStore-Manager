package com.rootlink.mystoremanager.ui.viewmodel.state

import com.rootlink.mystoremanager.data.entity.SaleEntity
import com.rootlink.mystoremanager.data.entity.SaleItemEntity

data class InvoiceUi(
    val sale: SaleEntity,
    val items: List<SaleItemEntity>
)